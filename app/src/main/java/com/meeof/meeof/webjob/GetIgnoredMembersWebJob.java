package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.event_comment_dto.EventCommentGet;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.ignored_members_dto.IgnoredMembersResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/1/17.
 */

public class GetIgnoredMembersWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetIgnoredMembersWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/friends/ignored";
    private final String token;


    public GetIgnoredMembersWebJob(String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        Log.d(TAG,"GetIgnoredMembersWebJob Token: "+token);

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
            Log.d(TAG, "GetIgnoredMembersWebJob");

            String url = Constant.BASE_URL + endPoint;
            Log.d(TAG, "GetIgnoredMembersWebJob URL : " + url);

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", this.token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetIgnoredMembersWebJob response: " + result);

            IgnoredMembersResponse ignoredMembersResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                ignoredMembersResponse = mapper.readValue(result, IgnoredMembersResponse.class);
                Log.d(TAG, "profileResponse: " + ignoredMembersResponse.toString());


                Log.d(TAG, ignoredMembersResponse.toString());

                if (ignoredMembersResponse != null) {
                    EventBus.getDefault().post(ignoredMembersResponse);
                } else {
                    EventBus.getDefault().post(new IgnoredMembersResponse());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(new IgnoredMembersResponse());
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, ex.getMessage());
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
