package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/18/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MyAttendendChannelEventsModel
{
    private int event_id;
    private int is_request;
    private int rsvp;

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getIs_request() {
        return is_request;
    }

    public void setIs_request(int is_request) {
        this.is_request = is_request;
    }

    public int getRsvp() {
        return rsvp;
    }

    public void setRsvp(int rsvp) {
        this.rsvp = rsvp;
    }
}
