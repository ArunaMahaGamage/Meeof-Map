package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/5/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class NearMeUpdateInsideModel
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
    private long distance;
    private ArrayList<NearMeUpdatePhotosModel> photos;
    private ArrayList<NearMeUpdateCommentModel> comments;
    private ArrayList<NearMeUpdatePhotosModel> likes;

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

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public ArrayList<NearMeUpdatePhotosModel> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<NearMeUpdatePhotosModel> photos) {
        this.photos = photos;
    }

    public ArrayList<NearMeUpdateCommentModel> getComments() {
        return comments;
    }

    public void setComments(ArrayList<NearMeUpdateCommentModel> comments) {
        this.comments = comments;
    }

    public ArrayList<NearMeUpdatePhotosModel> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<NearMeUpdatePhotosModel> likes) {
        this.likes = likes;
    }

}
