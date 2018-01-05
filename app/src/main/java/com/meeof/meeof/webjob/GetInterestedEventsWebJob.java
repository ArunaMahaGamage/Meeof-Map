package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.interested_ignored_event_reponse_dto.InterestedIgnoredEventResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/5/17.
 */

public class GetInterestedEventsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetInterestedEventsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/user/events/interested";
    private final String token;


    public GetInterestedEventsWebJob(String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        Log.d(TAG,"GetInterestedEventsWebJob Token: "+token);

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
            Log.d(TAG, "GetInterestedEventsWebJob");

            String url = Constant.BASE_URL + endPoint;
            Log.d(TAG, "GetInterestedEventsWebJob URL : " + url);

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", this.token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetInterestedEventsWebJob response: " + result);


            InterestedIgnoredEventResponse interestedIgnoredEventResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                interestedIgnoredEventResponse = mapper.readValue(result, InterestedIgnoredEventResponse.class);
                Log.d(TAG, "interestedIgnoredEventResponse: " + interestedIgnoredEventResponse.toString());


                Log.d(TAG, interestedIgnoredEventResponse.toString());

                if (interestedIgnoredEventResponse != null) {
                    if (interestedIgnoredEventResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(interestedIgnoredEventResponse);///
                    } else {
                        EventBus.getDefault().post(interestedIgnoredEventResponse);
                    }
                } else {
                    EventBus.getDefault().post(interestedIgnoredEventResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(interestedIgnoredEventResponse);
            }
        }catch (Exception ex) {
            Log.e(TAG, "Exception " + ex);
            EventBus.getDefault().post(new InterestedIgnoredEventResponse());
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
