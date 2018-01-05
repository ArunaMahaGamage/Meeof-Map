package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.DeleteNotificationWebJobResponse;
import com.meeof.meeof.model.DeleteUpdateWebJobResponse;
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
 * Created by ransikadesilva on 12/21/17.
 */

public class PostDeleteUpdateWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "PostDeleteUpdateWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates";
    private final String token;
    private final int zone_id;

    public PostDeleteUpdateWebJob(String token, int zone_id) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.zone_id = zone_id;
        Log.d(TAG,"Delete Event ID: "+zone_id);
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

            String url = Constant.BASE_URL + endPoint+"?zone_id="+zone_id;

            RequestBody formBody = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .delete()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "DeleteEventWebJob response: " + result);
            Log.d(TAG, "token : " + token);

            DeleteUpdateWebJobResponse acceptDecResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                acceptDecResponse = mapper.readValue(result, DeleteUpdateWebJobResponse.class);
                Log.d(TAG, "acceptDecResponse response: " + acceptDecResponse);
                Log.d(TAG, acceptDecResponse.toString());

                if (acceptDecResponse != null) {
                    if (acceptDecResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(acceptDecResponse);///
                    } else {
                        EventBus.getDefault().post(acceptDecResponse);
                    }
                } else {
                    EventBus.getDefault().post(acceptDecResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(acceptDecResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
             EventBus.getDefault().post(new DeleteUpdateWebJobResponse());
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

