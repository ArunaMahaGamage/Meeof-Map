package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.GooglePlaceIdForLatLongResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/12/17.
 */

public class GetPlaceIdForLatLongWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetPlaceIdForLatLongWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private final double lat;
    private final double longi;


    public GetPlaceIdForLatLongWebJob(double lat, double longi) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.lat = lat;
        this.longi = longi;
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
            Log.d(TAG, "GetPlaceIdWebJob");

            String latStr = String.valueOf(lat);
            String longiStr = String.valueOf(longi);


            String url = Constant.GOOGLE_MAPS + "latlng=" + latStr + "," + longiStr + "&" + "key=" + Constant.GOOGLE_PLACES_KEY;
            Log.d(TAG, "GetPlaceIdWebJob url " + url);

            RequestBody reqbody = RequestBody.create(null, new byte[0]);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            try {

                if (result != null) {
                    JSONObject jsonObj = new JSONObject(result);

                    if (jsonObj.getString("status").equalsIgnoreCase(Constant.OK)) {

                        JSONArray resultsArr = jsonObj.getJSONArray("results");

                        EventBus.getDefault().post(new GooglePlaceIdForLatLongResponse(Constant.SUCCESS, resultsArr));
                    } else {
                        EventBus.getDefault().post(new GooglePlaceIdForLatLongResponse(Constant.ERROR, null));
                    }
                } else {
                    EventBus.getDefault().post(new GooglePlaceIdForLatLongResponse(Constant.ERROR, null));
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(new GooglePlaceIdForLatLongResponse(Constant.ERROR, null));
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new GooglePlaceIdForLatLongResponse(Constant.ERROR, null));
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
