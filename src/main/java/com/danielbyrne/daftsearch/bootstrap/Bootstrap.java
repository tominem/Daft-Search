package com.danielbyrne.daftsearch.bootstrap;

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
            Document document = Jsoup.connect(link).get();
            Element element = document.select(".QuickPropertyDetails__propertyType").first();
            System.out.println(element.attributes().toString());
            // todo
        }
    }
}