package com.meeof.meeof.model;

import com.meeof.meeof.model.potential_friends.HttpPotentialFriendsResponse;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class RetrievePotentialFriendsWebJobResponse {

    private String status;
    private HttpPotentialFriendsResponse httpPotentialFriendsResponse;

    public RetrievePotentialFriendsWebJobResponse(String status, HttpPotentialFriendsResponse httpPotentialFriendsResponse) {
        this.status = status;
        this.httpPotentialFriendsResponse = httpPotentialFriendsResponse;
    }

    public HttpPotentialFriendsResponse getHttpPotentialFriendsResponse() {
        return httpPotentialFriendsResponse;
    }

    public void setHttpPotentialFriendsResponse(HttpPotentialFriendsResponse httpPotentialFriendsResponse) {
        this.httpPotentialFriendsResponse = httpPotentialFriendsResponse;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
