package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/6/17.
 */

public class UpdateProfilePictureResponse {

    private String status;
    private String smallurl;
    private String normalurl;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSmallurl() {
        return smallurl;
    }

    public void setSmallurl(String smallurl) {
        this.smallurl = smallurl;
    }

    public String getNormalurl() {
        return normalurl;
    }

    public void setNormalurl(String normalurl) {
        this.normalurl = normalurl;
    }
}
