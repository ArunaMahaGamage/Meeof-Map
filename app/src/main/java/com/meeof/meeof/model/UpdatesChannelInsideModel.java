package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/18/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdatesChannelInsideModel
{
    private String matrix;
    private int myID;
    private ArrayList<UpdateChannelsArrayModel> array_updates;

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

    public ArrayList<UpdateChannelsArrayModel> getArray_updates() {
        return array_updates;
    }

    public void setArray_updates(ArrayList<UpdateChannelsArrayModel> array_updates) {
        this.array_updates = array_updates;
    }
}
