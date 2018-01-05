package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/4/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelMainModel
{
    private String status;
    private ArrayList<ChannelInsideModel> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ChannelInsideModel> getData() {
        return data;
    }

    public void setData(ArrayList<ChannelInsideModel> data) {
        this.data = data;
    }
}
