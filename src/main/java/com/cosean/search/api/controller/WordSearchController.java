package com.cosean.search.api.controller;

import com.cosean.search.api.model.MultiWordSearchRequest;
import com.cosean.search.api.model.WordSearchResponse;
import com.cosean.search.api.model.OnlyWordSearchRequest;
import com.cosean.search.api.model.WebSiteProfile;
import com.cosean.search.api.service.UrlConnectService;
import com.cosean.search.api.service.TurkishWordService;
import com.cosean.search.api.util.SearchUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@CrossOrigin
@RestController
@RequestMapping("v1/word")
public class WordSearchController {
    private final UrlConnectService urlConnectService;
    private final TurkishWordService turkishWordService;
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private String mediaRegexPattern = "(http)?s?:?(\\/\\/[^\"']*\\.(?:png|jpg|jpeg|gif|PNG|JPG|JPEG|GIF|doc|docx|rar|zip|xls|pdf|svg|PDF|SVG))";
    private String aHref = "a[href]";
    private String absHref = "abs:href";

    public WordSearchController(UrlConnectService urlConnectService, TurkishWordService turkishWordService) {
        this.urlConnectService = urlConnectService;
        this.turkishWordService = turkishWordService;
    }

    @PostMapping("/only")
    public ResponseEntity<WordSearchResponse> getOnly(@RequestBody OnlyWordSearchRequest request) {
        List<String> error = new ArrayList<>();
        List<WebSiteProfile> profileList = new ArrayList<>();

        if (Objects.isNull(request.getUrl()) || Objects.isNull(request.getWord())){
            error.add("Url yolla da çalışsın");
            return new ResponseEntity<>(new WordSearchResponse(profileList,error),HttpStatus.BAD_REQUEST);
        }

        Document htmlDoc = urlConnectService.getHtmlByUrl(request.getUrl());
        if (Objects.isNull(htmlDoc)) {
            error.add("Read timeout: " + request.getUrl());
            return new ResponseEntity<>(new WordSearchResponse(profileList, error), HttpStatus.NOT_FOUND);
        }

        String[] htmlText = htmlDoc.body().text().split("\\s+");
        String[] turkishWord = turkishWordService.getTurkishWord(request.getWord()).split("\\s+");
        HashMap<String, Integer> map = stream(turkishWord).parallel()
                .collect(Collectors.toMap(word -> word, word -> SearchUtils.search(htmlText, word), (a, b) -> b, HashMap::new));

        profileList.add(new WebSiteProfile(request.getUrl(),map));
        return new ResponseEntity<>(new WordSearchResponse(profileList, error), HttpStatus.OK);
    }


    @PostMapping("/multi")
    public ResponseEntity<WordSearchResponse> getMulti(@RequestBody MultiWordSearchRequest request) {
        List<String> error = new ArrayList<>();
        List<WebSiteProfile> profileList = new ArrayList<>();
        String[] turkishWord = turkishWordService.getTurkishWord(request.getWord()).split("\\s+");

        if (Objects.isNull(request.getUrls()) || Objects.isNull(request.getWord())){
            error.add("Url yolla da çalışsın");
            return new ResponseEntity<>(new WordSearchResponse(profileList,error),HttpStatus.BAD_REQUEST);
        }

        stream(request.getUrls()).forEachOrdered(url -> {
            Document htmlDoc = urlConnectService.getHtmlByUrl(url);

            if (Objects.isNull(htmlDoc)) error.add("Read timeout: " + url);

            else {
                String[] htmlText = htmlDoc.body().text().split("\\s+");
                HashMap<String, Integer> map = stream(turkishWord).parallel()
                        .collect(Collectors.toMap(word -> word, word -> SearchUtils.search(htmlText, word), (a, b) -> b, HashMap::new));
                WebSiteProfile profile = new WebSiteProfile(url, map, map);
                profileList.add(profile);
            }
        });
        SearchUtils.executePoint(profileList);
        profileList.sort(Comparator.comparing(WebSiteProfile::getPoint));
        return new ResponseEntity<>(new WordSearchResponse(profileList, error), HttpStatus.OK);
    }


    @PostMapping("/multi/semantic/{depth}")
    public  ResponseEntity<WordSearchResponse> getMultiSemantikDepth(@PathVariable Integer depth, @RequestBody MultiWordSearchRequest request) {
        List<String> error = new ArrayList<>();
        List<WebSiteProfile> profileList = new ArrayList<>();

        if (Objects.isNull(request.getUrls()) || Objects.isNull(request.getWord())){
            error.add("Url yolla da çalışsın");
            return new ResponseEntity<>(new WordSearchResponse(profileList,error),HttpStatus.BAD_REQUEST);
        }

        String[] turkishWord = turkishWordService.getTurkishWord(request.getWord()).split("\\s+");

        ArrayList<String> wordList = new ArrayList<>(Arrays.asList(turkishWord));
        HashMap<String, String[]> esAnlamliMap = new HashMap<>();
        Arrays.stream(turkishWord).forEach((String w) -> {
            String[] esAnlamli = getEsAnlamli(w);
            esAnlamliMap.put(w, esAnlamli);
            wordList.addAll(Arrays.asList(esAnlamli));
        });

        rootUrlScan(depth, request.getUrls(), error, profileList, wordList.toArray(new String[0]));
        SearchUtils.executeSemanticPoint(profileList,esAnlamliMap);
        profileList.sort(Comparator.comparing(WebSiteProfile::getPoint));
        return new ResponseEntity<>(new WordSearchResponse(profileList, error), HttpStatus.OK);
    }

