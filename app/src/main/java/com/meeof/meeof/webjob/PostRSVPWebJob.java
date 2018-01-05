package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.comment_dto.HttpResponseComments;
import com.meeof.meeof.model.rsvp_response.RSVPResponse;
import com.meeof.meeof.util.Constant;


import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import okio.Buffer;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class PostRSVPWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostRSVPWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/action/response";
    private final String token;

    private final int evenId;
    private final int rsvp;


    public PostRSVPWebJob(String token, int evenId, int rsvp) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.evenId = evenId;
        this.rsvp = rsvp;


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
            Log.d(TAG, "rsvp: " + rsvp);

            String url = Constant.BASE_URL + endPoint;

            RequestBody formBody = new FormBody.Builder()
                    .addEncoded("eventid", String.valueOf(evenId))
                    .addEncoded("rsvp", String.valueOf(rsvp))
                    .build();

//            RequestBody formBody = RequestBody.create(null, new byte[]{});

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .post(formBody)
                    .url(url)
                    .build();

//            +"?eventid="+String.valueOf(this.evenId)+"&rsvp="+String.valueOf(this.rsvp)
            Log.d(TAG, "Request Body: " + bodyToString(request));

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            RSVPResponse httpResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponse = mapper.readValue(result, RSVPResponse.class);

//            Log.d(TAG, cropImageResponse.toString());

                if (httpResponse != null) {
                    if (httpResponse.getStatus().equals(Constant.SUCCESS)) {
                        httpResponse.setRequest(String.valueOf(rsvp));
                        EventBus.getDefault().post(httpResponse);//

                    } else {
                        EventBus.getDefault().post(httpResponse);
                    }
                } else {
                    EventBus.getDefault().post(httpResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponse);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new RSVPResponse(null, null));
        }


    }

    @Override
    protected void onCancel(int cancelReason, Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }


    private String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}

