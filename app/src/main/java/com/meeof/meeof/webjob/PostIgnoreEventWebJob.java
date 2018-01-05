package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/6/17.
 */

public class PostIgnoreEventWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostIgnoreEventWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/me/ignore";
    private final String token;
    private final int eventId;


    public PostIgnoreEventWebJob(String token, int eventId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.eventId = eventId;

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

            RequestBody formBody = new FormBody.Builder()
                    .add("event_id", String.valueOf(this.eventId))
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            HttpResponseData httpResponseForAddFriend = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponseForAddFriend = mapper.readValue(result, HttpResponseData.class);

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
            EventBus.getDefault().post(new HttpResponseData(null, null));
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
