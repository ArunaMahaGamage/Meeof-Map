package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.GetEventsByUserModel;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Dharmesh on 12/1/2017.
 */

public class GetUserWiseEventsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetMyAllFriendsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/user/events";
    private final String token;
    int UserId;

    public GetUserWiseEventsWebJob(int UserId, String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.UserId=UserId;

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

            String url = Constant.BASE_URL + endPoint + "?user_id=" + UserId;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            GetEventsByUserModel getEventsByUserModel = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                getEventsByUserModel = mapper.readValue(result, GetEventsByUserModel.class);
                Log.d(TAG, "profileResponse: " + getEventsByUserModel.toString());


                Log.d(TAG, getEventsByUserModel.toString());

                if (getEventsByUserModel != null) {
                    if (getEventsByUserModel.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(getEventsByUserModel);///
                    } else {
                        EventBus.getDefault().post(getEventsByUserModel);
                    }
                } else {
                    EventBus.getDefault().post(getEventsByUserModel);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(getEventsByUserModel);
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

