package com.danielbyrne.daftsearch.tasks;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RefreshRentalProperties {

    private final PropertyForRentRepository propertyForRentRepository;
    private LocalDateTime localDateTime;

    public RefreshRentalProperties(PropertyForRentRepository propertyForRentRepository) {
        this.propertyForRentRepository = propertyForRentRepository;
    }

    public void loadRentals() throws IOException {

        localDateTime = LocalDateTime.now();

        log.debug("Loading Rental Properties");
        long time = System.currentTimeMillis();

        for (County county : County.values()) {

            int offset = 0; boolean propertiesExist = true; Set<String> urls;
            while (propertiesExist) {
                String sourceUrl = "https://www.daft.ie/" + county + "/residential-property-for-rent/?offset=" + offset;
                log.debug("Current URL: {}", sourceUrl);

                Document document = Jsoup.connect(sourceUrl).get();
                Elements propertyElements = document.select("div.image a");

                if (propertyElements.size() == 0) propertiesExist = false;

                urls = new HashSet<>(20);
                for (Element pe : propertyElements) {
                    String u = pe.absUrl("href");
                    if (urls.add(u)) loadPropertyForRent(u, county);
                }
                offset+=20;
            }
        }
        time = System.currentTimeMillis()-time;
        log.debug("Rental properties have been refreshed. " +
                "Time taken: {} minutes.", TimeUnit.MILLISECONDS.toMinutes(time));
    }

    private void loadPropertyForRent(String link, County county) {

        Document doc = null;
        try {
            doc = Jsoup.connect(link).get();
        } catch (IOException e) {
            log.error("Unable to get to {}. Error: {}", link, e.getMessage());
            return;
        }

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

            try {
                lease = str.substring(str.indexOf(" Lease: ")).replace(" Lease: ", "");
                availability = str.substring(0, str.indexOf(" Lease: ")).replace("Available to Move In: ", "");
            } catch (StringIndexOutOfBoundsException e) {
                log.error("Error parsing lease and availability information for property {}. Error {} ", link, e.getMessage());
            }
        }

        PropertyForRent pfr = new PropertyForRent();

        pfr.setLeaseLength(lease);
        pfr.setMoveInDate(availability);
        pfr.setCounty(county);

        pfr.setPropertyType(checkIfElementIsNull(propertyType));
        pfr.setBeds(beds);
        pfr.setBaths(baths);

        pfr.setPriceString(priceString);
        pfr.setLink(link);

        Long id = Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", ""));
        pfr.setId(id);

        pfr.setAddress(address);
        pfr.setPrice(price);
        pfr.setMonthlyRent();
        pfr.setLocalDateTime(LocalDateTime.now());

        propertyForRentRepository.save(pfr);
        log.debug("Saved Property: {}", pfr);
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }
}
