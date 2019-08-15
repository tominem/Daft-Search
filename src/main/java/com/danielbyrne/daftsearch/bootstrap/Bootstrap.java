package com.danielbyrne.daftsearch.bootstrap;

import com.danielbyrne.daftsearch.domain.Property;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
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

    private PropertyRepository propertyRepository;
    private Set<String> propertyLinks = new HashSet<>();

    public Bootstrap(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadLinks();
        loadProperties();
    }

    private void loadLinks() throws IOException {
        Document document = Jsoup.connect("https://www.daft.ie/wicklow/property-for-sale/").get();

        Elements newsHeadlines = document.select(".PropertyCardContainer__container > a, " +
                ".PropertyImage__mainImageContainerStandard > a");

        for (Element headline : newsHeadlines) {
            propertyLinks.add(headline.absUrl("href"));
        }

        System.out.println(propertyLinks.size());
    }

    private void loadProperties() throws Exception {

        for (String link : propertyLinks) {

            System.out.println(link);

            Document doc = Jsoup.connect(link).get();

            Element eircode = doc.select(".PropertyMainInformation__eircode").first();
            Element beds = doc.select(".QuickPropertyDetails__iconCopy").first();
            Element baths = doc.select(".QuickPropertyDetails__iconCopy--WithBorder").first();
            Element price = doc.select(".PropertyInformationCommonStyles__costAmountCopy").first();

            Property property = new Property();

            property.setId(Long.valueOf(doc.select(".PropertyShortcode__link").text().replace("https://www.daft.ie/", "")));
            property.setPropertyType(doc.select(".QuickPropertyDetails__propertyType").first().text());
            property.setAddress(doc.select(".PropertyMainInformation__address").first().text());
            property.setEircode(eircode == null ? null : eircode.text().replace("Eircode: ", ""));
            property.setBeds(beds == null ? 0 : Integer.valueOf(beds.text()));
            property.setBaths(baths == null ? 0 : Integer.valueOf(baths.text()));
            property.setDescription(doc.select(".PropertyDescription__propertyDescription").first().text());
            property.setPrice(price.text().equals("Price On Application") ? 0 : Integer.valueOf(price.text().replaceAll("[^0-9.]", "")));

            propertyRepository.save(property);
        }
    }
}