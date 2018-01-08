package com.meeof.meeof.util;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class Constant {
    //    public static final String BASE_URL = "http://192.168.2.31:8000"; //Local
//    public static final String BASE_URL = "http://54.201.114.126:8000"; //Live Server
//   public static final String BASE_URL = "https://stag2017.meeof.com"; //Live Server
    public static final String BASE_URL = "https://dev2017.meeof.com"; //Live Server
    //Live Server

    public static final String GRANT_TYPE_FB = "facebook";
    public static final String GRANT_TYPE_GOOGLE = "google";

    public static final String IMAGE_SET_PATH = "IMAGE_SET_PATH";
    public static final String GRANT_TYPE_EMAIL = "GRANT_TYPE_EMAIL";
    public static final String OTHER_USER_ID = "OTHER_USER_ID";
    public static final String USER_PROFILE_OBJECT = "USER_PROFILE_OBJECT";
    public static final String PROFILE_PIC_BASE_URL = "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/";
    public static final String DEFAULT_AVATAR_URL = "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png";

    public static final String RABBIT_MQ_CLOUD_URI = "amqp://bytqideb:xhJNp1oRDa21Z-yh_8DvXqH6IMBmU9HU@elephant.rmq.cloudamqp.com/bytqideb";

    public static final String FRIEND = "friend";
    public static final String NONE = "none";
    public static final String DUMMY = "dummy";

    public static final String CURRENT_COUNTRY_NAME = "current_country_name";

    public static final String IS_FROM_OPTIONS_MENU = "IS_FROM_OPTIONS_MENU";
    public static final String IS_FROM_SETTINGS = "IS_FROM_SETTINGS";
    public static final String IS_FROM_CREATE_EVENT = "IS_FROM_CREATE_EVENT";
    public static final String IS_FROM_INTERESTS_ACTIVITY = "IS_FROM_INTERESTS_ACTIVITY";
    public static final String IS_EDIT_EVENT = "IS_EDIT_EVENT";
    public static final String IS_FROM_EDIT_PROFILE = "IS_FROM_EDIT_PROFILE";
    public static final String IS_FROM_INTERESTS = "IS_FROM_INTERESTS";
    public static final String EDIT_EVENT_EVENT_ID = "EDIT_EVENT_EVENT_ID";
    public static final float MAP_ZOOM_LEVEL = 18.0f;
    //    public static final String ACCES_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjQ3NDE5ZmVhZGVjOTcxZjE0NzcxYmYwMDI2NDJlZDJkNWEwNjFhN2U0OGI0NzY2OWM4MzVhZTVlNTZjMzI5NTJhYzU5ODMyOGM2NjNmYTMyIn0.eyJhdWQiOiIxIiwianRpIjoiNDc0MTlmZWFkZWM5NzFmMTQ3NzFiZjAwMjY0MmVkMmQ1YTA2MWE3ZTQ4YjQ3NjY5YzgzNWFlNWU1NmMzMjk1MmFjNTk4MzI4YzY2M2ZhMzIiLCJpYXQiOjE1MDg0MzY5NTksIm5iZiI6MTUwODQzNjk1OSwiZXhwIjoxNTM5OTcyOTU5LCJzdWIiOiIxNDkiLCJzY29wZXMiOltdfQ.CM_StQ_IHttueQcjlDZTSkb_y_6do-BhN6ZijN-6F2Pd0_OdgN4tziIIqaohDfcP-bWbIeim8rfRfQ-exDVqQTmDbgZt8mX7XIIe78W3D4sMaEmH2XdlsyCFAg8uUHjm6YWJaYGQFld5Y_0ut7EVD1YNN5h50KdBvdGoHmLWPOHVtHppPmKrJCYjN8CRXrr4XTnuYiO5ebmT3z8x-LUhQeNmmBufFKnLKnf-Xg1DVwflpn9D-Tr4JQLKIf7sVmicvLVXjIgJJfvCPuwZF8eM9ZqQBuTy7hDKEw2wAUocYaIue5vJQYWzsuNIWatDQXyJjDHWz9k_Wk1S77txPs_oTwki75XA31TPGa4qEotVYeAh2nHDwIy2G8p-PtZA1EDwFKaWAUHjBGoJDdygLU12EcvynZhq1qjgsCOVtpKkfLLdnCbbXzXwKvKmHUO1g65zUAuITXDuddqRNs8CsmMb_94XtsDRJj6O-cuVa6a3BDzggD83sxN4V8QvIh6_u39oS4uLYCX4V14fxw-hadiFOUwk3CmVBIPfnpIs4Eay8jidKVVBiOp23IHGK9qyAUbXPtAwt8iW-KPcR_uaBVoi0zm2lfVu8yR0MLbhxHraq4gBdRsv8zRlcxSl8vlxumlIxZ0fhjmMDMyxHnfJVz8K4wdi_uOSmYwP2fJAaPqmhxY";;
    public static final String SELECTED_EVENT_ITEM = "SELECTED_EVENT_ITEM";
    public static final String SELECTED_UPDATE_ITEM = "SELECTED_UPDATE_ITEM";
    public static final String EVENT_IMAGES_BASE_URL = "https://meeofbucket.s3.amazonaws.com/dev/public/eImages/";
    public static final String EDIT_EVENT_TITLE = "EDIT_EVENT_TITLE";
    public static final String EVENT_FILTER_OBJ = "EVENT_FILTER_OBJ";
    public static final String FILTER_MODEL_DEFAULT_OBJ = "FILTER_MODEL_DEFAULT_OBJ";
    public static final String CURRENT_LOCATION = "CURRENT_LOCATION";
    public static final String HOME = "HOME";
    public static final String CURRENT_LATITUDE = "CURRENT_LATITUDE";
    public static final String CURRENT_LONGITUDE = "CURRENT_LONGITUDE";
    public static final String USER_ID = "USER_ID";
    public static final String LAST_SYNC_TIME = "LAST_SYNC_TIME";
    public static final long MIN_SYNC_TIME_MS = 600000; //10 minutes in MiliSeconds
    public static final String UPDATE_FILTER_OBJ = "UPDATE_FILTER_OBJ";






    public static String MEEOF_SHARED_PREF = "MEEOF_SHARED_PREF";

    public static String PASSWORD = "PASSWORD";

    public static final String SELECTED_FRIENDS_FOR_EVENT = "SELECTED_FRIENDS_FOR_EVENT";

    public static String ACCESS_TOKEN = "ACCESS_TOKEN";
    public static String REFRESH_TOKEN = "REFRESH_TOKEN";

    public static String IS_MY_PROFILE = "IS_MY_PROFILE";
    public static String CHANGES_AVIALABLE = "CHANGES_AVAILABLE";


    public static String FACEBOOK = "FACEBOOK";
    public static String GOOGLE = "GOOGLE";
    public static String CONTACTS = "CONTACTS";
    public static String ACTIVE = "active";
    public static String PENDING = "pending";
    public static String FB_APP_LINK = "https://fb.me/487768124931442";
    public static String FB_APP_IMAGE_LINK = "https://meeof.com/img/logo_white.png";
    public static String SELECTED_ACTIVITY_EVENT = "SELECTED_ACTIVITY_EVENT";
    public static String EVENT_POSTER_BASE_URL = "https://meeofbucket.s3.amazonaws.com/dev/public/eAvatar/";
    public static final String UPDATE_IMAGES_BASE_URL = "https://meeofbucket.s3.amazonaws.com/dev/public/uImages/";
    public static String distanceLat= "distanceLat";
    public static String distanceLng = "distanceLng";


    public enum AcceptDeclineRequest {y, n}

    public static String USER_EMAIL = "USER_EMAIL";
    public static final String EMAIL_REGEX = "^[A-Z0-9a-z._%+-]+@([A-Za-z0-9-]+.)+[A-Za-z]{2,4}$";

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";
    public static final String OK = "OK";

    public static final String MALE = "MALE";
    public static final String FEMALE = "FEMALE";
    public static final String KM = "KM";
    public static final String MILES = "MILES";
    public static final String GOOGLE_MAPS = "https://maps.googleapis.com/maps/api/geocode/json?";
    public static final String GOOGLE_PLACES_KEY = "AIzaSyBIzj1esFBbrqDmsqVZNmkkNO8XLSyZkog";


    public static enum UserStatus {
        PENDING,
        ACTIVE,
        INACTIVE,
        DELETED;
    }


    public static String LOGGED_IN_WITH = "LOGGED_IN_WITH";


    //Broadcast receivers

    public static final long MIN_TIME_BW_UPDATES = 1;//0 seconds
    public static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; //0 meters

    public static final String lastKnownLatitudeService = ""; //0 meters
    public static final String lastKnownLongitudeService = ""; //0 meters

    public static final String latitudeService = "";
    public static final String longitudeService = "";

    public static final String LOCATION_UPDATED = "";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String EVENT = "EVENT";


    //Broadcast receivers
    public static String NEW_ACCEPT_INVITATION = "NEW_ACCEPT_INVITATION";
    public static String NEW_COMMENT_ON_EVENT = "NEW_COMMENT_ON_EVENT";
    public static String NEW_LIKE_ON_EVENT = "NEW_LIKE_ON_EVENT";


    public static String UPDATE_MY_EVENTS_TAB = "UPDATE_MY_EVENTS_TAB";
    public static String UPDATE_ALL_EVENTS_TAB = "UPDATE_ALL_EVENTS_TAB";
    public static String UPDATE_BOTTOM_BAR_UPDATE = "UPDATE_BOTTOM_BAR_UPDATE";

    public static String UPDATE_IMPORTANT_NOTIFICATIONS_TAB = "UPDATE_IMPORTANT_NOTIFICATIONS_TAB";
    public static String UPDATE_OTHER_NOTIFICATIONS_TAB = "UPDATE_OTHER_NOTIFICATIONS_TAB";
    public static String REFRESH_NOTIFICATIONS_TAB = "REFRESH_NOTIFICATIONS_TAB";
    public static String BADGE_COUNT = "BADGE_COUNT";

    public static String JOIN_REQUESTS_BADGE_COUNT = "JOIN_REQUESTS_BADGE_COUNT";
    public static String EVENTS_BADGE_COUNT = "EVENTS_BADGE_COUNT";
    public static String MY_EVENTS_BADGE_COUNT = "MY_EVENTS_BADGE_COUNT";
    public static String ALL_EVENTS_BADGE_COUNT = "ALL_EVENTS_BADGE_COUNT";

    public static String NOTIFICATIONS_BADGE_COUNT = "NOTIFICATIONS_BADGE_COUNT";
    public static String IMPORTANT_NOTIFICATIONS_BADGE_COUNT = "IMPORTANT_NOTIFICATIONS_BADGE_COUNT";
    public static String OTHER_NOTIFICATIONS_BADGE_COUNT = "OTHER_NOTIFICATIONS_BADGE_COUNT";


    public static String INVITATION_ACCEPT_BADGE_COUNT = "INVITATION_ACCEPT_BADGE_COUNT";

    public static String UPDATE_EVENT_LIST = "UPDATE_EVENT_LIST";
    public static String UPDATE_EVENTS = "UPDATE_EVENTS";

    public static String EVENT_ID = "EVENT_ID";
    public static String UPDATE_ID = "UPDATE_ID";
    public static String IS_INTERESTED_EVENTS = "IS_INTERESTED_EVENTS";


    public static final String MYUSERID = "MYUSER_ID";
    public static final String MYNAME = "MYNAME";
    public static final String MYIMAGE = "MYIMAGE";
    public static final String IS_LIKE = "IS_LIKE";
    public static String UPDATE_UPDATE_TAB = "UPDATE_UPDATE_TAB";
}
