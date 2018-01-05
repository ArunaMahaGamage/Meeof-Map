package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.GetConvertationWebModel;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GetConvertatioWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetMyAllFriendsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/messages/all";
    private final String token;
    private final int GroupId;

    public GetConvertatioWebJob(String token, int GroupId)
    {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.GroupId=GroupId;

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

            String url = Constant.BASE_URL + endPoint + "?group_id=" + GroupId;

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);
            JSONObject jsonObject=new JSONObject(result);
            String status=jsonObject.getString("status");
            if (status.equalsIgnoreCase("success"))
            {
                EventBus.getDefault().post(result);
            }
            else
            {
                EventBus.getDefault().post("something went wrong");
            }
            GetConvertationWebModel convertatioWebJob = null;
            try {
//                ObjectMapper mapper = new ObjectMapper();
//                convertatioWebJob = mapper.readValue(result, GetConvertationWebModel.class);
//                Log.d(TAG, "profileResponse: " + convertatioWebJob.toString());
//
//
//                Log.d(TAG, convertatioWebJob.toString());
//
//                if (convertatioWebJob != null) {
//                    if (convertatioWebJob.getStatus().equals(Constant.SUCCESS)) {
//                        EventBus.getDefault().post(convertatioWebJob);///
//                    } else {
//                        EventBus.getDefault().post(convertatioWebJob);
//                    }
//                } else {
//                    EventBus.getDefault().post(convertatioWebJob);
//                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post("something went wrong");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post("something went wrong");
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
