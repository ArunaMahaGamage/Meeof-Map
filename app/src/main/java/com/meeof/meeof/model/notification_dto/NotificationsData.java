package com.meeof.meeof.model.notification_dto;

import java.io.Serializable;

public class NotificationsData implements Serializable{
    private int zone_id;
    private String first_name;
    private int user_id;
    private String status;
    private String profilephoto;
    private String channel_id;
    private String entityhandle;
    private int notifyid;
    private String on_click;
    private String zone;
    private int mpublic;
    private int shown_on_screen;
    private String content;
    private int is_read;
    private String created_at;
    private String title;



    private String placeName;
    private int attendeeCount;

    public int getZone_id() {
        return zone_id;
    }

    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getEntityhandle() {
        return entityhandle;
    }

    public void setEntityhandle(String entityhandle) {
        this.entityhandle = entityhandle;
    }

    public int getNotifyid() {
        return notifyid;
    }

    public void setNotifyid(int notifyid) {
        this.notifyid = notifyid;
    }

    public String getOn_click() {
        return on_click;
    }

    public void setOn_click(String on_click) {
        this.on_click = on_click;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getMpublic() {
        return mpublic;
    }

    public void setMpublic(int mpublic) {
        this.mpublic = mpublic;
    }

    public int getShown_on_screen() {
        return shown_on_screen;
    }

    public void setShown_on_screen(int shown_on_screen) {
        this.shown_on_screen = shown_on_screen;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIs_read() {
        return is_read;
    }

    public void setIs_read(int is_read) {
        this.is_read = is_read;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {this.title = title;}

    public String getPlaceName() {return placeName;}

    public void setPlaceName(String placeName) {this.placeName = placeName;}

    public int getAttendeeCount() {return attendeeCount;}

    public void setAttendeeCount(int attendeeCount) {this.attendeeCount = attendeeCount;}
    @Override
    public String toString() {
        return "NotificationsData{" +
                "zone_id=" + zone_id +
                ", first_name='" + first_name + '\'' +
                ", user_id=" + user_id +
                ", status='" + status + '\'' +
                ", profilephoto='" + profilephoto + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", entityhandle='" + entityhandle + '\'' +
                ", notifyid=" + notifyid +
                ", on_click='" + on_click + '\'' +
                ", zone='" + zone + '\'' +
                ", mpublic=" + mpublic +
                ", shown_on_screen=" + shown_on_screen +
                ", content='" + content + '\'' +
                ", is_read=" + is_read +
                ", created_at='" + created_at + '\'' +
                ", title='" + title + '\'' +
                ", placeName='" + placeName + '\'' +
                ", attendeeCount='" + attendeeCount + '\'' +
                '}';
    }
}
