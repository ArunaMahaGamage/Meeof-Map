package com.meeof.meeof.model;

import com.meeof.meeof.model.events_dto.Event;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class ReloadEventData {

    private String status;
    private Event event;

    public ReloadEventData(String status, Event event){
        this.status = status;
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
