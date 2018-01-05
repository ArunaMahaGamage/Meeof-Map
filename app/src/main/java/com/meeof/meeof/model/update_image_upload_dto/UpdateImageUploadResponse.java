package com.meeof.meeof.model.update_image_upload_dto;

/**
 * Created by ransikadesilva on 12/20/17.
 */

public class UpdateImageUploadResponse {
    private String status;
    private Photo photo;
    private int myID;
    private String message;
    private String feedback;

    public UpdateImageUploadResponse() {
    }

    public UpdateImageUploadResponse(String status, String message, Photo photo) {
        this.status = status;
        this.message = message;
        this.photo = photo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Photo getPhoto() {
        return photo;
    }

    public void setPhoto(Photo photo) {
        this.photo = photo;
    }

    public int getMyID() {
        return myID;
    }

    public void setMyID(int myID) {
        this.myID = myID;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
