package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.model.AllEventsSyncedJobResponse;
import com.meeof.meeof.model.GetEventsWebJobCompletedEvent;
import com.meeof.meeof.model.HttpResponse;

import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by ransikadesilva on 10/19/17.
 */

public class GetEventsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetEventsWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events";
    private final String token;


    public GetEventsWebJob(String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;

    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if (id != jobCounter.get()) {

            Log.d(TAG, "onRun id != jobCounter.get()");
            // looks like other fetch jobs has been added after me. no reason to
            // keep fetching
            // many times, cancel me, let the other one fetch tweets.
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "SaveUserData");

            boolean isValid = true;

            SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();

            String url = "";
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.BASE_URL + endPoint).newBuilder();

            EventFilterModel eventFilterModel = retriveSavedFilterObject(sharedPreferences);

            EventFilterModel eventFilterModelDefault = retriveSavedFilterObjectDefault(sharedPreferences);

            ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);

            double lat = sharedPreferences.getFloat(Constant.CURRENT_LATITUDE, 0);
            double lon = sharedPreferences.getFloat(Constant.CURRENT_LONGITUDE, 0);

            if (eventFilterModel != null) {
                Log.d(TAG, "EventFilterModel: eventFilterModel != null " + eventFilterModel.toString());


                urlBuilder.addQueryParameter("masterfilter", eventFilterModel.getMasterfilter());
                urlBuilder.addQueryParameter("acceptabledistance", eventFilterModel.getAcceptabledistance());
                urlBuilder.addQueryParameter("sortbyproximity", eventFilterModel.getSortbyproximity());
                urlBuilder.addQueryParameter("hideoutsidefriends", eventFilterModel.getHideoutsidefriends());

                if (sharedPreferences.getBoolean(Constant.HOME, false)) {
                    Log.d(TAG, "Profile Response: " + profileResponse.toString());

                    urlBuilder.addQueryParameter("location", "home");
                    urlBuilder.addQueryParameter("niceaddress", profileResponse.getData().getAddress());
                    urlBuilder.addQueryParameter("lng", profileResponse.getData().getLongitude() + "");
                    urlBuilder.addQueryParameter("lat", profileResponse.getData().getLatitude() + "");

                } else if (sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false)) {
                    Log.d(TAG, "Profile Response: " + profileResponse.toString());
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", eventFilterModel.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", lon != 0 ? String.valueOf(lon) : eventFilterModel.getLng());
                    urlBuilder.addQueryParameter("lat", lat != 0 ? String.valueOf(lat) : eventFilterModel.getLat());

                } else {
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", eventFilterModel.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", eventFilterModel.getLng());
                    urlBuilder.addQueryParameter("lat", eventFilterModel.getLat());
                }

                urlBuilder.addQueryParameter("matrix", eventFilterModel.getMatrix());
                urlBuilder.addQueryParameter("stopexecution", eventFilterModel.getStopexecution());
                urlBuilder.addQueryParameter("offset", eventFilterModel.getOffset());
                urlBuilder.addQueryParameter("limit", eventFilterModel.getLimit());
                urlBuilder.addQueryParameter("rememberfilter", eventFilterModel.getRememberfilter());


            } else if (eventFilterModelDefault != null) {

                Log.d(TAG, "EventFilterModel: eventFilterModelDefault != null " + eventFilterModelDefault.toString());


                urlBuilder.addQueryParameter("masterfilter", eventFilterModelDefault.getMasterfilter());
                urlBuilder.addQueryParameter("acceptabledistance", eventFilterModelDefault.getAcceptabledistance());
                urlBuilder.addQueryParameter("sortbyproximity", eventFilterModelDefault.getSortbyproximity());
                urlBuilder.addQueryParameter("hideoutsidefriends", eventFilterModelDefault.getHideoutsidefriends());

                if (sharedPreferences.getBoolean(Constant.HOME, false)) {
                    urlBuilder.addQueryParameter("location", "home");
                    urlBuilder.addQueryParameter("niceaddress", profileResponse.getData().getAddress());
                    urlBuilder.addQueryParameter("lng", profileResponse.getData().getLongitude() + "");
                    urlBuilder.addQueryParameter("lat", profileResponse.getData().getLatitude() + "");

                } else if (sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false)) {

                    Log.d(TAG, "Profile Response: " + profileResponse.toString());
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", eventFilterModelDefault.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", lon != 0 ? String.valueOf(lon) : eventFilterModelDefault.getLng());
                    urlBuilder.addQueryParameter("lat", lat != 0 ? String.valueOf(lat) : eventFilterModelDefault.getLat());

                } else {
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", eventFilterModelDefault.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", eventFilterModelDefault.getLng());
                    urlBuilder.addQueryParameter("lat", eventFilterModelDefault.getLat());
                }

                urlBuilder.addQueryParameter("matrix", eventFilterModelDefault.getMatrix());
                urlBuilder.addQueryParameter("stopexecution", eventFilterModelDefault.getStopexecution());
                urlBuilder.addQueryParameter("offset", eventFilterModelDefault.getOffset());
                urlBuilder.addQueryParameter("limit", eventFilterModelDefault.getLimit());
                urlBuilder.addQueryParameter("rememberfilter", eventFilterModelDefault.getRememberfilter());

            } else {
                Log.d(TAG, "EventFilterModel: else ");
                isValid = false;
            }

            if(isValid){

                Log.wtf(TAG, "Url : " + urlBuilder.toString());
                Log.wtf(TAG, "Events Get:" + urlBuilder.build().toString());
                Request request = new Request.Builder()
                        .addHeader("Authorization", token)
                        .addHeader("Accept", "application/json")
                        .url(urlBuilder.build().toString())
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d(TAG, "response: " + result);
                Log.d(TAG, "token: " + token);

                JSONObject jsonResponse = new JSONObject(result);

                if (jsonResponse != null) {
                    try {
                        JSONArray events = jsonResponse.getJSONArray("array_events");
                        JSONArray attendance = jsonResponse.getJSONArray("myArrayAttendance");
                        Log.e(TAG, "events to string " + events.toString());
                        Log.e(TAG, "events to length " + events.length());

//                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
//                    dbHelper.insertOrUpdateEvents(events, attendance);
//                    sharedEditor.putLong(Constant.LAST_SYNC_TIME,(System.currentTimeMillis()));

                        if(events.length() > 0){
                            EventBus.getDefault().post(new GetEventsWebJobCompletedEvent(Constant.SUCCESS, events, attendance));
                        }else{
                            EventBus.getDefault().post(new GetEventsWebJobCompletedEvent(Constant.SUCCESS, null, null));

                        }

                    } catch (Exception e) {
                        Log.i(TAG, "error at converting to JsonArray " + e.getMessage().toString());
                        EventBus.getDefault().post(new GetEventsWebJobCompletedEvent(Constant.ERROR, null, null));
                    }
                }

            }else{
                EventBus.getDefault().post(new GetEventsWebJobCompletedEvent(Constant.ERROR, null, null));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new GetEventsWebJobCompletedEvent(Constant.ERROR, null, null));
        }
    }


    private EventFilterModel retriveSavedFilterObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.EVENT_FILTER_OBJ, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            EventFilterModel profileResponse = gson.fromJson(profileObjectJsonStr, EventFilterModel.class);
            return profileResponse;
        }
        return null;
    }

    private EventFilterModel retriveSavedFilterObjectDefault(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.FILTER_MODEL_DEFAULT_OBJ, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            EventFilterModel profileResponse = gson.fromJson(profileObjectJsonStr, EventFilterModel.class);
            return profileResponse;
        }
        return null;
    }

    private ProfileResponse retriveSavedProfileObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            return profileResponse;
        }
        return null;
    }

    @Override
    protected void onCancel(int cancelReason, Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    private String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
