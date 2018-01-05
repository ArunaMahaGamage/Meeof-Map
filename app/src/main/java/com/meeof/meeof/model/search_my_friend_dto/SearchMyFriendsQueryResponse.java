package com.meeof.meeof.model.search_my_friend_dto;

import com.meeof.meeof.model.search_my_friend_dto.Data;

import java.util.List;

/**
 * Created by ransikadesilva on 12/20/17.
 */

public class SearchMyFriendsQueryResponse {

    private String status;
    private List<Data> data;
    public SearchMyFriendsQueryResponse() {
    }

    public SearchMyFriendsQueryResponse(String status, List<Data> data) {
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

