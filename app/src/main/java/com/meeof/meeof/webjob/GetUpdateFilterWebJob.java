package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.UpdateFilterGetModel;
import com.meeof.meeof.model.event_filter_get_dto.EventFilterGetModel;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/18/17.
 */

public class GetUpdateFilterWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetEventFilterWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/filters/updates/me";
    private final String token;


    public GetUpdateFilterWebJob(String token) {
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
            Log.wtf(TAG, "GetUpdateFilterWebJob url: " + url);
            Log.wtf(TAG, "GetEventFilterWebJob token: " + token);
//
//

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "GetUpdateFilterWebJob response: " + result);

            UpdateFilterGetModel updateFilterGetModel= null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                updateFilterGetModel = mapper.readValue(result, UpdateFilterGetModel.class);
                Log.wtf(TAG, "GetEventFilterWebJob : "+updateFilterGetModel.toString());
                Log.d(TAG, updateFilterGetModel.toString());

                if (updateFilterGetModel!=null) {
                    if (updateFilterGetModel.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(updateFilterGetModel);///
                    } else {
                        EventBus.getDefault().post(new UpdateFilterGetModel());
                    }
                } else {
                    EventBus.getDefault().post(new UpdateFilterGetModel());
                }


            }catch (Exception ex){
                ex.printStackTrace();
                Log.d(TAG,ex.getMessage());
                EventBus.getDefault().post(new UpdateFilterGetModel());
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
