package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 12/18/17.
 */

public class DeleteNotificationWebJobResponse {
    private String status;

    public DeleteNotificationWebJobResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

