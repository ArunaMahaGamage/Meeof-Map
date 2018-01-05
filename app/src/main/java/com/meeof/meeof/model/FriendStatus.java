package com.meeof.meeof.model;

/**
 * Created by ransikadesilva on 12/1/17.
 */

public class FriendStatus {

    private int userId;
    private String friendStatus;

    public FriendStatus(int userId, String friendStatus) {
        this.userId = userId;
        this.friendStatus = friendStatus;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFriendStatus() {
        return friendStatus;
    }

    public void setFriendStatus(String friendStatus) {
        this.friendStatus = friendStatus;
    }
}
