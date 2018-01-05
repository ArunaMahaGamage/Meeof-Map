package com.meeof.meeof.webjob;

/**
 * Created by ransikadesilva on 12/14/17.
 */

public class NotificationsSyncJobCompleted {

    private String status;

    public NotificationsSyncJobCompleted(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
