package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.ChannelMainModel;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dharmesh on 12/4/2017.
 */

public class ChannelNearMeWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = ChannelNearMeWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/channels/near";
    private final String token;
    double latitude,longitude;
    public ChannelNearMeWebJob(double latitude,double longitude,String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.latitude=latitude;
        this.longitude=longitude;
        this.token = token;
    }

    @Override
    public void onAdded()
    {

    }

    @Override
    public void onRun() throws Throwable {
        if (id != jobCounter.get())
        {
            Log.d(TAG, "onRun id != jobCounter.get()");
            // looks like other fetch jobs has been added after me. no reason to
            // keep fetching
            // many times, cancel me, let the other one fetch tweets.
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "SaveUserData");

            String url = Constant.BASE_URL + endPoint + "?lat=" + latitude + "&lng=" +longitude + "&distance=100&limit=100&offset=0";

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            ChannelMainModel channelMainModel = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                channelMainModel = mapper.readValue(result, ChannelMainModel.class);
                Log.d(TAG, "profileResponse: " + channelMainModel.toString());


                Log.d(TAG, channelMainModel.toString());

                if (channelMainModel != null) {
                    if (channelMainModel.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(channelMainModel);///
                    } else {
                        EventBus.getDefault().post(channelMainModel);
                    }
                } else {
                    EventBus.getDefault().post(channelMainModel);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(channelMainModel);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new ChannelMainModel());
        }


    }

    @Override
    protected void onCancel(int cancelReason, Throwable throwable)
    {
        Log.e("Throws","Throws");
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}


