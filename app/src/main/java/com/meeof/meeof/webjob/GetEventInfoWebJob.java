package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.edit_event_dto.AttendeeList;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anuja Ranwalage on 10/8/2017.
 */

public class GetEventInfoWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetEventInfoWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/events/";
    private final String token;
    private final int eventId;


    public GetEventInfoWebJob(String token, int eventId) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.eventId = eventId;
        this.token = token;

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

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url + eventId)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "GetEventInfoWebJob response: " + result);


            EventInfoResponse eventInfoResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                eventInfoResponse = mapper.readValue(result, EventInfoResponse.class);

                JSONObject jsonObject = new JSONObject(result);
                JSONArray attendeesArray = jsonObject.getJSONArray("data").getJSONObject(0).getJSONArray("attendeeList");
                List<AttendeeList> attendeeListList = eventInfoResponse.getData().get(0).getAttendeeList();

                for(int i=0;i<attendeesArray.length();i++){
                    attendeeListList.get(i).setFriendstatus(attendeesArray.getJSONObject(i).getString("friendstatus"));
                }
                Log.d(TAG, "profileResponse: " + eventInfoResponse.toString());


                Log.d(TAG, "Response toString: " + eventInfoResponse.toString());

                if (eventInfoResponse != null) {
                    if (eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(eventInfoResponse);///
                    } else {
                        EventBus.getDefault().post(eventInfoResponse);
                    }
                } else {
                    EventBus.getDefault().post(eventInfoResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(eventInfoResponse);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new EventInfoResponse());
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

