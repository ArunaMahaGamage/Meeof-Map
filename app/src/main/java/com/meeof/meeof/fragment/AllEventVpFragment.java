package com.meeof.meeof.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.CreateEventActivity;
import com.meeof.meeof.activity.EventDetailsActivity;
import com.meeof.meeof.activity.EventDetailsHostActivity;
import com.meeof.meeof.activity.EventSocialActivity;
import com.meeof.meeof.adapter.ShowAllEventsRecyclerAdapter;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.helper.VpLifeCycleManager;
import com.meeof.meeof.model.AllEventsSyncedJobResponse;
import com.meeof.meeof.model.CopyEventWebJobResponse;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.ReportEventWebJobCompletedResponse;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.interested_in_event_dto.InterestedEventResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.CopyEventWebJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.PostIgnoreEventWebJob;
import com.meeof.meeof.webjob.PostInterestedInEventWebJob;
import com.meeof.meeof.webjob.PostLikeEventAsFriendWebJob;
import com.meeof.meeof.webjob.PostReportEventWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Visni on 10/12/2017.
 */

public class AllEventVpFragment extends BaseFragment implements VpLifeCycleManager, SwipeRefreshLayout.OnRefreshListener {
    private static final int RC_REFRESH_LIST = 5623;
    private View view;
    private RecyclerView eventsRv;
    final String TAG = "AllEventVpFragment";
    private String accessToken;

    private String updateEventId = "";
    private int currentAllEventsCount = 0;
    private SharedPreferences sharedPreferences;
    public ShowAllEventsRecyclerAdapter showEventsRecyclerAdapter;
    private LinearLayout noEventsLl;
    private LinearLayout eventsRvLl;
    private int i;
    private SwipeRefreshLayout allEventsSRL;
    private LinearLayout fiveKmLlTv;
    private DBHelper dbHelper;
    private boolean isReturingFromDetailsScreen;
    private AsyncTask fetchEventDataAsync;


    public AllEventVpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        view = inflater.inflate(R.layout.fragment_all_event, container, false);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateEventList,
                new IntentFilter(Constant.UPDATE_EVENT_LIST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateAcceptInvitation,
                new IntentFilter(Constant.NEW_ACCEPT_INVITATION));


        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        Log.i(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_all_event, container, false);
        isReturingFromDetailsScreen = false;
        initViews(view);

