package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.model.notification_settings_response.GetSettingsNotificationResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/8/17.
 */

public class GetSettingsNotificationPreference extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetSettingsNotification";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/notifications/settings";
    private final String token;


    public GetSettingsNotificationPreference(String token) {
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
            Log.d(TAG, "GetSettingsNotificationPreference");

            String url = Constant.BASE_URL + endPoint;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetSettingsNotificationPreference response: " + result);

            GetSettingsNotificationResponse getSettingsNotificationResponse = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                getSettingsNotificationResponse = mapper.readValue(result, GetSettingsNotificationResponse.class);
                Log.d(TAG, "GetSettingsNotificationPreference: " + getSettingsNotificationResponse.toString());
                if (getSettingsNotificationResponse != null) {
                    if (getSettingsNotificationResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(getSettingsNotificationResponse);///

                    } else {
                        EventBus.getDefault().post(getSettingsNotificationResponse);
                    }
                } else {
                    EventBus.getDefault().post(getSettingsNotificationResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(getSettingsNotificationResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new ProfileResponse());
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

