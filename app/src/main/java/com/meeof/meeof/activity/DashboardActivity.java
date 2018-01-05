package com.meeof.meeof.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.fragment.EventsFragment;
import com.meeof.meeof.fragment.MessageListScreen;
import com.meeof.meeof.fragment.MoreFragment;
import com.meeof.meeof.fragment.NotificationFragment;
import com.meeof.meeof.fragment.UpdatesFragment;
import com.meeof.meeof.service.location.LocationService;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.GetUpdatesWebJob;

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

import static com.meeof.meeof.util.Utility.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by ransikadesilva on 10/11/17.
 */

public class DashboardActivity extends BaseActivity {

    private TextView viewNameTv;
    private TextView viewActionTvBtn;
    private LinearLayout manageFriendsLlBtn;
    private String accessToken;
    private BottomNavigationViewEx bottomBar;
    private static final String TAG = "DashboardActivity";

    private int currentlySelectedTabId;

    private int[][] states = new int[][] {
            new int[] { android.R.attr.state_checked}, // enabled
            new int[] {-android.R.attr.enabled}, // disabled
    };

    int[] iconColors;

    int[] textColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        initViews();
//      getUserProfile();

        LocalBroadcastManager.getInstance(this).registerReceiver(updateBottomBar,
                new IntentFilter(Constant.UPDATE_BOTTOM_BAR_UPDATE));


//        if (Permission.checkCourseLocationPermission(this) && Permission.checkFineLocationPermission(this)) {
//            Intent intent = new Intent(this, LocationService.class);
//            this.startService(intent);
//        }else{
//            //show snackbar
//        }

        if (checkLocationPermission()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                //Request location updates:
                Intent intent = new Intent(this, LocationService.class);
                this.startService(intent);
            }
        }

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        bottomBar = (BottomNavigationViewEx) findViewById(R.id.bottomBar);

        bottomBar.enableAnimation(false);
        bottomBar.enableShiftingMode(false);
        bottomBar.enableItemShiftingMode(false);

        iconColors = new int[] {
                ContextCompat.getColor(this, R.color.bottom_bar_active_icon_color),
                ContextCompat.getColor(this, R.color.darkBrown)
        };


        textColors = new int[] {
                ContextCompat.getColor(this, R.color.bottom_bar_active_text_color),
                ContextCompat.getColor(this, R.color.darkBrown)
        };

        ColorStateList iconStateList = new ColorStateList(states, iconColors);
        ColorStateList textStateList = new ColorStateList(states, textColors);
        bottomBar.setItemIconTintList(iconStateList);

        bottomBar.setTextTintList(0, textStateList);
        bottomBar.setTextTintList(1, textStateList);
        bottomBar.setTextTintList(2, textStateList);
        bottomBar.setTextTintList(3, textStateList);
        bottomBar.setTextTintList(4, textStateList);

        currentlySelectedTabId = R.id.updates;


        bottomBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.getItemId();

                Intent intent = new Intent(Constant.UPDATE_BOTTOM_BAR_UPDATE);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                if(item.getItemId() == R.id.updates && currentlySelectedTabId != item.getItemId()){
                    UpdatesFragment dealsFragment = new UpdatesFragment();
                    setFragment(dealsFragment, "replace", UpdatesFragment.class.getSimpleName());
                    currentlySelectedTabId = R.id.updates;

                }else if(item.getItemId() == R.id.events && currentlySelectedTabId != item.getItemId()){
                    EventsFragment dealsFragment = new EventsFragment();
                    setFragment(dealsFragment, "replace", EventsFragment.class.getSimpleName());
                    currentlySelectedTabId = R.id.events;

                }else if(item.getItemId() == R.id.more){
                    MoreFragment dealsFragment = new MoreFragment();
                    setFragment(dealsFragment, "replace", MoreFragment.class.getSimpleName());
                    currentlySelectedTabId = R.id.more;

                }else if(item.getItemId() == R.id.notifications){
                    NotificationFragment notificationFragment = new NotificationFragment();
                    setFragment(notificationFragment, "replace", NotificationFragment.class.getSimpleName());
                    currentlySelectedTabId = R.id.notifications;

                }else if(item.getItemId() == R.id.messages){
                    MessageListScreen dealsFragment = new MessageListScreen();
                    setFragment(dealsFragment, "replace", MessageListScreen.class.getSimpleName());
                    currentlySelectedTabId = R.id.messages;
                }
                return true;
            }
        });

        bottomBar.setCurrentItem(0);

        Intent intent = new Intent(Constant.UPDATE_BOTTOM_BAR_UPDATE);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        //jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));


        UpdatesFragment dealsFragment = new UpdatesFragment();
        setFragment(dealsFragment, "replace", UpdatesFragment.class.getSimpleName());
        currentlySelectedTabId = R.id.updates;
    }


    @Override
    protected void onResume() {
        super.onResume();



    }

    private void getUserProfile() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
        } else {
            showSnackbar(viewNameTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private BroadcastReceiver updateBottomBar = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d(TAG, "updateEventList");
            int countInt = 0;

            if(intent.getStringExtra(Constant.BADGE_COUNT) != null){
                String count = intent.getStringExtra(Constant.BADGE_COUNT);
                countInt = Integer.parseInt(count);

            }else{
                SharedPreferences sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
                countInt = sharedPreferences.getInt(Constant.ALL_EVENTS_BADGE_COUNT, 0);
                countInt += sharedPreferences.getInt(Constant.MY_EVENTS_BADGE_COUNT, 0);
            }

            final int finalCountInt = countInt;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(finalCountInt > 0){
                        addBadgeAt(1, finalCountInt);
                    }else{
                        removeBadge(1);
                    }
                }
            });
        }
    };


    private Badge addBadgeAt(int position, int number) {
        return new QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(12, 2, true)
                .bindTarget(bottomBar.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener(new Badge.OnDragStateChangedListener() {
                    @Override
                    public void onDragStateChanged(int dragState, Badge badge, View targetView) {
                        if (Badge.OnDragStateChangedListener.STATE_SUCCEED == dragState){
                            //Toast.makeText(BadgeViewActivity.this, R.string.tips_badge_removed, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void removeBadge(int position){
        new QBadgeView(this).bindTarget(bottomBar.getBottomNavigationItemView(position)).setBadgeNumber(0);
    }



    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateBottomBar);
        super.onDestroy();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) DashboardActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //Prompt the user once explanation has been shown
                    ActivityCompat.requestPermissions((Activity) DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions((Activity) DashboardActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult:requestCode " + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    Log.d(TAG, "onRequestPermissionsResult:PERMISSION_GRANTED ");
                    //Request location updates:
                    Intent intent = new Intent(this, LocationService.class);
                    this.startService(intent);

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "onRequestPermissionsResult:PERMISSIONdenied ");
                }
                return;
            }

        }
    }
}
