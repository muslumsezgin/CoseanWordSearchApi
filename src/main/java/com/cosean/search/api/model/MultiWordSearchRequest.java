package com.cosean.search.api.model;

import java.util.Arrays;

public class MultiWordSearchRequest {
    private String[] urls;
    private String word;

    public MultiWordSearchRequest() {
    }

    public MultiWordSearchRequest(String[] urls, String word) {
        this.urls = urls;
        this.word = word;
    }

    public String[] getUrls() {
        return urls;
    }

    public void setUrls(String[] urls) {
        this.urls = urls;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultiWordSearchRequest that = (MultiWordSearchRequest) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(urls, that.urls) && (word != null ? word.equals(that.word) : that.word == null);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(urls);
        result = 31 * result + (word != null ? word.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MultiWordSearchRequest{" +
                "urls=" + Arrays.toString(urls) +
                ", word='" + word + '\'' +
                '}';
    }
}
