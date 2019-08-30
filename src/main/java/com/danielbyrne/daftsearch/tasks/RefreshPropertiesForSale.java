package com.danielbyrne.daftsearch.tasks;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RefreshPropertiesForSale {

    private PropertyForSaleRepository propertyForSaleRepository;
    private LocalDateTime localDateTime;

    public RefreshPropertiesForSale(PropertyForSaleRepository propertyForSaleRepository) {
        this.propertyForSaleRepository = propertyForSaleRepository;
    }

    public void loadSales() throws Exception {

        localDateTime = LocalDateTime.now();

        log.debug("Loading Properties For Sale");
        long time = System.currentTimeMillis();

        for (County county : County.values()) {

            int offset = 0; boolean propertiesExist = true; Set<String> urls;
            while (propertiesExist) {

                String startingUrl = "https://www.daft.ie/" + county + "/property-for-sale/?offset=" + offset;
                log.debug("Current URL: {}", startingUrl);

                Document document = Jsoup.connect(startingUrl).get();

                Elements sales = document.select(".PropertyCardContainer__container > a, " +
                        ".PropertyImage__mainImageContainerStandard > a");

                if (sales.size() == 0) propertiesExist = false;

                urls = new HashSet<>();
                for (Element headline : sales) {
                    String u = headline.absUrl("href");
                    if (urls.add(u)) loadPropertyForSale(u, county);
                }
                offset += 20;
            }
        }
        time = System.currentTimeMillis()-time;
        log.debug("Properties For Sale have been refreshed. " +
                "Time taken: {} minutes.", TimeUnit.MILLISECONDS.toMinutes(time));
    }

    private void loadPropertyForSale(String link, County county) throws Exception {

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
            price = priceString.toLowerCase().contains(" on application")
                    ? 0 : Integer.parseInt(priceString.replaceAll("[^0-9.]", ""));
        }

        PropertyForSale pfs = new PropertyForSale();

        pfs.setCounty(county);
        pfs.setPriceString(priceString);
        pfs.setLink(link);
        pfs.setId(Long.valueOf(link.substring(link.lastIndexOf("-")+1).replaceAll("[^0-9.]", "")));
        pfs.setPropertyType(checkIfElementIsNull(propertyType));
        pfs.setAddress(checkIfElementIsNull(address));
        pfs.setEircode(checkIfElementIsNull(eircode).replace("Eircode: ", ""));
        pfs.setBeds(beds == null ? 0 : Integer.parseInt(beds.text().replaceAll("[^0-9.]", "")));
        pfs.setBaths(baths == null ? 0 : Integer.parseInt(baths.text()));
        pfs.setLocalDateTime(LocalDateTime.now());

        if (developmentDescription != null) pfs.setPropertyType("New Development");

        if (pfs.getPropertyType().equals("New Development")
                && !pfs.getPriceString().toLowerCase().equals("price on application")) {
            pfs.setPriceString("Prices start at " + pfs.getPriceString());
        }

        pfs.setPrice(price);

        propertyForSaleRepository.save(pfs);
        log.debug("Saved Property: {}", pfs);
    }


    /**
     * Runs after refresh task. Finds properties with an datetime < the
     * time the refresh started. This are deemed to be no longer valid
     */
    public void removeOlderProperties(){

        log.debug("Retrieving properties that have a datetime < start of refresh task");
        List<PropertyForSale> pList = propertyForSaleRepository.getPropertiesWithDateBefore(this.localDateTime);

        if (pList!=null) propertyForSaleRepository.deleteAll(pList);
        log.debug("{} properties deleted", pList.size());
    }

    private String checkIfElementIsNull(Element e) {
        return e == null ? "" : e.text();
    }
}
