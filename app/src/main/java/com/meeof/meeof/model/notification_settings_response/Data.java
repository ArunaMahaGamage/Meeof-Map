package com.meeof.meeof.model.notification_settings_response;

public class Data {
    private int notify_comment;
    private int notify_like;
    private int notify_weeklyEmail;

    public int getNotify_comment() {
        return notify_comment;
    }

    public void setNotify_comment(int notify_comment) {
        this.notify_comment = notify_comment;
    }

    public int getNotify_like() {
        return notify_like;
    }

    public void setNotify_like(int notify_like) {
        this.notify_like = notify_like;
    }

    public int getNotify_weeklyEmail() {
        return notify_weeklyEmail;
    }

    public void setNotify_weeklyEmail(int notify_weeklyEmail) {
        this.notify_weeklyEmail = notify_weeklyEmail;
    }
}
