package com.meeof.meeof.model;

import org.json.JSONArray;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class GetEventsWebJobCompletedEvent {

    private String status;
    private JSONArray events;
    private JSONArray attendance;

    public GetEventsWebJobCompletedEvent(String status, JSONArray events, JSONArray attendance){
        this.status = status;
        this.events = events;
        this.attendance= attendance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONArray getEvents() {
        return events;
    }

    public void setEvents(JSONArray events) {
        this.events = events;
    }

    public JSONArray getAttendance() {
        return attendance;
    }

    public void setAttendance(JSONArray attendance) {
        this.attendance = attendance;
    }
}
