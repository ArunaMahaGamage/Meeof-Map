package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.model.edit_event_dto.AttendeeList;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.private_invitation_dto_model.PrivateInvitationsResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.search_tag_dto.SearchHashTagResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/21/17.
 */

public class GetPrivateInvitationWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetPrivateInvitationWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/user/private/invitation";
    private final String token;


    public GetPrivateInvitationWebJob(String token) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;

        Log.d(TAG, "GetPrivateInvitationWebJob");

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
            Log.d(TAG, "GetPrivateInvitationWebJob");

            String url = Constant.BASE_URL + endPoint;

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            Log.d(TAG,"GetPrivateInvitationWebJob URL: "+url);

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "GetPrivateInvitationWebJob response: " + result);

            PrivateInvitationsResponse privateInvitationsResponse = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                privateInvitationsResponse = mapper.readValue(result, PrivateInvitationsResponse.class);
                Log.d(TAG, "GetPrivateInvitationWebJob: " + privateInvitationsResponse.toString());
                if (privateInvitationsResponse != null) {
                    if (privateInvitationsResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(privateInvitationsResponse);
                    } else {
                        EventBus.getDefault().post(privateInvitationsResponse);
                    }
                } else {
                    EventBus.getDefault().post(privateInvitationsResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG,"GetPrivateInvitationWebJob Exception"+ex.getMessage());
                EventBus.getDefault().post(privateInvitationsResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "GetPrivateInvitationWebJob Exception " + e);
            EventBus.getDefault().post(new PrivateInvitationsResponse());
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

