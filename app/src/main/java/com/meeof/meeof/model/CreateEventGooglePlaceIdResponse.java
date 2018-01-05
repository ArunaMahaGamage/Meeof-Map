package com.meeof.meeof.model;

import org.json.JSONArray;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class CreateEventGooglePlaceIdResponse {

    private String status;
    private JSONArray data;

    public CreateEventGooglePlaceIdResponse(String status, JSONArray data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONArray getData() {
        return data;
    }

    public void setData(JSONArray data) {
        this.data = data;
    }
}
