package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class CancelEventWebJobResponse {

    private String status;

    public CancelEventWebJobResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
