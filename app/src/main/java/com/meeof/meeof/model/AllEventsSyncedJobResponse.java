package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class AllEventsSyncedJobResponse {

    private String status;

    public AllEventsSyncedJobResponse(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
