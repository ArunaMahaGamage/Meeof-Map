package com.meeof.meeof.model.event_filter_get_dto;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class EventFilterGetModel {

    public EventFilterGetModel() {
    }

    public EventFilterGetModel(String status, Data data) {
        this.status = status;
        this.data = data;
    }

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

    @Override
    public String toString() {
        return "EventFilterGetModel{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
