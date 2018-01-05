package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.Event_join_request2_dto.EventJoinRequest;
import com.meeof.meeof.model.event_join_request.EventJoinRequestsResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/11/17.
 */

public class GetEventJoinRequestsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetEventJoinRequestsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/host/requests";
    private final String token;


    public GetEventJoinRequestsWebJob(String token) {
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

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetEventJoinRequestsWebJob response: " + result);

            EventJoinRequest eventJoinRequest = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                eventJoinRequest = mapper.readValue(result, EventJoinRequest.class);
                Log.d(TAG, "profileResponse: " + eventJoinRequest.toString());


                Log.d(TAG, eventJoinRequest.toString());

                if (eventJoinRequest != null) {
                    if (eventJoinRequest.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(eventJoinRequest);///
                    } else {
                        EventBus.getDefault().post(eventJoinRequest);
                    }
                } else {
                    EventBus.getDefault().post(eventJoinRequest);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(eventJoinRequest);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new EventJoinRequestsResponse(null,null,null));
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
