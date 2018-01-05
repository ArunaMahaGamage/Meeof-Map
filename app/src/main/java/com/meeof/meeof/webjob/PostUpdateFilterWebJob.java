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
import com.meeof.meeof.model.UpdateFilterModel;
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
 * Created by ransikadesilva on 12/18/17.
 */

public class PostUpdateFilterWebJob extends Job {
    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostUpdateFilterWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/filters/updates/me";
    private final String token;
    private final UpdateFilterModel updateFilterModel;
    protected SharedPreferences sharedPreferences;


    public PostUpdateFilterWebJob(String token, UpdateFilterModel updateFilterModel) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        Log.i(TAG, "PostUpdateFilterWebJob 1");
        this.updateFilterModel = updateFilterModel;
        Log.i(TAG, "PostUpdateFilterWebJob 2");
        Log.i(TAG, "updateFilterModel get matrix: " + updateFilterModel.getMatrix());
        Log.i(TAG, "PostUpdateFilterWebJob 3");
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

        Log.wtf(TAG, "PostUpdateFilterWebJob 4");
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
            Log.wtf(TAG,"PostUpdateFilterWebJob location: "+location);

            Log.wtf(TAG, "PostUpdateFilterWebJob 5");
            Log.wtf(TAG, "updatefilterModel.getHideolderupdates() :" + updateFilterModel.getHideolderupdates());
            Log.wtf(TAG, "updatefilterModel.getFriends() :" + updateFilterModel.getFriends());
            Log.wtf(TAG, "updatefilterModel.getLocation() :" + updateFilterModel.getLocation());
            Log.wtf(TAG, "updatefilterModel.getMasterfilter() :" + updateFilterModel.getMasterfilter());
            Log.wtf(TAG, "updatefilterModel.getAcceptabledistance() :" + updateFilterModel.getAcceptabledistance());
            Log.wtf(TAG, "updatefilterModel.getSortbyproximity() :" + updateFilterModel.getSortbyproximity());
            Log.wtf(TAG, "updatefilterModel.getHideolderupdates() :" + updateFilterModel.getHideolderupdates());
            Log.wtf(TAG, "updatefilterModel.getRememberfilter() :" + updateFilterModel.getRememberfilter());
            Log.wtf(TAG, "updatefilterModel.getNiceaddress() :" + updateFilterModel.getNiceaddress());
            Log.wtf(TAG, "updatefilterModel.getMatrix() :" + updateFilterModel.getMatrix());
            Log.wtf(TAG, "updatefilterModel.getLng() :" + updateFilterModel.getLng());
            Log.wtf(TAG, "updatefilterModel.getLat() :" + updateFilterModel.getLat());
            Log.wtf(TAG, "updatefilterModel.getStopexecution() :" + updateFilterModel.getStopexecution());
            Log.wtf(TAG, "updatefilterModel.getOffset() :" + updateFilterModel.getOffset());
            Log.wtf(TAG, "updatefilterModel.getLimit() :" + updateFilterModel.getLimit());

            Log.wtf(TAG, "PostUpdateFilterWebJob 55");

            RequestBody formBody = new FormBody.Builder()
                    .add("location", location)
                    .add("hideolderupdates", updateFilterModel.getHideolderupdates())
                    .add("friends", updateFilterModel.getFriends())
                    .add("masterfilter", updateFilterModel.getMasterfilter())
                    .add("acceptabledistance", updateFilterModel.getAcceptabledistance())
                    .add("sortbyproximity", updateFilterModel.getSortbyproximity())
                    .add("hideoutsidefriends", (updateFilterModel.getHideoutsidefriends()))
                    .add("rememberfilter", updateFilterModel.getRememberfilter())
                    .add("niceaddress", updateFilterModel.getNiceaddress())
                    .add("matrix", updateFilterModel.getMatrix())
                    .add("lng", updateFilterModel.getLng())
                    .add("lat", updateFilterModel.getLat())
                    .add("stopexecution", updateFilterModel.getStopexecution())
                    .add("offset", updateFilterModel.getOffset())
                    .add("limit", updateFilterModel.getLimit())
                    .build();


            Log.wtf(TAG, "PostUpdateFilterWebJob 6");
            Log.wtf(TAG, "PostUpdateFilterWebJob 6 token: " + token);

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept", "application/json")
                    .post(formBody)
                    .url(url)
                    .build();

            Log.i(TAG, "PostUpdateFilterWebJob 7");

            Log.i(TAG, "PostUpdateFilterWebJob Request: " + bodyToString(request));
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.i(TAG, "PostUpdateFilterWebJob response: " + result);
            Log.i(TAG, "PostUpdateFilterWebJob response: " + result);

            Log.i(TAG, "PostUpdateFilterWebJob 8");

            HttpResponse httpResponse = null;

            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponse = mapper.readValue(result, HttpResponse.class);

                Log.wtf(TAG, "PostUpdateFilterWebJob 9");
                Log.d(TAG, httpResponse.toString());

                if (httpResponse != null) {
                    if(httpResponse.getStatus() != null && httpResponse.getStatus().equalsIgnoreCase(Constant.SUCCESS)){
                        EventBus.getDefault().post(httpResponse);///
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
            EventBus.getDefault().post(new HttpResponse());
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
