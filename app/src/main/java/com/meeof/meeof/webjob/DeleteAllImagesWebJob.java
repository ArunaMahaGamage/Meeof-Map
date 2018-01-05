package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.edit_event_dto.Photos;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 11/28/17.
 */

public class DeleteAllImagesWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = "DeleteEventImageWebJob";
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/photos";
    private final String token;
    private final String eventId;
    private final List<Photos> photos;
    private boolean success;


    public DeleteAllImagesWebJob(String token, String eventId, List<Photos> photos) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.eventId=eventId;
        this.photos=photos;
        success=true;

        Log.d(TAG,"DeleteEventImageWebJob eventId: "+eventId+" photoId: "+photos.toString());
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
            for(Photos photo:this.photos){
                String photoId=photo.getMedia_id()+"";

                OkHttpClient client = new OkHttpClient();
                Log.d(TAG, "SaveUserData");

                String url = Constant.BASE_URL + endPoint+"?eventid="+eventId+"&photoid="+photoId;

                RequestBody formBody = new FormBody.Builder()
                        .add("eventid", this.eventId)
                        .add("photoid", photoId)
                        .build();

                Request request = new Request.Builder()
                        .addHeader("Authorization", token)
                        .delete()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.d(TAG, "DeleteAllEventImageWebJob request URL: " + url);
                Log.d(TAG, "DeleteAllEventImageWebJob response: " + result);

                JSONObject json = new JSONObject(result);
                String status=json.getString("success");
                if(!status.equals("true")){
                    success=false;
                    break;
                }
            }

            HttpResponseData httpResponse = null;
            try {
                httpResponse=new HttpResponseData();
                if(success){
                    httpResponse.setStatus(Constant.SUCCESS);
                }else {
                    httpResponse.setStatus(Constant.ERROR);
                }

                EventBus.getDefault().post(httpResponse);


            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(httpResponse);
            }



        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new HttpResponse(null,null));
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

