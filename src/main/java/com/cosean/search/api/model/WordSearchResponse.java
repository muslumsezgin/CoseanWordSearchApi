package com.cosean.search.api.model;

import java.util.List;

public class WordSearchResponse extends Response{
    private List<WebSiteProfile> profile;

    public WordSearchResponse() {
    }

    public WordSearchResponse(List<WebSiteProfile> profile) {
        this.profile = profile;
    }

    public WordSearchResponse(List<WebSiteProfile> profile, List<String> errorMessage) {
        this.profile = profile;
        this.errorMessage = errorMessage;
    }

    public List<WebSiteProfile> getProfile() {
        return profile;
    }

    public void setProfile(List<WebSiteProfile> profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordSearchResponse that = (WordSearchResponse) o;

        return profile != null ? profile.equals(that.profile) : that.profile == null;
    }

    @Override
    public int hashCode() {
        return profile != null ? profile.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "WordSearchResponse{" +
                "profile=" + profile +
                '}';
    }
}
