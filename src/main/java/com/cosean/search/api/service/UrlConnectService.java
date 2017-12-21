package com.cosean.search.api.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UrlConnectService {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public Document getHtmlByUrl(String url) {
        Document document = null;
        try {
            document  = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.124 Safari/537.36")
                    .referrer("http://www.google.com")
                    .followRedirects(true)
                    .get();

        } catch (Exception e) {
            logger.error("Url exp: " + url + " -> "+ e.getMessage());
        }
        return document;
    }
}
