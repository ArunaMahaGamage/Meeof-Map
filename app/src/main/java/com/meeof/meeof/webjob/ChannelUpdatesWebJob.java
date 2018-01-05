package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.model.ChannelUpdatesMainModel;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dharmesh on 12/18/2017.
 */

public class ChannelUpdatesWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetPromotionsWebJov";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/channels/updates";
    private final String token;
    int limits,offset,entityid;
    public ChannelUpdatesWebJob(int limits,int offset,int entityid,String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.limits=limits;
        this.offset=offset;
        this.entityid=entityid;
    }
//    public GetPromotionsWebJob(int UserId,String token) {
//        super(new Params(PRIORITY).requireNetwork().persist());
//        id = jobCounter.incrementAndGet();
//        this.token = token;
//        this.UserId=UserId;
//
//    }

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
            String url;
            url = Constant.BASE_URL + endPoint + "?limit=" + limits + "&offset=" + offset + "&entityID=" +entityid;
//            if (UserId!=0) {
//                url = Constant.BASE_URL + endPoint;
//            }
//            else
//            {
//                url = Constant.BASE_URL + endPoint + "?user_id=" + UserId;
//            }


//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            ChannelUpdatesMainModel channelUpdatesMainModel = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                channelUpdatesMainModel = mapper.readValue(result, ChannelUpdatesMainModel.class);
                Log.d(TAG, "profileResponse: " + channelUpdatesMainModel.toString());

                if (channelUpdatesMainModel != null) {
                    if (channelUpdatesMainModel.isSuccess()) {
                        EventBus.getDefault().post(channelUpdatesMainModel);///
                        saveObjectToSharedPref(sharedEditor, channelUpdatesMainModel, Constant.USER_PROFILE_OBJECT);

                    } else {
                        EventBus.getDefault().post(channelUpdatesMainModel);
                    }
                } else {
                    EventBus.getDefault().post(channelUpdatesMainModel);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(channelUpdatesMainModel);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new ChannelUpdatesMainModel());
        }


    }

    private void saveObjectToSharedPref(SharedPreferences.Editor sharedEditor, Object object, String objectSaveName) {
        try {
            Gson gson = new Gson();
            String objectJson = gson.toJson(object);
            sharedEditor.putString(objectSaveName, objectJson);
            sharedEditor.apply();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCancel(int cancelReason, Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}



