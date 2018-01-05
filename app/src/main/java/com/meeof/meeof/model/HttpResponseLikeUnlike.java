package com.meeof.meeof.model;

import java.io.Serializable;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class HttpResponseLikeUnlike implements Serializable {

    private String status;
    private String zoneid;
    private int like_count;
    private boolean is_like;


    public HttpResponseLikeUnlike() {
    }

    public HttpResponseLikeUnlike(String status, String zoneid) {
        this.status = status;
        this.zoneid = zoneid;
    }

    public boolean is_like() {
        return is_like;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getZoneid() {
        return zoneid;
    }

    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }


    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public boolean getIs_like() {
        return is_like;
    }

    public void setIs_like(boolean is_like) {
        this.is_like = is_like;
    }
}
