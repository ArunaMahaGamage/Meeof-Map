package com.meeof.meeof.model.events_dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.util.List;

public class Event  implements Serializable {

    private String first_name;
    private String profilephoto;
    private int user_id;
    private String channel_id;
    private double distance;
    private int CountPhotos;

    @JsonProperty("CountComments")
    private int countComments;

    @JsonProperty("CountLikes")
    private int countLikes;

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
    private int responseCount;


    @JsonProperty("isLike")
    private boolean isLike;

    @JsonProperty("tierName")
    private String tierName;

    @JsonProperty("attendeeList")
    private List<AttendeeList> AttendeeList;

    @JsonProperty("myRSVP")
    private String myRSVP;

    @JsonProperty("isHost")
    private boolean isHost;

    @JsonGetter("isLike")
    public boolean isLike() {
        return isLike;
    }

    @JsonSetter("isLike")
    public void setLike(boolean like) {
        isLike = like;
    }

    @JsonGetter("tierName")
    public String getTierName() {
        return tierName;
    }

    @JsonSetter("tierName")
    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

     @JsonGetter("isHost")
    public boolean isHost() {
        return isHost;
    }
     @JsonSetter("isHost")
    public void setHost(boolean host) {
        isHost = host;
    }

    @JsonGetter("myRSVP")
    public String getRsvp() {
        return myRSVP;
    }

    @JsonSetter("myRSVP")
    public void setMyRSVP(String myRSVP) {
        this.myRSVP = myRSVP;
    }

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

    @JsonGetter("distance")
    public double getDistance() {
        return distance;
    }

    @JsonSetter("distance")
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @JsonGetter("CountPhotos")
    public int getCountPhotos() {
        return CountPhotos;
    }

    @JsonSetter("CountPhotos")
    public void setCountPhotos(int countPhotos) {
        CountPhotos = countPhotos;
    }

    @JsonGetter("CountComments")
    public int getCountComments() {
        return countComments;
    }

    @JsonSetter("CountComments")
    public void setCountComments(int countComments) {
        this.countComments = countComments;
    }

    @JsonGetter("CountLikes")
    public int getCountLikes() {
        return countLikes;
    }

    @JsonSetter("CountLikes")
    public void setCountLikes(int countLikes) {
        this.countLikes = countLikes;
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
    @JsonGetter("attendeeList")
    public List<com.meeof.meeof.model.events_dto.AttendeeList> getAttendeeList() {
        return AttendeeList;
    }
    @JsonSetter("attendeeList")
    public void setAttendeeList(List<com.meeof.meeof.model.events_dto.AttendeeList> attendeeList) {
        AttendeeList = attendeeList;
    }

    @JsonGetter("responseCount")
    public int getResponseCount() {
        return responseCount;
    }

    @JsonSetter("responseCount")
    public void setResponseCount(int responseCount) {
        this.responseCount = responseCount;
    }

    @Override
    public String toString() {
        return "Event{" +
                "first_name='" + first_name + '\'' +
                ", profilephoto='" + profilephoto + '\'' +
                ", user_id=" + user_id +
                ", channel_id='" + channel_id + '\'' +
                ", distance=" + distance +
                ", CountPhotos=" + CountPhotos +
                ", countComments=" + countComments +
                ", countLikes=" + countLikes +
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
                ", responseCount=" + responseCount +
                ", isLike=" + isLike +
                ", tierName='" + tierName + '\'' +
                ", AttendeeList=" + AttendeeList +
                ", myRSVP='" + myRSVP + '\'' +
                ", isHost=" + isHost +
                '}';
    }
}
