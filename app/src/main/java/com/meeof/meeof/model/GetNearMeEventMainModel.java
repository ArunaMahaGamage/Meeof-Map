package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Dharmesh on 12/5/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GetNearMeEventMainModel
{
    private String status;
    private List<GetNearMeEventInsideModel> events;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<GetNearMeEventInsideModel> getEvents() {
        return events;
    }

    public void setEvents(List<GetNearMeEventInsideModel> events) {
        this.events = events;
    }
}
