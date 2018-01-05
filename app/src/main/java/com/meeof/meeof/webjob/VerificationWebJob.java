package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class VerificationWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = VerificationWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/confirm";
    private final String code;


    public VerificationWebJob(String code) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.code = code;


    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        Log.d(TAG, "onRun start");
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

            Log.wtf(TAG, "Verification Code: " + code);
            Request request = new Request.Builder()
                    .url(url + "?code=" + code)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "response: " + result);

            JSONObject resultJSON = new JSONObject(result);

            String status = resultJSON.get("status").toString();
            String message = resultJSON.get("message").toString();


            if (status != null && message != null) {

                Log.wtf(TAG, "SaveUserData result " + status + " " + message);
                if (!status.isEmpty() && !message.isEmpty()) {
                    EventBus.getDefault().post(new HttpResponse(status, message));///
                } else {
                    EventBus.getDefault().post(new HttpResponse(null, null));
                }
            } else {
                EventBus.getDefault().post(new HttpResponse(null, null));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpResponse(null, null));
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