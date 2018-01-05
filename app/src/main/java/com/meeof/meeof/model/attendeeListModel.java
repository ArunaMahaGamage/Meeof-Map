package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/1/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class attendeeListModel
{
    private String profilephoto;
    private int channel_id;
    private String first_name;
    private String status;
    private String email;
    private int user_id;
    private String is_request;
    private String rsvp;
    private String invited;
    private String friendstatus;

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getIs_request() {
        return is_request;
    }

    public void setIs_request(String is_request) {
        this.is_request = is_request;
    }

    public String getRsvp() {
        return rsvp;
    }

    public void setRsvp(String rsvp) {
        this.rsvp = rsvp;
    }

    public String getInvited() {
        return invited;
    }

    public void setInvited(String invited) {
        this.invited = invited;
    }

    public String getFriendstatus() {
        return friendstatus;
    }

    public void setFriendstatus(String friendstatus) {
        this.friendstatus = friendstatus;
    }
}
