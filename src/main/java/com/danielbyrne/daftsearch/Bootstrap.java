package com.danielbyrne.daftsearch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Bootstrap {

    public Bootstrap() throws IOException {

        Document document = Jsoup.connect("http://en.wikipedia.org/").get();

        System.out.println(document.title());
        Elements newsHeadlines = document.select("#mp-itn b a");

        for (Element headline : newsHeadlines) {
            System.out.println(headline.attr("title") + ": " + headline.absUrl("href"));
        }
    }
}