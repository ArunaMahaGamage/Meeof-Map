package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
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
 * Created by ransikadesilva on 11/27/17.
 */

public class DeleteEventImageWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteEventImageWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/photos";
    private final String token;
    private final String eventId;
    private final String photoId;


    public DeleteEventImageWebJob(String token,String eventId,String photoId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.eventId=eventId;
        this.photoId=photoId;

        Log.d(TAG,"DeleteEventImageWebJob eventId: "+eventId+" photoId: "+photoId);
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

            String url = Constant.BASE_URL + endPoint+"?eventid="+eventId+"&photoid="+photoId;

            RequestBody formBody = new FormBody.Builder()
                    .add("eventid", this.eventId)
                    .add("photoid", this.photoId)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .delete()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "DeleteEventImageWebJob request URL: " + url);
            Log.d(TAG, "DeleteEventImageWebJob response: " + result);

            HttpResponse httpResponse = null;
            try {
                httpResponse=new HttpResponse();

                JSONObject json = new JSONObject(result);
                String status=json.getString("success");
                if(status.equals("true")){
                    httpResponse.setStatus(Constant.SUCCESS);
                }else{
                    httpResponse.setStatus(Constant.ERROR);
                }

                EventBus.getDefault().post(httpResponse);


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpResponse(null,null));
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

