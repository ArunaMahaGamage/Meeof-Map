package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpLikeUnlikeResponse;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
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
 * Created by ransikadesilva on 11/15/17.
 */

public class PostLikeUpdateWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostLikeEventAsFriendWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates/like";
    private final String token;
    private final int updateId;


    public PostLikeUpdateWebJob(String token, int updateId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.updateId = updateId;

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

            String url = Constant.BASE_URL + endPoint;

            RequestBody formBody = new FormBody.Builder()
                    .add("update_id", String.valueOf(this.updateId))
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "update item like response: " + result);

            HttpResponseLikeUnlike httpResponseForAddFriend = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponseForAddFriend = mapper.readValue(result, HttpResponseLikeUnlike.class);

//            Log.d(TAG, cropImageResponse.toString());

                if (httpResponseForAddFriend != null) {
                    if (httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(httpResponseForAddFriend);///
                    } else {
                        EventBus.getDefault().post(httpResponseForAddFriend);
                    }
                } else {
                    EventBus.getDefault().post(httpResponseForAddFriend);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponseForAddFriend);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpLikeUnlikeResponse(null,0));
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
