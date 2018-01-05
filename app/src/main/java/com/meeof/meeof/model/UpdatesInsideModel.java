package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by Dharmesh on 12/1/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class UpdatesInsideModel
{
    private String zone;
    private String gotupdates;
    private int myID;
    private ArrayList<Integer> whatDoILike;
    private ArrayList<array_updatesModel> array_updates;
    private int numberofUpdates;


    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getGotupdates() {
        return gotupdates;
    }

    public void setGotupdates(String gotupdates) {
        this.gotupdates = gotupdates;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public ArrayList<array_updatesModel> getArray_updates() {
        return array_updates;
    }

    public void setArray_updates(ArrayList<array_updatesModel> array_updates) {
        this.array_updates = array_updates;
    }

    public int getNumberofUpdates() {
        return numberofUpdates;
    }

    public void setNumberofUpdates(int numberofUpdates) {
        this.numberofUpdates = numberofUpdates;
    }

    public ArrayList<Integer> getWhatDoILike() {
        return whatDoILike;
    }

    public void setWhatDoILike(ArrayList<Integer> whatDoILike) {
        this.whatDoILike = whatDoILike;
    }
}
