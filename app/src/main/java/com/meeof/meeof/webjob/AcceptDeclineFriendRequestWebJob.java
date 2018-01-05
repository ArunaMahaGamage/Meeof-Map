package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpAcceptFriendResponse;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/12/17.
 */

public class AcceptDeclineFriendRequestWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetCountriesListWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/friends/requests";
    private final String token;
    private final boolean isAccept;
    private final int userId;


    public AcceptDeclineFriendRequestWebJob(String token, int userId, boolean isAccept) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.userId = userId;
        this.isAccept = isAccept;

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

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            RequestBody reqbody = RequestBody.create(null, new byte[0]);

            String sendUrl = url + "?reciever_id=" + userId + "&answer=" + (isAccept ? "y" : "n");

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(sendUrl)
                    .post(reqbody)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            HttpAcceptFriendResponse acceptDecResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                acceptDecResponse = mapper.readValue(result, HttpAcceptFriendResponse.class);

                Log.d(TAG, acceptDecResponse.toString());

                if (acceptDecResponse != null) {
                    if (acceptDecResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(acceptDecResponse);///
                    } else {
                        EventBus.getDefault().post(acceptDecResponse);
                    }
                } else {
                    EventBus.getDefault().post(acceptDecResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(acceptDecResponse);
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
