package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.updates_dto.HttpDeleteFriendResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/8/17.
 */

public class SaveNotificationSettingsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteFriendWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/notifications/settings?";
    private final String token;
    private final String notify_comment;
    private final String notify_like;
    private final String notify_weeklyEmail;

    public SaveNotificationSettingsWebJob(String token, String notify_comment, String notify_like, String notify_weeklyEmail) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.notify_comment = notify_comment;
        this.notify_like= notify_like;
        this.notify_weeklyEmail= notify_weeklyEmail;
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
            Log.d(TAG, "SaveNotificationSettingsWebJob");

            String url = Constant.BASE_URL + endPoint;

            RequestBody formBody = new FormBody.Builder()
                    .add("notify_comment", notify_comment)
                    .add("notify_like", notify_like)
                    .add("notify_weeklyEmail", notify_weeklyEmail)
                    .build();



            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .put(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "SaveNotificationSettingsWebJob response: " + result);

            HttpResponse httpResponse;

            try {

                JSONObject ojb = new JSONObject(result);
                httpResponse = new HttpResponse(ojb.getString("status"),ojb.getString("message"));
                Log.d(TAG, httpResponse.toString());

                if (httpResponse != null) {
                    if (httpResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(httpResponse);
                    } else {
                        EventBus.getDefault().post(null);
                    }
                } else {
                    EventBus.getDefault().post(null);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(null);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(null);
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
