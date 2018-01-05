package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/2/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class UpcomingEventModel
{
    private String Status;
    private ArrayList<UpcomingEventInsideModel> data;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public ArrayList<UpcomingEventInsideModel> getData() {
        return data;
    }

    public void setData(ArrayList<UpcomingEventInsideModel> data) {
        this.data = data;
    }
}
