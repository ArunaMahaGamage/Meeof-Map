package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/2/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class UpcomingEventInsideModel
{
    private int eventid;
    private String title;
    private String start_date;
    private String end_date;
    private String location;
    private String placeName;
    private int rsvp;
    private int is_request;

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public int getRsvp() {
        return rsvp;
    }

    public void setRsvp(int rsvp) {
        this.rsvp = rsvp;
    }

    public int getIs_request() {
        return is_request;
    }

    public void setIs_request(int is_request) {
        this.is_request = is_request;
    }
}
