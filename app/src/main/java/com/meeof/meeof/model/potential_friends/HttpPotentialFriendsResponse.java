package com.meeof.meeof.model.potential_friends;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by ransikadesilva on 12/14/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpPotentialFriendsResponse {

    private String status;
    private List<Data> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpPotentialFriendsResponse{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
