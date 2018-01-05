package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.updates_dto.HttpDeleteFriendResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/4/17.
 */

public class DeleteIgnoredUserWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteFriendWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/friends/delete/ignored?ignored_id=";
    private final String token;
    private final String friendId;

    public DeleteIgnoredUserWebJob(String token, String friendId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.friendId = friendId;
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

            String url = Constant.BASE_URL + endPoint;


            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url + friendId)
                    .delete()
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "DeleteIgnoredUserWebJob response: " + result);

            HttpDeleteFriendResponse httpResponse;

            try {

                JSONObject ojb = new JSONObject(result);
                httpResponse = new HttpDeleteFriendResponse(ojb.getString("status"));
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

