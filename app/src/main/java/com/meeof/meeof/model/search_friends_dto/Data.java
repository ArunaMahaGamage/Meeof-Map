package com.meeof.meeof.model.search_friends_dto;

public class Data {
    private int user_id;
    private String email;
    private String first_name;
    private String status;
    private String friend_status;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getFriend_status() {
        return friend_status;
    }

    public void setFriend_status(String friend_status) {
        this.friend_status = friend_status;
    }
}
