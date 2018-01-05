package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Anuja Ranwalage on 10/16/17.
 */

public class ForgotPasswordWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = ForgotPasswordWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/forget_password";
    //private final String token;
    private final String email;



    public ForgotPasswordWebJob(String token, String email) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
       // this.token = token;
        this.email = email;
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

           // Log.d(TAG,"token-"+token);
            Log.d(TAG,"email-"+email);


            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "SaveUserData");

            String url = Constant.BASE_URL + endPoint;

            RequestBody formBody = new FormBody.Builder()
                    .add("email", this.email)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "ForgotPasswordWebJob response: " + result);

            HttpResponse httpResponseForgotPassword = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponseForgotPassword = mapper.readValue(result, HttpResponse.class);
                Log.d(TAG, "profileResponse: " + httpResponseForgotPassword.toString());


            Log.d(TAG, httpResponseForgotPassword.toString());

            if (httpResponseForgotPassword != null) {
                if (httpResponseForgotPassword.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(httpResponseForgotPassword);///
                } else {
                    EventBus.getDefault().post(httpResponseForgotPassword);
                }
            } else {
                EventBus.getDefault().post(httpResponseForgotPassword);
            }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponseForgotPassword);
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
