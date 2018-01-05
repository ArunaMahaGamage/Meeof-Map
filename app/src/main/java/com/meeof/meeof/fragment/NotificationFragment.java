package com.meeof.meeof.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.MeeofUserSearchAdapter;
import com.meeof.meeof.adapter.NotificationRecyclerAdapter;
import com.meeof.meeof.custom.CustomFontTextView;
import com.meeof.meeof.helper.VpLifeCycleManager;
import com.meeof.meeof.model.GetNotificationsWebJobCompletedImportantNotifications;
import com.meeof.meeof.model.GetNotificationsWebJobCompletedOtherNotifications;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.model.meeof_search_users_dto.Data;
import com.meeof.meeof.model.meeof_search_users_dto.MeeofSearchResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetImportantNotificationsJob;
import com.meeof.meeof.webjob.GetInviteFriendWebJob;
import com.meeof.meeof.webjob.GetOtherNotificationsJob;
import com.meeof.meeof.webjob.NotificationsSyncJob;
import com.meeof.meeof.webjob.OtherNotificationsSyncJob;
import com.meeof.meeof.webjob.PostAddAsFriendWebJob;
import com.meeof.meeof.webjob.getMeeofSearchResultWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class NotificationFragment extends BaseFragment implements View.OnClickListener,ViewPager.OnPageChangeListener, TextWatcher {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int[] tabIcons = {
            R.drawable.ico_event_action_selected, R.drawable.ico_eventfilter_event_2
    };
    private TextView newNotificationTv;
//    private ImageView filterIv;
    private ViewPagerAdapter adapter;
    private int previousPosition = 0;
    private int currentPosition = 0;

    private int myNotificationsCount = 0;

    private RelativeLayout importantNotificationsBadgeRl;
    private RelativeLayout otherNotificationsBadgeRl;

    private TextView importantNotificationsBadgeCount;
    private TextView otherNotificationsBadgeCount;
//    private TextView currentLocationTv;
    private TextView currentLocationLabelTV;

    private static final String TAG = "NotificationsFragment";
    private SharedPreferences sharedPreferences;
    //private RelativeLayout filterOnRlTv;
    private Location mLastLocation;
    private LinearLayout editNotificationLlBtn;
    private LinearLayout searchViewLl;
    private EditText searchEt;
    private ImageView clear;
    private LinearLayout searchLlIv;
    private ImportantNotificationFragment importantNotificationsVpFragment;
    private OtherNotificationFragment otherNotificationsVpFragment;
    private String accessToken;
    private boolean showHideDeleteButton;

    private LinearLayout meeofSearchLl;
    private LinearLayout notificationFragmentLl;
    private RecyclerView meeofSearchResultRv;
    private MeeofUserSearchAdapter meeofUserSearchAdapter;
    private NotificationRecyclerAdapter notificationRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);

        initViews(view);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateImportantNotificationsTab,
                new IntentFilter(Constant.UPDATE_IMPORTANT_NOTIFICATIONS_TAB));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateOtherNotificationsTab,
                new IntentFilter(Constant.UPDATE_OTHER_NOTIFICATIONS_TAB));


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(refreshNotifications,
                new IntentFilter(Constant.REFRESH_NOTIFICATIONS_TAB));

