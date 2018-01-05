package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Dharmesh on 12/1/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GetEventsByUserModel
{
    private String status;
    private List<GetEventsByUserInsideModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GetEventsByUserInsideModel> getData() {
        return data;
    }

    public void setData(List<GetEventsByUserInsideModel> data) {
        this.data = data;
    }
}
