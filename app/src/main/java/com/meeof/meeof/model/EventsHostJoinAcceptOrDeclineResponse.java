package com.meeof.meeof.model;

import com.meeof.meeof.util.Constant;

/**
 * Created by ransikadesilva on 11/29/17.
 */

public class EventsHostJoinAcceptOrDeclineResponse {

    private String status;
    private String notify_id;

    public EventsHostJoinAcceptOrDeclineResponse(String status, String notify_id) {
        this.status = status;
        this.notify_id = notify_id;
    }

    public EventsHostJoinAcceptOrDeclineResponse() {
        this.status = Constant.ERROR;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotify_id() {
        return notify_id;
    }

    public void setNotify_id(String notify_id) {
        this.notify_id = notify_id;
    }
}