    @PostMapping("/multi/{depth}")
    public ResponseEntity<WordSearchResponse> getMultiDepth(@PathVariable Integer depth, @RequestBody MultiWordSearchRequest request) {
        List<String> error = new ArrayList<>();
        List<WebSiteProfile> profileList = new ArrayList<>();

        if (Objects.isNull(request.getUrls()) || Objects.isNull(request.getWord())){
            error.add("Url yolla da çalışsın");
            return new ResponseEntity<>(new WordSearchResponse(profileList,error),HttpStatus.BAD_REQUEST);
        }

        String[] turkishWord = turkishWordService.getTurkishWord(request.getWord()).split("\\s+");

        rootUrlScan(depth, request.getUrls(), error, profileList, turkishWord);
        SearchUtils.executePoint(profileList);
        profileList.sort(Comparator.comparing(WebSiteProfile::getPoint));
        return new ResponseEntity<>(new WordSearchResponse(profileList, error), HttpStatus.OK);
    }

    /**
     * kelimenin es anlamli kelimelerinin bulundugu diziyi cevirir
     * @param word
     * @return
     */
    private String[] getEsAnlamli(String word){
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.es-anlam.com/kelime/"+word).get();
        } catch (IOException e) {
            logger.info("Url exp: "+ e.getMessage());
        }
        if (doc == null)
            return new String[0];

        Elements select = doc.select("h2#esanlamlar");
        Elements strong = select.select("strong");

        if (strong.text().equals("BULUNAMADI !"))
            return new String[0];

        return strong.text().split(",");
    }

    /**
     * Ana derinlikte arama
     * @param depth
     * @param request
     * @param error
     * @param profileList
     * @param turkishWord
     */
    private void rootUrlScan(Integer depth, String[] request, List<String> error, List<WebSiteProfile> profileList, String[] turkishWord) {
        for (String url : request) {
            Document htmlDoc = urlConnectService.getHtmlByUrl(url);
            if (Objects.isNull(htmlDoc)) error.add("Root => Read timeout: " + url);
            else {
//                String baseURL = SearchUtils.getURLHost(url);
                String[] htmlText = htmlDoc.body().text().split("\\s+");

                HashMap<String, Integer> map = stream(turkishWord).parallel()
                        .collect(Collectors.toMap(word -> word, word -> SearchUtils.search(htmlText, word), (a, b) -> b, HashMap::new));

                Set<String> links = htmlDoc.select(aHref).stream().parallel().map(e -> e.attr(absHref))
                        .filter((String attr) -> !attr.matches(mediaRegexPattern)
                                && attr.regionMatches(0, url, 0, url.length())
                                && !attr.contains("#") && !attr.equals(url))
                        .collect(Collectors.toSet());
                HashSet<String> linkTotalSet = new HashSet<>(links);

                HashMap<String, Integer> totalMap = new HashMap<>(map);
                WebSiteProfile rootProfile = new WebSiteProfile(url, totalMap, map);

                profileList.add(rootProfile);
                crawler(urlConnectService, error, rootProfile, totalMap, turkishWord, linkTotalSet, links, 1, depth);
            }
        }
    }

    /**
     * Ic derinliklerde arama
     * @param service
     * @param error
     * @param root
     * @param totalMap
     * @param words
     * @param linkTotalSet
     * @param linkSet
     * @param depth
     * @param maxDepth
     */
    private void crawler(UrlConnectService service, List<String> error, WebSiteProfile root, HashMap<String, Integer> totalMap, String[] words, Set<String> linkTotalSet, Set<String> linkSet, int depth, final int maxDepth) {
        depth++;
        for (String link : linkSet) {
            Document htmlDoc = service.getHtmlByUrl(link);
            if (Objects.isNull(htmlDoc)) error.add(link);
            else {
                HashMap<String, Integer> map = new HashMap<>();
                String[] body = htmlDoc.body().text().split("\\s+");
                Set<String> links = new HashSet<>();

                stream(words).parallel().forEachOrdered( word -> {
                    int search = SearchUtils.search(body, word);
                    map.put(word, search);
                    totalMap.put(word, totalMap.get(word) + search);
                });

                WebSiteProfile rootProfile = new WebSiteProfile(link, map);
                root.getLinks().add(rootProfile);

                if (depth < maxDepth) {
//                    String baseURL = SearchUtils.getURLHost(link);
                    htmlDoc.select(aHref).stream().parallel().map((Element s) -> s.attr(absHref))
                            .filter((String url) -> !linkTotalSet.contains(url)
                                    && url.regionMatches(0, link, 0, link.length())
                                    && !url.matches(mediaRegexPattern)
                                    && !url.matches("#"))
                            .forEach((String url) -> {
                        links.add(url);
                        linkTotalSet.add(url);
                    });
                    crawler(service, error, rootProfile, totalMap, words, linkTotalSet, links, depth, maxDepth);
                }


            }
        }

    }

}
