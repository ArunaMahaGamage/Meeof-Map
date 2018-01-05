package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.DeleteEventWebJobResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/6/17.
 */

public class DeleteEventWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteEventWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/action/delete";
    private final String token;
    private final int eventId;

    public DeleteEventWebJob(String token, int eventId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.eventId = eventId;
        Log.d(TAG,"Delete Event ID: "+eventId);
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

            String url = Constant.BASE_URL + endPoint+"?event_id="+eventId;

            RequestBody formBody = new FormBody.Builder()
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .delete()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "DeleteEventWebJob response: " + result);
            Log.d(TAG, "token : " + token);

            if(result != null){

                try {

                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");

                    if(status != null && status.equalsIgnoreCase(Constant.SUCCESS)){
                        EventBus.getDefault().post(new DeleteEventWebJobResponse(Constant.SUCCESS));
                    }else{
                        EventBus.getDefault().post(new DeleteEventWebJobResponse(Constant.ERROR));
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.d(TAG,ex.getMessage());
                    EventBus.getDefault().post(new DeleteEventWebJobResponse(Constant.ERROR));
                }


            }else{
                EventBus.getDefault().post(new DeleteEventWebJobResponse(Constant.ERROR));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new DeleteEventWebJobResponse(Constant.ERROR));
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
