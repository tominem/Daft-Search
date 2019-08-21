package com.danielbyrne.daftsearch.bootstrap;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
import com.danielbyrne.daftsearch.services.GoogleMapServices;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Bootstrap implements CommandLineRunner {

    private final String DESTINATION = "38 Rathgar Road, Dublin, Ireland";
    private PropertyRepository propertyRepository;
    private GoogleMapServices googleMapServices;
    private Enum county;

    private final String BASE_URL = "https://www.daft.ie/";
    private String FOR_SALE = "/property-for-sale/?offset=";
    private String TO_LET = "/residential-property-for-rent/?offset=";
    private String TO_SHARE = "/rooms-to-share/?offset=";

    public Bootstrap(PropertyRepository propertyRepository, GoogleMapServices googleMapServices) {
        this.propertyRepository = propertyRepository;
        this.googleMapServices = googleMapServices;
    }

    @Override
    public void run(String... args) throws Exception {

        loadPropertyForSharing("https://www.daft.ie/cavan/flat-to-share/cavan/75-main-street-cavan-cavan-1100787/");
//        System.out.println("\nLoading Shared Properties...");
//        loadSharedProperties();
//
//        System.out.println("\nLoading Sales...");
//        loadSales();
//
//        System.out.println("\nLoading Rentals...");
//        loadRentals();
    }

    private void loadSales() throws Exception {

        for (Enum county : County.values()) {

            this.county = county;

            int offset = 0;
            boolean propertiesExist = true;

            while (propertiesExist) {

                String url = BASE_URL + county + FOR_SALE + offset;

                Document document = Jsoup.connect(url).get();

                Elements newsHeadlines = document.select(".PropertyCardContainer__container > a, " +
                        ".PropertyImage__mainImageContainerStandard > a");

                if (newsHeadlines.size() == 0) propertiesExist = false;

                for (Element headline : newsHeadlines) {
                    System.out.println("\nSource URL: " + url);
                    loadPropertyForSale(headline.absUrl("href"));
                }
                offset += 20;
            }
        }
    }

    private void loadRentals() throws IOException {

        for (Enum county : County.values()) {
            this.county = county;

            int offset = 0;
            boolean propertiesExist = true;
            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + TO_LET + offset;
                Document document = Jsoup.connect(url).get();
                Elements propertyElements = document.select("div.image a");

                if (propertyElements.size() == 0) propertiesExist = false;

                for (Element headline : propertyElements) {
                    System.out.println("\nSource URL: " + url);
                    loadPropertyForRent(headline.absUrl("href"));
                }
                offset += 20;
                // todo this needs to be removed in order to load all properties
                propertiesExist=false;
            }
        }
    }

    private void loadSharedProperties() throws IOException {

        for (Enum county : County.values()) {
            this.county = county;

            int offset = 0;
            boolean propertiesExist = true;
            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + TO_SHARE + offset;
                Document document = Jsoup.connect(url).get();
                Elements propertyElements = document.select("div.image a");

                if (propertyElements.size() == 0) propertiesExist = false;

                for (Element headline : propertyElements) {
                    System.out.println("\nSource URL: " + url);
                    loadPropertyForSharing(headline.absUrl("href"));
                }
                offset += 20;
                // todo this needs to be removed in order to load all properties
                propertiesExist=false;
            }
        }
    }

    private void loadPropertyForSale(String link) throws Exception {

        System.out.println("Creating Property From: " + link);

        Document doc = Jsoup.connect(link).get();

        Element eircode = doc.select(".PropertyMainInformation__eircode").first();
        Element description = doc.select(".PropertyDescription__propertyDescription").first();
        Element developmentDescription = doc.selectFirst(".PropertyDescription__developmentDescription");
        Element propertyType = doc.select(".QuickPropertyDetails__propertyType").first();
        Element beds = doc.select(".QuickPropertyDetails__iconCopy").first();
        Element baths = doc.select(".QuickPropertyDetails__iconCopy--WithBorder").first();
        Element priceElement = doc.select(".PropertyInformationCommonStyles__costAmountCopy").first();
//        Elements image = doc.select("img[\\.(jpg)]");
        Element address = doc.select(".PropertyMainInformation__address").first();

        int price;
        String priceString;
        if (priceElement.getAllElements().select(".priceFrom") != null
                && !priceElement.getAllElements().select(".priceFrom").text().equals("") ) {

            priceString = priceElement.getAllElements().select(".priceFrom").text();
            price = Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));
        } else {
            priceString = priceElement.text();
            price = priceString.toLowerCase().equals("price on application")
                    ? 0 : Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));
        }

        PropertyForSale propertyForSale = new PropertyForSale();

        propertyForSale.setCounty(county);
        propertyForSale.setPriceString(priceString);
        propertyForSale.setLink(link);
        propertyForSale.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
        propertyForSale.setPropertyType(checkIfElementIsNull(propertyType));
        propertyForSale.setAddress(checkIfElementIsNull(address));
        propertyForSale.setEircode(checkIfElementIsNull(eircode).replace("Eircode: ", ""));
        propertyForSale.setBeds(beds == null ? 0 : Integer.parseInt(beds.text()));
        propertyForSale.setBaths(baths == null ? 0 : Integer.parseInt(baths.text()));

        if (developmentDescription != null) {
            propertyForSale.setDescription(developmentDescription.text());
            propertyForSale.setPropertyType("New Development");
        } else {
            propertyForSale.setDescription(checkIfElementIsNull(description));
        }

        if (propertyForSale.getPropertyType().equals("New Development")
                && !propertyForSale.getPriceString().toLowerCase().equals("price on application")) {
            propertyForSale.setPriceString("Prices start at " + propertyForSale.getPriceString());
        }

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

    private void loadPropertyForRent(String link) throws IOException {

        System.out.println("Creating Property From: " + link);

        Document doc = Jsoup.connect(link).get();

        if (doc.getElementsByClass("warning").text().contains("This Property Has been" +
                " either let or withdrawn from Daft.ie")) {
            return;
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

        propertyForRent.setCounty(county);

        //todo bit of a pain to parse, need to focus more time on it

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

    private void loadPropertyForSharing(String link) throws IOException {

        System.out.println("Creating Shared Property From: " + link);

        Document doc = Jsoup.connect(link).get();

        if (doc.getElementsByClass("warning").text().contains("This Property Has been" +
                " either let or withdrawn from Daft.ie")) {
            return;
        }

        Elements summaryItems = doc.getElementById("smi-summary-items").select(".header_text");
        Element propertyType = summaryItems.get(0);

        int beds = 0;
        for (int i = 0; i < summaryItems.size(); i++) {
            String str = summaryItems.get(i).text();
            if (str.toLowerCase().contains("bed")) {
                beds = Integer.parseInt(str.substring(0 , 1));
            }
        }

        String priceString = doc.getElementById("smi-price-string").text();
        int price = Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));

        String address = doc.getElementsByClass("smi-object-header").select("h1").text();

        Elements propertyOverview = doc.getElementById("overview").select("li");

        PropertyForSharing propertyForRent = new PropertyForSharing();

        for (int i = 0; i < propertyOverview.size(); i++) {
            String str = propertyOverview.get(i).text().toLowerCase();
            if (str.contains("single")) propertyForRent.setHasSingle(true);
            if (str.contains("double")) propertyForRent.setHasDouble(true);
            if (str.contains("currently")) propertyForRent.setCurrentOccupants(Integer.parseInt(str.substring(0,1)));
            if (str.contains("is owner occupied")) propertyForRent.setOwnerOccupied(true);
            if (str.contains("males only")) propertyForRent.setMalesOnly(true);
            if (str.contains("females only")) propertyForRent.setFemalesOnly(true);
        }

        propertyForRent.setCounty(county);
        propertyForRent.setPropertyType(checkIfElementIsNull(propertyType));
        propertyForRent.setBeds(beds);
        propertyForRent.setPriceString(priceString);
        propertyForRent.setLink(link);
        propertyForRent.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
        propertyForRent.setAddress(address);
        propertyForRent.setDescription(doc.getElementById("description").text());
        propertyForRent.setPrice(price);

        propertyRepository.save(propertyForRent);
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }
}