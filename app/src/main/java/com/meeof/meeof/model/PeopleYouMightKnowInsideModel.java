package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/20/2017.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class PeopleYouMightKnowInsideModel
{
    private int user_id;
    private String status;
    private String first_name;
    private String profilephoto;
    private String entityhandle;
    private String channel_id;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public String getEntityhandle() {
        return entityhandle;
    }

    public void setEntityhandle(String entityhandle) {
        this.entityhandle = entityhandle;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }
}
