package com.meeof.meeof.model.update_like;

import java.util.List;

/**
 * Created by ransikadesilva on 12/22/17.
 */

public class UpdateLikeResponse {


    private String status;
    private List<Array_likes> array_likes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Array_likes> getArray_likes() {
        return array_likes;
    }

    public void setArray_likes(List<Array_likes> array_likes) {
        this.array_likes = array_likes;
    }
}
