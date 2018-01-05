package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/17/17.
 */

public class InterestHttpResponse {
    private String status;
    private String message;


    public InterestHttpResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public InterestHttpResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
