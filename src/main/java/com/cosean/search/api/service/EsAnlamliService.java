package com.cosean.search.api.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EsAnlamliService {
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    public Document getEsAnlamliByword(String name) {
        Document document = null;
        try {
            document = Jsoup.connect("http://www.es-anlam.com/kelime/"+name).get();
        } catch (IOException e) {
            logger.info("Url exp: "+ e.getMessage());
        }
        return document;
    }
}
