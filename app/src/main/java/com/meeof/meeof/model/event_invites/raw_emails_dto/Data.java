package com.meeof.meeof.model.event_invites.raw_emails_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data implements Serializable{
    private String email;
    private int id;
    private String dummy;


    public Data(String email, int id, String dummy) {
        this.email = email;
        this.id = id;
        this.dummy = dummy;
    }

    public Data() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }
}
