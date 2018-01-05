package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Created by RansikaDeSilva on 11/16/17.
 */

public class UpdateUserLocationTrackingStatusWebJob extends Job{

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = UpdateUserLocationTrackingStatusWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private final String token;
    private final String endPoint = "/api/profile/location/track/status";
    private int status;
//    https://dev2017.meeof.com/api/profile/location/track/status?choice=1

    public UpdateUserLocationTrackingStatusWebJob(String token, Integer status) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.status = status;

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
            Log.d(TAG, "UpdateUserLocationTrackingStatusWebJob1");

            String url = Constant.BASE_URL + endPoint;
//            Route::put('profile/update','country (alpha code),email,fullname,addniceaddress,placeID,placeName,lng,lat,gender,matrix,acceptabledistance');

            Log.d(TAG,"UpdateUserLocationTrackingStatusWebJob2: "+this.status +", URL: "+url+ ", Token: "+token);

            RequestBody formBody = new FormBody.Builder()
                    .add("choice", (String.valueOf(this.status)))
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept","application/json")
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "UpdateUserLocationTrackingStatusWebJob: response1: " + result);

            HttpResponse defaultResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                defaultResponse = mapper.readValue(result, HttpResponse.class);

                Log.d(TAG,"UpdateUserLocationTrackingStatusWebJob: response2: "+defaultResponse.toString());

                if (defaultResponse != null) {
                    if (defaultResponse.getStatus().equals(Constant.SUCCESS)) {
                        Log.d(TAG,"UpdateUserLocationTrackingStatusWebJob: response3: "+defaultResponse.getStatus());
                        Log.d(TAG,"UpdateUserLocationTrackingStatusWebJob: response4: "+defaultResponse.getMessage());
                        EventBus.getDefault().post(defaultResponse);///
                    } else {
                        EventBus.getDefault().post(defaultResponse);
                    }
                } else {
                    EventBus.getDefault().post(defaultResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(defaultResponse);
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
