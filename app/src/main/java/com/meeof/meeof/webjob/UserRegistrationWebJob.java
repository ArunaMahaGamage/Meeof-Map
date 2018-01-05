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

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class UserRegistrationWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = UserRegistrationWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/register";
    private final String name;
    private final String email;
    private final String password;

    public UserRegistrationWebJob(String name,String email, String password) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.name = name;
        this.email = email;
        this.password = password;

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

            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type","password")
                    .add("name", this.name)
                    .add("email", this.email)
                    .add("password", this.password)
                    .add("confirmed_password", this.password)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG,"response: "+result);

            JSONObject resultJSON = new JSONObject(result);

            String status = resultJSON.get("status").toString();
            String message = resultJSON.get("message").toString();


            if (status != null && message != null) {

                Log.d(TAG, "SaveUserData result " + status + " " + message);
                if (!status.isEmpty() && !message.isEmpty()) {
                    EventBus.getDefault().post(new HttpResponse(status,message));///
                } else {
                    EventBus.getDefault().post(new HttpResponse(null,null));
                }
            } else {
                EventBus.getDefault().post(new HttpResponse(null,null));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpResponse(null,null));
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