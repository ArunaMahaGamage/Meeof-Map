package com.meeof.meeof.model.image_upload_dto;

/**
 * Created by Anuja Ranwalage on 10/6/17.
 */

public class ImageUploadResponse {

    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
