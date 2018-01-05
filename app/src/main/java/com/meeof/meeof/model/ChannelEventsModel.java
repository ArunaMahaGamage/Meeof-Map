package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/18/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelEventsModel
{
    private boolean success;

    private boolean more;

    private ChannelEventsInsideModel data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isMore() {
        return more;
    }

    public void setMore(boolean more) {
        this.more = more;
    }

    public ChannelEventsInsideModel getData() {
        return data;
    }

    public void setData(ChannelEventsInsideModel data) {
        this.data = data;
    }
}
