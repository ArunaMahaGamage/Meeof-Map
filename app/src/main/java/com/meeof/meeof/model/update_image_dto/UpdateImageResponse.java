package com.meeof.meeof.model.update_image_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 12/22/17.
 */

public class UpdateImageResponse {

    private String status;
    private List<Data> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
