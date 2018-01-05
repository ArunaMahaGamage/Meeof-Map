package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/18/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdateChannelsArrayModel
{
    private String first_name;
    private String profilephoto;
    private String entityhandle;
    private int user_id;
    private int channel_id;
    private long distance;
    private ArrayList<String> photo_arrays;
    private int CountComments;
    private int CountLikes;
    private int updateid;
    private String title;
    private String location;
    private String placeID;
    private String placeName;
    private double longitude;
    private double latitude;
    private String created_at;
    private int poster_id;
    private String friends;
    private String tags;
    private String photocount;


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

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public ArrayList<String> getPhoto_arrays() {
        return photo_arrays;
    }

    public void setPhoto_arrays(ArrayList<String> photo_arrays) {
        this.photo_arrays = photo_arrays;
    }

    public int getCountComments() {
        return CountComments;
    }

    public void setCountComments(int countComments) {
        CountComments = countComments;
    }

    public int getCountLikes() {
        return CountLikes;
    }

    public void setCountLikes(int countLikes) {
        CountLikes = countLikes;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public String getPhotocount() {
        return photocount;
    }

    public void setPhotocount(String photocount) {
        this.photocount = photocount;
    }
}
