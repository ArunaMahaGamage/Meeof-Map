package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.DeleteNotificationWebJobResponse;
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
 * Created by ransikadesilva on 12/18/17.
 */

public class DeleteOtherNotificationsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteNotisWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/notifications";
    private final String token;
    private final int notificationsId;

    public DeleteOtherNotificationsWebJob(String token, int notificationsId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.notificationsId = notificationsId;
        Log.d(TAG,"Delete Other Notifications ID: "+notificationsId);
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

            String url = Constant.BASE_URL + endPoint+"?id="+notificationsId;

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
                        EventBus.getDefault().post(new DeleteNotificationWebJobResponse(Constant.SUCCESS));
                    }else{
                        EventBus.getDefault().post(new DeleteNotificationWebJobResponse(Constant.ERROR));
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.d(TAG,ex.getMessage());
                    EventBus.getDefault().post(new DeleteNotificationWebJobResponse(Constant.ERROR));
                }


            }else{
                EventBus.getDefault().post(new DeleteNotificationWebJobResponse(Constant.ERROR));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new DeleteNotificationWebJobResponse(Constant.ERROR));
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
