package com.cosean.search.api.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class TurkishWordService {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public String getTurkishWord(String value) {
        HashMap<String, String> mapParams = new HashMap<>();
        mapParams.put("tr_bad", value);
        Document post;
        try {
            post = Jsoup.connect("https://www.seslisozluk.net/yaz%C4%B1-t%C3%BCrk%C3%A7ele%C5%9Ftirme/")
                    .userAgent("Mozilla/5.0").data(mapParams).post();
            value = post.select("textarea#tr_good").text();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return value;
    }

}
