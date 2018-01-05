package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 11/24/17.
 */

public class ReportEventWebJobCompletedResponse {

    private String status;
    private String message;
    private String itemid;

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

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }
}
