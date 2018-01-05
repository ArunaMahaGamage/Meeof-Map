package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.model.EventFilterResponse;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class PostEventFilterWebJob extends Job {
    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostEventFilterWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/filters/me";
    private final String token;
    private final EventFilterModel eventFilterModel;
    protected SharedPreferences sharedPreferences;


    public PostEventFilterWebJob(String token, EventFilterModel eventFilterModel) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        Log.i(TAG, "PostEventFilterWebJob 1");
        this.eventFilterModel = eventFilterModel;
        Log.i(TAG, "PostEventFilterWebJob 2");
        Log.i(TAG, "eventFilterModel get matrix: " + eventFilterModel.getMatrix());
        Log.i(TAG, "PostEventFilterWebJob 3");
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if (id != jobCounter.get()) {


            // looks like other fetch jobs has been added after me. no reason to
            // keep fetching
            // many times, cancel me, let the other one fetch tweets.
            return;
        }

        Log.wtf(TAG, "PostEventFilterWebJob 4");
        try {
            OkHttpClient client = new OkHttpClient();
            Log.d(TAG, "SaveUserData");


            String url = Constant.BASE_URL + endPoint;
//            Route::put('
//profile/update','country (alpha code),email,fullname,addniceaddress,placeID,placeName,lng,lat,gender,matrix,acceptabledistance');

            SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
            String location = "";
            if (sharedPreferences.getBoolean(Constant.HOME, false)) {
                location = "home";
            } else {
                location = "current";
            }

            Log.wtf(TAG, "PostEventFilterWebJob 5");
            Log.wtf(TAG, "eventFilterModel.getMasterfilter() :" + eventFilterModel.getMasterfilter());
            Log.wtf(TAG, "eventFilterModel.getAcceptabledistance() :" + eventFilterModel.getAcceptabledistance());
            Log.wtf(TAG, "eventFilterModel.getSortbyproximity() :" + eventFilterModel.getSortbyproximity());
            Log.wtf(TAG, "eventFilterModel.getHideoutsidefriends() :" + eventFilterModel.getHideoutsidefriends());
            Log.wtf(TAG, "eventFilterModel.getRememberfilter() :" + eventFilterModel.getRememberfilter());
            Log.wtf(TAG, "eventFilterModel.getNiceaddress() :" + eventFilterModel.getNiceaddress());
            Log.wtf(TAG, "eventFilterModel.getMatrix() :" + eventFilterModel.getMatrix());
            Log.wtf(TAG, "eventFilterModel.getLng() :" + eventFilterModel.getLng());
            Log.wtf(TAG, "eventFilterModel.getLat() :" + eventFilterModel.getLat());
            Log.wtf(TAG, "eventFilterModel.getStopexecution() :" + eventFilterModel.getStopexecution());
            Log.wtf(TAG, "eventFilterModel.getOffset() :" + eventFilterModel.getOffset());
            Log.wtf(TAG, "eventFilterModel.getRememberfilter() :" + eventFilterModel.getRememberfilter());
            Log.wtf(TAG, "eventFilterModel.getLimit() :" + eventFilterModel.getLimit());
            Log.wtf(TAG, "eventFilterModel.getAcceptabledistance() :" + eventFilterModel.getAcceptabledistance());

            RequestBody formBody = new FormBody.Builder()
                    .add("location", location)
                    .add("masterfilter", eventFilterModel.getMasterfilter())
                    .add("acceptabledistance", eventFilterModel.getAcceptabledistance())
                    .add("sortbyproximity", eventFilterModel.getSortbyproximity())
                    .add("hideoutsidefriends", eventFilterModel.getHideoutsidefriends())
                    .add("rememberfilter", eventFilterModel.getRememberfilter())
                    .add("niceaddress", (eventFilterModel.getNiceaddress()))
                    .add("matrix", eventFilterModel.getMatrix())
                    .add("lng", eventFilterModel.getLng())
                    .add("lat", eventFilterModel.getLat())
                    .add("stopexecution", eventFilterModel.getStopexecution())
                    .add("offset", eventFilterModel.getOffset())
                    .add("limit", eventFilterModel.getLimit())
                    .add("rememberfilter", eventFilterModel.getRememberfilter())
                    .build();


            Log.wtf(TAG, "PostEventFilterWebJob 6");
            Log.wtf(TAG, "PostEventFilterWebJob 6 token: " + token);

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .post(formBody)
                    .url(url)
                    .build();

            Log.i(TAG, "PostEventFilterWebJob 7");

            Log.i(TAG, "PostEventFilterWebJob Request: " + bodyToString(request));
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.i(TAG, "PostEventFilterWebJob response: " + result);
            Log.i(TAG, "PostEventFilterWebJob response: " + result);

            Log.i(TAG, "PostEventFilterWebJob 8");

            EventFilterResponse eventFilterResponse = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                eventFilterResponse = mapper.readValue(result, EventFilterResponse.class);

                Log.wtf(TAG, "PostEventFilterWebJob 9");
                Log.d(TAG, eventFilterResponse.toString());

                if (eventFilterResponse != null) {
                    if(eventFilterResponse.getStatus() != null && eventFilterResponse.getStatus().equalsIgnoreCase(Constant.SUCCESS)){
                        EventBus.getDefault().post(eventFilterResponse);///
                    } else {
                        EventBus.getDefault().post(eventFilterResponse);
                    }
                } else {
                    EventBus.getDefault().post(eventFilterResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(eventFilterResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new EventFilterResponse(null, null, null));
        }
//

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


    @Override
    protected void onCancel(int cancelReason, Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}

