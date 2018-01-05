package com.meeof.meeof.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.model.UpdateFilterModel;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventsWebJob;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Anuja Ranwalage on 10/17/17.
 */

public class Helper {


    private static JobManager jobManager;
    public static final long minimumInterval = 2000;
    public static Map<View, Long> lastClickMap = new WeakHashMap<View, Long>();
    ;


    public static void delay(int timeMillisecond, final DelayCallBack delayCallBack) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallBack.postDelay();
            }
        }, timeMillisecond);
    }

    public interface DelayCallBack {
        void postDelay();
    }

    public static void syncDbWithEvents(String accessToken, Context context) {
        SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, context.getApplicationContext().MODE_PRIVATE);
        jobManager = MeeofApplication.getInstance().getJobManager();
        if (isNetworkAvailable(context)) {
            if (sharedPreferences.getBoolean(Constant.HOME, false)) {
//                if ((sharedPreferences.getLong(Constant.LAST_SYNC_TIME, 0)) > Constant.MIN_SYNC_TIME_MS) {
                    jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
//                }
            } else if (sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false)) {

                jobManager.addJobInBackground(new GetEventsWebJob(accessToken));

            }

        } else {

        }
    }

    private static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static String getProfilePhotoUser(String imageName) {

        String photoUrl = imageName == null || imageName.trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                Constant.PROFILE_PIC_BASE_URL + imageName;

        return photoUrl;
    }

    public static EventFilterModel getEventFilterDefaultModel(double lat, double lng) {

        EventFilterModel eventFilterModel = new EventFilterModel();

        eventFilterModel.setLocation("current");
        eventFilterModel.setMasterfilter("0");
        eventFilterModel.setAcceptabledistance("100");
        eventFilterModel.setSortbyproximity("0");
        eventFilterModel.setHideoutsidefriends("0");
        eventFilterModel.setRememberfilter("0");
        eventFilterModel.setNiceaddress(null);
        eventFilterModel.setMatrix("0");
        eventFilterModel.setLng(String.valueOf(lng));
        eventFilterModel.setLat(String.valueOf(lat));
        eventFilterModel.setStopexecution("0");
        eventFilterModel.setOffset("0");
        eventFilterModel.setLimit("20");

        return eventFilterModel;
    }

    public static UpdateFilterModel getUpdateFilterDefaultModel(double lat, double lng) {

        UpdateFilterModel updateFilterModel = new UpdateFilterModel();

        updateFilterModel.setLocation("current");
        updateFilterModel.setMasterfilter("0");
        updateFilterModel.setAcceptabledistance("100");
        updateFilterModel.setSortbyproximity("0");
        updateFilterModel.setHideoutsidefriends("0");
        updateFilterModel.setRememberfilter("0");
        updateFilterModel.setNiceaddress(null);
        updateFilterModel.setMatrix("0");
        updateFilterModel.setLng(String.valueOf(lng));
        updateFilterModel.setLat(String.valueOf(lat));
        updateFilterModel.setStopexecution("0");
        updateFilterModel.setOffset("0");
        updateFilterModel.setLimit("20");
        updateFilterModel.setHideolderupdates("false");
        updateFilterModel.setFriends("0");

        return updateFilterModel;
    }


    public static void clickGaurd(View view) {
        view.setEnabled(false);

        Long previousClickTimestamp = lastClickMap.get(view);
        long currentTimestamp = SystemClock.uptimeMillis();
        lastClickMap.put(view, currentTimestamp);
        if (previousClickTimestamp == null || (currentTimestamp - previousClickTimestamp.longValue() > minimumInterval)) {
            view.setEnabled(true);
        }
    }
}
