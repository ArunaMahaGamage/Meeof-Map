package com.meeof.meeof.model.search_friends_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 10/10/17.
 */

public class SearchFriendsResponse {

    private String status;
    private List<Data> data;

    public SearchFriendsResponse() {
    }

    public SearchFriendsResponse(String status, List<Data> data) {
        this.status = status;
        this.data = data;
    }

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
}