package com.meeof.meeof.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ransikadesilva on 12/21/17.
 */

public class PostAddUpdateModel implements Serializable {

    private double lat;
    private double lng;
    private String placeID;
    private String placeName;
    private String addniceaddress;

    private ArrayList<Integer> images=new ArrayList<>();
    private ArrayList<Integer> tags=new ArrayList<>();
    private ArrayList<Integer> toFriends=new ArrayList<>();

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

    public ArrayList<Integer> getImages() {
        return images;
    }

    public void setImages(ArrayList<Integer> images) {
        this.images = images;
    }


    public ArrayList<Integer> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Integer> tags) {
        this.tags = tags;
    }

    public ArrayList<Integer> getToFriends() {
        return toFriends;
    }

    public void setToFriends(ArrayList<Integer> toFriends) {
        this.toFriends = toFriends;
    }

    public String getTxtupdate() {
        return txtupdate;
    }

    public void setTxtupdate(String txtupdate) {
        this.txtupdate = txtupdate;
    }

    private String txtupdate;

    @Override
    public String toString() {
        return "PostAddUpdateModel{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", placeID='" + placeID + '\'' +
                ", placeName='" + placeName + '\'' +
                ", addniceaddress='" + addniceaddress + '\'' +
                ", images=" + images +
                ", tags=" + tags +
                ", toFriends=" + toFriends +
                ", txtupdate='" + txtupdate + '\'' +
                '}';
    }


}
