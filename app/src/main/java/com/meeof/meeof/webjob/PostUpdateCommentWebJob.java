package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.UpdateCommentReponse;
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
 * Created by ransikadesilva on 12/16/17.
 */

public class PostUpdateCommentWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostUpdateCommentWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/updates/comment";
    private final String token;

    private final String update_id;
    private final String comment;


    public PostUpdateCommentWebJob(String token, String update_id, String comment) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.update_id=update_id;
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
                    .add("update_id", this.update_id)
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
            Log.d(TAG, "PostUpdateCommentWebJob response: " + result);

            UpdateCommentReponse httpResponse= null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                httpResponse = mapper.readValue(result, UpdateCommentReponse.class);

//            Log.d(TAG, cropImageResponse.toString());

                if (httpResponse!=null) {
                    if (httpResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(httpResponse);///
                    } else {
                        EventBus.getDefault().post(httpResponse);
                    }
                } else {
                    EventBus.getDefault().post(new UpdateCommentReponse());
                }

            }catch (Exception ex){
                ex.printStackTrace();
                Log.d(TAG,ex.getMessage());
                EventBus.getDefault().post(new UpdateCommentReponse());
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new UpdateCommentReponse());
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
