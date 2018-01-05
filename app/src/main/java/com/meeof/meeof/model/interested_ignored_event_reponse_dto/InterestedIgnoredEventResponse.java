package com.meeof.meeof.model.interested_ignored_event_reponse_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Created by ransikadesilva on 12/5/17.
 */

public class InterestedIgnoredEventResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("Events")
    private List<Events> Events;

    @JsonGetter("status")
    public String getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonGetter("Events")
    public List<Events> getEvents() {
        return Events;
    }

    @JsonSetter("Events")
    public void setEvents(List<Events> Events) {
        this.Events = Events;
    }
}
