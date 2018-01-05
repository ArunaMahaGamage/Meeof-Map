package com.meeof.meeof.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
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

import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.CreateEventActivity;
import com.meeof.meeof.activity.EventFilterActivity;
import com.meeof.meeof.activity.ManageFriendsActivity;
import com.meeof.meeof.adapter.FriendsSearchRecyclerAdapter;
import com.meeof.meeof.adapter.MeeofUserSearchAdapter;
import com.meeof.meeof.adapter.ShowAllEventsRecyclerAdapter;
import com.meeof.meeof.adapter.ShowEventsRecyclerAdapter;
import com.meeof.meeof.adapter.UserFriendsRecyclerAdapter;
import com.meeof.meeof.custom.CustomFontTextView;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.helper.VpLifeCycleManager;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.GetEventsWebJobCompletedEvent;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.meeof_search_users_dto.Data;
import com.meeof.meeof.model.meeof_search_users_dto.MeeofSearchResponse;
import com.meeof.meeof.model.search_query_dto.SearchFriendsQueryResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.Utility;
import com.meeof.meeof.webjob.EventsSyncJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.GetInviteFriendWebJob;
import com.meeof.meeof.webjob.PostAddAsFriendWebJob;
import com.meeof.meeof.webjob.getMeeofSearchResultWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.meeof.meeof.util.Utility.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by Anuja Ranwalage on 10/18/17.
 */

public class EventsFragment extends BaseFragment implements View.OnClickListener, ViewPager.OnPageChangeListener, TextWatcher {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private int[] tabIcons = {
            R.drawable.ico_event_action_selected, R.drawable.ico_eventfilter_event_2
    };
    private TextView newEventTv;
    private ImageView filterIv;
    private ViewPagerAdapter adapter;
    private int previousPosition = 0;
    private int currentPosition = 0;

    private int myEventsCount = 0;

    private RelativeLayout myEventsBadgeRl;
    private RelativeLayout allEventsBadgeRl;

    private TextView myEventsBadgeCount;
    private TextView allEventsBadgeCount;
    private TextView currentLocationTv;
    private TextView currentLocationLabelTV;

    private static final String TAG = "EventsFragment";
    private SharedPreferences sharedPreferences;
    private RelativeLayout filterOnRlTv;
    private Location mLastLocation;
    private LinearLayout newEventLlBtn;
    private LinearLayout searchViewLl;
    private EditText searchEt;
    private ImageView clear;
    private LinearLayout searchLlIv;
    private MyEventsVpFragment myEventsVpFragment;
    private AllEventVpFragment allEventVpFragment;
    private String accessToken;

