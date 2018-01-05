package com.meeof.meeof.webjob;

import android.text.TextUtils;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.event_invites.raw_emails_dto.RawEmailsResponse;
import com.meeof.meeof.model.geo_dto.GeoResponse;
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

public class PostGeoInfoWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostGeoInfoWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/profile/geo";
    private final String token;
    private final AddressPlaceModel addressPlaceModel;


    public PostGeoInfoWebJob(String token, AddressPlaceModel addressPlaceModel) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.addressPlaceModel = addressPlaceModel;

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

            String url = Constant.BASE_URL + endPoint;

            RequestBody formBody = new FormBody.Builder()
                    .add("lat", addressPlaceModel.getLat())
                    .add("lng",  addressPlaceModel.getLng())
                    .add("place_id",  addressPlaceModel.getPlaceID())
                    .add("address",  addressPlaceModel.getAddniceaddress())
                    .build();

//            http://localhost:8000/api/events/invite/external?rawemails=vishva@gmail.com,aa@&eventid=70
            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            GeoResponse geoResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                geoResponse = mapper.readValue(result, GeoResponse.class);


                if (geoResponse != null) {
                    if (geoResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(geoResponse);///
                    } else {
                        EventBus.getDefault().post(geoResponse);
                    }
                } else {
                    EventBus.getDefault().post(geoResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(geoResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new GeoResponse(null, null));
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
