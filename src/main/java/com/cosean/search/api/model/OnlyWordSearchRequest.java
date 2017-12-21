package com.cosean.search.api.model;

public class OnlyWordSearchRequest {
    private String url;
    private String word;

    public OnlyWordSearchRequest() {
    }

    public OnlyWordSearchRequest(String url, String word) {
        this.url = url;
        this.word = word;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

        OnlyWordSearchRequest that = (OnlyWordSearchRequest) o;

        return (url != null ? url.equals(that.url) : that.url == null) && (word != null ? word.equals(that.word) : that.word == null);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (word != null ? word.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "OnlyWordSearchRequest{" +
                "url='" + url + '\'' +
                ", word='" + word + '\'' +
                '}';
    }
}
