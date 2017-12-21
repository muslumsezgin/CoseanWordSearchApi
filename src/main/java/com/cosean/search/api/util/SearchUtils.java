package com.cosean.search.api.util;

import com.cosean.search.api.model.WebSiteProfile;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SearchUtils {

    public static String getURLHost(String url) {
        String baseURL = "";
        try {
            URL temp = new URL(url);
            baseURL = String.format("%s://%s", temp.getProtocol(), temp.getHost());
        } catch (MalformedURLException ignored) { }
        return baseURL;
    }

    /**
     * Kelime dizisi icerisinde aranan kelimenin sayisini cevirir
     * @param wordArray
     * @param word
     * @return
     */
    public static int search(String[] wordArray, String word) {
        return (int) Arrays.stream(wordArray)
                .filter(anArray -> StringUtils.startsWithIgnoreCase(anArray.trim(), word.trim())).count();
    }

    /**
     * Web site profillerinin puanlarini hesaplar
     * @param profileList
     */
    public static void executePoint(List<WebSiteProfile> profileList) {
        HashMap<String, Integer> totalWordNumber = getTotalWordNumber(profileList);
        profileList.forEach(p -> {
            p.setPoint(p.getTotalWordsValue().keySet().stream()
                    .mapToDouble(word -> p.getTotalWordsValue().get(word) == 0 ? 99 :
                            Math.log(Math.PI * (totalWordNumber.get(word) / (p.getTotalWordsValue().get(word)))))
                    .sum());
        });
    }

    /**
     * Aranan kelimelerin toplamını bulur
     * @param profileList
     * @return
     */
    private static HashMap<String, Integer> getTotalWordNumber(List<WebSiteProfile> profileList) {
        HashMap<String, Integer> totalMap = new HashMap<>();
        profileList.forEach(p -> {
            p.getTotalWordsValue().keySet().forEach(word -> {
                int size = 0;
                if (Objects.nonNull(totalMap.get(word)))
                    size = totalMap.get(word);
                size += p.getTotalWordsValue().get(word);
                totalMap.put(word, size);
            });
        });
        return totalMap;
    }

    /**
     * Semantik analiz icin profillerin puanlarını hesaplar
     * (eş anlamlılar tek kelime gibi davranilmasi icin)
     * @param profileList
     * @param esAnlamliMap
     */
    public static void executeSemanticPoint(List<WebSiteProfile> profileList, HashMap<String, String[]> esAnlamliMap) {
        HashMap<String, Integer> totalWordNumber = new HashMap<>();
        profileList.forEach(p -> {
            esAnlamliMap.keySet().forEach((String key) -> {
                Integer val = p.getTotalWordsValue().get(key);
                for (String s : esAnlamliMap.get(key))
                    val += p.getTotalWordsValue().get(s);
                if (Objects.nonNull(totalWordNumber.get(key)))
                    val += totalWordNumber.get(key);
                totalWordNumber.put(key, val);
            });
        });


        profileList.forEach(p ->
                esAnlamliMap.keySet().forEach(key -> {
                    int val = p.getTotalWordsValue().get(key);
                    val += Arrays.stream(esAnlamliMap.get(key)).mapToInt(s -> p.getTotalWordsValue().get(s)).sum();
                    p.setPoint(val == 0 ? 99 + p.getPoint() : p.getPoint() + Math.log(Math.PI * (totalWordNumber.get(key) / val)));
                }));
    }

}
