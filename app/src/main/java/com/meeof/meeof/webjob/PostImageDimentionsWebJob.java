package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseCropImage;
import com.meeof.meeof.model.ImageDimentionModel;
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

public class PostImageDimentionsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostImageDimentionsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/profile/image/crop";
    private final String token;
    private final ImageDimentionModel imageDimentionModel;


    public PostImageDimentionsWebJob(String token, ImageDimentionModel imageDimentionModel) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.imageDimentionModel = imageDimentionModel;

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
                    .add("width", imageDimentionModel.getWidth())
                    .add("height", imageDimentionModel.getHeight())
                    .add("scalex", imageDimentionModel.getScalex())
                    .add("scaley", imageDimentionModel.getScaley())
                    .add("imgurl", imageDimentionModel.getImgurl())
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            HttpResponseCropImage cropImageResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                cropImageResponse = mapper.readValue(result, HttpResponseCropImage.class);


                Log.d(TAG, cropImageResponse.toString());

                if (cropImageResponse != null) {
                    if (cropImageResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(cropImageResponse);///
                    } else {
                        EventBus.getDefault().post(cropImageResponse);
                    }
                } else {
                    EventBus.getDefault().post(cropImageResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpResponseCropImage(null, null));
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
