package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class HttpResponseCropImage {

    private String status;
    private String data;

    public HttpResponseCropImage() {
    }

    public HttpResponseCropImage(String status, String data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
