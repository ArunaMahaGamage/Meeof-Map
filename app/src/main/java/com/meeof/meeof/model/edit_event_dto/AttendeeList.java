package com.meeof.meeof.model.edit_event_dto;

public class AttendeeList {
    private String profilephoto;
    private String channel_id;
    private String first_name;
    private String status;
    private String email;
    private int user_id;
    private int is_request;
    private int rsvp;
    private int invited;
    private String friendstatus;

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
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

    public int getIs_request() {
        return is_request;
    }

    public void setIs_request(int is_request) {
        this.is_request = is_request;
    }

    public int getRsvp() {
        return rsvp;
    }

    public void setRsvp(int rsvp) {
        this.rsvp = rsvp;
    }

    public int getInvited() {
        return invited;
    }

    public void setInvited(int invited) {
        this.invited = invited;
    }

    public String getFriendstatus() {
        return friendstatus;
    }

    public void setFriendstatus(String friendstatus) {
        this.friendstatus = friendstatus;
    }
}
