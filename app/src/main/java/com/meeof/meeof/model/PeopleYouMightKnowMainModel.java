package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/20/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class PeopleYouMightKnowMainModel
{
    private String status;
    private ArrayList<PeopleYouMightKnowInsideModel> data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<PeopleYouMightKnowInsideModel> getData() {
        return data;
    }

    public void setData(ArrayList<PeopleYouMightKnowInsideModel> data) {
        this.data = data;
    }
}
