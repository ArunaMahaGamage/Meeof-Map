package com.meeof.meeof.model.updates_all_dto;

import java.util.List;

public class Data {
    private String zone;
    private Filters filters;
    private String gotupdates;
    private String matrix;
    private int myID;
    private List<Integer> whatDoILike;
    private List<Integer> whatIFollow;
    private String limit;
    private List<Array_updates> array_updates;
    private int offset;
    private int numberofUpdates;

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public String getGotupdates() {
        return gotupdates;
    }

    public void setGotupdates(String gotupdates) {
        this.gotupdates = gotupdates;
    }

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

    public List<Integer> getWhatDoILike() {
        return whatDoILike;
    }

    public void setWhatDoILike(List<Integer> whatDoILike) {
        this.whatDoILike = whatDoILike;
    }

    public List<Integer> getWhatIFollow() {
        return whatIFollow;
    }

    public void setWhatIFollow(List<Integer> whatIFollow) {
        this.whatIFollow = whatIFollow;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public List<Array_updates> getArray_updates() {
        return array_updates;
    }

    public void setArray_updates(List<Array_updates> array_updates) {
        this.array_updates = array_updates;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getNumberofUpdates() {
        return numberofUpdates;
    }

    public void setNumberofUpdates(int numberofUpdates) {
        this.numberofUpdates = numberofUpdates;
    }
}
