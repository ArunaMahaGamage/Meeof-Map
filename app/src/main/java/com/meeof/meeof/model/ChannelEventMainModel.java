package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/18/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelEventMainModel
{
    private boolean success;
    private ChannelEventInsideModel data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ChannelEventInsideModel getData() {
        return data;
    }

    public void setData(ChannelEventInsideModel data) {
        this.data = data;
    }
}
