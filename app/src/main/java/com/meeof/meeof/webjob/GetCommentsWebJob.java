package com.meeof.meeof.webjob;

import android.content.SharedPreferences;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.event_comment_dto.EventCommentGet;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class GetCommentsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "GetCommentsWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static  String endPoint = "";
    private final String token;
    private final String eventId;



    public GetCommentsWebJob(String token,String eventId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.eventId=eventId;
        endPoint="/api/events/"+eventId+"/comments";
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

//            RequestBody formBody = new FormBody.Builder()
//                    .add("email", email)
//                    .build();

            SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetCommentsWebJob response: " + result);

            EventCommentGet eventCommentGet= null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                eventCommentGet = mapper.readValue(result, EventCommentGet.class);
                Log.d(TAG, "GetCommentsWebJob eventCommentGet: "+eventCommentGet.toString());


            Log.d(TAG, eventCommentGet.toString());

            if (eventCommentGet!=null) {
                if (eventCommentGet.getStatus().equals(Constant.SUCCESS)) {
                    EventBus.getDefault().post(eventCommentGet);///


                } else {
                    EventBus.getDefault().post(eventCommentGet);
                }
            } else {
                EventBus.getDefault().post(eventCommentGet);
            }

            }catch (Exception ex){
                ex.printStackTrace();
                Log.d(TAG,ex.getMessage());
                EventBus.getDefault().post(eventCommentGet);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new EventCommentGet(null, null));
        }


    }

    private void saveObjectToSharedPref(SharedPreferences.Editor sharedEditor, Object object, String objectSaveName) {
        try{
            Gson gson = new Gson();
            String objectJson = gson.toJson(object);
            sharedEditor.putString(objectSaveName, objectJson);
            sharedEditor.apply();

        }catch (Exception ex){
            ex.printStackTrace();
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

