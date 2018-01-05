package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.UpdateFilterModel;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.updates_all_dto.UpdatesAllResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 11/9/17.
 */

public class GetUpdatesWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetUpdatesWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates/public";
    private final String token;


    public GetUpdatesWebJob(String token) {
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
            Log.d(TAG, "Getting updates");

            //String url = Constant.BASE_URL + endPoint;
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.BASE_URL + endPoint).newBuilder();
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();


            boolean isValid = true;

            SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();

            double currentLat = sharedPreferences.getFloat(Constant.lastKnownLatitudeService,0);
            double currentLng =sharedPreferences.getFloat(Constant.lastKnownLongitudeService, 0);

            UpdateFilterModel updateFilterModel = retriveSavedFilterObject(sharedPreferences);
            UpdateFilterModel updateFilterModelDefault = Helper.getUpdateFilterDefaultModel(currentLat,currentLng);
            ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);

            double lat = sharedPreferences.getFloat(Constant.CURRENT_LATITUDE, 0);
            double lon = sharedPreferences.getFloat(Constant.CURRENT_LONGITUDE, 0);

            Log.d(TAG,"LAT 1: "+lat+" LONG 1: "+lon);
            Log.d(TAG,"LAT 2: "+currentLat+" LONG 2: "+currentLng);


            if (updateFilterModel != null) {
                Log.d(TAG, "updateFilterModel: updateFilterModel != null " + updateFilterModel.toString());

                if (sharedPreferences.getBoolean(Constant.HOME, false)) {
                    Log.d(TAG, "Profile Response: " + profileResponse.toString());

                    urlBuilder.addQueryParameter("location", "home");
                    urlBuilder.addQueryParameter("niceaddress", profileResponse.getData().getAddress());
                    urlBuilder.addQueryParameter("lng", profileResponse.getData().getLongitude() + "");
                    urlBuilder.addQueryParameter("lat", profileResponse.getData().getLatitude() + "");

                } else if (sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false)) {
                    Log.d(TAG, "Profile Response: " + profileResponse.toString());
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", updateFilterModel.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", lon != 0 ? String.valueOf(lon) : updateFilterModel.getLng());
                    urlBuilder.addQueryParameter("lat", lat != 0 ? String.valueOf(lat) : updateFilterModel.getLat());

                } else {
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", updateFilterModel.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", updateFilterModel.getLng());
                    urlBuilder.addQueryParameter("lat", updateFilterModel.getLat());
                }

                urlBuilder.addQueryParameter("masterfilter", updateFilterModel.getMasterfilter());
                urlBuilder.addQueryParameter("acceptabledistance", updateFilterModel.getAcceptabledistance());
                urlBuilder.addQueryParameter("sortbyproximity", updateFilterModel.getSortbyproximity());
                urlBuilder.addQueryParameter("hideoutsidefriends", updateFilterModel.getHideoutsidefriends());
                urlBuilder.addQueryParameter("matrix", updateFilterModel.getMatrix());
                urlBuilder.addQueryParameter("stopexecution", updateFilterModel.getStopexecution());
                urlBuilder.addQueryParameter("offset", updateFilterModel.getOffset());
                urlBuilder.addQueryParameter("limit", updateFilterModel.getLimit());
                urlBuilder.addQueryParameter("rememberfilter", updateFilterModel.getRememberfilter());
                urlBuilder.addQueryParameter("hideolderupdates", updateFilterModel.getHideolderupdates());
                urlBuilder.addQueryParameter("friends", updateFilterModel.getFriends());


            } else if (updateFilterModelDefault != null) {

                Log.d(TAG, "EventFilterModel: eventFilterModelDefault != null " + updateFilterModelDefault.toString());

                if (sharedPreferences.getBoolean(Constant.HOME, false)) {
                    urlBuilder.addQueryParameter("location", "home");
                    urlBuilder.addQueryParameter("niceaddress", profileResponse.getData().getAddress());
                    urlBuilder.addQueryParameter("lng", profileResponse.getData().getLongitude() + "");
                    urlBuilder.addQueryParameter("lat", profileResponse.getData().getLatitude() + "");

                } else if (sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false)) {

                    Log.d(TAG, "Profile Response: " + profileResponse.toString());
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", updateFilterModelDefault.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", lon != 0 ? String.valueOf(lon) : updateFilterModelDefault.getLng());
                    urlBuilder.addQueryParameter("lat", lat != 0 ? String.valueOf(lat) : updateFilterModelDefault.getLat());

                } else {
                    urlBuilder.addQueryParameter("location", "current");
                    urlBuilder.addQueryParameter("niceaddress", updateFilterModelDefault.getNiceaddress());
                    urlBuilder.addQueryParameter("lng", updateFilterModelDefault.getLng());
                    urlBuilder.addQueryParameter("lat", updateFilterModelDefault.getLat());
                }

                urlBuilder.addQueryParameter("masterfilter", updateFilterModelDefault.getMasterfilter());
                urlBuilder.addQueryParameter("acceptabledistance", updateFilterModelDefault.getAcceptabledistance());
                urlBuilder.addQueryParameter("sortbyproximity", updateFilterModelDefault.getSortbyproximity());
                urlBuilder.addQueryParameter("hideoutsidefriends", updateFilterModelDefault.getHideoutsidefriends());
                urlBuilder.addQueryParameter("matrix", updateFilterModelDefault.getMatrix());
                urlBuilder.addQueryParameter("stopexecution", updateFilterModelDefault.getStopexecution());
                urlBuilder.addQueryParameter("offset", updateFilterModelDefault.getOffset());
                urlBuilder.addQueryParameter("limit", updateFilterModelDefault.getLimit());
                urlBuilder.addQueryParameter("rememberfilter", updateFilterModelDefault.getRememberfilter());
                urlBuilder.addQueryParameter("hideolderupdates", updateFilterModelDefault.getHideolderupdates());
                urlBuilder.addQueryParameter("friends", updateFilterModelDefault.getFriends());

            } else {
                Log.d(TAG, "EventFilterModel: else ");
                isValid = false;
            }


