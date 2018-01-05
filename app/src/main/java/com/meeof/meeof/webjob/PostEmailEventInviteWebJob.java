package com.meeof.meeof.webjob;

import android.text.TextUtils;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.event_invites.raw_emails_dto.RawEmailsResponse;
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

public class PostEmailEventInviteWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostEmailEventInviteWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/invite/external";
    private final String token;
    private final String[] emails;
    private final int eventId;


    public PostEmailEventInviteWebJob(String token, String[] emails, int eventId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.emails = emails;
        this.eventId = eventId;

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

            String url = Constant.BASE_URL + endPoint;

            String emailsJoined = TextUtils.join(",", emails);
            RequestBody formBody = new FormBody.Builder()
                    .add("eventid", String.valueOf(eventId))
                    .add("rawemails", emailsJoined)
                    .build();

//            http://localhost:8000/api/events/invite/external?rawemails=vishva@gmail.com,aa@&eventid=70
            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "PostEmailEventInviteWebJob response: " + result);

            RawEmailsResponse rawEmailsResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                rawEmailsResponse = mapper.readValue(result, RawEmailsResponse.class);

//            Log.d(TAG, cropImageResponse.toString());

                if (rawEmailsResponse != null) {
                    if (rawEmailsResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(rawEmailsResponse);///
                    } else {
                        EventBus.getDefault().post(rawEmailsResponse);
                    }
                } else {
                    EventBus.getDefault().post(rawEmailsResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(rawEmailsResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new RawEmailsResponse(null, null));
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
