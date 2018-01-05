package com.meeof.meeof.model.updates_dto;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class HttpDeleteFriendResponse {

    String status;

    public HttpDeleteFriendResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
