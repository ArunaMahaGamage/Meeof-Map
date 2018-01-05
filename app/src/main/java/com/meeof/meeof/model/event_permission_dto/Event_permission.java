package com.meeof.meeof.model.event_permission_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;

public class Event_permission implements Serializable{

    @JsonProperty("IsChannelContributor")
    private boolean IsChannelContributor;

    @JsonProperty("IsHost")
    private boolean IsHost;

    @JsonProperty("See")
    private boolean See;

    @JsonProperty("Join")
    private boolean Join;

    @JsonProperty("Interact")
    private boolean Interact;

    @JsonProperty("Like")
    private boolean Like;

    @JsonProperty("Share")
    private boolean Share;

    @JsonProperty("ShowLocation")
    private boolean ShowLocation;

    @JsonProperty("IsFull")
    private boolean IsFull;

    @JsonProperty("GotAttendance")
    private boolean GotAttendance;

    @JsonGetter("IsChannelContributor")
    public boolean getIsChannelContributor() {
        return IsChannelContributor;
    }

    @JsonSetter("IsChannelContributor")
    public void setIsChannelContributor(boolean IsChannelContributor) {
        this.IsChannelContributor = IsChannelContributor;
    }

    @JsonGetter("IsHost")
    public boolean getIsHost() {
        return IsHost;
    }

    @JsonSetter("IsHost")
    public void setIsHost(boolean IsHost) {
        this.IsHost = IsHost;
    }

    @JsonGetter("See")
    public boolean getSee() {
        return See;
    }

    @JsonSetter("See")
    public void setSee(boolean See) {
        this.See = See;
    }

    @JsonGetter("Join")
    public boolean getJoin() {
        return Join;
    }

    @JsonSetter("Join")
    public void setJoin(boolean Join) {
        this.Join = Join;
    }

    @JsonGetter("Interact")
    public boolean getInteract() {
        return Interact;
    }

    @JsonSetter("Interact")
    public void setInteract(boolean Interact) {
        this.Interact = Interact;
    }

    @JsonGetter("Like")
    public boolean getLike() {
        return Like;
    }

    @JsonSetter("Like")
    public void setLike(boolean Like) {
        this.Like = Like;
    }

    @JsonGetter("Share")
    public boolean getShare() {
        return Share;
    }

    @JsonSetter("Share")
    public void setShare(boolean Share) {
        this.Share = Share;
    }

    @JsonGetter("ShowLocation")
    public boolean getShowLocation() {
        return ShowLocation;
    }

    @JsonSetter("ShowLocation")
    public void setShowLocation(boolean ShowLocation) {
        this.ShowLocation = ShowLocation;
    }

    @JsonGetter("IsFull")
    public boolean getIsFull() {
        return IsFull;
    }

    @JsonSetter("IsFull")
    public void setIsFull(boolean IsFull) {
        this.IsFull = IsFull;
    }

    @JsonGetter("GotAttendance")
    public boolean getGotAttendance() {
        return GotAttendance;
    }

    @JsonSetter("GotAttendance")
    public void setGotAttendance(boolean GotAttendance) {
        this.GotAttendance = GotAttendance;
    }


    @Override
    public String toString() {
        return "Event_permission{" +
                "IsChannelContributor=" + IsChannelContributor +
                ", IsHost=" + IsHost +
                ", See=" + See +
                ", Join=" + Join +
                ", Interact=" + Interact +
                ", Like=" + Like +
                ", Share=" + Share +
                ", ShowLocation=" + ShowLocation +
                ", IsFull=" + IsFull +
                ", GotAttendance=" + GotAttendance +
                '}';
    }
}
