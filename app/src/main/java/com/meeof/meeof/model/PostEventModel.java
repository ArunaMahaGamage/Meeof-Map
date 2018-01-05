package com.meeof.meeof.model;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class PostEventModel implements Serializable{
    int eventid;
    int audience; ////0 public 1 friends 2 private
    String title;
    int activity;

    String datestartdate;
    String dateenddate;

    String datestartFtime;
    String dateendtime;

    double lat;
    double lng;
    String placeID;
    String placeName;
    String addniceaddress;

    String detailedaddress;
    String description;

    String maxnumbers;

    String hidelocation; //- null : 1
    String approvaltojoin;// null : 1
    String informparticipants;

    public String getInformparticipants() {
        return informparticipants;
    }

    public void setInformparticipants(String informparticipants) {
        this.informparticipants = informparticipants;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public int getAudience() {
        return audience;
    }

    public void setAudience(int audience) {
        this.audience = audience;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public String getDatestartdate() {
        return datestartdate;
    }

    public void setDatestartdate(String datestartdate) {
        this.datestartdate = datestartdate;
    }

    public String getDateenddate() {
        return dateenddate;
    }

    public void setDateenddate(String dateenddate) {
        this.dateenddate = dateenddate;
    }

    public String getDatestartFtime() {
        return datestartFtime;
    }

    public void setDatestartFtime(String datestartFtime) {
        this.datestartFtime = datestartFtime;
    }

    public String getDateendtime() {
        return dateendtime;
    }

    public void setDateendtime(String dateendtime) {
        this.dateendtime = dateendtime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getAddniceaddress() {
        return addniceaddress;
    }

    public void setAddniceaddress(String addniceaddress) {
        this.addniceaddress = addniceaddress;
    }

    public String getDetailedaddress() {
        return detailedaddress;
    }

    public void setDetailedaddress(String detailedaddress) {
        this.detailedaddress = detailedaddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMaxnumbers() {
        return maxnumbers;
    }

    public void setMaxnumbers(String maxnumbers) {
        this.maxnumbers = maxnumbers;
    }

    public String getHidelocation() {
        return hidelocation;
    }

    public void setHidelocation(String hidelocation) {
        this.hidelocation = hidelocation;
    }

    public String getApprovaltojoin() {
        return approvaltojoin;
    }

    public void setApprovaltojoin(String approvaltojoin) {
        this.approvaltojoin = approvaltojoin;
    }

    @Override
    public String toString() {
        return "PostEventModel{" +
                "eventid=" + eventid +
                ", audience=" + audience +
                ", title='" + title + '\'' +
                ", activity='" + activity + '\'' +
                ", datestartdate='" + datestartdate + '\'' +
                ", dateenddate='" + dateenddate + '\'' +
                ", datestartFtime='" + datestartFtime + '\'' +
                ", dateendtime='" + dateendtime + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", placeID='" + placeID + '\'' +
                ", placeName='" + placeName + '\'' +
                ", addniceaddress='" + addniceaddress + '\'' +
                ", detailedaddress='" + detailedaddress + '\'' +
                ", description='" + description + '\'' +
                ", maxnumbers='" + maxnumbers + '\'' +
                ", hidelocation='" + hidelocation + '\'' +
                ", approvaltojoin='" + approvaltojoin + '\'' +
                '}';
    }
}
