package com.meeof.meeof.webjob;

import android.util.ArraySet;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.GoogleAutocompletePlace;
import com.meeof.meeof.model.GoogleAutocompleteResponse;
import com.meeof.meeof.model.HttpAcceptFriendResponse;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/12/17.
 */

public class GoogleAutocompleteRequestWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GoogleAutocompleteRequestWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    private String input;
    private String country;
    private String apiKey;


    public GoogleAutocompleteRequestWebJob(String input, String country, String apiKey) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.input = input;
        this.country = country;
        this.apiKey = apiKey;
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

            //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=soph&components=country:sg&key=AIzaSyBIzj1esFBbrqDmsqVZNmkkNO8XLSyZkog
            String url = endPoint + input;

            if(country != null && country.length() > 0){
                url = url + "&components=country:" + country + "&key=" + apiKey;
            }else{
                url = url + "&key=" + apiKey;
            }

            Log.wtf(TAG, "GoogleAutocompleteRequestWebJob:url " + url);

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "GoogleAutocompleteRequestWebJob: " + result);

            JSONObject resultObj = new JSONObject(result);
            String status =  resultObj.getString("status");

            try {

                if(status.equalsIgnoreCase("OK")){

                    JSONArray predrr =  resultObj.getJSONArray("predictions");

                    if(predrr.length() > 0){

                        List<GoogleAutocompletePlace> predictionsList = new ArrayList<>();

                        for(int i = 0 ; i < predrr.length() ; i++){

                            GoogleAutocompletePlace googleAutocompletePlace = new GoogleAutocompletePlace();

                            String placeId = predrr.getJSONObject(i).getString("place_id");
                            String description = predrr.getJSONObject(i).getString("description");
                            String id = predrr.getJSONObject(i).getString("id");

                            googleAutocompletePlace.setId(id);
                            googleAutocompletePlace.setDescription(description);
                            googleAutocompletePlace.setPlace_id(placeId);

                            predictionsList.add(googleAutocompletePlace);
                        }

                        EventBus.getDefault().post(new GoogleAutocompleteResponse(Constant.SUCCESS, predictionsList));

                    }else{
                        EventBus.getDefault().post(new GoogleAutocompleteResponse(Constant.SUCCESS, null));
                    }

                }else if (status.equalsIgnoreCase("INVALID_REQUEST")){
                    EventBus.getDefault().post(new GoogleAutocompleteResponse(Constant.ERROR, new ArrayList<GoogleAutocompletePlace>()));
                }else{
                    EventBus.getDefault().post(new GoogleAutocompleteResponse(Constant.ERROR, null));
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                EventBus.getDefault().post(new GoogleAutocompleteResponse(Constant.ERROR, null));
            }

        } catch (Exception e) {
            Log.wtf(TAG, "Exception " + e);
            EventBus.getDefault().post(new GoogleAutocompleteResponse(null, null));
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
