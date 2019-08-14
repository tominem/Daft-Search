package com.danielbyrne.daftsearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class Bootstrap {

    public Bootstrap() throws IOException {

        Document document = Jsoup.connect("https://www.daft.ie/wicklow/property-for-sale/").get();
        Elements newsHeadlines = document.select(".PropertyCardContainer__container > a, .PropertyImage__mainImageContainerStandard > a");

        Set<String> links = new HashSet<>();

        for (Element headline : newsHeadlines) {
            links.add(headline.absUrl("href"));
        }

        System.out.println(links.size());
        for (String s : links) {
            System.out.println(s);
        }
    }
}