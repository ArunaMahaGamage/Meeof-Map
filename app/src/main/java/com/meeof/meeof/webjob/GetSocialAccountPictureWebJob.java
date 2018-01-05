package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class GetSocialAccountPictureWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetSocialAccountPictureWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/profile/image/";
    private final String token;
    private final String socialAccount;


    public GetSocialAccountPictureWebJob(String token, String socialAccount) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.socialAccount = socialAccount;

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

            String url = Constant.BASE_URL + endPoint + this.socialAccount;

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            HttpResponseData httpResponseData = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                httpResponseData = objectMapper.readValue(result, HttpResponseData.class);


                if (httpResponseData != null) {
                    if (httpResponseData.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(httpResponseData);///
                    } else {
                        EventBus.getDefault().post(httpResponseData);
                    }
                } else {
                    EventBus.getDefault().post(httpResponseData);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                EventBus.getDefault().post(httpResponseData);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpResponseData(null, null));
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