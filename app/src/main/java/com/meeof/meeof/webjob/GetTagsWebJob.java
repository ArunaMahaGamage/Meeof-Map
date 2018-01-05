package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.search_tag_dto.SearchHashTagResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/19/17.
 */

public class GetTagsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetTagsWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates/tags";
    private final String token;
    private final String searchTags;


    public GetTagsWebJob(String token,String searchTags) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.searchTags=searchTags;
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
            Log.d(TAG, "GetTagsWebJob");

            String url = Constant.BASE_URL + endPoint;

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url+"?searchtags="+this.searchTags)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "GetTagsWebJob response: " + result);

            SearchHashTagResponse searchHashTagResponse = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                searchHashTagResponse = mapper.readValue(result, SearchHashTagResponse.class);
                Log.d(TAG, "GetTagsWebJob: " + searchHashTagResponse.toString());
                if (searchHashTagResponse != null) {
                    if (searchHashTagResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(searchHashTagResponse);
                    } else {
                        EventBus.getDefault().post(searchHashTagResponse);
                    }
                } else {
                    EventBus.getDefault().post(searchHashTagResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(searchHashTagResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new ProfileResponse());
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

