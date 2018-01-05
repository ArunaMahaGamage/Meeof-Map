package com.meeof.meeof.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/6/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileResponse implements Serializable {

    private String mStatus;
    private Data mData;
    private String mFacebook;
    private String mGoogle;

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public Data getData() {
        return mData;
    }

    public void setData(Data mData) {
        this.mData = mData;
    }

    public String getFacebook() {
        return mFacebook;
    }

    public void setFacebook(String mFacebook) {
        this.mFacebook = mFacebook;
    }

    public String getGoogle() {
        return mGoogle;
    }

    public void setGoogle(String mGoogle) {
        this.mGoogle = mGoogle;
    }

    @Override
    public String toString() {
        return "ProfileResponse{" +
                "mStatus='" + mStatus + '\'' +
                ", mData=" + mData +
                ", mFacebook='" + mFacebook + '\'' +
                ", mGoogle='" + mGoogle + '\'' +
                '}';
    }
}
