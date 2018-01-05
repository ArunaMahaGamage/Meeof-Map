package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ransikadesilva on 10/12/17.
 */

public class EventsSyncJob extends Job {

    public static final int PRIORITY = 1;
    private final int id;
    private static final String TAG = EventsSyncJob.class.getSimpleName();
    private static final AtomicInteger jobCounter = new AtomicInteger(0);

    private String events;
    private String attendance;


    public EventsSyncJob(String events, String attendance) {
        super(new Params(PRIORITY).requireNetwork().persist());
        id = jobCounter.incrementAndGet();
        this.events = events;
        this.attendance = attendance;
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

            DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
            Log.e(TAG, "EventsSyncJob123456 ");

            if(events != null){
                Log.d(TAG, "EventsSyncJob 1: " + events.toString());
                Log.d(TAG, "EventsSyncJob 2: " + attendance.toString());

                JSONArray eventsJson = new JSONArray(events);
                JSONArray attendanceJson = new JSONArray(attendance);
                dbHelper.insertOrUpdateEvents(eventsJson, attendanceJson);
            }else{

                Log.d(TAG, "EventsSyncJob deleteAllEvents:");
                dbHelper.deleteAllEvents();
            }

            EventBus.getDefault().post(new EventsSyncJobCompleted(Constant.SUCCESS));

        } catch (Exception e) {
            Log.e(TAG, "EventsSyncJob Exception " + e);
            EventBus.getDefault().post(new EventsSyncJobCompleted(Constant.ERROR));
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
