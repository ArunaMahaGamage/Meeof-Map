package com.meeof.meeof;

import android.app.Application;
import android.content.Context;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.crashlytics.android.Crashlytics;
import com.meeof.meeof.onesignal.NotificationOpenedHandler;
import com.meeof.meeof.onesignal.NotificationReceivedHandler;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ransikadesilva on 10/3/17.
 */

public class MeeofApplication extends Application {

    private static MeeofApplication meeofApplication;
    private JobManager jobManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        meeofApplication = this;
        configureJobManager();

        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new NotificationReceivedHandler())
                .setNotificationOpenedHandler(new NotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();



        Picasso picasso = new Picasso.Builder(getApplicationContext())
                .loggingEnabled(true)
                .build();

        Picasso.setSingletonInstance(picasso);

    }

    public static MeeofApplication getInstance() {

        return meeofApplication;
    }



    private void configureJobManager() {

        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {


                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {

                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {

                    }

                    @Override
                    public void e(String text, Object... args) {

                    }

                    @Override
                    public void v(String text, Object... args) {

                    }
                }).minConsumerCount(5)// always keep at least five consumer
                // alive
                .maxConsumerCount(10)// up to 10 consumers at a time
                .loadFactor(3)// 3 jobs per consumer
                .consumerKeepAlive(120)// wait 2 minute
                .build();

        jobManager = new JobManager(configuration);

    }

    public JobManager getJobManager() {
        return jobManager;
    }

}