package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Dharmesh on 11/23/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MessagesModel implements Serializable
{
    private String first_name;
    private int user_id;
    private String profilephoto;
    private String channel_id;
    private int sender_id;
    private String message;
    private String created_at;


    public MessagesModel()
    {

    }

    public MessagesModel(String first_name, int user_id, String profilephoto, String channel_id, int sender_id, String message, String  created_at)
    {
        this.first_name=first_name;
        this.user_id=user_id;
        this.profilephoto=profilephoto;
        this.channel_id=channel_id;
        this.sender_id=sender_id;
        this.message=message;
        this.created_at=created_at;

    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

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

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
