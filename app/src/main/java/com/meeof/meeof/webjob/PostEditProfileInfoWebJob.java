package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.EditProfileInfo;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.ProfileSaveResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/7/17.
 */

public class PostEditProfileInfoWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = PostEditProfileInfoWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/profile/update";
    private final String token;
    private final EditProfileInfo editProfileInfo;


    public PostEditProfileInfoWebJob(String token, EditProfileInfo editProfileInfo) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.editProfileInfo = editProfileInfo;

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

            String url = Constant.BASE_URL + endPoint;
//            Route::put('profile/update','country (alpha code),email,fullname,addniceaddress,placeID,placeName,lng,lat,gender,matrix,acceptabledistance');

            Log.wtf(TAG,"Edit profile Info: "+editProfileInfo.toString());

            RequestBody formBody;

            if(editProfileInfo.getPlaceId() != null && editProfileInfo.getPlaceName() != null){

                formBody = new FormBody.Builder()
                        .add("country", editProfileInfo.getCountry())
                        .add("email", editProfileInfo.getEmail())
                        .add("fullname", editProfileInfo.getFullName())
                        .add("addniceaddress", editProfileInfo.getAddress())
                        .add("placeID", editProfileInfo.getPlaceId())
                        .add("placeName", editProfileInfo.getPlaceName())
                        .add("lng", editProfileInfo.getLng())
                        .add("lat", editProfileInfo.getLat())
                        .add("gender", String.valueOf(editProfileInfo.getGender()))
                        .add("matrix", editProfileInfo.getMatrix())
                        .add("acceptabledistance", editProfileInfo.getAcceptedDistance())
                        .build();
            }else{

                formBody = new FormBody.Builder()
                        .add("country", editProfileInfo.getCountry())
                        .add("email", editProfileInfo.getEmail())
                        .add("fullname", editProfileInfo.getFullName())
                        .add("addniceaddress", editProfileInfo.getAddress())
                        .add("lng", editProfileInfo.getLng())
                        .add("lat", editProfileInfo.getLat())
                        .add("gender", String.valueOf(editProfileInfo.getGender()))
                        .add("matrix", editProfileInfo.getMatrix())
                        .add("acceptabledistance", editProfileInfo.getAcceptedDistance())
                        .build();
            }

            Log.wtf(TAG,"Edit profile Info: 2");

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .put(formBody)
                    .url(url)
                    .build();

            Log.wtf(TAG,"Edit profile Info: 3");

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG,"Edit profile Info: 4");

            Log.wtf(TAG, "response: " + result);

            ProfileSaveResponse editProfileInfo = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                editProfileInfo = mapper.readValue(result, ProfileSaveResponse.class);


                Log.d(TAG, editProfileInfo.toString());

                if (editProfileInfo != null) {
                    if (editProfileInfo.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(editProfileInfo);///
                    } else {
                        EventBus.getDefault().post(editProfileInfo);
                    }
                } else {
                    EventBus.getDefault().post(editProfileInfo);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(editProfileInfo);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new ProfileSaveResponse(null, null));
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