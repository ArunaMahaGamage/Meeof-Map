package com.meeof.meeof.model.update_comments_dto;

import java.util.List;

/**
 * Created by ransikadesilva on 12/15/17.
 */

public class UpdateCommentsResponse {

    private String status;
    private List<Array_comments> array_comments;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Array_comments> getArray_comments() {
        return array_comments;
    }

    public void setArray_comments(List<Array_comments> array_comments) {
        this.array_comments = array_comments;
    }
}
