package com.meeof.meeof.onesignal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.birbit.android.jobqueue.Constraint;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.util.Constant;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ransika on 9/18/2017.
 */

public class NotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

    private static final String TAG = "NotificationReceived";

    @Override
    public void notificationReceived(OSNotification notification) {
        JSONObject data = notification.payload.additionalData;
        String msgBody = notification.payload.body;

        Log.wtf(TAG , "Received: " + notification.toString());
        Log.wtf(TAG , "push messsage" + msgBody);
        Log.wtf(TAG , "push data" + data);

        broadcastNotificationTabUpdate();

        if (data != null) {

            if(data.has("en")){

                JSONObject customKey = data.optJSONObject("en");

                if (customKey != null && customKey.has("type")){
                    Log.wtf(TAG, "customkey type: " + customKey.optString("type"));

                    if(customKey.optString("type").equalsIgnoreCase("1")){

                    }else if (customKey.optString("type").equalsIgnoreCase("2")){

                    }else if (customKey.optString("type").equalsIgnoreCase("3")){
                        broadcastUpdateTabUpdate();
                    }else if (customKey.optString("type").equalsIgnoreCase("4")){
                        broadcastUpdateTabUpdate();
                    }else if (customKey.optString("type").equalsIgnoreCase("5")){

                    }else if (customKey.optString("type").equalsIgnoreCase("6")){
                        //someone changed rsvp to my event, should refresh my events list
                        broadcastEventsUpdate();

                    }else if (customKey.optString("type").equalsIgnoreCase("7")){
                        //private invitation received, should update my events badge count
                        updateEventsTabBadgeCount(true);
                        //should refresh my events list
                        broadcastEventsUpdate();
                    }else if (customKey.optString("type").equalsIgnoreCase("8")){
                        //event join request received, should update my events badge count
                        updateEventsTabBadgeCount(true);
                        //should refresh my events list
                        broadcastEventsUpdate();
                    }
                }


            }
        }
    }

    private void updateJoinRequestsBadgeCount(boolean add) {
        SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        int badgeCount = sharedPreferences.getInt(Constant.JOIN_REQUESTS_BADGE_COUNT, 0);

        if(add){
            sharedEditor.putInt(Constant.JOIN_REQUESTS_BADGE_COUNT, ++badgeCount);
        }else{
            if(badgeCount > 0){
                sharedEditor.putInt(Constant.JOIN_REQUESTS_BADGE_COUNT, --badgeCount);
            }
        }
        sharedEditor.commit();
        Log.wtf(TAG , "JOIN_REQUESTS_BADGE_COUNT: " + badgeCount);
    }


    private void updateEventsTabBadgeCount(boolean add) {
        SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        int badgeCount = sharedPreferences.getInt(Constant.MY_EVENTS_BADGE_COUNT, 0);

        if(add){
            sharedEditor.putInt(Constant.MY_EVENTS_BADGE_COUNT, ++badgeCount);
        }else{
            if(badgeCount > 0){
                sharedEditor.putInt(Constant.MY_EVENTS_BADGE_COUNT, --badgeCount);
            }
        }
        sharedEditor.commit();
        Log.wtf(TAG , "GOING_EVENTS_BADGE_COUNT: " + badgeCount);
    }


    private void broadcastEventsUpdate() {
        Intent intent = new Intent(Constant.UPDATE_EVENTS);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }


    private void broadcastBottomBarTabUpdate() {
        Intent intent = new Intent(Constant.UPDATE_BOTTOM_BAR_UPDATE);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void broadcastUpdateTabUpdate() {
        Intent intent = new Intent(Constant.UPDATE_UPDATE_TAB);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    private void broadcastNotificationTabUpdate() {
        Intent intent = new Intent(Constant.REFRESH_NOTIFICATIONS_TAB);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

}