package com.meeof.meeof.model.private_invitation_dto_model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;


public class Array_events {
    private String first_name;
    private String profilephoto;
    private int user_id;
    private String channel_id;
    private String entityhandle;
    private double distance;
    @JsonProperty("CountPhotos")
    private int CountPhotos;
    @JsonProperty("CountComments")
    private int CountComments;
    @JsonProperty("CountLikes")
    private int CountLikes;
    private int eventid;
    private String title;
    private String location;
    private String placeName;
    private String placeID;
    private double longitude;
    private int live;
    private double latitude;
    private String event_poster;
    private String created_at;
    private int organizer_id;
    private String description;
    private int category_id;
    private int type;
    private String start_date;
    private String end_date;
    private int is_hide_location;
    private int max_attendees;
    private String detailedaddress;
    private String interestName;
    private int tier1;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
    }

    public String getEntityhandle() {
        return entityhandle;
    }

    public void setEntityhandle(String entityhandle) {
        this.entityhandle = entityhandle;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @JsonGetter("CountPhotos")
    public int getCountPhotos() {
        return CountPhotos;
    }

    @JsonSetter("CountPhotos")
    public void setCountPhotos(int CountPhotos) {
        this.CountPhotos = CountPhotos;
    }

    @JsonGetter("CountComments")
    public int getCountComments() {
        return CountComments;
    }

    @JsonSetter("CountComments")
    public void setCountComments(int CountComments) {
        this.CountComments = CountComments;
    }

    @JsonGetter("CountLikes")
    public int getCountLikes() {
        return CountLikes;
    }

    @JsonSetter("CountLikes")
    public void setCountLikes(int CountLikes) {
        this.CountLikes = CountLikes;
    }

    public int getEventid() {
        return eventid;
    }

    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getLive() {
        return live;
    }

    public void setLive(int live) {
        this.live = live;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getEvent_poster() {
        return event_poster;
    }

    public void setEvent_poster(String event_poster) {
        this.event_poster = event_poster;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getOrganizer_id() {
        return organizer_id;
    }

    public void setOrganizer_id(int organizer_id) {
        this.organizer_id = organizer_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getIs_hide_location() {
        return is_hide_location;
    }

    public void setIs_hide_location(int is_hide_location) {
        this.is_hide_location = is_hide_location;
    }

    public int getMax_attendees() {
        return max_attendees;
    }

    public void setMax_attendees(int max_attendees) {
        this.max_attendees = max_attendees;
    }

    public String getDetailedaddress() {
        return detailedaddress;
    }

    public void setDetailedaddress(String detailedaddress) {
        this.detailedaddress = detailedaddress;
    }

    public String getInterestName() {
        return interestName;
    }

    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    public int getTier1() {
        return tier1;
    }

    public void setTier1(int tier1) {
        this.tier1 = tier1;
    }

    @Override
    public String toString() {
        return "Array_events{" +
                "first_name='" + first_name + '\'' +
                ", profilephoto='" + profilephoto + '\'' +
                ", user_id=" + user_id +
                ", channel_id='" + channel_id + '\'' +
                ", entityhandle='" + entityhandle + '\'' +
                ", distance=" + distance +
                ", CountPhotos=" + CountPhotos +
                ", CountComments=" + CountComments +
                ", CountLikes=" + CountLikes +
                ", eventid=" + eventid +
                ", title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", placeName='" + placeName + '\'' +
                ", placeID='" + placeID + '\'' +
                ", longitude=" + longitude +
                ", live=" + live +
                ", latitude=" + latitude +
                ", event_poster='" + event_poster + '\'' +
                ", created_at='" + created_at + '\'' +
                ", organizer_id=" + organizer_id +
                ", description='" + description + '\'' +
                ", category_id=" + category_id +
                ", type=" + type +
                ", start_date='" + start_date + '\'' +
                ", end_date='" + end_date + '\'' +
                ", is_hide_location=" + is_hide_location +
                ", max_attendees=" + max_attendees +
                ", detailedaddress='" + detailedaddress + '\'' +
                ", interestName='" + interestName + '\'' +
                ", tier1=" + tier1 +
                '}';
    }
}
