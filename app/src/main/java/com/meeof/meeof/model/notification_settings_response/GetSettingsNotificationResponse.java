package com.meeof.meeof.model.notification_settings_response;

/**
 * Created by ransikadesilva on 12/8/17.
 */

public class GetSettingsNotificationResponse {

    private String status;
    private Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
