package com.meeof.meeof.model;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class EditProfileInfo implements Serializable{

    String country;
    String email;
    String fullName;
    String address;
    String placeId;
    String placeName;
    String lat;
    String lng;
    int gender;
    String matrix;
    String acceptedDistance;

    public EditProfileInfo() {
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getAcceptedDistance() {
        return acceptedDistance;
    }

    public void setAcceptedDistance(String acceptedDistance) {
        this.acceptedDistance = acceptedDistance;
    }

    @Override
    public String toString() {
        return "EditProfileInfo{" +
                "country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", address='" + address + '\'' +
                ", placeId='" + placeId + '\'' +
                ", placeName='" + placeName + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", gender='" + gender + '\'' +
                ", matrix='" + matrix + '\'' +
                ", acceptedDistance='" + acceptedDistance + '\'' +
                '}';
    }
}
