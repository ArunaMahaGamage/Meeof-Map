package com.meeof.meeof.webjob;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ransikadesilva on 12/14/17.
 */

public class NotificationsSyncJob extends Job {



        public static final int PRIORITY = 1;
        private final int id;
        private static final String TAG = NotificationsSyncJob.class.getSimpleName();
        private static final AtomicInteger jobCounter = new AtomicInteger(0);

        private String notifications;



        public NotificationsSyncJob(String notifications) {
            super(new Params(PRIORITY).requireNetwork().persist());
            id = jobCounter.incrementAndGet();
            this.notifications = notifications;

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
                Log.e(TAG, "NotificationsSyncJob123456 ");

                if(notifications != null){
                    Log.d(TAG, "NotificationsSyncJob 1: " + notifications.toString());
                    //Log.d(TAG, "EventsSyncJob 2: " + attendance.toString());

                    JSONArray eventsJson = new JSONArray(notifications);
                    dbHelper.insertOrUpdateNotification(eventsJson);
                }else{

                    Log.d(TAG, "NotificationsSyncJob deleteAllEvents:");
                    dbHelper.deleteAllNotifications();
                }

                EventBus.getDefault().post(new NotificationsSyncJobCompleted(Constant.SUCCESS));

            } catch (Exception e) {
                Log.e(TAG, "NotificationsSyncJob Exception " + e);
                EventBus.getDefault().post(new NotificationsSyncJobCompleted(Constant.ERROR));
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

