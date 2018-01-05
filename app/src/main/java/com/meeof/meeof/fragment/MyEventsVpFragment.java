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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.activity.CreateEventActivity;
import com.meeof.meeof.activity.EventDetailsActivity;
import com.meeof.meeof.activity.EventDetailsHostActivity;
import com.meeof.meeof.activity.EventSocialActivity;
import com.meeof.meeof.activity.InviteFriendsToEventActivity;
import com.meeof.meeof.adapter.PrivateInvitationAdapter;
import com.meeof.meeof.adapter.ShowEventsRecyclerAdapter;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.helper.VpLifeCycleManager;
import com.meeof.meeof.model.AllEventsSyncedJobResponse;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.interested_in_event_dto.InterestedEventResponse;
import com.meeof.meeof.model.private_invitation_dto_model.Array_events;
import com.meeof.meeof.model.private_invitation_dto_model.PrivateInvitationsResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.CopyEventWebJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.GetPrivateInvitationWebJob;
import com.meeof.meeof.webjob.PostIgnoreEventWebJob;
import com.meeof.meeof.webjob.PostInterestedInEventWebJob;
import com.meeof.meeof.webjob.PostLikeEventAsFriendWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class MyEventsVpFragment extends BaseFragment implements View.OnClickListener, VpLifeCycleManager, SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = MyEventsVpFragment.class.getSimpleName();
    private static final int RC_REFRESH_LIST = 5672;

    private String accessToken;
    private int currentMyEventsCount = 0;
    private SharedPreferences sharedPreferences;
    private RecyclerView privateInvitaionRv;
    private RecyclerView privateInvitationRecyclerView;
    public ShowEventsRecyclerAdapter showEventsRecyclerAdapter;
    public PrivateInvitationAdapter privateInvitationAdapter;
    private SwipeRefreshLayout myEventsSRL;
    private DBHelper dbHelper;
    private TextView noEventsTv;
    private AsyncTask fetchEventDataAsync;


    public MyEventsVpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_my_event, container, false);

        initViews(view);

        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        return view;
    }

    private void initViews(View view) {
        privateInvitaionRv = (RecyclerView) view.findViewById(R.id.privateInvitaionRv);
        privateInvitationRecyclerView=(RecyclerView)view.findViewById(R.id.privateInvitationRecyclerView);
        noEventsTv=(TextView)view.findViewById(R.id.noEventsTv);

        myEventsSRL = (SwipeRefreshLayout) view.findViewById(R.id.myEventsSRL);
        int mycolor = Color.parseColor("#7d9fb7");
        myEventsSRL.setColorSchemeColors(mycolor);

        myEventsSRL.setOnRefreshListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {


        }
    }

    public ShowEventsRecyclerAdapter getAdapter(){
        return this.showEventsRecyclerAdapter;
    }

    private void setDataToListEvents(final List<Event> events) {
        broadcastMyEventTabUpdate(currentMyEventsCount);
        currentMyEventsCount = 0;

        Log.i(TAG, "setDataToListEvents "+events);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (events.size() > 0) {
                    noEventsTv.setVisibility(View.GONE);
                    privateInvitaionRv.setVisibility(View.VISIBLE);
                    showEventsRecyclerAdapter = new ShowEventsRecyclerAdapter(getActivity(), events);
                    privateInvitaionRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    privateInvitaionRv.setHasFixedSize(true);
                    privateInvitaionRv.setNestedScrollingEnabled(false);
                    privateInvitaionRv.setAdapter(showEventsRecyclerAdapter);
                    privateInvitaionRv.setItemAnimator(new DefaultItemAnimator());
                    showEventsRecyclerAdapter.notifyDataSetChanged();
                    Log.i(TAG, "onCreateView end");



                    showEventsRecyclerAdapter.setOnClick(new ShowEventsRecyclerAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position, Event item, View threeDots) {
                            if (item.isHost()) {
                                showPopUpMenu(item.isHost(), threeDots, item);
                            } else {
                                showPopUpMenu(item.isHost(), threeDots, item);
                            }
                        }
                    });

                    showEventsRecyclerAdapter.setItemOnClick(new ShowEventsRecyclerAdapter.OnLlItemClicked() {
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


                    showEventsRecyclerAdapter.setOnLiketClick(new ShowEventsRecyclerAdapter.OnLikeLlItemClicked() {
                        @Override
                        public void onItemLlClick(int position, Event item, View button) {

                            startProgressBar();
                            likeEvent(accessToken, item);

                        }
                    });


                    showEventsRecyclerAdapter.setOnCommentClick(new ShowEventsRecyclerAdapter.onCommentClick() {
                        @Override
                        public void onCommentItemClick(int position, Event item) {
                            sendToEventSocialAvtivity(item);
                        }

                    });

                    showEventsRecyclerAdapter.setOnCommentClick(new ShowEventsRecyclerAdapter.onCommentClick() {
                        @Override
                        public void onCommentItemClick(int position, Event item) {
                            sendToEventSocialAvtivity(item);
                        }

                    });
                } else {
                    noEventsTv.setVisibility(View.VISIBLE);
                    privateInvitaionRv.setVisibility(View.GONE);
                }
            }
        });

        Log.d(TAG,"getPrivateInvitaions");
        getPrivateInvitaions();


    }

    private void likeEvent(String accessToken, Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostLikeEventAsFriendWebJob(accessToken, event.getEventid()));
        } else {
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

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetPrivateInvitationsCompleted(PrivateInvitationsResponse privateInvitationsResponse) {
        Log.d(TAG, "onGetPrivateInvitationsCompleted "+ privateInvitationsResponse.toString());

        if (privateInvitationsResponse != null) {
            {
                if (privateInvitationsResponse.getStatus().equals(Constant.SUCCESS)) {
//                    showEventsRecyclerAdapter.addPrivateInvitaions(privateInvitationsResponse.getReturnData().getArray_events());
//                    showEventsRecyclerAdapter.notifyDataSetChanged();
//
//                    noEventsTv.setVisibility(View.GONE);
//                    privateInvitaionRv.setVisibility(View.VISIBLE);


                    List<Event> listItems=new ArrayList<>();

                    for(Array_events privateInvitation:privateInvitationsResponse.getReturnData().getArray_events()){
                        Event event=new Event();
                        event.setCountLikes(privateInvitation.getCountLikes());
                        event.setCategory_id(privateInvitation.getCategory_id());
                        event.setChannel_id(privateInvitation.getChannel_id());
                        event.setCountComments(privateInvitation.getCountComments());
                        event.setCountPhotos(privateInvitation.getCountPhotos());
                        event.setCreated_at(privateInvitation.getCreated_at());
                        event.setDescription(privateInvitation.getDescription());
                        event.setDetailedaddress(privateInvitation.getDetailedaddress());
                        event.setDistance(privateInvitation.getDistance());
                        event.setCountLikes(privateInvitation.getCountLikes());
                        event.setEnd_date(privateInvitation.getEnd_date());
                        event.setFirst_name(privateInvitation.getFirst_name());
                        event.setEvent_poster(privateInvitation.getEvent_poster());
                        event.setUser_id(privateInvitation.getUser_id());
                        event.setType(privateInvitation.getType());
                        event.setTitle(privateInvitation.getTitle());
                        event.setStart_date(privateInvitation.getStart_date());
                        event.setLocation(privateInvitation.getLocation());
                        event.setProfilephoto(privateInvitation.getProfilephoto());
                        event.setLive(privateInvitation.getLive());
                        event.setChannel_id(privateInvitation.getChannel_id());
                        event.setEventid(privateInvitation.getEventid());
                        event.setPlaceName(privateInvitation.getPlaceName());
                        event.setPlaceID(privateInvitation.getPlaceID());
                        event.setLongitude(privateInvitation.getLongitude());
                        event.setLatitude(privateInvitation.getLatitude());
                        event.setCreated_at(privateInvitation.getCreated_at());
                        event.setOrganizer_id(privateInvitation.getOrganizer_id());
                        event.setCategory_id(privateInvitation.getCategory_id());
                        event.setIs_hide_location(privateInvitation.getIs_hide_location());
                        event.setMax_attendees(privateInvitation.getMax_attendees());
                        event.setTier1(privateInvitation.getTier1());
                        event.setInterestName(privateInvitation.getInterestName());


                        Log.d(TAG,"addPrivateInvitaions :"+event.toString());

                        listItems.add(0,event);
                    }


                    privateInvitationAdapter = new PrivateInvitationAdapter(getActivity(), listItems);
                    privateInvitationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    privateInvitationRecyclerView.setHasFixedSize(true);
                    privateInvitationRecyclerView.setNestedScrollingEnabled(false);
                    privateInvitationRecyclerView.setAdapter(privateInvitationAdapter);
                    privateInvitationRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    privateInvitationAdapter.notifyDataSetChanged();
                } else {
                    showSnackbar(privateInvitationRecyclerView, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                    stopProgressBar();
                }
            }
        } else {
            showSnackbar(privateInvitationRecyclerView, getString(R.string.oop_something_went_wrong), Constant.ERROR);
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
                            interestedInEvent(event);
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

    private void getPrivateInvitaions(){
        if (isNetworkAvailable()) {
            Log.d(TAG,"getPrivateInvitaions access: "+accessToken);
            jobManager.addJobInBackground(new GetPrivateInvitationWebJob(accessToken));
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

    private void interestedInEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostInterestedInEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.no_internet), Constant.ERROR);
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

        jobManager.addJobInBackground(new GetEventsWebJob(accessToken));

//        if(myEventsSRL != null){
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    myEventsSRL.setRefreshing(true);
//                }
//            });
//        }
    }

    private class FetchEventDataAsync extends AsyncTask<String, Void, List<Event>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Event> doInBackground(String... params) {
            List<Event> eventData = new ArrayList<Event>();
            try {
                dbHelper = DBHelper.getInstance(getActivity());
                eventData = dbHelper.searchMyEvents();
                Log.d(TAG, "MyEvents data size " + eventData.size());

                return eventData;

            } catch (Exception ex) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Event> eventData) {
            super.onPostExecute(eventData);

            if(myEventsSRL != null){
                myEventsSRL.setRefreshing(false);
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
        currentMyEventsCount = sharedPreferences.getInt(Constant.MY_EVENTS_BADGE_COUNT, 0);
        sharedEditor.remove(Constant.MY_EVENTS_BADGE_COUNT);
        sharedEditor.commit();

        if (fetchEventDataAsync != null && fetchEventDataAsync.getStatus() != AsyncTask.Status.FINISHED){
            fetchEventDataAsync.cancel(true);
        }
        fetchEventDataAsync = new FetchEventDataAsync().execute("params");
    }

    private BroadcastReceiver updateEventList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "updateEventList");
            jobManager.addJobInBackground(new GetEventsWebJob(accessToken));

            if(myEventsSRL != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myEventsSRL.setRefreshing(true);
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
    public void onEventsSyncJobCompleted(EventsSyncJobCompleted eventsSyncJobCompleted) {
        Log.d(TAG, "onEventsSyncJobCompleted");
        stopProgressBar();

        if (eventsSyncJobCompleted != null) {
            if (eventsSyncJobCompleted.getStatus() != null && eventsSyncJobCompleted.getStatus().equals(Constant.SUCCESS)) {
                if (fetchEventDataAsync != null && fetchEventDataAsync.getStatus() != AsyncTask.Status.FINISHED){
                    fetchEventDataAsync.cancel(true);
                }
                fetchEventDataAsync = new FetchEventDataAsync().execute("params");

            } else {
                showSnackbar(privateInvitaionRv, getString(R.string.something_went_wrong_loading_your_events), Constant.ERROR);
            }
        } else {
            showSnackbar(privateInvitaionRv, getString(R.string.something_went_wrong_loading_your_events), Constant.ERROR);
        }
    }
}