        return view;
    }

    void initViews(View view) {

        eventsRv = (RecyclerView) view.findViewById(R.id.eventsRv);
        noEventsLl = (LinearLayout) view.findViewById(R.id.noEventsLl);
        eventsRvLl = (LinearLayout) view.findViewById(R.id.eventsRvLl);


        fiveKmLlTv = (LinearLayout) view.findViewById(R.id.fiveKmLlTv);

        allEventsSRL = (SwipeRefreshLayout) view.findViewById(R.id.allEventsSRL);
        int mycolor = Color.parseColor("#7d9fb7");
        allEventsSRL.setColorSchemeColors(mycolor);
        allEventsSRL.setOnRefreshListener(this);
    }

    public ShowAllEventsRecyclerAdapter getAdapter() {
        return this.showEventsRecyclerAdapter;
    }

    private void setDataToListEvents(final List<Event> events) {
        Log.d(TAG, "setDataToListEvents12345: " + events.size());
        Log.d(TAG, "currentAllEventsCount " + currentAllEventsCount);

        broadcastAllEventTabUpdate(currentAllEventsCount);
        currentAllEventsCount = 0;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (events.size() > 0) {
                    Log.d(TAG, "Event List Size: " + events.size());
                    noEventsLl.setVisibility(View.GONE);
                    eventsRvLl.setVisibility(View.VISIBLE);

                    showEventsRecyclerAdapter = new ShowAllEventsRecyclerAdapter(getActivity(), events);
                    //showEventsRecyclerAdapter.notifyDataSetChanged();
                    eventsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    eventsRv.setAdapter(showEventsRecyclerAdapter);
                    eventsRv.setItemAnimator(new DefaultItemAnimator());
                    showEventsRecyclerAdapter.notifyDataSetChanged();

                    Log.i(TAG, "onCreateView end");

                    showEventsRecyclerAdapter.setOnClick(new ShowAllEventsRecyclerAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position, Event item, View button) {
                            if (item.isHost()) {
                                showPopUpMenu(item.isHost(), button, item);
                            } else {
                                showPopUpMenu(item.isHost(), button, item);
                            }
                        }
                    });

                    showEventsRecyclerAdapter.setItemOnClick(new ShowAllEventsRecyclerAdapter.OnLlItemClicked() {
                        @Override
                        public void onItemLlClick(int position, Event item, View button) {
                            Toast.makeText(getActivity(),"Event: "+item.getEventid()+" isHost: "+item.isHost(),Toast.LENGTH_SHORT).show();
                            if (item.isHost()) {
                                sendToHostEventDetailsActivity(item);
                            } else {
                                sendToEventDetailsActivity(item);
                            }
                        }
                    });


                    showEventsRecyclerAdapter.setOnLiketClick(new ShowAllEventsRecyclerAdapter.OnLikeLlItemClicked() {
                        @Override
                        public void onItemLlClick(int position, Event item, View button) {

                            startProgressBar();
                            likeEvent(accessToken, item);

                        }
                    });


                    showEventsRecyclerAdapter.setOnCommentClick(new ShowAllEventsRecyclerAdapter.onCommentClick() {
                        @Override
                        public void onCommentItemClick(int position, Event item) {
                            sendToEventSocialAvtivity(item);
                        }

                    });
                } else {
                    Log.d(TAG, "Event List Empty: " + events.size());
                    noEventsLl.setVisibility(View.VISIBLE);
//                    eventsRvLl.setVisibility(View.GONE);

                    EventFilterModel eventFilterModel = retriveSavedFilterObject(sharedPreferences);
                    if (eventFilterModel != null) {
                        Log.d(TAG, "eventFilterModel != null");
                        switch (eventFilterModel.getMatrix()) {
                            case "KM": //Km  (if Km then it should be less than five )
                                Log.d(TAG, "case KM");
                                Log.d(TAG, "case KM : "+Double.parseDouble(eventFilterModel.getAcceptabledistance()));
                                if (Double.parseDouble(eventFilterModel.getAcceptabledistance()) < 5) {
                                    fiveKmLlTv.setVisibility(View.VISIBLE);
                                } else {
                                    fiveKmLlTv.setVisibility(View.GONE);
                                }
                                break;
                            case "MILES": //Miles (if Miles then it should be less than 3 Miles | 5km ~ 3Miles )
                                Log.d(TAG, "case MILES");
                                Log.d(TAG, "case MILES : "+Double.parseDouble(eventFilterModel.getAcceptabledistance()));
                                if (Double.parseDouble(eventFilterModel.getAcceptabledistance()) < 3) {
                                    fiveKmLlTv.setVisibility(View.VISIBLE);
                                } else {
                                    fiveKmLlTv.setVisibility(View.GONE);
                                }
                                break;
                        }

                    } else {
                        Log.d(TAG, "eventFilterModel == null");
                        fiveKmLlTv.setVisibility(View.GONE);
                    }
                    Log.d(TAG, "eventFilterModel");
                }
            }
        });
    }

    private EventFilterModel retriveSavedFilterObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.EVENT_FILTER_OBJ, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            EventFilterModel profileResponse = gson.fromJson(profileObjectJsonStr, EventFilterModel.class);
            return profileResponse;
        }
        return null;
    }

    private void likeEvent(String accessToken, Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostLikeEventAsFriendWebJob(accessToken, event.getEventid()));
            startProgressBar();
        } else {
            showSnackbar(fiveKmLlTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void PostLikeEventAsFriendWebJobCompleted(HttpResponseLikeUnlike httpResponseLikeData) {
        Log.d(TAG, "onPostLikeEventAsFriendWebJobCompleted ");
        if (httpResponseLikeData != null) {
            {
                if (httpResponseLikeData.getStatus().equals(Constant.SUCCESS)) {
                    for (int i = 0; i < showEventsRecyclerAdapter.listItems.size(); i++) {
                        Event events = showEventsRecyclerAdapter.listItems.get(i);
                        if (events.getEventid() == Integer.parseInt(httpResponseLikeData.getZoneid())) {

                            Log.d(TAG, "EVENT ID MATCH " + events.getEventid() + " " + httpResponseLikeData.getZoneid());
                            showEventsRecyclerAdapter.listItems.get(i).setLike(httpResponseLikeData.is_like());
                            showEventsRecyclerAdapter.listItems.get(i).setCountLikes(httpResponseLikeData.getLike_count());
                            showEventsRecyclerAdapter.notifyItemChanged(i);
                            stopProgressBar();
                            break;
                        }
                    }
                } else {
                    showSnackbar(view, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                    stopProgressBar();
                }
            }
        } else {
            showSnackbar(view, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            stopProgressBar();
        }
        stopProgressBar();
    }

    private void sendToEventSocialAvtivity(Event item) {
        Intent intent = new Intent(getActivity(), EventSocialActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) item);
        intent.putExtra("BUNDLE_EVENT", args);
        startActivity(intent);

    }

    private void sendToEventDetailsActivity(Event item) {
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) item);
        intent.putExtra("BUNDLE_EVENT", args);
        startActivityForResult(intent, RC_REFRESH_LIST);
    }

    private void sendToHostEventDetailsActivity(Event item) {
        Intent intent = new Intent(getActivity(), EventDetailsHostActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) item);
        intent.putExtra("BUNDLE_EVENT", args);
        startActivityForResult(intent, RC_REFRESH_LIST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        isReturingFromDetailsScreen = true;

        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult RESULT_OK");
            if (requestCode == RC_REFRESH_LIST) {
                Log.d(TAG, "onActivityResult RC_REFRESH_LIST");
                new FetchEventDataAsync().execute("params");
            }
        }
    }

    private void showPopUpMenu(final boolean isHost, View anchor, final Event event) {
        final PopupMenu popup = new PopupMenu(getActivity(), anchor);
        //Inflating the Popup using xml file
        if (isHost) {
            popup.getMenuInflater().inflate(R.menu.popup_menu_host, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.popup_menu_non_host, popup.getMenu());
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (isHost) {
                    switch (item.getItemId()) {
                        case R.id.editItm:
                            sendToEditEventAvtivity(event.getEventid());
                            break;
                        case R.id.copyItm:
                            copyEvent(event);
                            break;
                    }
                } else {
                    switch (item.getItemId()) {
                        case R.id.interestItm:
                            interestedInEvent(event);
                            break;
                        case R.id.ignoreItm:
                            ignoreEvent(event);
                            break;
                        case R.id.reportItm:
                            reportEvent(event);
                            break;
                    }
                }
                return true;
            }


        });
        popup.show();//showing popup menu
    }

    private void copyEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new CopyEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(eventsRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onCopyEventWebJobCompleted(CopyEventWebJobResponse copyEventWebJobRespons) {
        if (copyEventWebJobRespons != null) {
            if (copyEventWebJobRespons.getStatus() != null && copyEventWebJobRespons.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(eventsRv, getString(R.string.copy_event_sucessful), Constant.SUCCESS);
            } else {
                showSnackbar(eventsRv, getString(R.string.copy_event_unsucessful), Constant.ERROR);
            }
        } else {
            showSnackbar(eventsRv, getString(R.string.copy_event_unsucessful), Constant.ERROR);
        }
    }

    private void ignoreEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostIgnoreEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(eventsRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostIgnoreEventWebJobCompleted(HttpResponseData httpResponseData) {
        if (httpResponseData != null) {
            if (httpResponseData.getStatus() != null && httpResponseData.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(eventsRv, getString(R.string.ignore_event_successful), Constant.SUCCESS);
            } else {
                showSnackbar(eventsRv, getString(R.string.ignore_event_unsucessful), Constant.ERROR);
            }
        } else {
            showSnackbar(eventsRv, getString(R.string.ignore_event_unsucessful), Constant.ERROR);
        }
    }

    private void interestedInEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostInterestedInEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(eventsRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostInterestedInEventWebJobCompleted(InterestedEventResponse interestedEventResponse) {
        if (interestedEventResponse != null) {
            if (interestedEventResponse.getStatus() != null && interestedEventResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(eventsRv, getString(R.string.interest_event_successful), Constant.SUCCESS);
            } else {
                showSnackbar(eventsRv, getString(R.string.interest_event_unsuccessful), Constant.ERROR);
            }
        } else {
            showSnackbar(eventsRv, getString(R.string.interest_event_unsuccessful), Constant.ERROR);
        }
    }

    private void reportEvent(Event event) {
        if (isNetworkAvailable()) {
            Log.d(TAG,"Access token report event :"+accessToken);
            jobManager.addJobInBackground(new PostReportEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(eventsRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostReportEventWebJobCompleted(ReportEventWebJobCompletedResponse reportEventWebJobCompletedResponse) {
        if (reportEventWebJobCompletedResponse != null) {
            if (reportEventWebJobCompletedResponse.getStatus() != null && reportEventWebJobCompletedResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(eventsRv, getString(R.string.report_event_successful), Constant.SUCCESS);
            } else {
                showSnackbar(eventsRv, getString(R.string.retport_event_unsuccessful), Constant.ERROR);
            }
        } else {
            showSnackbar(eventsRv, getString(R.string.retport_event_unsuccessful), Constant.ERROR);
        }
    }

    private void sendToEditEventAvtivity(int eventId) {
        Intent intent = new Intent(getActivity(), CreateEventActivity.class);
        intent.putExtra(Constant.IS_EDIT_EVENT, true);
        intent.putExtra(Constant.EDIT_EVENT_EVENT_ID, eventId);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        jobManager.addJobInBackground(new GetEventsWebJob(accessToken));

        if(allEventsSRL != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    allEventsSRL.setRefreshing(true);
                }
            });
        }
    }


    private class FetchEventDataAsync extends AsyncTask<String, Void, List<Event>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.wtf(TAG, "FetchEventDataAsync: onPreExecute");
        }

        @Override
        protected List<Event> doInBackground(String... params) {
            Log.wtf(TAG, "FetchEventDataAsync: doInBackground");
            List<Event> eventData = new ArrayList<Event>();

            try {
                dbHelper = DBHelper.getInstance(getActivity());
                //dbHelper = new DBHelper(getActivity());  // this should work
                eventData = dbHelper.getAllEvents();

                Log.i(TAG, "onCreateView " + eventData.size());

                return eventData;

            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Event> eventData) {
            super.onPostExecute(eventData);
            allEventsSRL.setRefreshing(false);
            stopProgressBar();
            Log.d(TAG, "FetchEventDataAsync: onPostExecute");
            if (eventData != null) {
                setDataToListEvents(eventData);
            } else {
                noEventsLl.setVisibility(View.VISIBLE);
                eventsRvLl.setVisibility(View.GONE);
            }
        }
    }



    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {
        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        currentAllEventsCount = sharedPreferences.getInt(Constant.ALL_EVENTS_BADGE_COUNT, 0);
        sharedEditor.remove(Constant.ALL_EVENTS_BADGE_COUNT);
        sharedEditor.commit();

        Log.wtf(TAG, "currentAllEventsCount: " + currentAllEventsCount);

        if(!isReturingFromDetailsScreen){

            if (fetchEventDataAsync != null && fetchEventDataAsync.getStatus() != AsyncTask.Status.FINISHED){
                fetchEventDataAsync.cancel(true);
            }
            fetchEventDataAsync = new FetchEventDataAsync().execute("params");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateEventList);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateAcceptInvitation);
        super.onDestroy();
    }

    private BroadcastReceiver updateEventList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "updateEventList");
            jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
            if(allEventsSRL != null){
                allEventsSRL.setRefreshing(true);
            }

            currentAllEventsCount = sharedPreferences.getInt(Constant.ALL_EVENTS_BADGE_COUNT, 0);

            SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
            sharedEditor.putInt(Constant.ALL_EVENTS_BADGE_COUNT, 0);
            sharedEditor.commit();
        }
    };


    private BroadcastReceiver updateAcceptInvitation = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateEventId = intent.getStringExtra(Constant.EVENT_ID);
            Log.d(TAG, "updateAcceptInvitation " + updateEventId);
            //new FetchEventDataAsync().execute("param");

            jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
            if(allEventsSRL != null){
                allEventsSRL.setRefreshing(true);
            }
        }
    };

    private void broadcastAllEventTabUpdate(int count) {
        Intent intent = new Intent(Constant.UPDATE_ALL_EVENTS_TAB);
        intent.putExtra(Constant.BADGE_COUNT, String.valueOf(count));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Subscribe
    public void onEventsSyncJobCompleted(EventsSyncJobCompleted eventsSyncJobCompleted) {
        Log.d(TAG, "onEventsSyncJobCompleted all events");
        stopProgressBar();

        if (eventsSyncJobCompleted != null) {
            if (eventsSyncJobCompleted.getStatus() != null && eventsSyncJobCompleted.getStatus().equals(Constant.SUCCESS)) {
                if (fetchEventDataAsync != null && fetchEventDataAsync.getStatus() != AsyncTask.Status.FINISHED){
                    fetchEventDataAsync.cancel(true);
                }
                fetchEventDataAsync = new FetchEventDataAsync().execute("params");
            } else {
                showSnackbar(noEventsLl, getString(R.string.something_went_wrong_loading_events), Constant.ERROR);
            }
        } else {
            showSnackbar(noEventsLl, getString(R.string.something_went_wrong_loading_events), Constant.ERROR);
        }
    }

}

