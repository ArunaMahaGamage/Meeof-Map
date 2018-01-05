package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dharmesh on 12/18/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelEventsInsideModel
{
    private String matrix;
    private int myID;
    private List<InterestTierModel> InterestTier1Names;
    private ArrayList<MyAttendendChannelEventsModel> myAttendance;
    private String zone;


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


    public List<InterestTierModel> getInterestTier1Names() {
        return InterestTier1Names;
    }

    public void setInterestTier1Names(List<InterestTierModel> interestTier1Names) {
        InterestTier1Names = interestTier1Names;
    }

    public ArrayList<MyAttendendChannelEventsModel> getMyAttendance() {
        return myAttendance;
    }

    public void setMyAttendance(ArrayList<MyAttendendChannelEventsModel> myAttendance) {
        this.myAttendance = myAttendance;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
