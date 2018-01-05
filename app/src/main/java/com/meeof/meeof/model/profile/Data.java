package com.meeof.meeof.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
    private int mUser_id;
    private String mProfilephoto;
    private String mEmail;
    private String mBackup_email;
    private String mTimezone;
    private String mPhone_number;
    private String mStatus;
    private String mFirst_name;
    private String mLast_name;
    private String mRemember_token;
    private String mFilter_updates;
    private String mDeleted_at;
    private String mChannel_id;
    private int mIs_first_login;
    private int mCountry_id;
    private String mAddress;
    private double mLongitude;
    private String mPlaceID;
    private String mPlaceName;
    private double mLatitude;
    private int mGender;
    private String mMatrix;
    private double mAccept_distance;
    private String mCode;
    private String mAvatar;
    private String mList_friend;
    private int mtrack_current_location;
    private List<Country> mCountry;

    public int getTrack_current_location() { return mtrack_current_location;}

    public void setTrack_current_location(int track_current_location) { this.mtrack_current_location = track_current_location;}

    public int getUser_id() {
        return mUser_id;
    }

    public void setUser_id(int mUser_id) {
        this.mUser_id = mUser_id;
    }

    public String getProfilephoto() {
        return mProfilephoto;
    }

    public void setProfilephoto(String mProfilephoto) {
        this.mProfilephoto = mProfilephoto;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getBackup_email() {
        return mBackup_email;
    }

    public void setBackup_email(String mBackup_email) {
        this.mBackup_email = mBackup_email;
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

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String mStatus) {
        this.mStatus = mStatus;
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

    public String getDeleted_at() {
        return mDeleted_at;
    }

    public void setDeleted_at(String mDeleted_at) {
        this.mDeleted_at = mDeleted_at;
    }

    public String getChannel_id() {
        return mChannel_id;
    }

    public void setChannel_id(String mChannel_id) {
        this.mChannel_id = mChannel_id;
    }

    public int getIs_first_login() {
        return mIs_first_login;
    }

    public void setIs_first_login(int mIs_first_login) {
        this.mIs_first_login = mIs_first_login;
    }

    public int getCountry_id() {
        return mCountry_id;
    }

    public void setCountry_id(int mCountry_id) {
        this.mCountry_id = mCountry_id;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getPlaceID() {
        return mPlaceID;
    }

    public void setPlaceID(String mPlaceID) {
        this.mPlaceID = mPlaceID;
    }

    public String getPlaceName() {
        return mPlaceName;
    }

    public void setPlaceName(String mPlaceName) {
        this.mPlaceName = mPlaceName;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public int getGender() {
        return mGender;
    }

    public void setGender(int mGender) {
        this.mGender = mGender;
    }

    public String getMatrix() {
        return mMatrix;
    }

    public void setMatrix(String mMatrix) {
        this.mMatrix = mMatrix;
    }

    public double getAccept_distance() {
        return mAccept_distance;
    }

    public void setAccept_distance(double mAccept_distance) {
        this.mAccept_distance = mAccept_distance;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public String getList_friend() {
        return mList_friend;
    }

    public void setList_friend(String mList_friend) {
        this.mList_friend = mList_friend;
    }

    public List<Country> getCountry() {
        return mCountry;
    }

    public void setCountry(List<Country> mCountry) {
        this.mCountry = mCountry;
    }


    @Override
    public String toString() {
        return "Data{" +
                "mUser_id=" + mUser_id +
                ", mProfilephoto='" + mProfilephoto + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mBackup_email='" + mBackup_email + '\'' +
                ", mTimezone='" + mTimezone + '\'' +
                ", mPhone_number='" + mPhone_number + '\'' +
                ", mStatus='" + mStatus + '\'' +
                ", mFirst_name='" + mFirst_name + '\'' +
                ", mLast_name='" + mLast_name + '\'' +
                ", mRemember_token='" + mRemember_token + '\'' +
                ", mFilter_updates='" + mFilter_updates + '\'' +
                ", mDeleted_at='" + mDeleted_at + '\'' +
                ", mChannel_id='" + mChannel_id + '\'' +
                ", mIs_first_login=" + mIs_first_login +
                ", mCountry_id=" + mCountry_id +
                ", mAddress='" + mAddress + '\'' +
                ", mLongitude=" + mLongitude +
                ", mPlaceID='" + mPlaceID + '\'' +
                ", mPlaceName='" + mPlaceName + '\'' +
                ", mLatitude=" + mLatitude +
                ", mGender=" + mGender +
                ", mMatrix='" + mMatrix + '\'' +
                ", mAccept_distance=" + mAccept_distance +
                ", mCode='" + mCode + '\'' +
                ", mAvatar='" + mAvatar + '\'' +
                ", mList_friend='" + mList_friend + '\'' +
                ", mCountry=" + mCountry +
                '}';
    }
}
