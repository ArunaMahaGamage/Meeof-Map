package com.meeof.meeof.model.friends_all_dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Friends implements Serializable{


    private int mId;
    private String mEmail;
    private String mUsername;
    private String mStatus;
    private String mPassword;
    private String mLast_login;
    private String mFirst_name;
    private String mLast_name;
    private String mCreated_at;
    private String mUpdated_at;
    private String mTimezone;
    private String mPhone_number;
    private String mDeleted_at;
    private String mRemember_token;
    private String mFilter_updates;
    private int mFilter_events;
    private int mTrack_current_location;
    private String mProfilephoto;
    private String mBackup_email;
    private String mEmail_validation_required;
    private String mEmail_validation_time;
    private String mEmail_validation_code;
    private int mIs_first_login;
    private String mChannel_master;
    private int mChannel_published;
    private String mChannel_id;
    private String mEntityhandle;
    private String mChannel_type;
    private String mChannel_d_type;
    private String mChannel_description;
    private String mFriend_status;


    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getLast_login() {
        return mLast_login;
    }

    public void setLast_login(String mLast_login) {
        this.mLast_login = mLast_login;
    }

    public String getFirst_name() {
        return mFirst_name;
    }

    public void setFirst_name(String mFirst_name) {
        this.mFirst_name = mFirst_name;
    }

    public String getLast_name() {
        return mLast_name;
    }

    public void setLast_name(String mLast_name) {
        this.mLast_name = mLast_name;
    }

    public String getCreated_at() {
        return mCreated_at;
    }

    public void setCreated_at(String mCreated_at) {
        this.mCreated_at = mCreated_at;
    }

    public String getUpdated_at() {
        return mUpdated_at;
    }

    public void setUpdated_at(String mUpdated_at) {
        this.mUpdated_at = mUpdated_at;
    }

    public String getTimezone() {
        return mTimezone;
    }

    public void setTimezone(String mTimezone) {
        this.mTimezone = mTimezone;
    }

    public String getPhone_number() {
        return mPhone_number;
    }

    public void setPhone_number(String mPhone_number) {
        this.mPhone_number = mPhone_number;
    }

    public String getDeleted_at() {
        return mDeleted_at;
    }

    public void setDeleted_at(String mDeleted_at) {
        this.mDeleted_at = mDeleted_at;
    }

    public String getRemember_token() {
        return mRemember_token;
    }

    public void setRemember_token(String mRemember_token) {
        this.mRemember_token = mRemember_token;
    }

    public String getFilter_updates() {
        return mFilter_updates;
    }

    public void setFilter_updates(String mFilter_updates) {
        this.mFilter_updates = mFilter_updates;
    }

    public int getFilter_events() {
        return mFilter_events;
    }

    public void setFilter_events(int mFilter_events) {
        this.mFilter_events = mFilter_events;
    }

    public int getTrack_current_location() {
        return mTrack_current_location;
    }

    public void setTrack_current_location(int mTrack_current_location) {
        this.mTrack_current_location = mTrack_current_location;
    }

    public String getProfilephoto() {
        return mProfilephoto;
    }

    public void setProfilephoto(String mProfilephoto) {
        this.mProfilephoto = mProfilephoto;
    }

    public String getBackup_email() {
        return mBackup_email;
    }

    public void setBackup_email(String mBackup_email) {
        this.mBackup_email = mBackup_email;
    }

    public String getEmail_validation_required() {
        return mEmail_validation_required;
    }

    public void setEmail_validation_required(String mEmail_validation_required) {
        this.mEmail_validation_required = mEmail_validation_required;
    }

    public String getEmail_validation_time() {
        return mEmail_validation_time;
    }

    public void setEmail_validation_time(String mEmail_validation_time) {
        this.mEmail_validation_time = mEmail_validation_time;
    }

    public String getEmail_validation_code() {
        return mEmail_validation_code;
    }

    public void setEmail_validation_code(String mEmail_validation_code) {
        this.mEmail_validation_code = mEmail_validation_code;
    }

    public int getIs_first_login() {
        return mIs_first_login;
    }

    public void setIs_first_login(int mIs_first_login) {
        this.mIs_first_login = mIs_first_login;
    }

    public String getChannel_master() {
        return mChannel_master;
    }

    public void setChannel_master(String mChannel_master) {
        this.mChannel_master = mChannel_master;
    }

    public int getChannel_published() {
        return mChannel_published;
    }

    public void setChannel_published(int mChannel_published) {
        this.mChannel_published = mChannel_published;
    }

    public String getChannel_id() {
        return mChannel_id;
    }

    public void setChannel_id(String mChannel_id) {
        this.mChannel_id = mChannel_id;
    }

    public String getEntityhandle() {
        return mEntityhandle;
    }

    public void setEntityhandle(String mEntityhandle) {
        this.mEntityhandle = mEntityhandle;
    }

    public String getChannel_type() {
        return mChannel_type;
    }

    public void setChannel_type(String mChannel_type) {
        this.mChannel_type = mChannel_type;
    }

    public String getChannel_d_type() {
        return mChannel_d_type;
    }

    public void setChannel_d_type(String mChannel_d_type) {
        this.mChannel_d_type = mChannel_d_type;
    }

    public String getChannel_description() {
        return mChannel_description;
    }

    public void setChannel_description(String mChannel_description) {
        this.mChannel_description = mChannel_description;
    }

    public String getFriend_status() {
        return mFriend_status;
    }

    public void setFriend_status(String mFriend_status) {
        this.mFriend_status = mFriend_status;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Friends)) {
            return false;
        }

        Friends friends = (Friends) o;

        return new EqualsBuilder()
                .append(mId, friends.getId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(mId)
                .toHashCode();
    }

}
