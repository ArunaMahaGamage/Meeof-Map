package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Created by ransikadesilva on 10/7/17.
 */

public class PostEventDetailsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostEventDetailsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events";
    private final String token;
    private final PostEventModel postEventModel;


    public PostEventDetailsWebJob(String token, PostEventModel postEventModel) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.postEventModel = postEventModel;

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
            Log.d(TAG, "PostEventModel: "+this.postEventModel.toString());

            String url = Constant.BASE_URL + endPoint;
//            Route::put('profile/update','country (alpha code),email,fullname,addniceaddress,placeID,placeName,lng,lat,gender,matrix,acceptabledistance');

            RequestBody formBody = new FormBody.Builder()
                    .addEncoded("eventid", postEventModel.getEventid() + "")
                    .addEncoded("audience", postEventModel.getAudience() + "")
                    .addEncoded("title", postEventModel.getTitle() + "")
                    .addEncoded("activity", String.valueOf(postEventModel.getActivity()))
                    .addEncoded("datestartdate", postEventModel.getDatestartdate())
                    .addEncoded("dateenddate", postEventModel.getDateenddate())
                    .addEncoded("datestartFtime", postEventModel.getDatestartFtime())
                    .addEncoded("dateendtime", postEventModel.getDateendtime())
                    .addEncoded("lat", postEventModel.getLat() + "")
                    .addEncoded("lng", postEventModel.getLng() + "")
                    .addEncoded("placeID", postEventModel.getPlaceID())
                    .addEncoded("placeName", postEventModel.getPlaceName())
                    .addEncoded("addniceaddress", postEventModel.getAddniceaddress())
                    .addEncoded("detailedaddress", postEventModel.getDetailedaddress())
                    .addEncoded("description", postEventModel.getDescription())
                    .addEncoded("maxnumbers", postEventModel.getMaxnumbers())
                    .addEncoded("hidelocation", postEventModel.getHidelocation().equals("1") ? "1" : "0")
                    .addEncoded("approvaltojoin", postEventModel.getApprovaltojoin().equals("1") ? "1" : "0")
                    .addEncoded("informparticipants", postEventModel.getInformparticipants()!=null &&
                            postEventModel.getInformparticipants().equals("on") ? "on" : "off")
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Log.d(TAG, "Request Okhttp: " + bodyToString(request));

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            CreateEventResponse createEventResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                createEventResponse = mapper.readValue(result, CreateEventResponse.class);


                Log.d(TAG, createEventResponse.toString());

                if (createEventResponse != null) {
                    if (createEventResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(createEventResponse);///
                    } else {
                        EventBus.getDefault().post(createEventResponse);
                    }
                } else {
                    EventBus.getDefault().post(createEventResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, "Exception: " + ex.getMessage());
                EventBus.getDefault().post(createEventResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new CreateEventResponse(null,null,null));
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