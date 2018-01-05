package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class HttpDeclineFriendResponse {

    String status;
    String message;

    public HttpDeclineFriendResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpDeclineFriendResponse() {
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
