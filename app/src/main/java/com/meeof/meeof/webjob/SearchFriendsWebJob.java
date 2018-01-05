package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meeof.meeof.model.search_friends_dto.SearchFriendsResponse;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ransikadesilva on 10/6/17.
 */

public class SearchFriendsWebJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = SearchFriendsWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "/api/meeof/search/email";
    private final String token;
    private final String[] emailList;


    public SearchFriendsWebJob(String token, String[] emailList) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.emailList = emailList;

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

            String url1 = "";
            String tempStr = "";
            if (emailList.length > 0) {
                for (int i = 0; i < emailList.length; i++) {
                    if (i == emailList.length - 1) {
                        tempStr += "emails[" + i + "]=" + emailList[i];
                    } else {
                        tempStr += "emails[" + i + "]=" + emailList[i] + "&";
                    }

                }
                url1 = url + "?" + tempStr;
            } else {
                url1 = url + "?emails[]";
            }

            RequestBody formBody = new FormBody.Builder()
                    .add("emails", tempStr)
                    .build();

            Log.wtf(TAG, "Request URL : " + url1);
            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .url(url1)
                    .build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.wtf(TAG, "response: " + result);

            SearchFriendsResponse searchFriendsResponse = null;
            try {
                ObjectMapper mapper = new ObjectMapper();
                searchFriendsResponse = mapper.readValue(result, SearchFriendsResponse.class);

                Log.d(TAG, searchFriendsResponse.toString());

                if (searchFriendsResponse != null) {
                    if (searchFriendsResponse.getStatus().equals(Constant.SUCCESS)) {
                        EventBus.getDefault().post(searchFriendsResponse);///
                    } else {
                        EventBus.getDefault().post(searchFriendsResponse);
                    }
                } else {
                    EventBus.getDefault().post(searchFriendsResponse);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d(TAG, ex.getMessage());
                EventBus.getDefault().post(searchFriendsResponse);
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new SearchFriendsResponse(null,null));
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
