package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.GetNotificationsWebJobCompletedImportantNotifications;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/14/17.
 */

public class GetOtherNotificationsJob extends Job

    {

        public static final int PRIORITY = 1;
        private final int id;
        private static final String TAG = "GetNotification";
        private static final AtomicInteger jobCounter = new AtomicInteger(0);
        private static final String endPoint = "/api/notifications/flags";
        private final String token;


    public GetOtherNotificationsJob(String token) {
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
            Log.d(TAG, "GetNotificationPreference");

            String url = Constant.BASE_URL + endPoint;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetNotificationPreference response: " + result);

            JSONObject jsonResponse = new JSONObject(result);
            if (jsonResponse != null) {
                try {
                    JSONArray notifications = jsonResponse.getJSONArray("data");
                    //JSONArray attendance = jsonResponse.getJSONArray("myArrayAttendance");
                    Log.e(TAG, "events to string " + notifications.toString());
                    Log.e(TAG, "events to length " + notifications.length());

//                    DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
//                    dbHelper.insertOrUpdateEvents(events, attendance);
//                    sharedEditor.putLong(Constant.LAST_SYNC_TIME,(System.currentTimeMillis()));

                    if(notifications.length() > 0){
                        EventBus.getDefault().post(new GetNotificationsWebJobCompletedImportantNotifications(Constant.SUCCESS, notifications));
                    }else{
                        EventBus.getDefault().post(new GetNotificationsWebJobCompletedImportantNotifications(Constant.SUCCESS, null));

                    }

                } catch (Exception e) {
                    Log.i(TAG, "error at converting to JsonArray " + e.getMessage().toString());
                    EventBus.getDefault().post(new GetNotificationsWebJobCompletedImportantNotifications(Constant.ERROR, null));
                }
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