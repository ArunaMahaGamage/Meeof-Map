package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.updates_all_dto.UpdatesAllResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 11/15/17.
 */

public class GetUpdatesLikeWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetUpdatesWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates/like";
    private final String token;


    public GetUpdatesLikeWebJob(String token, int id) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.id=id;

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
            Log.d(TAG, "Getting updates");

            //String url = Constant.BASE_URL + endPoint;
            HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.BASE_URL + endPoint).newBuilder();
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            urlBuilder.addQueryParameter("update_id", id+"");

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(urlBuilder.build().toString())
                    .build();





            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "getUpdates like response: " + result);


            UpdatesAllResponse updatesAllResponse = null;

            try {

                ObjectMapper mapper = new ObjectMapper();
                updatesAllResponse = mapper.readValue(result,UpdatesAllResponse.class);


                Log.d(TAG, updatesAllResponse.toString());
                EventBus.getDefault().post(updatesAllResponse);

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "Updates Mapping Error: "+ex.getMessage());
                EventBus.getDefault().post(updatesAllResponse);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new FriendsAllResponse(null,null,null));
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

