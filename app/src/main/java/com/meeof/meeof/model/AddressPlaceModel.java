package com.meeof.meeof.model;

import java.io.Serializable;

/**
 * Created by Anuja Ranwalage on 10/7/17.
 */

public class AddressPlaceModel implements Serializable{
    String country;
    String addniceaddress;
    String placeID;
    String placeName;
    String lng;
    String lat;

    public AddressPlaceModel() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddniceaddress() {
        return addniceaddress;
    }

    public void setAddniceaddress(String addniceaddress) {
        this.addniceaddress = addniceaddress;
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

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "AddressPlaceModel{" +
                "country='" + country + '\'' +
                ", addniceaddress='" + addniceaddress + '\'' +
                ", placeID='" + placeID + '\'' +
                ", placeName='" + placeName + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                '}';
    }
}
