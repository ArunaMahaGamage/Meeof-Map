package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/9/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelDetailModel
{
    private String status;
    private ChannelDetailDataModel data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ChannelDetailDataModel getData() {
        return data;
    }

    public void setData(ChannelDetailDataModel data) {
        this.data = data;
    }
}
