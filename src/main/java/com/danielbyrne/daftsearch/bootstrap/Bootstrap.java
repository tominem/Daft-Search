package com.danielbyrne.daftsearch.bootstrap;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import com.danielbyrne.daftsearch.repositories.PropertyForSharingRepository;
import com.danielbyrne.daftsearch.services.GoogleMapServices;
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
    private PropertyForSaleRepository propertyForSaleRepository;
    private PropertyForRentRepository propertyForRentRepository;
    private PropertyForSharingRepository propertyForSharingRepository;
    private GoogleMapServices googleMapServices;
    private County county;

    private final String BASE_URL = "https://www.daft.ie/";
    private String FOR_SALE = "/property-for-sale/?offset=";
    private String TO_LET = "/residential-property-for-rent/?offset=";
    private String TO_SHARE = "/rooms-to-share/?offset=";

    public Bootstrap(PropertyForSaleRepository propertyForSaleRepository, PropertyForRentRepository propertyForRentRepository,
                     PropertyForSharingRepository propertyForSharingRepository, GoogleMapServices googleMapServices) {
        this.propertyForSaleRepository = propertyForSaleRepository;
        this.propertyForRentRepository = propertyForRentRepository;
        this.propertyForSharingRepository = propertyForSharingRepository;
        this.googleMapServices = googleMapServices;
    }

    @Override
    public void run(String... args) throws Exception {

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

        for (County c : County.values()) {

            this.county = c;

            int offset = 0;
            boolean propertiesExist = true;

            while (propertiesExist) {

                String startingUrl = BASE_URL + county + FOR_SALE + offset;

                Document document = Jsoup.connect(startingUrl).get();

                Elements sales = document.select(".PropertyCardContainer__container > a, " +
                        ".PropertyImage__mainImageContainerStandard > a");

                if (sales.size() == 0) propertiesExist = false;

                Set<String> urls = new HashSet<>();
                for (Element headline : sales) {
                    String u = headline.absUrl("href");
                    // dupe href elements being ignored
                    if (!urls.contains(u)){
                        System.out.println("\nSource URL: " + startingUrl);
                        loadPropertyForSale(headline.absUrl("href"));
                        urls.add(u);
                    }
                }
                offset += 20;
                propertiesExist=false;
                urls.clear();
            }
        }
    }

    private void loadRentals() throws IOException {

        for (County county : County.values()) {
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
                propertiesExist=false;
            }
        }
    }

    private void loadSharedProperties() throws IOException {

        for (County county : County.values()) {
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
        propertyForSale.setBeds(beds == null ? 0 : Integer.parseInt(beds.text().replaceAll("[^0-9.]", "")));
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

        propertyForSaleRepository.save(propertyForSale);

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

        propertyForRentRepository.save(propertyForRent);

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

        PropertyForSharing propertyForSharing = new PropertyForSharing();

        for (int i = 0; i < propertyOverview.size(); i++) {
            String str = propertyOverview.get(i).text().toLowerCase();
            if (str.contains("single")) propertyForSharing.setHasSingle(true);
            if (str.contains("double")) propertyForSharing.setHasDouble(true);
            if (str.contains("currently")) propertyForSharing.setCurrentOccupants(Integer.parseInt(str.substring(0,1)));
            if (str.contains("is owner occupied")) propertyForSharing.setOwnerOccupied(true);
            if (str.contains(" males only")) propertyForSharing.setMalesOnly(true);
            if (str.contains("females only")) propertyForSharing.setFemalesOnly(true);
        }

        propertyForSharing.setCounty(county);
        propertyForSharing.setPropertyType(checkIfElementIsNull(propertyType));
        propertyForSharing.setBeds(beds);
        propertyForSharing.setPriceString(priceString);
        propertyForSharing.setLink(link);
        propertyForSharing.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
        propertyForSharing.setAddress(address);
        propertyForSharing.setDescription(doc.getElementById("description").text());
        propertyForSharing.setPrice(price);
        propertyForSharing.setMalesOrFemales();

        propertyForSharingRepository.save(propertyForSharing);
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }
}