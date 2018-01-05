package com.meeof.meeof.model.geo_dto;

/**
 * Created by Anuja Ranwalage on 11/2/2017.
 */

public class GeoResponse {

    private String mStatus;
    private String mDistance;


    public GeoResponse() {
    }

    public GeoResponse(String mStatus, String mDistance) {
        this.mStatus = mStatus;
        this.mDistance = mDistance;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String mDistance) {
        this.mDistance = mDistance;
    }
}
