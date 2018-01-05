package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Created by ransikadesilva on 11/17/17.
 */

public class EventLikeData {

    @JsonProperty("user_id")
    private int user_id;

    @JsonProperty("profilephoto")
    private String profilephoto;

    @JsonProperty("zone_id")
    private int zone_id;

    @JsonProperty("status")
    private String status;

    @JsonGetter("getUser_id")
    public int getUser_id() {
        return user_id;
    }

    @JsonSetter("user_id")
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @JsonGetter("profilephoto")
    public String getProfilephoto() {
        return profilephoto;
    }

    @JsonSetter("profilephoto")
    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    @JsonGetter("zone_id")
    public int getZone_id() {
        return zone_id;
    }

    @JsonSetter("zone_id")
    public void setZone_id(int zone_id) {
        this.zone_id = zone_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventLikeData{" +
                "user_id=" + user_id +
                ", profilephoto='" + profilephoto + '\'' +
                ", zone_id='" + zone_id + '\'' +
                ", status='" + status + '\'' +
                '}';
    }


    public String toJson() {
        return "{\"event_like\":{" +
                "\"user_id\":" + user_id +
                ", \"profilephoto\":\"" + profilephoto + "\"" +
                ", \"zone_id\":\"" + zone_id + "\"" +
                ", \"status\":" + status +
                '}' +

                "}";
    }


}
