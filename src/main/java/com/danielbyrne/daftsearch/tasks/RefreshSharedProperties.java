package com.danielbyrne.daftsearch.tasks;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import com.danielbyrne.daftsearch.repositories.PropertyForSharingRepository;
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
public class RefreshSharedProperties {

    private final PropertyForSharingRepository propertyForSharingRepository;
    private LocalDateTime localDateTime;

    public RefreshSharedProperties(PropertyForSharingRepository propertyForSharingRepository) {
        this.propertyForSharingRepository = propertyForSharingRepository;
    }

    public void loadSharedProperties() throws IOException {

        localDateTime = LocalDateTime.now();

        log.debug("Loading Shared Properties");
        long time = System.currentTimeMillis();

        for (County county : County.values()) {

            int offset = 0; boolean propertiesExist = true;
            while (propertiesExist) {

                String url = "https://www.daft.ie/" + county + "/rooms-to-share/?offset=" + offset;
                log.debug("Current URL: {}", url);

                Document document = Jsoup.connect(url).get(); Set<String> urls;
                Elements propertyElements = document.select("div.image a");

                if (propertyElements.size() == 0) propertiesExist = false;

                urls = new HashSet<>(20);
                for (Element headline : propertyElements) {
                    String u = headline.absUrl("href");
                    if (urls.add(u)) loadPropertyForSharing(u, county);
                }
                offset += 20;
            }
        }
        time = System.currentTimeMillis()-time;
        log.debug("Shared properties have been refreshed. " +
                "Time taken: {} minutes.", TimeUnit.MILLISECONDS.toMinutes(time));
    }

    private void loadPropertyForSharing(String link, County county) throws IOException {

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

        Element leaseAndAvailability = doc.getElementsByClass("description_block").get(1);

        String lease = null;
        String availability = null;

        try {
            if (leaseAndAvailability != null) {
                String str = leaseAndAvailability.text();

                lease = str.substring(str.indexOf(" Available for: ")).replace(" Available for: ", "");
                availability = str.substring(0, str.indexOf(" Available for: ")).replace("Available to Move In: ", "");
            }
        } catch (StringIndexOutOfBoundsException e) {
            log.error("Error parsing lease and availability information for property {}. Error {} ", link, e.getMessage());
        }

        PropertyForSharing propertyForSharing = new PropertyForSharing();

        propertyForSharing.setLeaseLength(lease);
        propertyForSharing.setMoveInDate(availability);

        for (int i = 0; i < propertyOverview.size(); i++) {
            String str = propertyOverview.get(i).text().toLowerCase();
            if (str.contains("shared")) propertyForSharing.setHasSharedRoom(true);
            if (str.contains("single")) propertyForSharing.setHasSingle(true);
            if (str.contains("double")) propertyForSharing.setHasDouble(true);
            if (str.contains("currently")) propertyForSharing.setCurrentOccupants(Integer.parseInt(str.substring(0,1)));
            if (str.contains("is owner occupied")) propertyForSharing.setOwnerOccupied(true);
            if (str.contains(" males only")) propertyForSharing.setMalesOnly(true);
            if (str.contains("females only")) propertyForSharing.setFemalesOnly(true);
        }

        propertyForSharing.setCounty(county);
        propertyForSharing.setLocalDateTime(LocalDateTime.now());
        propertyForSharing.setPropertyType(checkIfElementIsNull(propertyType));
        propertyForSharing.setBeds(beds);
        propertyForSharing.setPriceString(priceString);
        propertyForSharing.setLink(link);
        propertyForSharing.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
        propertyForSharing.setAddress(address);
        propertyForSharing.setPrice(price);
        propertyForSharing.setMalesOrFemales();
        propertyForSharing.setMonthlyRent();

        propertyForSharingRepository.save(propertyForSharing);
        log.debug("Saved Property: {}", propertyForSharing);
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }
}
