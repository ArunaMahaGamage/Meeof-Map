package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/5/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class GetNearMeEventInsideModel implements ClusterItem
{
    private int channel_id;
    private int user_id;
    private String profilephoto;
    private String first_name;
    private int updateid;
    private double longitude;
    private double latitude;
    private String title;
    private String location;
    private String start_date;
    private String end_date;
    private String interestName;
    private double distance;
    private ArrayList<attendeeListModel> attendeeList;
    private ArrayList<photosModel> photos;
    private ArrayList<commentsModel> comments;
    private ArrayList<likesModel> likes;

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public int getUpdateid() {
        return updateid;
    }

    public void setUpdateid(int updateid) {
        this.updateid = updateid;
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

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public ArrayList<attendeeListModel> getAttendeeList() {
        return attendeeList;
    }

    public void setAttendeeList(ArrayList<attendeeListModel> attendeeList) {
        this.attendeeList = attendeeList;
    }

    public ArrayList<photosModel> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<photosModel> photos) {
        this.photos = photos;
    }

    public ArrayList<commentsModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<commentsModel> comments) {
        this.comments = comments;
    }

    public ArrayList<likesModel> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<likesModel> likes) {
        this.likes = likes;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude,longitude);
    }

    @Override
    public String getSnippet() {
        return null;
    }
}

