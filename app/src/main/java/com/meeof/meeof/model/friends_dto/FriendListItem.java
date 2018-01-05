package com.meeof.meeof.model.friends_dto;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/9/17.
 */

public class FriendListItem implements Serializable{
    String id;
    String name;
    String status;
    String photoUrl;
    String email;
    String friend_status;

    public FriendListItem(String id, String name, String status, String photoUrl, String email, String friend_status) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.photoUrl = photoUrl;
        this.email = email;
        this.friend_status = friend_status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFriend_status() {
        return friend_status;
    }

    public void setFriend_status(String friend_status) {
        this.friend_status = friend_status;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "FriendListItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", email='" + email + '\'' +
                ", friend_status='" + friend_status + '\'' +
                '}';
    }
}
