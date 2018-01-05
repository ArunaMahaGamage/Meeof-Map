package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.search_my_friend_dto.SearchMyFriendsQueryResponse;

import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 12/20/17.
 */

public class SearchMyFriendsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = GetSearchMyFriendsInterestsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/friends/search";
    private final String token;
    private final String query;


    public SearchMyFriendsWebJob(String token, String query) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.query = query;

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
                    .url(url + "?keyword="+query+"&toFriends="+null)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            SearchMyFriendsQueryResponse searchMyFriendsQueryResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                searchMyFriendsQueryResponse = mapper.readValue(result, SearchMyFriendsQueryResponse.class);
                Log.d(TAG, "profileResponse: " + searchMyFriendsQueryResponse.toString());


                Log.d(TAG, searchMyFriendsQueryResponse.toString());

                if (searchMyFriendsQueryResponse != null) {
                    if (searchMyFriendsQueryResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(searchMyFriendsQueryResponse);///
                    } else {
                        EventBus.getDefault().post(searchMyFriendsQueryResponse);

                    }
                } else {
                    EventBus.getDefault().post(searchMyFriendsQueryResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(searchMyFriendsQueryResponse);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new SearchMyFriendsQueryResponse(null, null));
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