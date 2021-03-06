package com.meeof.meeof.webjob;

import android.text.TextUtils;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
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
 * Created by ransikadesilva on 10/13/17.
 */

public class PostUserInterestsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostImageDimentionsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/interests";
    private final String token;
    private final String[] checkList;


    public PostUserInterestsWebJob(String token, String[] checkList) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.checkList = checkList;


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

            String joinedCheckList = TextUtils.join(",",checkList);

//            String joinedCheckList = String.join(",", checkList);


            Log.wtf(TAG, "CheckList:: " + joinedCheckList);

            RequestBody formBody = new FormBody.Builder()
                    .add("chk", joinedCheckList)
                    .build();


            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Log.d(TAG, bodyToString(request));

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "response: " + result);

            HttpResponse httpResponseInterests = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponseInterests = mapper.readValue(result, HttpResponse.class);

                Log.d(TAG, httpResponseInterests.toString());

                if (httpResponseInterests != null) {
                    if (httpResponseInterests.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(httpResponseInterests);///
                    } else {
                        EventBus.getDefault().post(httpResponseInterests);
                    }
                } else {
                    EventBus.getDefault().post(httpResponseInterests);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponseInterests);
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
