package com.danielbyrne.daftsearch.tasks;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;
import com.danielbyrne.daftsearch.services.PropertyForRentService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RefreshRentalProperties {

    PropertyForRentService propertyForRentService;
    PropertyForRentRepository propertyForRentRepository;
    private County county;
    private final String TO_LET = "/residential-property-for-rent/?offset=";
    private List<Long> propertyIds = new ArrayList<>();

    public RefreshRentalProperties(PropertyForRentService propertyForRentService) {
        this.propertyForRentService = propertyForRentService;
    }

    public void loadRentals() throws IOException {

        log.debug("Loading Rental Properties");
        long time = System.currentTimeMillis();

        for (County county : County.values()) {

            this.county = county;

            int offset = 0;
            boolean propertiesExist = true;
            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + TO_LET + offset;
                log.debug("Current URL: {}", url);

                Document document = Jsoup.connect(url).get();
                Elements propertyElements = document.select("div.image a");

                if (propertyElements.size() == 0) propertiesExist = false;

                for (Element headline : propertyElements) {
                    loadPropertyForRent(headline.absUrl("href"));
                }
                offset += 20;
            }
        }

        time = System.currentTimeMillis()-time;
        log.debug("Rental properties have been refreshed. " +
                "Time taken: {} minutes.", TimeUnit.MILLISECONDS.toMinutes(time));
    }

    private void loadPropertyForRent(String link) throws IOException {

        log.debug("Attempting to save property at the following: {}", link);

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

        Element leaseAndAvailability = doc.getElementsByClass("description_block").get(1);

        String lease = null;
        String availability = null;
        if (leaseAndAvailability != null) {
            String str = leaseAndAvailability.text();

            lease = str.substring(str.indexOf(" Lease: ")).replace(" Lease: ", "");
            availability = str.substring(0, str.indexOf(" Lease: ")).replace("Available to Move In: ", "");
        }

        PropertyForRent propertyForRent = new PropertyForRent();

        propertyForRent.setLeaseLength(lease);
        propertyForRent.setMoveInDate(availability);
        propertyForRent.setCounty(county);

        propertyForRent.setPropertyType(checkIfElementIsNull(propertyType));
        propertyForRent.setBeds(beds);
        propertyForRent.setBaths(baths);

        propertyForRent.setPriceString(priceString);
        propertyForRent.setLink(link);

        Long id = Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", ""));
        propertyForRent.setId(id);
        propertyIds.add(id);

        propertyForRent.setAddress(address);
        propertyForRent.setDescription(doc.getElementById("description").text());
        propertyForRent.setPrice(price);
        propertyForRent.setMonthlyRent();

        log.debug("Save property to repository: {}", propertyForRent);
        propertyForRentRepository.save(propertyForRent);
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }



}