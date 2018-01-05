package com.meeof.meeof.model.events_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

/**
 * Created by janitha on 10/23/17.
 */

public class AttendeeList implements Serializable{
    @JsonProperty("profilephoto")
    private String profilephoto;

    @JsonProperty("channel_id")
    private String channelId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("email")
    private String email;

    @JsonProperty("user_id")
    private int userId;

    @JsonProperty("is_request")
    private int isRequest;

    @JsonProperty("rsvp")
    private int rsvp;

    @JsonProperty("invited")
    private int invited;

    @JsonProperty("friendstatus")
    private String friendstatus;

    @JsonGetter("friendstatus")
    public String getFriendstatus() {
        return friendstatus;
    }

    @JsonSetter("friendstatus")
    public void setFriendstatus(String friendstatus) {
        this.friendstatus = friendstatus;
    }

    @JsonGetter("profilephoto")
    public String getProfilephoto() {
        return profilephoto;
    }

    @JsonSetter("profilephoto")
    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    @JsonGetter("channel_id")
    public String getChannelId() {
        return channelId;
    }

    @JsonSetter("channel_id")
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    @JsonGetter("first_name")
    public String getFirstName() {
        return firstName;
    }

    @JsonSetter("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonGetter("status")
    public String getStatus() {
        return status;
    }

    @JsonSetter("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonGetter("email")
    public String getEmail() {
        return email;
    }

    @JsonSetter("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonGetter("user_id")
    public int getUserId() {
        return userId;
    }

    @JsonSetter("user_id")
    public void setUserId(int userId) {
        this.userId = userId;
    }

    @JsonGetter("is_request")
    public int getIsRequest() {
        return isRequest;
    }

    @JsonSetter("is_request")
    public void setIsRequest(int isRequest) {
        this.isRequest = isRequest;
    }

    @JsonGetter("rsvp")
    public int getRsvp() {
        return rsvp;
    }

    @JsonSetter("rsvp")
    public void setRsvp(int rsvp) {
        this.rsvp = rsvp;
    }

    @JsonGetter("invited")
    public int getInvited() {
        return invited;
    }

    @JsonSetter("invited")
    public void setInvited(int invited) {
        this.invited = invited;
    }

    @Override
    public String toString() {
        return "AttendeeList{" +
                "profilephoto='" + profilephoto + '\'' +
                ", channelId='" + channelId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", status='" + status + '\'' +
                ", email='" + email + '\'' +
                ", userId=" + userId +
                ", isRequest=" + isRequest +
                ", rsvp=" + rsvp +
                ", invited=" + invited +
                '}';
    }

}
