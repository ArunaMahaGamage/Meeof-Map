package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.meeof_search_users_dto.MeeofSearchResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/5/17.
 */

public class getMeeofSearchResultWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = getMeeofSearchResultWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/meeof/search?keyword=";
    private final String token;
    private final String search;


    public getMeeofSearchResultWebJob(String token, String search) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.search=search;

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
            Log.d(TAG, "getMeeofSearchResultWebJob");

            String url = Constant.BASE_URL + endPoint+search;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "getMeeofSearchResultWebJob response: " + result);

            MeeofSearchResponse meeofSearchResponse = null;
            try {

                ObjectMapper mapper = new ObjectMapper();
                meeofSearchResponse = mapper.readValue(result, MeeofSearchResponse.class);
                Log.d(TAG, "getMeeofSearchResultWebJob : " + meeofSearchResponse.toString());


                Log.d(TAG, meeofSearchResponse.toString());

                if (meeofSearchResponse != null) {
                    if (meeofSearchResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(meeofSearchResponse);///
                    } else {
                        EventBus.getDefault().post(meeofSearchResponse);
                    }
                } else {
                    EventBus.getDefault().post(meeofSearchResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(meeofSearchResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new MeeofSearchResponse());
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
