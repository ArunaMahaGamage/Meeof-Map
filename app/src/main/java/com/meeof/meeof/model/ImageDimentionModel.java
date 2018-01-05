package com.meeof.meeof.model;

import java.io.Serializable;

/**
 * Created by Anuja Ranwalage on 10/6/17.
 */

public class ImageDimentionModel implements Serializable{

    String width;
    String height;
    String scalex;
    String scaley;
    String imgurl;

    public ImageDimentionModel() {
    }

    @Override
    public String toString() {
        return "ImageDimentionModel{" +
                "width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", scalex='" + scalex + '\'' +
                ", scaley='" + scaley + '\'' +
                ", imgurl='" + imgurl + '\'' +
                '}';
    }

    public ImageDimentionModel(String width, String height, String scalex, String scaley, String imgurl) {
        this.width = width;
        this.height = height;
        this.scalex = scalex;
        this.scaley = scaley;
        this.imgurl = imgurl;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getScalex() {
        return scalex;
    }

    public void setScalex(String scalex) {
        this.scalex = scalex;
    }

    public String getScaley() {
        return scaley;
    }

    public void setScaley(String scaley) {
        this.scaley = scaley;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
}
