package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransikadesilva on 11/29/17.
 */

public class UserInterestResponse {

    @JsonProperty("status")
    private String status;

    @JsonProperty("data")
    private ArrayList data;


    @JsonGetter("status")
    public String getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonGetter("data")
    public ArrayList getData() {
        return data;
    }

    @JsonSetter("data")
    public void setData(ArrayList data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "UserInterestResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
