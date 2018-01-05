package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.event_filter_get_dto.EventFilterGetModel;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class GetEventFilterWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetEventFilterWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/filters/me";
    private final String token;


    public GetEventFilterWebJob(String token) {
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
            Log.wtf(TAG, "GetEventFilterWebJob url: " + url);
            Log.wtf(TAG, "GetEventFilterWebJob token: " + token);
//
//

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "GetEventFilterWebJob response: " + result);

            EventFilterGetModel eventFilterGetModel= null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                eventFilterGetModel = mapper.readValue(result, EventFilterGetModel.class);
                Log.wtf(TAG, "GetEventFilterWebJob : "+eventFilterGetModel.toString());
                Log.d(TAG, eventFilterGetModel.toString());

            if (eventFilterGetModel!=null) {
                if (eventFilterGetModel.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(eventFilterGetModel);///
                } else {
                    EventBus.getDefault().post(new EventFilterGetModel(null,null));
                }
            } else {
                EventBus.getDefault().post(new EventFilterGetModel(null,null));
            }


            }catch (Exception ex){
                ex.printStackTrace();
                Log.d(TAG,ex.getMessage());
                EventBus.getDefault().post(new EventFilterGetModel(null,null));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new EventFilterGetModel(null,null));
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

