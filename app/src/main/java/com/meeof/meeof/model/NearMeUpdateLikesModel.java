package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/5/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class NearMeUpdateLikesModel {
    private String first_name;
    private String profilephoto;
    private int channel_id;
    private int user_id;
    private String friendstatus;


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getFriendstatus() {
        return friendstatus;
    }

    public void setFriendstatus(String friendstatus) {
        this.friendstatus = friendstatus;
    }
}
