package com.cosean.search.api.controller;

import com.cosean.search.api.service.EsAnlamliService;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/esanlamli")
public class EsAnlamliController {

    private EsAnlamliService esAnlamliService;

    public EsAnlamliController(EsAnlamliService esAnlamliService) {
        this.esAnlamliService = esAnlamliService;
    }

    @GetMapping("/{word}")
    public ResponseEntity<String[]> getOnly(@PathVariable("word") String word) {

        Document doc = esAnlamliService.getEsAnlamliByword(word);
        if (doc == null)
            return new ResponseEntity<>(new String[0], HttpStatus.NOT_FOUND);

        Elements select = doc.select("h2#esanlamlar");
        Elements strong = select.select("strong");

        if (strong.text().equals("BULUNAMADI !"))
            return new ResponseEntity<>(new String[0], HttpStatus.NOT_FOUND);

        String[] split = strong.text().split(",");
        return new ResponseEntity<>(split, HttpStatus.OK);
    }

}
