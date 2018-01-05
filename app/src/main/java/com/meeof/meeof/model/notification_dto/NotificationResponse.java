package com.meeof.meeof.model.notification_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 12/13/17.
 */

public class NotificationResponse {

    private String status;
    private List<NotificationsData> data;
    private int $count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<NotificationsData> getData() {
        return data;
    }

    public void setData(List<NotificationsData> data) {
        this.data = data;
    }

    public int get$count() {
        return $count;
    }

    public void set$count(int $count) {
        this.$count = $count;
    }
}
