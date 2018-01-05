package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.LoginResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.message;

/**
 * Created by Anuja Ranwalage on 10/3/17.
 */

public class SocialRegistrationWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = SocialRegistrationWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/register";
    private final String token;
    private final String grantType;
    private final String oneSignalId;

    public SocialRegistrationWebJob(String token, String grantType, String oneSignalId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.grantType = grantType;
        this.oneSignalId = oneSignalId;
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
            Log.d(TAG, "register data");
            Log.d(TAG, "register one signal id : " + oneSignalId);
            Log.d(TAG, "register token : " + token);

            String url = Constant.BASE_URL + endPoint;

            Log.wtf(TAG,"grant_type: "+grantType+"\ntoken: "+this.token+"\none_signal_id: "+oneSignalId+"\n");

            RequestBody formBody = new FormBody.Builder()
                    .add("grant_type", grantType)
                    .add("token", this.token)
                    .add("one_signal_id", oneSignalId)
                    .add("platform", "mobile")
                    .build();

            Request request = new Request.Builder()
                    .addHeader("accept","application/json")
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "formBody: " + formBody.toString());
            Log.wtf(TAG, "response: " + result);

            JSONObject resultJSON = new JSONObject(result);

            String tokenType = resultJSON.get("token_type").toString();
            String accessToken = resultJSON.get("access_token").toString();
            String refreshToken = resultJSON.get("refresh_token").toString();


            if (tokenType != null && accessToken != null && refreshToken != null) {

                Log.wtf(TAG, "SaveUserData result " + accessToken + " " + message);
                if (!accessToken.isEmpty() && !refreshToken.isEmpty() && !tokenType.isEmpty()) {
                    EventBus.getDefault().post(new LoginResponse(Constant.SUCCESS, null, tokenType, accessToken, refreshToken,grantType));///
                } else {
                    EventBus.getDefault().post(new LoginResponse(Constant.ERROR, null,null, null, null,grantType));
                }
            } else {
                EventBus.getDefault().post(new LoginResponse(Constant.ERROR, null,null, null, null,grantType));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new LoginResponse(Constant.ERROR, null,null, null, null,grantType));
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