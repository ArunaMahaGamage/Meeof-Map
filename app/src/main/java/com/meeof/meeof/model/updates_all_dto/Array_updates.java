package com.meeof.meeof.model.updates_all_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

public class Array_updates implements Serializable {
    private String first_name;
    private String profilephoto;
    private String entityhandle;
    private int user_id;
    private String channel_id;
    private double distance;
    private List<String> photo_arrays;
    @JsonProperty("CountComments")
    private int CountComments;
    @JsonProperty("CountLikes")
    private int CountLikes;
    private int updateid;
    private String title;
    private String location;
    private double longitude;
    private String placeID;
    private String placeName;
    private double latitude;
    private String created_at;
    private int poster_id;
    private String friends;
    private String tags;
    private int photocount;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getEntityhandle() {
        return entityhandle;
    }

    public void setEntityhandle(String entityhandle) {
        this.entityhandle = entityhandle;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<String> getPhoto_arrays() {
        return photo_arrays;
    }

    public void setPhoto_arrays(List<String> photo_arrays) {
        this.photo_arrays = photo_arrays;
    }

    @JsonGetter("CountComments")
    public int getCountComments() {
        return CountComments;
    }

    @JsonSetter("CountComments")
    public void setCountComments(int CountComments) {
        this.CountComments = CountComments;
    }

    @JsonGetter("CountLikes")
    public int getCountLikes() {
        return CountLikes;
    }

    @JsonSetter("CountLikes")
    public void setCountLikes(int CountLikes) {
        this.CountLikes = CountLikes;
    }

    public int getUpdateid() {
        return updateid;
    }

    public void setUpdateid(int updateid) {
        this.updateid = updateid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getPoster_id() {
        return poster_id;
    }

    public void setPoster_id(int poster_id) {
        this.poster_id = poster_id;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getPhotocount() {
        return photocount;
    }

    public void setPhotocount(int photocount) {
        this.photocount = photocount;
    }

    @Override
    public String toString() {
        return "Array_updates{" +
                "first_name='" + first_name + '\'' +
                ", profilephoto='" + profilephoto + '\'' +
                ", entityhandle='" + entityhandle + '\'' +
                ", user_id=" + user_id +
                ", channel_id='" + channel_id + '\'' +
                ", distance=" + distance +
                ", photo_arrays=" + photo_arrays +
                ", CountComments=" + CountComments +
                ", CountLikes=" + CountLikes +
                ", updateid=" + updateid +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", longitude=" + longitude +
                ", placeID='" + placeID + '\'' +
                ", placeName='" + placeName + '\'' +
                ", latitude=" + latitude +
                ", created_at='" + created_at + '\'' +
                ", poster_id=" + poster_id +
                ", friends='" + friends + '\'' +
                ", tags='" + tags + '\'' +
                ", photocount=" + photocount +
                '}';
    }
}
