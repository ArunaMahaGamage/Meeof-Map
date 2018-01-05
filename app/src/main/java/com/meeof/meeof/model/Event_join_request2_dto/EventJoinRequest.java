package com.meeof.meeof.model.Event_join_request2_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 11/28/17.
 */

public class EventJoinRequest {

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
