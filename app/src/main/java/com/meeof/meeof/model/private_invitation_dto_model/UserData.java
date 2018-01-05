package com.meeof.meeof.model.private_invitation_dto_model;

public class UserData {
    private int user_id;
    private String profilephoto;
    private String email;
    private String entityhandle;
    private String backup_email;
    private String timezone;
    private String phone_number;
    private String status;
    private String first_name;
    private String last_name;
    private String remember_token;
    private int filter_updates;
    private String deleted_at;
    private String channel_id;
    private int is_first_login;
    private int track_current_location;
    private int country_id;
    private String address;
    private double longitude;
    private String placeID;
    private String placeName;
    private double latitude;
    private int gender;
    private String matrix;
    private int accept_distance;
    private String code;
    private String avatar;
    private String list_friend;

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEntityhandle() {
        return entityhandle;
    }

    public void setEntityhandle(String entityhandle) {
        this.entityhandle = entityhandle;
    }

    public String getBackup_email() {
        return backup_email;
    }

    public void setBackup_email(String backup_email) {
        this.backup_email = backup_email;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getRemember_token() {
        return remember_token;
    }

    public void setRemember_token(String remember_token) {
        this.remember_token = remember_token;
    }

    public int getFilter_updates() {
        return filter_updates;
    }

    public void setFilter_updates(int filter_updates) {
        this.filter_updates = filter_updates;
    }

    public String getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(String deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public int getIs_first_login() {
        return is_first_login;
    }

    public void setIs_first_login(int is_first_login) {
        this.is_first_login = is_first_login;
    }

    public int getTrack_current_location() {
        return track_current_location;
    }

    public void setTrack_current_location(int track_current_location) {
        this.track_current_location = track_current_location;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getMatrix() {
        return matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public int getAccept_distance() {
        return accept_distance;
    }

    public void setAccept_distance(int accept_distance) {
        this.accept_distance = accept_distance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getList_friend() {
        return list_friend;
    }

    public void setList_friend(String list_friend) {
        this.list_friend = list_friend;
    }

    @Override
    public String toString() {
        return "UserData{" +
                "user_id=" + user_id +
                ", profilephoto='" + profilephoto + '\'' +
                ", email='" + email + '\'' +
                ", entityhandle='" + entityhandle + '\'' +
                ", backup_email='" + backup_email + '\'' +
                ", timezone='" + timezone + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", status='" + status + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", remember_token='" + remember_token + '\'' +
                ", filter_updates=" + filter_updates +
                ", deleted_at='" + deleted_at + '\'' +
                ", channel_id='" + channel_id + '\'' +
                ", is_first_login=" + is_first_login +
                ", track_current_location=" + track_current_location +
                ", country_id=" + country_id +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", placeID='" + placeID + '\'' +
                ", placeName='" + placeName + '\'' +
                ", latitude=" + latitude +
                ", gender=" + gender +
                ", matrix='" + matrix + '\'' +
                ", accept_distance=" + accept_distance +
                ", code='" + code + '\'' +
                ", avatar='" + avatar + '\'' +
                ", list_friend='" + list_friend + '\'' +
                '}';
    }
}
