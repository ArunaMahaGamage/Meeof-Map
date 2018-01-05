package com.meeof.meeof.model.event_image_upload_dto;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/26/17.
 */

public class EventImageUploadResponse implements Serializable {


    private String status;
    private String message;
    private Photo photo;


    public EventImageUploadResponse() {
    }

    public EventImageUploadResponse(String status, String message, Photo photo) {
        this.status = status;
        this.message = message;
        this.photo = photo;
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

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }
}
