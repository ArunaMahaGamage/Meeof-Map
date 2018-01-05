package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Dharmesh on 11/23/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GetInboxModel
{
    String status;
    List<InboxDataModel> data;

    public GetInboxModel()
    {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<InboxDataModel> getData() {
        return data;
    }

    public void setData(List<InboxDataModel> data) {
        this.data = data;
    }
}
