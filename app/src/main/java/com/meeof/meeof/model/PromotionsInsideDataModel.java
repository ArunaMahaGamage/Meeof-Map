package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/18/2017.
 */


@JsonIgnoreProperties(ignoreUnknown=true)
public class PromotionsInsideDataModel
{
    private int pid;
    private int entity_id;
    private int segment_id;
    private String promo_name;
    private String promo_start;
    private String promo_end;
    private String publish_date;
    private int promo_type;
    private String promo_poster;
    private String promo_desc;
    private String created_at;
    private double longitude;
    private double latitude;
    private String deleted_at;
    private int placeID;
    private String placeName;
    private long distance;


    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(int entity_id) {
        this.entity_id = entity_id;
    }

    public int getSegment_id() {
        return segment_id;
    }

    public void setSegment_id(int segment_id) {
        this.segment_id = segment_id;
    }

    public String getPromo_name() {
        return promo_name;
    }

    public void setPromo_name(String promo_name) {
        this.promo_name = promo_name;
    }

    public String getPromo_start() {
        return promo_start;
    }

    public void setPromo_start(String promo_start) {
        this.promo_start = promo_start;
    }

    public String getPromo_end() {
        return promo_end;
    }

    public void setPromo_end(String promo_end) {
        this.promo_end = promo_end;
    }

    public String getPublish_date() {
        return publish_date;
    }

    public void setPublish_date(String publish_date) {
        this.publish_date = publish_date;
    }

    public int getPromo_type() {
        return promo_type;
    }

    public void setPromo_type(int promo_type) {
        this.promo_type = promo_type;
    }

    public String getPromo_poster() {
        return promo_poster;
    }

    public void setPromo_poster(String promo_poster) {
        this.promo_poster = promo_poster;
    }

    public String getPromo_desc() {
        return promo_desc;
    }

    public void setPromo_desc(String promo_desc) {
        this.promo_desc = promo_desc;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public int getPlaceID() {
        return placeID;
    }

    public void setPlaceID(int placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }
}
