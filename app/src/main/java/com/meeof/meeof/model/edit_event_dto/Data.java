package com.meeof.meeof.model.edit_event_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

public class Data {
    @JsonProperty("first_name")
    private String first_name;

    @JsonProperty("profilephoto")
    private String profilephoto;

    @JsonProperty("user_id")
    private int user_id;

    @JsonProperty("channel_id")
    private String channel_id;

    @JsonProperty("CountPhotos")
    private int CountPhotos;

    @JsonProperty("CountComments")
    private int CountComments;

    @JsonProperty("CountLikes")
    private int CountLikes;

    @JsonProperty("eventid")
    private int eventid;

    @JsonProperty("title")
    private String title;

    @JsonProperty("location")
    private String location;

    @JsonProperty("placeName")
    private String placeName;

    @JsonProperty("placeID")
    private String placeID;

    @JsonProperty("longitude")
    private double longitude;

    @JsonProperty("live")
    private int live;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("event_poster")
    private String event_poster;

    @JsonProperty("created_at")
    private String created_at;

    @JsonProperty("organizer_id")
    private int organizer_id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category_id")
    private int category_id;

    @JsonProperty("type")
    private int type;

    @JsonProperty("start_date")
    private String start_date;

    @JsonProperty("end_date")
    private String end_date;

    @JsonProperty("is_hide_location")
    private int is_hide_location;

    @JsonProperty("max_attendees")
    private int max_attendees;

    @JsonProperty("detailedaddress")
    private String detailedaddress;

    @JsonProperty("interestName")
    private String interestName;

    @JsonProperty("tier1")
    private int tier1;

    private List<AttendeeList> attendeeList;
    private List<Photos> photos;
    private List<Comments> comments;
    private List<Likes> likes;

    @JsonGetter("first_name")
    public String getFirst_name() {
        return first_name;
    }

    @JsonSetter("first_name")
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    @JsonGetter("profilephoto")
    public String getProfilephoto() {
        return profilephoto;
    }

    @JsonSetter("profilephoto")
    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    @JsonGetter("user_id")
    public int getUser_id() {
        return user_id;
    }

    @JsonSetter("user_id")
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @JsonGetter("channel_id")
    public String getChannel_id() {
        return channel_id;
    }

    @JsonSetter("channel_id")
    public void setChannel_id(String channel_id) {
        this.channel_id = channel_id;
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

    @JsonGetter("eventid")
    public int getEventid() {
        return eventid;
    }

    @JsonSetter("eventid")
    public void setEventid(int eventid) {
        this.eventid = eventid;
    }

    @JsonGetter("title")
    public String getTitle() {
        return title;
    }

    @JsonSetter("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonGetter("location")
    public String getLocation() {
        return location;
    }

    @JsonSetter("location")
    public void setLocation(String location) {
        this.location = location;
    }

    @JsonGetter("placeName")
    public String getPlaceName() {
        return placeName;
    }

    @JsonSetter("placeName")
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    @JsonGetter("placeID")
    public String getPlaceID() {
        return placeID;
    }

    @JsonSetter("placeID")
    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    @JsonGetter("longitude")
    public double getLongitude() {
        return longitude;
    }

    @JsonSetter("longitude")
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @JsonGetter("live")
    public int getLive() {
        return live;
    }

    @JsonSetter("live")
    public void setLive(int live) {
        this.live = live;
    }

    @JsonGetter("latitude")
    public double getLatitude() {
        return latitude;
    }

    @JsonSetter("latitude")
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @JsonGetter("event_poster")
    public String getEvent_poster() {
        return event_poster;
    }

    @JsonSetter("event_poster")
    public void setEvent_poster(String event_poster) {
        this.event_poster = event_poster;
    }

    @JsonGetter("created_at")
    public String getCreated_at() {
        return created_at;
    }

    @JsonSetter("created_at")
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    @JsonGetter("organizer_id")
    public int getOrganizer_id() {
        return organizer_id;
    }

    @JsonSetter("organizer_id")
    public void setOrganizer_id(int organizer_id) {
        this.organizer_id = organizer_id;
    }

    @JsonGetter("description")
    public String getDescription() {
        return description;
    }

    @JsonSetter("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonGetter("category_id")
    public int getCategory_id() {
        return category_id;
    }

    @JsonSetter("category_id")
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    @JsonGetter("type")
    public int getType() {
        return type;
    }

    @JsonSetter("type")
    public void setType(int type) {
        this.type = type;
    }

    @JsonGetter("start_date")
    public String getStart_date() {
        return start_date;
    }

    @JsonSetter("start_date")
    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    @JsonGetter("end_date")
    public String getEnd_date() {
        return end_date;
    }

    @JsonSetter("end_date")
    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    @JsonGetter("is_hide_location")
    public int getIs_hide_location() {
        return is_hide_location;
    }

    @JsonSetter("is_hide_location")
    public void setIs_hide_location(int is_hide_location) {
        this.is_hide_location = is_hide_location;
    }

    @JsonGetter("max_attendees")
    public int getMax_attendees() {
        return max_attendees;
    }

    @JsonSetter("max_attendees")
    public void setMax_attendees(int max_attendees) {
        this.max_attendees = max_attendees;
    }

    @JsonGetter("detailedaddress")
    public String getDetailedaddress() {
        return detailedaddress;
    }

    @JsonSetter("detailedaddress")
    public void setDetailedaddress(String detailedaddress) {
        this.detailedaddress = detailedaddress;
    }

    @JsonGetter("interestName")
    public String getInterestName() {
        return interestName;
    }

    @JsonSetter("interestName")
    public void setInterestName(String interestName) {
        this.interestName = interestName;
    }

    @JsonGetter("tier1")
    public int getTier1() {
        return tier1;
    }

    @JsonSetter("tier1")
    public void setTier1(int tier1) {
        this.tier1 = tier1;
    }

    public List<AttendeeList> getAttendeeList() {
        return attendeeList;
    }

    public void setAttendeeList(List<AttendeeList> attendeeList) {
        this.attendeeList = attendeeList;
    }

    public List<Photos> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photos> photos) {
        this.photos = photos;
    }

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }
}
