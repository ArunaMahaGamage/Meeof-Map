package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.GooglePlaceIdResponse;
import com.meeof.meeof.model.UserInterestResponse;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 11/29/17.
 */

public class GetUserInterestsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetPotentialFriends.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/interests/me";
    private final String token;


    public GetUserInterestsWebJob(String token) {
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
            Log.d(TAG, "SaveUserData");

            String url = Constant.BASE_URL + endPoint;
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "GetUserInterestsWebJob response: " + result);

            UserInterestResponse userInterestResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                userInterestResponse = mapper.readValue(result, UserInterestResponse.class);
                Log.d(TAG, "GetUserInterestsWebJob userInterestResponse: " + userInterestResponse.toString());
                Log.d(TAG, "GetUserInterestsWebJob userInterestResponse status: " + userInterestResponse.getStatus());



                Log.d(TAG, userInterestResponse.toString());

                if (userInterestResponse != null) {
                    if (userInterestResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(userInterestResponse);///
                    } else {
                        EventBus.getDefault().post(userInterestResponse);
                    }
                } else {
                    EventBus.getDefault().post(userInterestResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(userInterestResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "GetUserInterestsWebJob Exception " + e);
            EventBus.getDefault().post(new UserInterestResponse());
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