//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(locationChanged,
//                new IntentFilter(Constant.LOCATION_UPDATED));

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        jobManager.addJobInBackground(new GetImportantNotificationsJob(accessToken));


        showHideDeleteButton=true;

        return view;
    }

    private void initViews(View view) {



        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        newNotificationTv = (TextView) view.findViewById(R.id.editNotificationTv);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        searchViewLl = (LinearLayout) view.findViewById(R.id.searchViewLl);
        searchEt = (EditText) view.findViewById(R.id.searchEt);
        clear = (ImageView) view.findViewById(R.id.clear);
        searchLlIv = (LinearLayout) view.findViewById(R.id.searchLlIv);


        editNotificationLlBtn = (LinearLayout) view.findViewById(R.id.editNotificationLlBtn);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        currentLocationLabelTV = (TextView) view.findViewById(R.id.currentLocationLabelTV);

        editNotificationLlBtn.setOnClickListener(this);

        //searchEt.addTextChangedListener(this);
        clear.setOnClickListener(this);
        searchLlIv.setOnClickListener(this);
        //newNotificationTv.setOnClickListener(this);

        viewPager.setOnPageChangeListener(this);

          viewPager.post(new Runnable() {
            @Override
            public void run() {
                NotificationFragment.this.onPageSelected(viewPager.getCurrentItem());
            }

        });

        meeofSearchLl=(LinearLayout)view.findViewById(R.id.meeofSearchLl);
        notificationFragmentLl =(LinearLayout)view.findViewById(R.id.eventFragmentLl);
        meeofSearchResultRv=(RecyclerView) view.findViewById(R.id.meeofSearchResultRv);
    }




    private void saveObjectToSharedPref(SharedPreferences.Editor sharedEditor, Object object, String objectSaveName) {
        try {
            Gson gson = new Gson();
            String objectJson = gson.toJson(object);
            sharedEditor.putString(objectSaveName, objectJson);
            sharedEditor.apply();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void setupTabIcons() {

        LinearLayout tabLinearLayout1 = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tabContent1 = (TextView) tabLinearLayout1.findViewById(R.id.tabContent);
        tabContent1.setText("  Important");
        tabContent1.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkblue));
        tabContent1.setCompoundDrawablesWithIntrinsicBounds(tabIcons[0], 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabLinearLayout1);

        importantNotificationsBadgeRl = tabLinearLayout1.findViewById(R.id.badgeCountRl);
        importantNotificationsBadgeCount = (CustomFontTextView) tabLinearLayout1.findViewById(R.id.badgeCount);

        LinearLayout tabLinearLayout2 = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tabContent2 = (TextView) tabLinearLayout2.findViewById(R.id.tabContent);
        tabContent2.setText("  Other");
        tabContent2.setTextColor(ContextCompat.getColor(getActivity(), R.color.usualTextColor));
        tabContent2.setCompoundDrawablesWithIntrinsicBounds(tabIcons[1], 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabLinearLayout2);

        otherNotificationsBadgeRl = tabLinearLayout2.findViewById(R.id.badgeCountRl);
        otherNotificationsBadgeCount = (CustomFontTextView) tabLinearLayout2.findViewById(R.id.badgeCount);


    }

     private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getFragmentManager());
        importantNotificationsVpFragment = new ImportantNotificationFragment();
         otherNotificationsVpFragment = new OtherNotificationFragment();
        adapter.addFragment(importantNotificationsVpFragment, "Important");
        adapter.addFragment(otherNotificationsVpFragment, "Other");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                searchViewLl.setVisibility(View.GONE);
                meeofSearchLl.setVisibility(View.GONE);
                notificationFragmentLl.setVisibility(View.VISIBLE);
                break;
            case R.id.searchLlIv:
                //Log.d(TAG,"editNotificationLlBtn");
                if(searchEt.getText().toString().length()>0){
                    meeofSearchLl.setVisibility(View.VISIBLE);
                    notificationFragmentLl.setVisibility(View.GONE);
                }
                searchViewLl.setVisibility(View.VISIBLE);
                break;
            case R.id.editNotificationLlBtn:
                //Log.d(TAG,"editNotificationLlBtn");
                showHideDeleteButtons();
                break;
        }
    }

    private void showHideDeleteButtons(){
        Log.d(TAG,"showHideDeleteButtons :"+ showHideDeleteButton);

        if(showHideDeleteButton){
            showHideDeleteButton=false;
            if(importantNotificationsVpFragment.showNotificationsRecyclerAdapter!=null||otherNotificationsVpFragment.showNotificationsRecyclerAdapter!=null){
                importantNotificationsVpFragment.showNotificationsRecyclerAdapter.showdeleteButtons();
                otherNotificationsVpFragment.showNotificationsRecyclerAdapter.showdeleteButtons();
            }
        }else{
            showHideDeleteButton=true;
            if(importantNotificationsVpFragment.showNotificationsRecyclerAdapter!=null||otherNotificationsVpFragment.showNotificationsRecyclerAdapter!=null){
                importantNotificationsVpFragment.showNotificationsRecyclerAdapter.hidedeleteButtons();
                otherNotificationsVpFragment.showNotificationsRecyclerAdapter.hidedeleteButtons();
            }
        }

    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        VpLifeCycleManager fragmentToShow = (VpLifeCycleManager) adapter.getItem(position);
        fragmentToShow.onResumeFragment();

        LinearLayout tabLl = (LinearLayout) tabLayout.getTabAt(position).getCustomView();
        TextView tabContent1 = (TextView) tabLl.findViewById(R.id.tabContent);
        tabContent1.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkblue));
        tabContent1.setCompoundDrawablesWithIntrinsicBounds(tabIcons[0], 0, 0, 0);


        VpLifeCycleManager fragmentToHide = (VpLifeCycleManager) adapter.getItem(currentPosition);
        fragmentToHide.onPauseFragment();

        LinearLayout tabL2 = (LinearLayout) tabLayout.getTabAt(currentPosition).getCustomView();
        TextView tabContent2 = (TextView) tabL2.findViewById(R.id.tabContent);
        tabContent2.setTextColor(ContextCompat.getColor(getActivity(), R.color.usualTextColor));
        tabContent2.setCompoundDrawablesWithIntrinsicBounds(tabIcons[1], 0, 0, 0);

        currentPosition = position;
        if(currentPosition==0){
            jobManager.addJobInBackground(new GetImportantNotificationsJob(accessToken));
        }else{
            jobManager.addJobInBackground(new GetOtherNotificationsJob(accessToken));
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Fragment fragment = adapter.getItem(currentPosition);
        NotificationRecyclerAdapter showNotificationsRecyclerAdapter = null;
        if (fragment instanceof ImportantNotificationFragment) {
            showNotificationsRecyclerAdapter = ((ImportantNotificationFragment) fragment).getAdapter();

        } else if (fragment instanceof OtherNotificationFragment) {
           showNotificationsRecyclerAdapter = ((OtherNotificationFragment) fragment).getAdapter();
        }

        if (showNotificationsRecyclerAdapter != null) {
            //showNotificationsRecyclerAdapter.filter(s.toString());
            if(searchEt.getText().toString().length()>0){
                startProgressBar();
                jobManager.addJobInBackground(new getMeeofSearchResultWebJob(accessToken,searchEt.getText().toString()));
                meeofSearchLl.setVisibility(View.VISIBLE);
                notificationFragmentLl.setVisibility(View.GONE);
            }else{
                meeofSearchLl.setVisibility(View.GONE);
                notificationFragmentLl.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMeeofUserSearchResult(MeeofSearchResponse meofSearchResponse) {
        stopProgressBar();

        Log.d(TAG, "onGetNotificationsWebJobCompletedNotification 1");

        if (meofSearchResponse != null) {

            if (meofSearchResponse.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {

                if(meeofUserSearchAdapter!=null){
                    meeofUserSearchAdapter.updateList(meofSearchResponse.getData());
                }else{
                    meeofUserSearchAdapter = new MeeofUserSearchAdapter(getContext(), meofSearchResponse.getData());
                    meeofSearchResultRv.setLayoutManager(new LinearLayoutManager(getContext()));
                    meeofSearchResultRv.setAdapter(meeofUserSearchAdapter);
                    meeofSearchResultRv.setItemAnimator(new DefaultItemAnimator());
                    meeofUserSearchAdapter.notifyDataSetChanged();
                    Log.d(TAG, "onGetNotificationsWebJobCompletedNotification 4");

                    meeofUserSearchAdapter.setOnClick(new MeeofUserSearchAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position, Data item, MeeofUserSearchAdapter.MyViewHolderFriend myViewHolderParcel) {
                            if (item.getStatus().equals(Constant.ACTIVE)) {
                                if (item.getFriend_status().equals(Constant.FRIEND)) {
                                } else if (item.getFriend_status().equals(Constant.PENDING)) {
                                } else if (item.getFriend_status().equals(Constant.NONE)) {
                                    addAsFriend(item);
                                } else {
                                    inviteFriend(item);
                                }
                            } else if (item.getStatus().equals(Constant.DUMMY)) {
                                inviteFriend(item);
                            } else {
                                inviteFriend(item);
                            }
                            jobManager.addJobInBackground(new getMeeofSearchResultWebJob(accessToken,searchEt.getText().toString()));
                        }
                    });
                }

            } else {
                showSnackbar(currentLocationLabelTV, "check eror 1", Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, "check error 2", Constant.ERROR);
        }
    }

    private void addAsFriend(Data item) {
        if (isNetworkAvailable()) {
            int userId = item.getId();
            if (userId >= 0) {
                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
            } else {
                showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostAddAsFriendJobCompleted(HttpResponseForAddFriend httpResponseForAddFriend) {
        if (httpResponseForAddFriend != null) {
            if (httpResponseForAddFriend.getStatus() != null && httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(currentLocationLabelTV, getString(R.string.successfully_added_as_friend), Constant.SUCCESS);
            } else {
                showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void inviteFriend(Data item) {
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new GetInviteFriendWebJob(accessToken, item.getEmail()));
        } else {
            showSnackbar(currentLocationLabelTV, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetInviteFriendsWebJob(HttpResponse httpResponseInviteFriend) {
        stopProgressBar();

        if (httpResponseInviteFriend != null) {
            if (httpResponseInviteFriend.getStatus()!=null&&httpResponseInviteFriend.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(currentLocationLabelTV, getString(R.string.successfully_invited_as_friend), Constant.SUCCESS);
            } else {
                showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Throwable t) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateImportantNotificationsTab);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateOtherNotificationsTab);
        //LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationChanged);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver updateImportantNotificationsTab = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String count = intent.getStringExtra(Constant.BADGE_COUNT);
            Log.d(TAG, "updateNotificationsList count: " + count);
            broadcastBottomBarTabUpdate(count);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (count.equalsIgnoreCase("0")) {
                        importantNotificationsBadgeRl.setVisibility(View.GONE);
                        importantNotificationsBadgeCount.setText("");
                    } else {
                        importantNotificationsBadgeRl.setVisibility(View.VISIBLE);
                        importantNotificationsBadgeCount.setText(count);
                    }
                }
            });
        }
    };

    private BroadcastReceiver updateOtherNotificationsTab = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "updateNotificationsList");

            final String count = intent.getStringExtra(Constant.BADGE_COUNT);
            Log.d(TAG, "updateNotificationsList count: " + count);
            broadcastBottomBarTabUpdate(count);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (count.equalsIgnoreCase("0")) {
                        otherNotificationsBadgeRl.setVisibility(View.GONE);
                        otherNotificationsBadgeCount.setText("");
                    } else {
                        otherNotificationsBadgeRl.setVisibility(View.VISIBLE);
                        otherNotificationsBadgeCount.setText(count);
                    }
                }
            });
        }
    };

    private BroadcastReceiver refreshNotifications = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "updateNotificationsList");

            if(currentPosition==0){
                jobManager.addJobInBackground(new GetImportantNotificationsJob(accessToken));
            }else{
                jobManager.addJobInBackground(new GetOtherNotificationsJob(accessToken));
            }
        }
    };


    private void broadcastBottomBarTabUpdate(String count) {
        Intent intent = new Intent(Constant.UPDATE_BOTTOM_BAR_UPDATE);
        intent.putExtra(Constant.BADGE_COUNT, count);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

//    private BroadcastReceiver locationChanged = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d(TAG, "locationChanged BroadcastReceiver");
//
//            double lat = intent.getDoubleExtra(Constant.LATITUDE, 0);
//            double longi = intent.getDoubleExtra(Constant.LONGITUDE, 0);
//
//            if(lat !=0 && longi!=0){
//                sharedEditor.putFloat(Constant.CURRENT_LATITUDE,(float) lat);
//                sharedEditor.putFloat(Constant.CURRENT_LONGITUDE,(float) longi);
//                sharedEditor.apply();
//            }
//
//            List<Address> addresses = getAddressFromLatLong(lat, longi);
//
//            if(addresses != null && addresses.size() > 0){
//                final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                if(sharedPreferences.getBoolean(Constant.CURRENT_LOCATION,false)){
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            currentLocationLabelTV.setText("Current Location : ");
////                            currentLocationTv.setText(address);
//                        }
//                    });
//                }
//            }
//        }
//    };

    @Subscribe
    public void onGetNotificationsWebJobCompletedNotifications(GetNotificationsWebJobCompletedImportantNotifications getNotificationsWebJobCompletedNotifications
    ) {
        stopProgressBar();

        Log.wtf(TAG, "onGetNotificationsWebJobCompletedNotification 1");

        if (getNotificationsWebJobCompletedNotifications != null) {

            if (getNotificationsWebJobCompletedNotifications.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {

                if(getNotificationsWebJobCompletedNotifications.getNotifications() != null && getNotificationsWebJobCompletedNotifications.getNotifications().length() > 0){
                    String notifications = getNotificationsWebJobCompletedNotifications.getNotifications().toString();
                    syncNotificationsToDb(notifications);
                    Log.wtf(TAG, "onGetNotificationsWebJobCompletedNotification not null");
                }else{
                    Log.wtf(TAG, "onGetNotificationsWebJobCompletedNotification null");
                    syncNotificationsToDb(null);
                }

            } else {
                showSnackbar(currentLocationLabelTV, "check eror 1", Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, "check error 2", Constant.ERROR);
        }
    }

    public void syncNotificationsToDb(String notifications){
        jobManager.addJobInBackground(new NotificationsSyncJob(notifications));
        //jobManager.addJobInBackground(new OtherNotificationsSyncJob(othernotification));
    }

    @Subscribe
    public void onGetNotificationsWebJobCompletedNotifications(GetNotificationsWebJobCompletedOtherNotifications getNotificationsWebJobCompletedNotifications
    ) {
        stopProgressBar();

        Log.wtf(TAG, "onGetNotificationsWebJobCompletedNotification 1");

        if (getNotificationsWebJobCompletedNotifications != null) {

            if (getNotificationsWebJobCompletedNotifications.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {

                if(getNotificationsWebJobCompletedNotifications.getNotifications() != null && getNotificationsWebJobCompletedNotifications.getNotifications().length() > 0){
                    String notifications = getNotificationsWebJobCompletedNotifications.getNotifications().toString();
                   syncOtherNotificationsToDb(notifications);
                    Log.wtf(TAG, "onGetNotificationsWebJobCompletedNotification not null");
                }else{
                    Log.wtf(TAG, "onGetNotificationsWebJobCompletedNotification null");
                    syncOtherNotificationsToDb(null);
                }

            } else {
                showSnackbar(currentLocationLabelTV, "check eror 1", Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, "check error 2", Constant.ERROR);
        }
    }

    public void syncOtherNotificationsToDb(String notifications){
        jobManager.addJobInBackground(new OtherNotificationsSyncJob(notifications));
    }



}
