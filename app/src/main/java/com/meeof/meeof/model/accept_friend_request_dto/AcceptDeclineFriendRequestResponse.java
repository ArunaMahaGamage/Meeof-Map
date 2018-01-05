package com.meeof.meeof.model.accept_friend_request_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 10/12/17.
 */

public class AcceptDeclineFriendRequestResponse {

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
}
