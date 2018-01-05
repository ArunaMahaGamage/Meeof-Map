package com.meeof.meeof.model.friends_all_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ransikadesilva on 10/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FriendsAllResponse implements Serializable {



    private String status;
    private List<Friends> friends;
    private List<Pending_requests> pending_requests;

    public FriendsAllResponse() {
    }

    public FriendsAllResponse(String status, List<Friends> friends, List<Pending_requests> pending_requests) {
        this.status = status;
        this.friends = friends;
        this.pending_requests = pending_requests;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Friends> getFriends() {
        return friends;
    }

    public void setFriends(List<Friends> friends) {
        this.friends = friends;
    }

    public List<Pending_requests> getPending_requests() {
        return pending_requests;
    }

    public void setPending_requests(List<Pending_requests> pending_requests) {
        this.pending_requests = pending_requests;
    }
}
