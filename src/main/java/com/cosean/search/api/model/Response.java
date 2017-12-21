package com.cosean.search.api.model;

import java.util.List;

public abstract class Response {
    List<String> errorMessage;

    public List<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(List<String> errorMessage) {
        this.errorMessage = errorMessage;
    }

}
