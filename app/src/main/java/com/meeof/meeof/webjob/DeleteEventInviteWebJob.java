package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.EventInviteDeleteWebJobCompleted;
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
 * Created by ransikadesilva on 10/6/17.
 */

public class DeleteEventInviteWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteEventInviteWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/invite";
    private final String token;
    private final int eventId;
    private final int userId;
//    user_id
//    event_id
//    api/events/invite


    public DeleteEventInviteWebJob(String token, int eventId, int userId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.eventId = eventId;
        this.userId = userId;

        Log.d(TAG,"DeleteEventInviteWebJob eventId: "+this.eventId);
        Log.d(TAG,"DeleteEventInviteWebJob userId: "+this.userId);


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

            String url = Constant.BASE_URL + endPoint+"?user_id="+this.userId+"&event_id="+this.eventId;

            RequestBody formBody = new FormBody.Builder()
                    .add("user_id", String.valueOf(userId))
                    .add("event_id", String.valueOf(eventId))
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .delete()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "DeleteEventInviteWebJob response: " + result);

            EventInviteDeleteWebJobCompleted eventInviteDeleteWebJobCompleted = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                eventInviteDeleteWebJobCompleted = mapper.readValue(result, EventInviteDeleteWebJobCompleted.class);
                Log.d(TAG, "profileResponse: " + eventInviteDeleteWebJobCompleted.toString());

                Log.d(TAG, eventInviteDeleteWebJobCompleted.toString());

                if (eventInviteDeleteWebJobCompleted != null) {
                    if (eventInviteDeleteWebJobCompleted.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(eventInviteDeleteWebJobCompleted);///
                    } else {
                        EventBus.getDefault().post(eventInviteDeleteWebJobCompleted);
                    }
                } else {
                    EventBus.getDefault().post(eventInviteDeleteWebJobCompleted);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(eventInviteDeleteWebJobCompleted);
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
