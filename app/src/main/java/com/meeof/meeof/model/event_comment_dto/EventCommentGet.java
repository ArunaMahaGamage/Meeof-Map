package com.meeof.meeof.model.event_comment_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class EventCommentGet {


    public EventCommentGet() {
    }

    public EventCommentGet(String status, List<CommentData> data) {
        this.status = status;
        this.data = data;
    }

    private String status;
    @JsonProperty("data")
    private List<CommentData> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    @JsonGetter("data")
    public List<CommentData> getData() {
        return data;
    }
    @JsonSetter("data")
    public void setData(List<CommentData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "EventCommentGet{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
