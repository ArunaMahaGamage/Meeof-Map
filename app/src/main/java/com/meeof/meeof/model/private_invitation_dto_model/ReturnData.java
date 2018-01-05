package com.meeof.meeof.model.private_invitation_dto_model;

import java.util.ArrayList;
import java.util.List;

public class ReturnData {
    private int myID;
    private UserData userData;
    private String zone;
    private ArrayList<Integer> whatDoILike;
    private ArrayList<Array_events> array_events;
    private boolean adminpage;
    private String matrix;

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public UserData getUserData() {
        return userData;
    }

    public void setUserData(UserData userData) {
        this.userData = userData;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public ArrayList<Integer> getWhatDoILike() {
        return whatDoILike;
    }

    public void setWhatDoILike(ArrayList<Integer> whatDoILike) {
        this.whatDoILike = whatDoILike;
    }

    public List<Array_events> getArray_events() {
        return array_events;
    }

    public void setArray_events(ArrayList<Array_events> array_events) {
        this.array_events = array_events;
    }

    public boolean getAdminpage() {
        return adminpage;
    }

    public void setAdminpage(boolean adminpage) {
        this.adminpage = adminpage;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    @Override
    public String toString() {
        return "ReturnData{" +
                "myID=" + myID +
                ", userData=" + userData +
                ", zone='" + zone + '\'' +
                ", whatDoILike=" + whatDoILike +
                ", array_events=" + array_events +
                ", adminpage=" + adminpage +
                ", matrix='" + matrix + '\'' +
                '}';
    }
}
