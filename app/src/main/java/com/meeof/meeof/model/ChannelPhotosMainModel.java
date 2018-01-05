package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/19/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelPhotosMainModel
{
    private boolean success;
    private ChannelPhotosInsideModel data;
    private boolean more;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ChannelPhotosInsideModel getData() {
        return data;
    }

    public void setData(ChannelPhotosInsideModel data) {
        this.data = data;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }
}
