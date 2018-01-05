package com.meeof.meeof.model.event_invites;

/**
 * Created by Anuja Ranwalage on 10/23/2017.
 */

public class EmailModel {
    String email;
    int userId;
    String dummy;

    public EmailModel(String email, int userId, String dummy) {
        this.email = email;
        this.userId = userId;
        this.dummy = dummy;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    @Override
    public String toString() {
        return "EmailModel{" +
                "email='" + email + '\'' +
                ", userId=" + userId +
                ", dummy='" + dummy + '\'' +
                '}';
    }
}
