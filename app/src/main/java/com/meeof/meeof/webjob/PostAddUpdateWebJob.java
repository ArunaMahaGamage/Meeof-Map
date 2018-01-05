package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.PostAddUpdateModel;
import com.meeof.meeof.model.PostEventModel;
import com.meeof.meeof.model.addUpdate.AddUpdateResponse;
import com.meeof.meeof.model.events.CreateEventResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

/**
 * Created by ransikadesilva on 12/21/17.
 */

public class PostAddUpdateWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostAddUpdateWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates";
    private final String token;
    private final PostAddUpdateModel postAddUpdateModel;


    public PostAddUpdateWebJob(String token, PostAddUpdateModel postAddUpdateModel) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.postAddUpdateModel = postAddUpdateModel;

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

        AddUpdateResponse addUpdateResponse = null;
        String url = Constant.BASE_URL + endPoint;
        String response = setDetails(url, postAddUpdateModel, token);
        Log.d(TAG,"Response "+response.toString());
        logLargeString("response" + response.toString());
        try {
            ObjectMapper mapper = new ObjectMapper();
            addUpdateResponse = mapper.readValue(response, AddUpdateResponse.class);


            Log.d(TAG, addUpdateResponse.toString());

            if (addUpdateResponse != null) {
                if (addUpdateResponse.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(addUpdateResponse);///
                } else {
                    EventBus.getDefault().post(addUpdateResponse);
                }
            } else {
                EventBus.getDefault().post(addUpdateResponse);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG, "Exception: " + ex.getMessage());
            EventBus.getDefault().post(addUpdateResponse);
        }


    }

    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str); // continuation
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

    private String setDetails(String url, PostAddUpdateModel postAddUpdateModel, String token) {
        try {

            Log.d(TAG, "SaveUserData");
            Log.d(TAG, "PostEventModel: " + this.postAddUpdateModel.toString());
            MultipartBody.Builder buildernew = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("lat", postAddUpdateModel.getLat() + "")
                    .addFormDataPart("lng", postAddUpdateModel.getLng() + "")
                    .addFormDataPart("placeID", postAddUpdateModel.getPlaceID())
                    .addFormDataPart("placeName", postAddUpdateModel.getPlaceName())
                    .addFormDataPart("addniceaddress", postAddUpdateModel.getAddniceaddress())
                    .addFormDataPart("txtupdate", postAddUpdateModel.getTxtupdate());


            if (postAddUpdateModel.getToFriends().size() > 0) {
                for (int i = 0; i < postAddUpdateModel.getToFriends().size(); i++) {
                    buildernew.addFormDataPart("toFriends[" + i + "]", String.valueOf(postAddUpdateModel.getToFriends().get(i)));
                }
            }
            if (postAddUpdateModel.getTags().size() > 0) {
                for (int i = 0; i < postAddUpdateModel.getTags().size(); i++) {
                    buildernew.addFormDataPart("tags[" + i + "]", String.valueOf(postAddUpdateModel.getTags().get(i)));
                }
            }
            if (postAddUpdateModel.getImages().size() > 0) {
                for (int i = 0; i < postAddUpdateModel.getImages().size(); i++) {
                    buildernew.addFormDataPart("images[" + i + "]", String.valueOf(postAddUpdateModel.getImages().get(i)));
                    Log.d(TAG, "imagePostEventModel: " + postAddUpdateModel.getImages().get(i));
                }
            }
            MultipartBody requestBody = buildernew.build();
            Log.d(TAG, "requestBody: " + requestBody.toString());
            final Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .post(requestBody)
                    .build();


            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            return new String(response.body().string());


        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }
}