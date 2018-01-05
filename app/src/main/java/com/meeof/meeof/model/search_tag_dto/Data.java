package com.meeof.meeof.model.search_tag_dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Data implements Serializable {
    private int hashid;
    private String hashtag;


    public int getHashid() {
        return hashid;
    }

    public void setHashid(int hashid) {
        this.hashid = hashid;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public String toString() {
        return "Data{" +
                "hashid=" + hashid +
                ", hashtag='" + hashtag + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {


        if (obj == null) {
            return false;
        }
        if (!Data.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final Data other = (Data) obj;
        if (this.hashid != other.getHashid()) {
            return false;
        }
        return true;


        //return super.equals(obj);
    }

}
