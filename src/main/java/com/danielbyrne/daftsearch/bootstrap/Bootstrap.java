package com.danielbyrne.daftsearch.bootstrap;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.Property;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
import com.danielbyrne.daftsearch.services.GoogleMapServices;
import com.google.maps.model.DistanceMatrix;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class Bootstrap implements CommandLineRunner {

    private final String DESTINATION = "38 Rathgar Road, Dublin, Ireland";
    private PropertyRepository propertyRepository;

    private Set<String> propertyLinks = new HashSet<>();

    public Bootstrap(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        loadLinks();
        loadProperties();
        System.out.println("Done");
    }

    private void loadLinks() throws IOException {

        for (Enum county : County.values()) {

            int offset = 0;
            boolean propertiesExist = true;

            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + "/houses-for-sale/?s[mxp]=200000&s[mnb]=2/?offset=" + offset;

                Document document = Jsoup.connect(url).get();

                Elements newsHeadlines = document.select(".PropertyCardContainer__container > a, " +
                        ".PropertyImage__mainImageContainerStandard > a");

                if (newsHeadlines.size() == 0) propertiesExist = false;

                for (Element headline : newsHeadlines) {
                    propertyLinks.add(headline.absUrl("href"));
                }
                offset += 20;
            }
        }
        System.out.println(propertyLinks.size());
    }

    private void loadProperties() throws Exception {

        for (String link : propertyLinks) {

//            System.out.println(link);

            Document doc = Jsoup.connect(link).get();

            Element eircode = doc.select(".PropertyMainInformation__eircode").first();
            Element beds = doc.select(".QuickPropertyDetails__iconCopy").first();
            Element baths = doc.select(".QuickPropertyDetails__iconCopy--WithBorder").first();
            Element price = doc.select(".PropertyInformationCommonStyles__costAmountCopy").first();

            String address = doc.select(".PropertyMainInformation__address").first().text();

            Property property = new Property();
            property.setLink(doc.select(".PropertyShortcode__link").text());
            property.setId(Long.valueOf(doc.select(".PropertyShortcode__link").text().replace("https://www.daft.ie/", "")));
            property.setPropertyType(doc.select(".QuickPropertyDetails__propertyType").first().text());
            property.setAddress(address);
            property.setEircode(eircode == null ? null : eircode.text().replace("Eircode: ", ""));
            property.setBeds(beds == null ? 0 : Integer.parseInt(beds.text()));
            property.setBaths(baths == null ? 0 : Integer.parseInt(baths.text()));
            property.setDescription(doc.select(".PropertyDescription__propertyDescription").first().text());
            property.setPrice(price.text().equals("Price On Application") ? 0 : Integer.parseInt(price.text().replaceAll("[^0-9.]", "")));

            DistanceMatrix distanceMatrix = GoogleMapServices.getDrivingDistance(address, DESTINATION);

            Long distance = distanceMatrix.rows[0].elements[0].distance.inMeters;
            Long duration = distanceMatrix.rows[0].elements[0].duration.inSeconds;
            Long durationInTraffic = distanceMatrix.rows[0].elements[0].durationInTraffic == null
                                    ? Long.valueOf(0) : distanceMatrix.rows[0].elements[0].durationInTraffic.inSeconds;

            property.setDistanceInMetres(distance);
            property.setDuration(duration);
            property.setDurationInTraffic(durationInTraffic);

            propertyRepository.save(property);

            if (property.getDuration()/60 < 60 ) {
                System.out.println(property.toString());
            }
        }
    }
}