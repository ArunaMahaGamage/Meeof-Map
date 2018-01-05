package com.meeof.meeof.model;

import java.io.Serializable;

/**
 * Created by Anuja Ranwalage on 10/8/2017.
 */

public class ProfileSaveResponse implements Serializable{

    private String status;
    private String message;

    public ProfileSaveResponse() {
    }

    public ProfileSaveResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ProfileSaveResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
