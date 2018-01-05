package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/10/17.
 */

public class HttpResponseForAddFriend {

    String status;
    String message;

    public HttpResponseForAddFriend() {
    }

    public HttpResponseForAddFriend(String status, String message) {
        this.status = status;
        this.message = message;
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
