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

public class ChangePasswordWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = ChangePasswordWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/profile/change_password";
    private final String token;
    private final String oldPassword;
    private final String newPassword;


    public ChangePasswordWebJob(String token, String oldPassword, String newPassword) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
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

            RequestBody formBody = new FormBody.Builder()
                    .add("current_password", oldPassword)
                    .add("password", newPassword)
                    .add("password_confirmation", newPassword)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .put(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            HttpResponse httpResponseChangePassword = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponseChangePassword = mapper.readValue(result, HttpResponse.class);
                Log.d(TAG, "profileResponse: " + httpResponseChangePassword.toString());


            Log.d(TAG, httpResponseChangePassword.toString());

            if (httpResponseChangePassword != null) {
                if (httpResponseChangePassword.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(httpResponseChangePassword);///
                } else {
                    EventBus.getDefault().post(httpResponseChangePassword);
                }
            } else {
                EventBus.getDefault().post(httpResponseChangePassword);
            }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponseChangePassword);
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
