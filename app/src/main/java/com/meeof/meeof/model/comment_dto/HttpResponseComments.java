package com.meeof.meeof.model.comment_dto;

/**
 * Created by ransikadesilva on 10/30/17.
 */

public class HttpResponseComments {


    private String status;
    private int comment_count;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }
}
