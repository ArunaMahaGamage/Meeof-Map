package com.meeof.meeof.model.events_filter_dto;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class EventFilterModel implements Serializable {

    private String location ;
    private String masterfilter ;
    private String acceptabledistance  ;
    private String hideoutsidefriends ;
    private String sortbyproximity ;
    private String rememberfilter ;
    private String niceaddress ;
    private String matrix;
    private String lng ;
    private String lat;
    private String stopexecution="0";
    private String offset ="0";
    private String limit ="20";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMasterfilter() {
        return masterfilter;
    }

    public void setMasterfilter(String masterfilter) {
        this.masterfilter = masterfilter;
    }

    public String getAcceptabledistance() {
        return acceptabledistance;
    }

    public void setAcceptabledistance(String acceptabledistance) {
        this.acceptabledistance = acceptabledistance;
    }

    public String getHideoutsidefriends() {
        return hideoutsidefriends;
    }

    public void setHideoutsidefriends(String hideoutsidefriends) {
        this.hideoutsidefriends = hideoutsidefriends;
    }

    public String getSortbyproximity() {
        return sortbyproximity;
    }

    public void setSortbyproximity(String sortbyproximity) {
        this.sortbyproximity = sortbyproximity;
    }

    public String getRememberfilter() {
        return rememberfilter;
    }

    public void setRememberfilter(String rememberfilter) {
        this.rememberfilter = rememberfilter;
    }

    public String getNiceaddress() {
        return niceaddress;
    }

    public void setNiceaddress(String niceaddress) {
        this.niceaddress = niceaddress;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getStopexecution() {
        return stopexecution;
    }

    public void setStopexecution(String stopexecution) {
        this.stopexecution = stopexecution;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "EventFilterModel{" +
                "location='" + location + '\'' +
                ", masterfilter='" + masterfilter + '\'' +
                ", acceptabledistance='" + acceptabledistance + '\'' +
                ", hideoutsidefriends='" + hideoutsidefriends + '\'' +
                ", sortbyproximity='" + sortbyproximity + '\'' +
                ", rememberfilter='" + rememberfilter + '\'' +
                ", niceaddress='" + niceaddress + '\'' +
                ", matrix='" + matrix + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", stopexecution='" + stopexecution + '\'' +
                ", offset='" + offset + '\'' +
                ", limit='" + limit + '\'' +
                '}';
    }
}
