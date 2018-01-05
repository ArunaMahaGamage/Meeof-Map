package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.InterestsSearchResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class GetInterestsSearchResultsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetInterestsSearchResultsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/interests/search?search=";
    private final String token;
    private final String searchText;


    public GetInterestsSearchResultsWebJob(String token, String searchText) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.searchText = searchText;
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
            Log.d(TAG, "GetInterestsSearchResultsWebJob");

            String url = Constant.BASE_URL + endPoint + searchText;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            if (result != null) {
                JSONObject resultObj = new JSONObject(result);
                String status = resultObj.getString("status");

                if (status.equalsIgnoreCase("success")) {
                    Log.d(TAG, "response:status " + status);
                    JSONArray dataArr = resultObj.getJSONArray("data");
                    EventBus.getDefault().post(new InterestsSearchResponse(status, dataArr));

                } else {
                    Log.d(TAG, "response:status " + status);
                    EventBus.getDefault().post(new InterestsSearchResponse(status, null));
                }
            } else {
                EventBus.getDefault().post(new InterestsSearchResponse(null, null));
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new InterestsSearchResponse(null, null));
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


