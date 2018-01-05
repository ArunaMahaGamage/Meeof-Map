package com.meeof.meeof.model.friends_dto;

import java.io.Serializable;

public class Data implements Serializable{
    private boolean is_silhouette;
    private String url;
    private int height;
    private int width;


    public boolean isIs_silhouette() {
        return is_silhouette;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public boolean getIs_silhouette() {
        return is_silhouette;
    }

    public void setIs_silhouette(boolean is_silhouette) {
        this.is_silhouette = is_silhouette;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "Data{" +
                "is_silhouette=" + is_silhouette +
                ", url='" + url + '\'' +
                ", height=" + height +
                ", width=" + width +
                '}';
    }
}
