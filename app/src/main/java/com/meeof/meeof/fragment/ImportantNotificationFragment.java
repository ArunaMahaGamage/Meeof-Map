package com.meeof.meeof.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.activity.CreateEventActivity;
import com.meeof.meeof.activity.EventDetailsActivity;
import com.meeof.meeof.activity.EventDetailsHostActivity;
import com.meeof.meeof.activity.EventSocialActivity;
import com.meeof.meeof.adapter.NotificationRecyclerAdapter;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.helper.VpLifeCycleManager;
import com.meeof.meeof.model.DeleteNotificationWebJobResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.interested_in_event_dto.InterestedEventResponse;
import com.meeof.meeof.model.notification_dto.NotificationsData;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.CopyEventWebJob;
import com.meeof.meeof.webjob.GetImportantNotificationsJob;
import com.meeof.meeof.webjob.NotificationsSyncJobCompleted;
import com.meeof.meeof.webjob.PostIgnoreEventWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImportantNotificationFragment extends BaseFragment implements View.OnClickListener, VpLifeCycleManager, SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = ImportantNotificationFragment.class.getSimpleName();
    private static final int RC_REFRESH_LIST = 5672;

    private String accessToken;
    private int currentImportantNotificationsCount = 0;
    private SharedPreferences sharedPreferences;
    private RecyclerView privateInvitaionRv;
    public NotificationRecyclerAdapter showNotificationsRecyclerAdapter;
    private SwipeRefreshLayout importantNotificationsSRL;
    private DBHelper dbHelper;
    private TextView noEventsTv;
    private AsyncTask fetchEventDataAsync;


    public ImportantNotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_important_notification, container, false);

        initViews(view);

        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        return view;
    }

    private void initViews(View view) {
        privateInvitaionRv = (RecyclerView) view.findViewById(R.id.privateInvitaionRv);
        noEventsTv=(TextView)view.findViewById(R.id.noEventsTv);

        importantNotificationsSRL = (SwipeRefreshLayout) view.findViewById(R.id.myEventsSRL);
        int mycolor = Color.parseColor("#7d9fb7");
        importantNotificationsSRL.setColorSchemeColors(mycolor);

        importantNotificationsSRL.setOnRefreshListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {


        }
    }

    public NotificationRecyclerAdapter getAdapter(){
        return this.showNotificationsRecyclerAdapter;
    }

    private void setDataToListEvents(final List<NotificationsData> events) {
        broadcastMyEventTabUpdate(currentImportantNotificationsCount);
        currentImportantNotificationsCount = 0;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (events.size() > 0) {
                    noEventsTv.setVisibility(View.GONE);
                    privateInvitaionRv.setVisibility(View.VISIBLE);
                    showNotificationsRecyclerAdapter = new NotificationRecyclerAdapter(getActivity(), events);
                    showNotificationsRecyclerAdapter.notifyDataSetChanged();
                    privateInvitaionRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    privateInvitaionRv.setAdapter(showNotificationsRecyclerAdapter);
                    privateInvitaionRv.setItemAnimator(new DefaultItemAnimator());
                    showNotificationsRecyclerAdapter.notifyDataSetChanged();
                    Log.i(TAG, "onCreateView end");

//                    showNotificationsRecyclerAdapter.setOnClick(new showNotificationsRecyclerAdapter.OnItemClicked() {
//                        @Override
//                        public void onItemClick(int position, Event item, View threeDots) {
//                            if (item.isHost()) {
//                                showPopUpMenu(item.isHost(), threeDots, item);
//                            } else {
//                                showPopUpMenu(item.isHost(), threeDots, item);
//                            }
//                        }
//                    });

//                    showNotificationsRecyclerAdapter.setItemOnClick(new showNotificationsRecyclerAdapter.OnLlItemClicked() {
//                        @Override
//                        public void onItemLlClick(int position, Event item, View button) {
//                            Toast.makeText(getActivity(),"Event: "+item.getEventid()+" isHost: "+item.isHost(),Toast.LENGTH_SHORT).show();
//                            if (item.isHost()) {
//                                sendToHostEventDetailsActivity(item);
//                            } else {
//                                sendToEventDetailsActivity(item);
//                            }
//                        }
//                    });


//                    showNotificationsRecyclerAdapter.setOnLiketClick(new showNotificationsRecyclerAdapter.OnLikeLlItemClicked() {
//                        @Override
//                        public void onItemLlClick(int position, Event item, View button) {
//
//                            startProgressBar();
//                           // likeEvent(accessToken, item);
//
//                        }
//                    });
//
//
//                    showNotificationsRecyclerAdapter.setOnCommentClick(new showNotificationsRecyclerAdapter.onCommentClick() {
//                        @Override
//                        public void onCommentItemClick(int position, Event item) {
//                            sendToEventSocialAvtivity(item);
//                        }
//
//                    });
//
//                    showNotificationsRecyclerAdapter.setOnCommentClick(new showNotificationsRecyclerAdapter.onCommentClick() {
//                        @Override
//                        public void onCommentItemClick(int position, Event item) {
//                            sendToEventSocialAvtivity(item);
//                        }
//
//                    });
                } else {
                    noEventsTv.setVisibility(View.VISIBLE);
                    privateInvitaionRv.setVisibility(View.GONE);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void PostLikeEventAsFriendWebJobCompleted(HttpResponseLikeUnlike httpResponseLikeData) {
        Log.d(TAG, "onPostLikeEventAsFriendWebJobCompleted ");
        if (httpResponseLikeData != null) {
            {
                if (httpResponseLikeData.getStatus().equals(Constant.SUCCESS)) {
                    for (int i = 0; i < showNotificationsRecyclerAdapter.listItem.size(); i++) {
                        NotificationsData notificationsData = showNotificationsRecyclerAdapter.listItem.get(i);
                        if (notificationsData.getZone_id() == Integer.parseInt(httpResponseLikeData.getZoneid())) {

                            Log.d(TAG, "EVENT ID MATCH " + notificationsData.getContent() + " " + httpResponseLikeData.getZoneid());
                            //showNotificationsRecyclerAdapter.listItem.get(i).setLike(httpResponseLikeData.is_like());
                            //showNotificationsRecyclerAdapter.listItem.get(i).setCountLikes(httpResponseLikeData.getLike_count());
                            showNotificationsRecyclerAdapter.notifyItemChanged(i);
                           stopProgressBar();
                            break;
                        }
                    }
                } else {
                    showSnackbar(privateInvitaionRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                    stopProgressBar();
                }
            }
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
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
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult RESULT_OK");
            if (requestCode == RC_REFRESH_LIST) {
                Log.d(TAG, "onActivityResult RC_REFRESH_LIST");
                new ImportantNotificationFragment.FetchEventDataAsync().execute("params");
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
                Log.d(TAG, "Inside Menu Click");
                Log.d(TAG, "Clicked Item: " + item.getItemId());
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
                            //interestItminterestedInEvent(event);
                            break;
                        case R.id.ignoreItm:
                            ignoreEvent(event);
                            break;
                        case R.id.reportItm:
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
            showSnackbar(privateInvitaionRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void ignoreEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostIgnoreEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostIgnoreEventWebJobCompleted(HttpResponseData httpResponseData) {
        if (httpResponseData != null) {
            if (httpResponseData.getStatus() != null && httpResponseData.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(privateInvitaionRv, getString(R.string.ignore_event_successful), Constant.ERROR);
            } else {
                showSnackbar(privateInvitaionRv, getString(R.string.ignore_event_unsucessful), Constant.ERROR);
            }
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.ignore_event_unsucessful), Constant.ERROR);
        }
    }



    @Subscribe
    public void onPostInterestedInEventWebJobCompleted(InterestedEventResponse interestedEventResponse) {
        if (interestedEventResponse != null) {
            if (interestedEventResponse.getStatus() != null && interestedEventResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(privateInvitaionRv, getString(R.string.interest_event_successful), Constant.ERROR);
            } else {
                showSnackbar(privateInvitaionRv, getString(R.string.interest_event_unsuccessful), Constant.ERROR);
            }
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.interest_event_unsuccessful), Constant.ERROR);
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
        Log.d(TAG, "pull to refresh");

        jobManager.addJobInBackground(new GetImportantNotificationsJob(accessToken));

//        if(importantNotificationsSRL != null){
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    importantNotificationsSRL.setRefreshing(true);
//                }
//            });
//        }
    }

    private class FetchEventDataAsync extends AsyncTask<String, Void, List<NotificationsData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<NotificationsData> doInBackground(String... params) {
            List<NotificationsData> eventData = new ArrayList<NotificationsData>();
            try {
                dbHelper = DBHelper.getInstance(getActivity());
                eventData = dbHelper.getAllNotifications();
                Log.d(TAG, "MyNotifications data size " + eventData.size());

                return eventData;

            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<NotificationsData> eventData) {
            super.onPostExecute(eventData);

            if(importantNotificationsSRL != null){
                importantNotificationsSRL.setRefreshing(false);
            }
            stopProgressBar();
            if (eventData != null) {
                setDataToListEvents(eventData);
            }
        }
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
        super.onDestroy();
    }

    @Override
    public void onPauseFragment() {

    }

    @Override
    public void onResumeFragment() {

        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPreferences.edit();
        currentImportantNotificationsCount = sharedPreferences.getInt(Constant.IMPORTANT_NOTIFICATIONS_BADGE_COUNT, 0);
        sharedEditor.remove(Constant.IMPORTANT_NOTIFICATIONS_BADGE_COUNT);
        sharedEditor.commit();

        if (fetchEventDataAsync != null && fetchEventDataAsync.getStatus() != AsyncTask.Status.FINISHED){
            fetchEventDataAsync.cancel(true);
        }
        fetchEventDataAsync = new ImportantNotificationFragment.FetchEventDataAsync().execute("params");
    }

    private BroadcastReceiver updateEventList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "updateEventList");
            jobManager.addJobInBackground(new GetImportantNotificationsJob(accessToken));

            if(importantNotificationsSRL != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        importantNotificationsSRL.setRefreshing(true);
                    }
                });
            }
        }
    };

    private void broadcastMyEventTabUpdate(int count) {
        Intent intent = new Intent(Constant.UPDATE_MY_EVENTS_TAB);
        intent.putExtra(Constant.BADGE_COUNT, String.valueOf(count));
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Subscribe
    public void onEventsSyncJobCompleted(NotificationsSyncJobCompleted notificationsSyncJobCompleted) {
        Log.d(TAG, "onEventsSyncJobCompleted");
        stopProgressBar();

        if (notificationsSyncJobCompleted != null) {
            if (notificationsSyncJobCompleted.getStatus() != null && notificationsSyncJobCompleted.getStatus().equals(Constant.SUCCESS)) {
                if (fetchEventDataAsync != null && fetchEventDataAsync.getStatus() != AsyncTask.Status.FINISHED){
                    fetchEventDataAsync.cancel(true);
                }
                fetchEventDataAsync = new ImportantNotificationFragment.FetchEventDataAsync().execute("params");

            } else {
                showSnackbar(privateInvitaionRv, getString(R.string.something_went_wrong_loading_your_notifications), Constant.ERROR);
            }
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.something_went_wrong_loading_your_notifications), Constant.ERROR);
        }
    }
    @Subscribe
    public void onDeleteEventWebJob(DeleteNotificationWebJobResponse deleteNotificationWebJobResponse) {
        Log.w(TAG, "deleteNotificationWebJobResponse "+deleteNotificationWebJobResponse.getStatus());
        if (deleteNotificationWebJobResponse != null) {
            if (deleteNotificationWebJobResponse.getStatus() != null && deleteNotificationWebJobResponse.getStatus().equals(Constant.SUCCESS)) {

                showSnackbar(privateInvitaionRv, getString(R.string.notification_delete_successfully), Constant.SUCCESS);
                dbHelper.deleteAllNotifications();
                jobManager.addJobInBackground(new GetImportantNotificationsJob(accessToken));
            } else {
                showSnackbar(privateInvitaionRv, getString(R.string.notification_delete_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.notification_delete_failed), Constant.ERROR);
        }
    }
}