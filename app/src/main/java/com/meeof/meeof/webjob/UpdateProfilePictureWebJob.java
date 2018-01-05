package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.UpdateProfilePictureResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/5/17.
 */

public class UpdateProfilePictureWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = UpdateProfilePictureWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/profile/image?photourl=";
    private final String token;
    private final String photoUrl;
    private Boolean isDeleteImage;


    public UpdateProfilePictureWebJob(String token, String photoUrl) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.photoUrl = photoUrl;

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

            String url = null;
            if (this.photoUrl.equals("")) {
                url = Constant.BASE_URL + endPoint;
                this.isDeleteImage = true;
            } else if (this.photoUrl.length() > 1) {
                url = Constant.BASE_URL + endPoint + photoUrl;
                this.isDeleteImage = false;
            }

            Log.wtf(TAG, "URL:: " + url);
//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Log.wtf(TAG, "Request:: " + request.toString());

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "response: " + result);

            UpdateProfilePictureResponse updateProfilePictureResponse = null;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                updateProfilePictureResponse = objectMapper.readValue(result, UpdateProfilePictureResponse.class);
                String status = updateProfilePictureResponse.getStatus();
                updateProfilePictureResponse.setStatus(status + "_" + isDeleteImage.toString());

                if (updateProfilePictureResponse != null) {
                    if (updateProfilePictureResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(updateProfilePictureResponse);///
                    } else {
                        EventBus.getDefault().post(updateProfilePictureResponse);
                    }
                } else {
                    EventBus.getDefault().post(updateProfilePictureResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(null);
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