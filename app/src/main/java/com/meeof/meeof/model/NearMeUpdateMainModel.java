package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Dharmesh on 12/5/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class NearMeUpdateMainModel
{
    private String status;
    private List<NearMeUpdateInsideModel> updates;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NearMeUpdateInsideModel> getUpdates() {
        return updates;
    }

    public void setUpdates(List<NearMeUpdateInsideModel> updates) {
        this.updates = updates;
    }
}
