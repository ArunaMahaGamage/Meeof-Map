package com.meeof.meeof.model.edit_event_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Anuja Ranwalage on 10/23/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventInfoResponse implements Serializable{


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
