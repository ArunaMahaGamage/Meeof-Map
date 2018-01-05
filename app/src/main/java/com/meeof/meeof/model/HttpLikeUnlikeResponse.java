package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 11/15/17.
 */

public class HttpLikeUnlikeResponse {
    String status;
    int id;

    public HttpLikeUnlikeResponse(String status, int id) {
        this.status = status;
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
