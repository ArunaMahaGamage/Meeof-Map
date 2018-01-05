package com.meeof.meeof.model;

import org.json.JSONArray;

/**
 * Created by ransikadesilva on 12/14/17.
 */

public class GetNotificationsWebJobCompletedImportantNotifications {
    private String status;
    private JSONArray notifications;

    public GetNotificationsWebJobCompletedImportantNotifications(String status, JSONArray notifications){
        this.status = status;
        this.notifications = notifications;

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public JSONArray getNotifications() {
        return notifications;
    }

    public void setNotifications(JSONArray notifications) {
        this.notifications = notifications;
    }


}


