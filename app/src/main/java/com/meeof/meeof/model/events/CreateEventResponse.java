package com.meeof.meeof.model.events;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/19/17.
 */

public class CreateEventResponse implements Serializable {

    private String status;
    private String eventid;
    private String event_status;

    public CreateEventResponse() {
    }

    public CreateEventResponse(String status, String eventid, String event_status) {
        this.status = status;
        this.eventid = eventid;
        this.event_status = event_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getEvent_status() {
        return event_status;
    }

    public void setEvent_status(String event_status) {
        this.event_status = event_status;
    }
}
