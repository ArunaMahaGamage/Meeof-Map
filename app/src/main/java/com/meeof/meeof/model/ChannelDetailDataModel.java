package com.meeof.meeof.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Dharmesh on 12/9/2017.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class ChannelDetailDataModel
{
    private int id;
    private int entityhandle;
    private String channel_name;
    private int owner_id;
    private String channel_description;
    private int channel_type;
    private int channel_d_type;
    private int stripe_id;
    private String card_brand;
    private String card_last_four;
    private String trial_ends_at;
    private int masterid;
    private int entityid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEntityhandle() {
        return entityhandle;
    }

    public void setEntityhandle(int entityhandle) {
        this.entityhandle = entityhandle;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getChannel_description() {
        return channel_description;
    }

    public void setChannel_description(String channel_description) {
        this.channel_description = channel_description;
    }

    public int getChannel_type() {
        return channel_type;
    }

    public void setChannel_type(int channel_type) {
        this.channel_type = channel_type;
    }

    public int getChannel_d_type() {
        return channel_d_type;
    }

    public void setChannel_d_type(int channel_d_type) {
        this.channel_d_type = channel_d_type;
    }

    public int getStripe_id() {
        return stripe_id;
    }

    public void setStripe_id(int stripe_id) {
        this.stripe_id = stripe_id;
    }

    public String getCard_brand() {
        return card_brand;
    }

    public void setCard_brand(String card_brand) {
        this.card_brand = card_brand;
    }

    public String getCard_last_four() {
        return card_last_four;
    }

    public void setCard_last_four(String card_last_four) {
        this.card_last_four = card_last_four;
    }

    public String getTrial_ends_at() {
        return trial_ends_at;
    }

    public void setTrial_ends_at(String trial_ends_at) {
        this.trial_ends_at = trial_ends_at;
    }

    public int getMasterid() {
        return masterid;
    }

    public void setMasterid(int masterid) {
        this.masterid = masterid;
    }

    public int getEntityid() {
        return entityid;
    }

    public void setEntityid(int entityid) {
        this.entityid = entityid;
    }
}
