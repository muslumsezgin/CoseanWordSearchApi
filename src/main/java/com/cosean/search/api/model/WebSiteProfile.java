package com.cosean.search.api.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class WebSiteProfile {
    private String url;
    private Map<String,Integer> wordsValue;
    private Map<String,Integer> totalWordsValue;
    private List<WebSiteProfile> links;
    private Double point;

    public WebSiteProfile() {
    }

    public WebSiteProfile(String url, Map<String, Integer> totalWordsValue , Map<String, Integer> wordsValue) {
        this.url = url;
        this.wordsValue = wordsValue;
        this.totalWordsValue = totalWordsValue;
        this.links = new ArrayList<>();
        this.point = 0.0;
    }

    public WebSiteProfile(String url ,Map<String, Integer> wordsValue) {
        this.url = url;
        this.wordsValue = wordsValue;
        this.links = new ArrayList<>();
        this.point = 0.0;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Integer> getWordsValue() {
        return wordsValue;
    }

    public void setWordsValue(Map<String, Integer> wordsValue) {
        this.wordsValue = wordsValue;
    }

    public List<WebSiteProfile> getLinks() {
        return links;
    }

    public void setLinks(List<WebSiteProfile> links) {
        this.links = links;
    }

    public Double getPoint() {
        return point;
    }

    public void setPoint(Double point) {
        this.point = point;
    }


    public Map<String, Integer> getTotalWordsValue() {
        return totalWordsValue;
    }

    public void setTotalWordsValue(Map<String, Integer> totalWordsValue) {
        this.totalWordsValue = totalWordsValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSiteProfile that = (WebSiteProfile) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(wordsValue, that.wordsValue) &&
                Objects.equals(totalWordsValue, that.totalWordsValue) &&
                Objects.equals(links, that.links) &&
                Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, wordsValue, totalWordsValue, links, point);
    }

    @Override
    public String toString() {
        return "WebSiteProfile{" +
                "url='" + url + '\'' +
                ", wordsValue=" + wordsValue +
                ", totalWordsValue=" + totalWordsValue +
                ", links=" + links +
                ", point=" + point +
                '}';
    }
}
