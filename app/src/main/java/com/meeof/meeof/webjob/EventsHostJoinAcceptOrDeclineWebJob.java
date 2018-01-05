package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.EventsHostJoinAcceptOrDeclineResponse;
import com.meeof.meeof.model.PostEventModel;
import com.meeof.meeof.model.events.CreateEventResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

/**
 * Created by ransikadesilva on 11/28/17.
 */

public class EventsHostJoinAcceptOrDeclineWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostEventDetailsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/host/response";
    private final String token;
    private final String notifyid;
    private final String requestor_id;
    private final String eventid;
    private final String answer;



    public EventsHostJoinAcceptOrDeclineWebJob(String token, String notifyid, String requestor_id, String eventid, String answer) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.notifyid=notifyid;
        this.requestor_id=requestor_id;
        this.eventid=eventid;
        this.answer=answer;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        if (id != jobCounter.get()) {

            Log.d(TAG, "onRun id != jobCounter.get()");
            // looks like other fetch jobs has been added after me. no reason to
            // keep fetching
            // many times, cancel me, let the other one fetch tweets.
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();
            Log.d(TAG, "SaveUserData");


            String url = Constant.BASE_URL + endPoint;
//            Route::put('profile/update','country (alpha code),email,fullname,addniceaddress,placeID,placeName,lng,lat,gender,matrix,acceptabledistance');

            RequestBody formBody = new FormBody.Builder()
                    .addEncoded("notifyid", this.notifyid + "")
                    .addEncoded("requestor_id", this.requestor_id + "")
                    .addEncoded("eventid", this.eventid + "")
                    .addEncoded("answer", this.answer)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Log.d(TAG, "Request Okhttp: " + bodyToString(request));

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "EventsHostJoinAcceptOrDeclineWebJob response: " + result);

            EventsHostJoinAcceptOrDeclineResponse eventsHostJoinAcceptOrDeclineResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                eventsHostJoinAcceptOrDeclineResponse = mapper.readValue(result, EventsHostJoinAcceptOrDeclineResponse.class);


                Log.d(TAG, eventsHostJoinAcceptOrDeclineResponse.toString());

                if (eventsHostJoinAcceptOrDeclineResponse != null) {

                    EventBus.getDefault().post(eventsHostJoinAcceptOrDeclineResponse);

                } else {
                    EventBus.getDefault().post(new EventsHostJoinAcceptOrDeclineResponse());
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "Exception: " + ex.getMessage());
                EventBus.getDefault().post(eventsHostJoinAcceptOrDeclineResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new EventsHostJoinAcceptOrDeclineResponse(null,null));
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
