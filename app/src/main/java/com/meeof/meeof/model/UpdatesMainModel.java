package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/1/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdatesMainModel
{
    private String status;
    private UpdatesInsideModel data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UpdatesInsideModel getData() {
        return data;
    }

    public void setData(UpdatesInsideModel data) {
        this.data = data;
    }
}
