package com.meeof.meeof.model.interested_in_event_dto;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/27/17.
 */

public class InterestedEventResponse implements Serializable{


    private String status;
    private String direction;

    public InterestedEventResponse(String status, String direction) {
        this.status = status;
        this.direction = direction;
    }

    public InterestedEventResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
