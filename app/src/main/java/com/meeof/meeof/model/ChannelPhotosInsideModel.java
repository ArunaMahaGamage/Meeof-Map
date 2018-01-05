package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/19/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelPhotosInsideModel
{
    private String matrix;
    private int myID;
    private ArrayList<ChannelPhotosArrayModel> photos;

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public ArrayList<ChannelPhotosArrayModel> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<ChannelPhotosArrayModel> photos) {
        this.photos = photos;
    }
}
