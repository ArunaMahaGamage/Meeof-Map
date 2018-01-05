package com.meeof.meeof.model.event_comment_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

public class CommentData {


    @JsonProperty("zone_id")
    private int zone_id;
    @JsonProperty("content")
    private String content;
    @JsonProperty("created_at")
    private String created_at;
    @JsonProperty("first_name")
    private String first_name;
    @JsonProperty("user_id")
    private int user_id;
    @JsonProperty("profilephoto")
    private String profilephoto;
    @JsonProperty("entityhandle")
    private String entityhandle;
    @JsonProperty("channel_id")
    private String channel_id;



    @JsonGetter("created_at")
    public int getZone_id() {
        return zone_id;
    }

    @JsonSetter("zone_id")
    public void setZoneId(int zone_id) {
        this.zone_id = zone_id;
    }

    @JsonGetter("created_at")
    public String getContent() {
        return content;
    }

    @JsonSetter("content")
    public void setContent(String content) {
        this.content = content;
    }


    @JsonGetter("created_at")
    public String getCreatedAt() {
        return created_at;
    }

    @JsonSetter("created_at")
    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }

    @JsonGetter("first_name")
    public String getFirstName() {
        return first_name;
    }

    @JsonSetter("first_name")
    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    @JsonGetter("user_id")
    public int getUser_id() {
        return user_id;
    }

    @JsonSetter("user_id")
    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    @JsonGetter("profilephoto")
    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    @JsonGetter("entityhandle")
    public String getEntityhandle() {
        return entityhandle;
    }

    @JsonSetter("entityhandle")
    public void setEntityhandle(String entityhandle) {
        this.entityhandle = entityhandle;
    }

    @JsonGetter("channel_id")
    public String getChannel_id() {
        return channel_id;
    }

    @JsonSetter("channel_id")
    public void setChannelId(String channel_id) {
        this.channel_id = channel_id;
    }

    public String toJson() {
        return "{\"event_comment\":{" +
                "\"zone_id\":" + zone_id +
                ", \"content\":\"" + content + "\"" +
                ", \"created_at\":\"" + created_at + "\"" +
                ", \"first_name\":\"" + first_name + "\"" +
                ", \"user_id\":" + user_id +
                ", \"profilephoto\":\"" + profilephoto + '\"' +
                ", \"channel_id\":" + channel_id +
                '}' +

                "}";
    }

    @Override
    public String toString() {
        return "CommentData{" +
                "zone_id=" + zone_id +
                ", content='" + content + '\'' +
                ", created_at='" + created_at + '\'' +
                ", first_name='" + first_name + '\'' +
                ", user_id=" + user_id +
                ", profilephoto='" + profilephoto + '\'' +
                ", entityhandle='" + entityhandle + '\'' +
                ", channel_id='" + channel_id + '\'' +
                '}';
    }
}
