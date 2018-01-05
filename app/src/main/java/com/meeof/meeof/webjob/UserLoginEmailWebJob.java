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
 * Created by ransikadesilva on 10/3/17.
 */

public class UserLoginEmailWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = UserLoginEmailWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/login";
    private final String email;
    private final String password;

    public UserLoginEmailWebJob(String email, String password) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.email = email;
        this.password = password;

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
                    .add("email", this.email)
                    .add("password", this.password)
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.i(TAG, "response: " + result);

            JSONObject resultJSON = new JSONObject(result);

            if(!resultJSON.has("error")){
                String tokenType = resultJSON.get("token_type").toString();
                String accessToken = resultJSON.get("access_token").toString();
                String refreshToken = resultJSON.get("refresh_token").toString();

                if (tokenType != null && accessToken != null && refreshToken != null) {

                    Log.i(TAG, "SaveUserData result " + accessToken + " " + message);
                    if (!accessToken.isEmpty() && !refreshToken.isEmpty() && !tokenType.isEmpty()) {
                        EventBus.getDefault().post(new LoginResponse(Constant.SUCCESS, null, tokenType, accessToken, refreshToken,null));///
                    } else {
                        EventBus.getDefault().post(new LoginResponse(Constant.ERROR, null, null,null, null,null));
                    }
                } else {
                    EventBus.getDefault().post(new LoginResponse(Constant.ERROR, null, null,null, null,null));
                }
            }else{
                if(resultJSON.has("message")){
                    EventBus.getDefault().post(new LoginResponse(resultJSON.getString("error"), resultJSON.getString("message"), null,null, null,null));
                }else{
                    EventBus.getDefault().post(new LoginResponse(resultJSON.getString("error"), null, null,null, null,null));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new LoginResponse(Constant.ERROR, null, null,null, null,null));
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