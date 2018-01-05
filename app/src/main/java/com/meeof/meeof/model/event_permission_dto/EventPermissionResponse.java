package com.meeof.meeof.model.event_permission_dto;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/31/17.
 */

public class EventPermissionResponse implements Serializable{


    private String status;
    private Event_permission event_permission;

    public EventPermissionResponse() {
    }

    public EventPermissionResponse(String status, Event_permission event_permission) {
        this.status = status;
        this.event_permission = event_permission;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Event_permission getEvent_permission() {
        return event_permission;
    }

    public void setEvent_permission(Event_permission event_permission) {
        this.event_permission = event_permission;
    }
}
