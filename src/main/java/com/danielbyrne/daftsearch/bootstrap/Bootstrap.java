package com.danielbyrne.daftsearch.bootstrap;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
import com.danielbyrne.daftsearch.services.GoogleMapServices;
import com.google.maps.errors.ApiException;
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
    private GoogleMapServices googleMapServices;

    private Set<String> propertyLinks = new HashSet<>();

    public Bootstrap(PropertyRepository propertyRepository, GoogleMapServices googleMapServices) {
        this.propertyRepository = propertyRepository;
        this.googleMapServices = googleMapServices;
    }

    @Override
    public void run(String... args) throws Exception {

////        propertyLinks.add("https://www.daft.ie/wexford/houses-for-sale/rosslare-harbour/glenelg-barryville-court-rosslare-harbour-wexford-2151678/");
//        loadSaleLinks();
//        loadPropertiesForSale();

        propertyLinks.add("https://www.daft.ie/wicklow/studio-apartments-for-rent/greystones/new-road-greystones-wicklow-1953887/");
//        loadRentalLinks();
        loadPropertiesForRent();

        System.out.println("Done");
    }

    private void loadSaleLinks() throws IOException {

        for (Enum county : County.values()) {

            int offset = 0;
            boolean propertiesExist = true;

            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + "/houses-for-sale/?s[mxp]=150000&s[mnb]=2/?offset=" + offset;

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

    private void loadRentalLinks() throws IOException {

        for (Enum county : County.values()) {

            int offset = 0;
            boolean propertiesExist = true;

            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + "/residential-property-for-rent/?offset=" + offset;

                Document document = Jsoup.connect(url).get();

                Elements newsHeadlines = document.select("div.image a");

                if (newsHeadlines.size() == 0) propertiesExist = false;

                for (Element headline : newsHeadlines) {
                    propertyLinks.add(headline.absUrl("href"));
                }
                offset += 20;
            }
        }
        System.out.println(propertyLinks.size());
    }


    private void loadPropertiesForSale() throws Exception {

        for (String link : propertyLinks) {

            System.out.println(link);

            Document doc = Jsoup.connect(link).get();

            Element eircode = doc.select(".PropertyMainInformation__eircode").first();
            Element propertyType = doc.select(".QuickPropertyDetails__propertyType").first();
            Element beds = doc.select(".QuickPropertyDetails__iconCopy").first();
            Element baths = doc.select(".QuickPropertyDetails__iconCopy--WithBorder").first();
            Element priceElement = doc.select(".PropertyInformationCommonStyles__costAmountCopy").first();
            Elements image = doc.select("img[\\.(jpg)]");


            int price;
            String priceString;
            if (priceElement.getAllElements().select(".priceFrom") != null
                    && !priceElement.getAllElements().select(".priceFrom").text().equals("") ) {

                priceString = priceElement.getAllElements().select(".priceFrom").text();
                price = Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));
            } else {
                priceString = priceElement.text();
                price = priceString.equals("Price On Application")
                        ? 0 : Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));
            }

//            String pr = priceElement.getAllElements().select(".priceFrom").text();

            String address = doc.select(".PropertyMainInformation__address").first().text();

            PropertyForSale propertyForSale = new PropertyForSale();

            propertyForSale.setPriceString(priceString);
            propertyForSale.setLink(doc.select(".PropertyShortcode__link").text());

            propertyForSale.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
            propertyForSale.setPropertyType(checkIfElementIsNull(propertyType));
            propertyForSale.setAddress(address);
            propertyForSale.setEircode(checkIfElementIsNull(eircode).replace("Eircode: ", ""));
            propertyForSale.setBeds(beds == null ? 0 : Integer.parseInt(beds.text()));
            propertyForSale.setBaths(baths == null ? 0 : Integer.parseInt(baths.text()));
            propertyForSale.setDescription(doc.select(".PropertyDescription__propertyDescription").first().text());
            propertyForSale.setPrice(price);

//            DistanceMatrix distanceMatrix = googleMapServices.getDrivingDistance(address, DESTINATION);
//            DistanceMatrixElement distanceMatrixElement = distanceMatrix.rows[0].elements[0];
//
//            if (distanceMatrixElement.distance != null) {
//                propertyForSale.setDistanceInMetres(distanceMatrixElement.distance.inMeters);
//                propertyForSale.setDuration(distanceMatrixElement.duration.inSeconds);
//                propertyForSale.setReadableDistance(distanceMatrixElement.distance.humanReadable);
//                propertyForSale.setReadableDuration(distanceMatrixElement.duration.humanReadable);
//            }

            propertyRepository.save(propertyForSale);

//            if (property.getDuration() != null && property.getDuration()/60 < 60 ) {
//                System.out.println(property.toString());
//            }
        }
        propertyLinks.clear();
    }

    private void loadPropertiesForRent() throws InterruptedException, ApiException, IOException {

        for (String link : propertyLinks) {

            System.out.println(link);

            Document doc = Jsoup.connect(link).get();

            if (doc.getElementsByClass("warning").text().contains("This Property Has been" +
                    " either let or withdrawn from Daft.ie")) {
                break;
            }

            Elements summaryItems = doc.getElementById("smi-summary-items").select(".header_text");

            Element propertyType = summaryItems.get(0);

            int beds = 0;
            int baths = 0;
            for (int i = 0; i < summaryItems.size(); i++) {
                String str = summaryItems.get(i).text();
                if (str.contains("Beds")) {
                    beds = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                } else if (str.contains("Bath")) {
                    baths = Integer.parseInt(str.replaceAll("[^0-9]", ""));
                }
            }

            String priceString = doc.getElementById("smi-price-string").text();
            int price = Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));

            String address = doc.getElementsByClass("smi-object-header").select("h1").text();

//            String leaseAndAvailString = doc.getElementsByClass("description_block")
//                                                    .get(1)
//                                                    .getAllElements()
//                                                    .first()
//                                                    .text();

            PropertyForRent propertyForRent = new PropertyForRent();

//            propertyForRent.setLeaseLength(leaseAndAvailString.substring(leaseAndAvailString.indexOf("Lease: ") + "Lease: ".length()));
//            propertyForRent.setMoveInDate(leaseAndAvailString.substring(22, leaseAndAvailString.indexOf(" Lease: ")));
            propertyForRent.setPropertyType(checkIfElementIsNull(propertyType));
            propertyForRent.setBeds(beds);
            propertyForRent.setBaths(baths);

            propertyForRent.setPriceString(priceString);
            propertyForRent.setLink(link);

            propertyForRent.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
            propertyForRent.setAddress(address);
            propertyForRent.setDescription(doc.getElementById("description").text());
            propertyForRent.setPrice(price);

            // todo move into separate method
//            DistanceMatrix distanceMatrix = googleMapServices.getDrivingDistance(address, DESTINATION);
//            DistanceMatrixElement distanceMatrixElement = distanceMatrix.rows[0].elements[0];
//
//            if (distanceMatrixElement.distance != null) {
//                propertyForRent.setDistanceInMetres(distanceMatrixElement.distance.inMeters);
//                propertyForRent.setDuration(distanceMatrixElement.duration.inSeconds);
//                propertyForRent.setReadableDistance(distanceMatrixElement.distance.humanReadable);
//                propertyForRent.setReadableDuration(distanceMatrixElement.duration.humanReadable);
//            }

            propertyRepository.save(propertyForRent);

//            if (property.getDuration() != null && property.getDuration()/60 < 60 ) {
//                System.out.println(property.toString());
//            }
        }
        propertyLinks.clear();
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }
}