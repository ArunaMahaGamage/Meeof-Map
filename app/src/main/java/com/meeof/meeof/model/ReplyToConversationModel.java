package com.meeof.meeof.model;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.CancelEventWebJob;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;

/**
 * Created by Dharmesh on 11/23/2017.
 */

public class ReplyToConversationModel extends Job
{
    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = CancelEventWebJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);
    private static final String endPoint = "api/messages";
    private final String token;
    int friendsId;
    String Message;
    public ReplyToConversationModel(String token, int friendsId, String Message) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.token = token;
        this.friendsId = friendsId;
        this.Message=Message;
    }
    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        if (id != jobCounter.get()) {

            Log.d(TAG, "onRun id != jobCounter.get()");
            // looks like other fetch jobs has been added after me. no reason to
            // keep fetching
            // many times, cancel me, let the other one fetch tweets.
            return;
        }
        try {
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();

            String url = Constant.BASE_URL + endPoint;

            RequestBody formBody = new FormBody.Builder()
                    .add( "group_id", String.valueOf(friendsId))
                    .add("message",Message)
                    .build();

            Request request = new Request.Builder()
                    .addHeader("Authorization", token)
                    .post(formBody)
                    .url(url)
                    .build();

            Log.d(TAG, "Request Okhttp: " + bodyToString(request));

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            Log.d(TAG, "response: " + result);

            if(result != null){

                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    int GroupId=obj.getInt("group_id");
                    if(status != null && status.equalsIgnoreCase(Constant.SUCCESS)){
                        EventBus.getDefault().post(new AddNewConversationModel(Constant.SUCCESS,GroupId));
                    }else{
                        EventBus.getDefault().post(new AddNewConversationModel(Constant.ERROR));
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.d(TAG,ex.getMessage());
                    EventBus.getDefault().post(new AddNewConversationModel(Constant.ERROR));
                }

            }else{
                EventBus.getDefault().post(new AddNewConversationModel(Constant.ERROR));
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception " + e);
            EventBus.getDefault().post(new AddNewConversationModel(Constant.ERROR));
        }
    }

    @Override
    protected void onCancel(int cancelReason, Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

    private String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
