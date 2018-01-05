package com.meeof.meeof.model.edit_event_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Photos implements Serializable{

    private String user_id;
    private String first_name;
    private String profilephoto;
    private String channel_id;
    private int media_id;
    private String file_name;
    private String created_at;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public int getMedia_id() {
        return media_id;
    }

    public void setMedia_id(int media_id) {
        this.media_id = media_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }


    @Override
    public String toString() {
        return "Photos{" +
                "user_id='" + user_id + '\'' +
                ", first_name='" + first_name + '\'' +
                ", profilephoto='" + profilephoto + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", media_id=" + media_id +
                ", file_name='" + file_name + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