    private LinearLayout meeofSearchLl;
    private LinearLayout eventFragmentLl;
    private RecyclerView meeofSearchResultRv;
    private MeeofUserSearchAdapter meeofUserSearchAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);

        initViews(view);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateEventsList,
                new IntentFilter(Constant.UPDATE_EVENTS));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateAllEventTab,
                new IntentFilter(Constant.UPDATE_ALL_EVENTS_TAB));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(locationChanged,
                new IntentFilter(Constant.LOCATION_UPDATED));

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
        //startProgressBar();

        return view;
    }

    private void initViews(View view) {


        float lastKnownLat = sharedPreferences.getFloat(Constant.latitudeService, 0);
        float lastKnownLongi = sharedPreferences.getFloat(Constant.longitudeService, 0);


        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        newEventTv = (TextView) view.findViewById(R.id.newEventTv);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);

        searchViewLl = (LinearLayout) view.findViewById(R.id.searchViewLl);
        searchEt = (EditText) view.findViewById(R.id.searchEt);
        clear = (ImageView) view.findViewById(R.id.clear);
        searchLlIv = (LinearLayout) view.findViewById(R.id.searchLlIv);

        currentLocationTv = (CustomFontTextView) view.findViewById(R.id.currentLocationTv);
        filterOnRlTv = (RelativeLayout) view.findViewById(R.id.filterOnRlTv);

        //badgeTabLayout = (BadgeTabLayout) view.findViewById(R.id.tabs);

        newEventLlBtn = (LinearLayout) view.findViewById(R.id.newEventLlBtn);
        tabLayout.setupWithViewPager(viewPager);
        //badgeTabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        filterIv = (ImageView) view.findViewById(R.id.filterIv);

        currentLocationLabelTV = (TextView) view.findViewById(R.id.currentLocationLabelTV);

        newEventLlBtn.setOnClickListener(this);

        searchEt.addTextChangedListener(this);
        clear.setOnClickListener(this);
        searchLlIv.setOnClickListener(this);

        filterIv.setOnClickListener(this);

        viewPager.setOnPageChangeListener(this);
        currentLocationTv.setText("Loading ...");


        viewPager.post(new Runnable() {
            @Override
            public void run() {
                EventsFragment.this.onPageSelected(viewPager.getCurrentItem());
            }
        });

        if (!sharedPreferences.getString(Constant.EVENT_FILTER_OBJ, "").equals("")) {
            filterOnRlTv.setVisibility(View.VISIBLE);
        } else {
            filterOnRlTv.setVisibility(View.GONE);
        }

        meeofSearchLl=(LinearLayout)view.findViewById(R.id.meeofSearchLl);
        eventFragmentLl=(LinearLayout)view.findViewById(R.id.eventFragmentLl);
        meeofSearchResultRv=(RecyclerView) view.findViewById(R.id.meeofSearchResultRv);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        Boolean trackingUserLocation = sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false);
        Log.d(TAG,"sharedPreferencesGetBooleanConstant.CURRENT_LOCATION: "+trackingUserLocation);
        if (trackingUserLocation){
            if (Utility.checkLocationPermission(this.getContext())) {

//                currentLocationLabelTV.setText("");
//                currentLocationTv.setText("Meeof will update the content when you are moving around");

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {

                    sharedEditor.putFloat(Constant.CURRENT_LATITUDE,(float) mLastLocation.getLatitude());
                    sharedEditor.putFloat(Constant.CURRENT_LONGITUDE,(float) mLastLocation.getLongitude());
                    sharedEditor.apply();

                    List<Address> addresses = getAddressFromLatLong(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    final String address = addresses.get(0).getAddressLine(0);
                    currentLocationLabelTV.setText("Current Location : ");
                    currentLocationTv.setText(String.valueOf(address));
                }

                try {
                    saveObjectToSharedPref(sharedEditor, Helper.getEventFilterDefaultModel(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Constant.FILTER_MODEL_DEFAULT_OBJ);
                }catch (Exception ex){

                }
            }

        }
        else{
            currentLocationLabelTV.setText("");
//            currentLocationTv.setText(sharedPreferences.getString(Constant.USER_ADDRESS,"Home Address Unavailable"));
            currentLocationTv.setText("Content near Home");
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult:requestCode " + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.onConnected(null);
                    Log.d(TAG, "onRequestPermissionsResult:PERMISSION_GRANTED ");

                } else {

                    Log.d(TAG, "onRequestPermissionsResult:PERMISSIONdenied ");
                }
                return;
            }

        }
    }

    private void setupTabIcons() {

        LinearLayout tabLinearLayout1 = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tabContent1 = (TextView) tabLinearLayout1.findViewById(R.id.tabContent);
        tabContent1.setText("  My Events");
        tabContent1.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkblue));
        tabContent1.setCompoundDrawablesWithIntrinsicBounds(tabIcons[0], 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabLinearLayout1);

        myEventsBadgeRl = tabLinearLayout1.findViewById(R.id.badgeCountRl);
        myEventsBadgeCount = (CustomFontTextView) tabLinearLayout1.findViewById(R.id.badgeCount);

        LinearLayout tabLinearLayout2 = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        TextView tabContent2 = (TextView) tabLinearLayout2.findViewById(R.id.tabContent);
        tabContent2.setText("  Events");
        tabContent2.setTextColor(ContextCompat.getColor(getActivity(), R.color.usualTextColor));
        tabContent2.setCompoundDrawablesWithIntrinsicBounds(tabIcons[1], 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabLinearLayout2);

        allEventsBadgeRl = tabLinearLayout2.findViewById(R.id.badgeCountRl);
        allEventsBadgeCount = (CustomFontTextView) tabLinearLayout2.findViewById(R.id.badgeCount);


    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(this.getFragmentManager());
        myEventsVpFragment = new MyEventsVpFragment();
        allEventVpFragment = new AllEventVpFragment();
        adapter.addFragment(myEventsVpFragment, "My Events");
        adapter.addFragment(allEventVpFragment, "Events");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newEventLlBtn:
                sendToCreateEventActivity();
                Helper.clickGaurd(newEventTv);
                break;
            case R.id.filterIv:
                sendToFilterActivity();
                Helper.clickGaurd(filterIv);
                break;
            case R.id.clear:
                searchViewLl.setVisibility(View.GONE);
                meeofSearchLl.setVisibility(View.GONE);
                eventFragmentLl.setVisibility(View.VISIBLE);
                break;
            case R.id.searchLlIv:
                if(searchEt.getText().toString().length()>0){
                    meeofSearchLl.setVisibility(View.VISIBLE);
                    eventFragmentLl.setVisibility(View.GONE);
                }
                searchViewLl.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendToFilterActivity() {
        Intent intent = new Intent(getActivity(), EventFilterActivity.class);
        startActivity(intent);
    }

    private void sendToCreateEventActivity() {
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        startActivity(intent);
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
        ShowEventsRecyclerAdapter showEventsRecyclerAdapter = null;
        ShowAllEventsRecyclerAdapter showAllEventsRecyclerAdapter = null;

        if (fragment instanceof MyEventsVpFragment) {
            showEventsRecyclerAdapter = ((MyEventsVpFragment) fragment).getAdapter();

            if (showEventsRecyclerAdapter != null) {
                //showEventsRecyclerAdapter.filter(s.toString());
                if(searchEt.getText().toString().length()>0){
                    startProgressBar();
                    jobManager.addJobInBackground(new getMeeofSearchResultWebJob(accessToken,searchEt.getText().toString()));
                    meeofSearchLl.setVisibility(View.VISIBLE);
                    eventFragmentLl.setVisibility(View.GONE);
                }else{
                    meeofSearchLl.setVisibility(View.GONE);
                    eventFragmentLl.setVisibility(View.VISIBLE);
                }
            }

        } else if (fragment instanceof AllEventVpFragment) {
            showAllEventsRecyclerAdapter = ((AllEventVpFragment) fragment).getAdapter();

            if (showAllEventsRecyclerAdapter != null) {
                if(searchEt.getText().toString().length()>0){
                    startProgressBar();
                    jobManager.addJobInBackground(new getMeeofSearchResultWebJob(accessToken,searchEt.getText().toString()));
                    meeofSearchLl.setVisibility(View.VISIBLE);
                    eventFragmentLl.setVisibility(View.GONE);
                }else{
                    meeofSearchLl.setVisibility(View.GONE);
                    eventFragmentLl.setVisibility(View.VISIBLE);
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMeeofUserSearchResult(MeeofSearchResponse meofSearchResponse) {
        stopProgressBar();

        Log.d(TAG, "onGetEventsWebJobCompletedEvent 1");

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
                    Log.d(TAG, "onGetEventsWebJobCompletedEvent 4");

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
                showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV,  getString(R.string.oop_something_went_wrong), Constant.ERROR);
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateMyEventTab);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateAllEventTab);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(locationChanged);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver updateMyEventTab = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String count = intent.getStringExtra(Constant.BADGE_COUNT);
            Log.d(TAG, "updateEventList count: " + count);
            broadcastBottomBarTabUpdate(count);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (count.equalsIgnoreCase("0")) {
                        myEventsBadgeRl.setVisibility(View.GONE);
                        myEventsBadgeCount.setText("");
                    } else {
                        myEventsBadgeRl.setVisibility(View.VISIBLE);
                        myEventsBadgeCount.setText(count);
                    }
                }
            });
        }
    };

    private BroadcastReceiver updateAllEventTab = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "updateEventList");

            final String count = intent.getStringExtra(Constant.BADGE_COUNT);
            Log.d(TAG, "updateEventList count: " + count);
            broadcastBottomBarTabUpdate(count);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (count.equalsIgnoreCase("0")) {
                        allEventsBadgeRl.setVisibility(View.GONE);
                        allEventsBadgeCount.setText("");
                    } else {
                        allEventsBadgeRl.setVisibility(View.VISIBLE);
                        allEventsBadgeCount.setText(count);
                    }
                }
            });
        }
    };


    private BroadcastReceiver updateEventsList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
        }
    };


    private void broadcastBottomBarTabUpdate(String count) {
        Intent intent = new Intent(Constant.UPDATE_BOTTOM_BAR_UPDATE);
        intent.putExtra(Constant.BADGE_COUNT, count);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private BroadcastReceiver locationChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "locationChanged BroadcastReceiver");

            double lat = intent.getDoubleExtra(Constant.LATITUDE, 0);
            double longi = intent.getDoubleExtra(Constant.LONGITUDE, 0);

            if(lat !=0 && longi!=0){
                sharedEditor.putFloat(Constant.CURRENT_LATITUDE,(float) lat);
                sharedEditor.putFloat(Constant.CURRENT_LONGITUDE,(float) longi);
                sharedEditor.apply();
            }

            List<Address> addresses = getAddressFromLatLong(lat, longi);

            if(addresses != null && addresses.size() > 0){
                final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                if(sharedPreferences.getBoolean(Constant.CURRENT_LOCATION,false)){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentLocationLabelTV.setText("Current Location : ");
                            currentLocationTv.setText(address);
                        }
                    });
                }
            }
        }
    };

    @Subscribe
    public void onGetEventsWebJobCompletedEvent(GetEventsWebJobCompletedEvent getEventsWebJobCompletedEvent) {
        stopProgressBar();

        Log.wtf(TAG, "onGetEventsWebJobCompletedEvent 1");

        if (getEventsWebJobCompletedEvent != null) {

            if (getEventsWebJobCompletedEvent.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {

                if(getEventsWebJobCompletedEvent.getEvents() != null && getEventsWebJobCompletedEvent.getEvents().length() > 0){
                    String events = getEventsWebJobCompletedEvent.getEvents().toString();
                    String attendance = getEventsWebJobCompletedEvent.getAttendance().toString();
                    syncEventToDb(events, attendance);
                    Log.wtf(TAG, "onGetEventsWebJobCompletedEvent not null");
                }else{
                    Log.wtf(TAG, "onGetEventsWebJobCompletedEvent null");
                    syncEventToDb(null, null);
                }

            } else {
                showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(currentLocationLabelTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    public void syncEventToDb(String events, String attendance){
        jobManager.addJobInBackground(new EventsSyncJob(events, attendance));
    }
}
