package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class UploadEventPosterResponse {

    private String smallurl;
    private String normalurl;
    private int eventid;

    public UploadEventPosterResponse() {
    }

    public UploadEventPosterResponse(String smallurl, String normalurl, int eventid) {
        this.smallurl = smallurl;
        this.normalurl = normalurl;
        this.eventid = eventid;
    }

    public String getSmallurl() {
        return smallurl;
    }

    public void setSmallurl(String smallurl) {
        this.smallurl = smallurl;
    }

    public String getNormalurl() {
        return normalurl;
    }

    public void setNormalurl(String normalurl) {
        this.normalurl = normalurl;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }
}
