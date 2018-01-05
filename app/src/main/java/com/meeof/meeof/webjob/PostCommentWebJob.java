package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.InterestHttpResponse;
import com.meeof.meeof.model.comment_dto.HttpResponseComments;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class PostCommentWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostCommentWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/comments";
    private final String token;

    private final String evenId;
    private final String comment;


    public PostCommentWebJob(String token, String evenId, String comment) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.evenId=evenId;
        this.comment=comment;


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
                    .add("event_id", this.evenId)
                    .add("comment", this.comment)

                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .addHeader("Accept","application/json")
                    .post(formBody)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            HttpResponseComments httpResponse= null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponse = mapper.readValue(result, HttpResponseComments.class);

//            Log.d(TAG, cropImageResponse.toString());

            if (httpResponse!=null) {
                if (httpResponse.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(httpResponse);///
                } else {
                    EventBus.getDefault().post(httpResponse);
                }
            } else {
                EventBus.getDefault().post(httpResponse);
            }

            }catch (Exception ex){
                ex.printStackTrace();
                Log.d(TAG,ex.getMessage());
                EventBus.getDefault().post(httpResponse);
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

