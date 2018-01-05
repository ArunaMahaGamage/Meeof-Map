package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.RetrievePotentialFriendsWebJobResponse;
import com.meeof.meeof.model.potential_friends.HttpPotentialFriendsResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/12/17.
 */

public class RetrievePotentialFriendsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetCountriesListWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/friends/search/potential";
    private final String token;

    public RetrievePotentialFriendsWebJob(String token) {
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

            String url = Constant.BASE_URL + endPoint;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "RetrievePotentialFriendsWebJob response: " + result);

            HttpPotentialFriendsResponse potentialFriendsResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                potentialFriendsResponse = mapper.readValue(result, HttpPotentialFriendsResponse.class);

                Log.d(TAG, potentialFriendsResponse.toString());

                if (potentialFriendsResponse != null) {
                    if (potentialFriendsResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(new RetrievePotentialFriendsWebJobResponse(Constant.SUCCESS, potentialFriendsResponse));
                    } else {
                        EventBus.getDefault().post(new RetrievePotentialFriendsWebJobResponse(Constant.ERROR, null));
                    }
                } else {
                    EventBus.getDefault().post(new RetrievePotentialFriendsWebJobResponse(Constant.ERROR, null));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(new RetrievePotentialFriendsWebJobResponse(Constant.ERROR, null));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new RetrievePotentialFriendsWebJobResponse(Constant.ERROR, null));
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