//            urlBuilder.addQueryParameter("hideolderupdates", "false");
//            urlBuilder.addQueryParameter("friends", "");
//            urlBuilder.addQueryParameter("location", "home");
//            urlBuilder.addQueryParameter("masterfilter", "0");
//            urlBuilder.addQueryParameter("acceptabledistance", "100.0");
//            urlBuilder.addQueryParameter("sortbyproximity", "0");
//            urlBuilder.addQueryParameter("hideoutsidefriends", "1");
//            urlBuilder.addQueryParameter("rememberfilter", "0");
//            urlBuilder.addQueryParameter("niceaddress", "Outram%20Road%2C%20Singapore%20169608");
//            urlBuilder.addQueryParameter("matrix", "KM");
//            urlBuilder.addQueryParameter("lng", "103.848649");
//            urlBuilder.addQueryParameter("lat", "1.300139");
//            urlBuilder.addQueryParameter("stopexecution", "0");
//            urlBuilder.addQueryParameter("offset", "0");
//            urlBuilder.addQueryParameter("limit", "20");
//            urlBuilder.addQueryParameter("rememberfilter", "0");


            if(isValid){
                Request request = new Request.Builder()
                        .addHeader("Authorization", token)
                        .addHeader("Accept", "application/json")
                        .url(urlBuilder.build().toString())
                        .build();

                Log.d(TAG,"GetUpdatesWebJob url "+urlBuilder.build().toString());


                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d(TAG, "getUpdates response: " + result);


                UpdatesAllResponse updatesAllResponse = null;

                try {

                    ObjectMapper mapper = new ObjectMapper();
                    updatesAllResponse = mapper.readValue(result,UpdatesAllResponse.class);

                    Log.d(TAG, "update response: " + updatesAllResponse);
                    Log.d(TAG, "update map: " + updatesAllResponse.getStatus());

                    Log.d(TAG, updatesAllResponse.toString());
                    EventBus.getDefault().post(updatesAllResponse);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d(TAG, "Updates Mapping Error: "+ex.getMessage());
                    EventBus.getDefault().post(updatesAllResponse);
                }
            }else{
                EventBus.getDefault().post(new UpdatesAllResponse());
            }




        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new UpdatesAllResponse());
        }


    }

    @Override
    protected void onCancel(int cancelReason, Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    private UpdateFilterModel retriveSavedFilterObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.UPDATE_FILTER_OBJ, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            UpdateFilterModel profileResponse = gson.fromJson(profileObjectJsonStr, UpdateFilterModel.class);
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
}
