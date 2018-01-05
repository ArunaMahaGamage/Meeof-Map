package com.meeof.meeof.model.events_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by ransikadesilva on 10/19/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class EventResponse {


    private String Status;
    private List<Event> Event;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public List<Event> getEvent() {
        return Event;
    }

    @Override
    public String toString() {
        return "EventResponse{" +
                "Status='" + Status + '\'' +
                ", Event=" + Event +
                '}';
    }

    public void setEvent(List<Event> Event) {
        this.Event = Event;
    }
}
