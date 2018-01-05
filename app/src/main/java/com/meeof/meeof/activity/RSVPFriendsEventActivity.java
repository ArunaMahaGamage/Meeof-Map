package com.meeof.meeof.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.GoingFriendRecyclerAdapter;
import com.meeof.meeof.adapter.InterestedFriendsRecyclerAdapter;
import com.meeof.meeof.adapter.InvitedFriendsRecyclerAdapter;
import com.meeof.meeof.adapter.MaybeFriendsRecyclerAdapter;
import com.meeof.meeof.adapter.NotGoingFriendsRecyclerAdapter;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.FriendStatus;
import com.meeof.meeof.model.GetEventsWebJobCompletedEvent;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.model.ReloadEventData;
import com.meeof.meeof.model.events_dto.AttendeeList;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.EventsSyncJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;
import com.meeof.meeof.webjob.PostAddAsFriendWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Visni on 10/24/2017.
 */

public class RSVPFriendsEventActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = RSVPFriendsEventActivity.class.getSimpleName();
    private static final int RC_REFRESH_EVENTS_DATA = 98678;
    private RecyclerView goingRv, notGoingRv, maybeGoingRv, invitedRv, interestedRv;
    List<AttendeeList> attendeeList;
    private Event event;
    private String accessToken;
    private String currentInvitedAddedFriend;
    private LinearLayout closeLlBtn;
    private LinearLayout goingLl, notGoingLl, maybeLl, interestedLl, invitedLl;
    private int currentUserId;
    private boolean hasDataSetChanged;
    private RelativeLayout whiteView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsvp_invited_details);
        hasDataSetChanged = false;
        initViews();
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");


        if (this.getIntent().hasExtra("BUNDLE_EVENT")) {
            Log.d(TAG, "extrass not null");
            Bundle args = this.getIntent().getBundleExtra("BUNDLE_EVENT");
            event = (Event) args.getSerializable(Constant.SELECTED_EVENT_ITEM);
            attendeeList = event.getAttendeeList();
        } else {
            Log.d(TAG, "extrass null");
        }

        getAllFriends(true);
        Log.d(TAG, "event: " + event.toString());
    }


    private void setFrinendList(List<AttendeeList> attendeeList, List<FriendStatus> friendStatusList) {

        List<AttendeeList> goingList = new ArrayList<>();
        List<AttendeeList> notGoingList = new ArrayList<>();
        List<AttendeeList> maybeList = new ArrayList<>();
        List<AttendeeList> invitedList = new ArrayList<>();
        List<AttendeeList> interestedList = new ArrayList<>();

        for (AttendeeList list : attendeeList) {
            if (list.getRsvp() == 1) {
                invitedList.add(list);
            } else if (list.getRsvp() == 2) {
                interestedList.add(list);
            } else if (list.getRsvp() == 3) {
                goingList.add(list);
            } else if (list.getRsvp() == 4) {
                notGoingList.add(list);
            } else if (list.getRsvp() == 5) {
                maybeList.add(list);
            }
        }


        Log.d(TAG, "Going list size - " + goingList.size());
        Log.d(TAG, "notGoingList list size - " + notGoingList.size());
        Log.d(TAG, "maybeList list size - " + maybeList.size());
        Log.d(TAG, "invitedList list size - " + invitedList.size());
        Log.d(TAG, "interestedList list size - " + interestedList.size());

        if (goingList.size() == 0) {
            goingLl.setVisibility(View.GONE);
        }
        if (notGoingList.size() == 0) {
            notGoingLl.setVisibility(View.GONE);
        }
        if (maybeList.size() == 0) {
            maybeLl.setVisibility(View.GONE);
        }
        if (invitedList.size() == 0) {
            invitedLl.setVisibility(View.GONE);
        }
        if (interestedList.size() == 0) {
            interestedLl.setVisibility(View.GONE);
        }

        setDataToGoing(goingList, friendStatusList);
        setDataToNotGoing(notGoingList, friendStatusList);
        setDataToMaybeGoing(maybeList, friendStatusList);
        setDataToInvited(invitedList, friendStatusList);
        setDataToInterested(interestedList, friendStatusList);

    }

    private void initViews() {
        goingRv = (RecyclerView) findViewById(R.id.goingRv);
        notGoingRv = (RecyclerView) findViewById(R.id.notGoingRv);
        maybeGoingRv = (RecyclerView) findViewById(R.id.maybeGoingRv);
        invitedRv = (RecyclerView) findViewById(R.id.invitedRv);
        interestedRv = (RecyclerView) findViewById(R.id.interestedRv);
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        goingLl = (LinearLayout) findViewById(R.id.goingLl);
        notGoingLl = (LinearLayout) findViewById(R.id.notGoingLl);
        maybeLl = (LinearLayout) findViewById(R.id.maybeLl);
        interestedLl = (LinearLayout) findViewById(R.id.interestedLl);
        invitedLl = (LinearLayout) findViewById(R.id.invitedLl);
        whiteView = (RelativeLayout) findViewById(R.id.whiteView);

        closeLlBtn.setOnClickListener(this);
    }


    private void setDataToGoing(List<AttendeeList> goingList, List<FriendStatus> friendStatusList) {

        //delete going GoingFriendsRecyclerAdapter(s)

        if (goingList != null) {
            GoingFriendRecyclerAdapter goingFriendsRecyclerAdapter = new GoingFriendRecyclerAdapter(RSVPFriendsEventActivity.this, goingList, friendStatusList);
            goingRv.setLayoutManager(new LinearLayoutManager(RSVPFriendsEventActivity.this));
            goingRv.setAdapter(goingFriendsRecyclerAdapter);
            goingRv.setItemAnimator(new DefaultItemAnimator());
            goingFriendsRecyclerAdapter.notifyDataSetChanged();


            goingFriendsRecyclerAdapter.setOnClick(new GoingFriendRecyclerAdapter.OnItemClicked() {

                @Override
                public void onItemClick(int position, AttendeeList item, GoingFriendRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {

                    if (item.getStatus() != null && item.getStatus().equals("active")) {
                        if (item.getFriendstatus().equals("friend")) {
                            //NOTHING
                        } else if (item.getFriendstatus().equals("pending")) {
                            //NOTHING
                        } else if (item.getFriendstatus().equals("notfriend")) {
                            //TODO ADD AS FRIEND
                            addAsFriend(item);
                            currentInvitedAddedFriend = item.getFirstName();
                        }
                    } else if (item.getStatus().equals("dummy")) {
                        Log.d(TAG, "dummy");
                    }
                }
            });
        } else {
            Log.d(TAG, "going list null");
        }


    }

    private void setDataToNotGoing(List<AttendeeList> notGoingList, List<FriendStatus> friendStatusList) {

        if (notGoingList != null) {
            NotGoingFriendsRecyclerAdapter notGoingFriendsRecyclerAdapter = new NotGoingFriendsRecyclerAdapter(RSVPFriendsEventActivity.this, notGoingList);
            notGoingRv.setLayoutManager(new LinearLayoutManager(RSVPFriendsEventActivity.this));
            notGoingRv.setAdapter(notGoingFriendsRecyclerAdapter);
            notGoingRv.setItemAnimator(new DefaultItemAnimator());
            notGoingFriendsRecyclerAdapter.notifyDataSetChanged();

            notGoingFriendsRecyclerAdapter.setOnClick(new NotGoingFriendsRecyclerAdapter.OnItemClicked() {
                @Override
                public void onItemClick(int position, AttendeeList item, NotGoingFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {

                    if (item.getStatus() != null && item.getStatus().equals("active")) {
                        if (item.getFriendstatus().equals("friend")) {
                            //NOTHING
                        } else if (item.getFriendstatus().equals("pending")) {
                            //NOTHING
                        } else if (item.getFriendstatus().equals("notfriend")) {
                            //TODO ADD AS FRIEND
                            addAsFriend(item);
                            currentInvitedAddedFriend = item.getFirstName();
                        }
                    } else {
                        Log.d(TAG, "dummy");
                    }
                }
            });

        } else {
            Log.d(TAG, "notgoing list null");
        }

    }


    private void setDataToMaybeGoing(List<AttendeeList> maybeList, List<FriendStatus> friendStatusList) {

        MaybeFriendsRecyclerAdapter maybeFriendsRecyclerAdapter = new MaybeFriendsRecyclerAdapter(RSVPFriendsEventActivity.this, maybeList);
        maybeGoingRv.setLayoutManager(new LinearLayoutManager(RSVPFriendsEventActivity.this));
        maybeGoingRv.setAdapter(maybeFriendsRecyclerAdapter);
        maybeGoingRv.setItemAnimator(new DefaultItemAnimator());
        maybeFriendsRecyclerAdapter.notifyDataSetChanged();

        maybeFriendsRecyclerAdapter.setOnClick(new MaybeFriendsRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, AttendeeList item, MaybeFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {

                if (item.getStatus() != null && item.getStatus().equals("active")) {
                    if (item.getFriendstatus().equals("friend")) {
                        //NOTHING
                    } else if (item.getFriendstatus().equals("pending")) {
                        //NOTHING
                    } else if (item.getFriendstatus().equals("notfriend")) {
                        //TODO ADD AS FRIEND
                        addAsFriend(item);
                        currentInvitedAddedFriend = item.getFirstName();
                    }
                } else if (item.getStatus().equals("dummy")) {
                    Log.d(TAG, "dummy");
                }
            }
        });
    }

    private void setDataToInvited(List<AttendeeList> invitedList, List<FriendStatus> friendStatusList) {

        InvitedFriendsRecyclerAdapter invitedFriendsRecyclerAdapter = new InvitedFriendsRecyclerAdapter(RSVPFriendsEventActivity.this, invitedList);
        invitedRv.setLayoutManager(new LinearLayoutManager(RSVPFriendsEventActivity.this));
        invitedRv.setAdapter(invitedFriendsRecyclerAdapter);
        invitedRv.setItemAnimator(new DefaultItemAnimator());
        invitedFriendsRecyclerAdapter.notifyDataSetChanged();

        invitedFriendsRecyclerAdapter.setOnClick(new InvitedFriendsRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, AttendeeList item, InvitedFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {

                if (item.getStatus() != null && item.getStatus().equals("active")) {
                    if (item.getFriendstatus().equals("friend")) {
                        //NOTHING
                    } else if (item.getFriendstatus().equals("pending")) {
                        //NOTHING
                    } else if (item.getFriendstatus().equals("notfriend")) {
                        //TODO ADD AS FRIEND
                        addAsFriend(item);
                        currentInvitedAddedFriend = item.getFirstName();
                    }
                } else if (item.getStatus().equals("dummy")) {
                    Log.d(TAG, "dummy");
                }
            }
        });
    }

    private void setDataToInterested(List<AttendeeList> interestedList, List<FriendStatus> friendStatusList) {

        InterestedFriendsRecyclerAdapter interestedFriendsRecyclerAdapter = new InterestedFriendsRecyclerAdapter(RSVPFriendsEventActivity.this, interestedList, friendStatusList);
        interestedRv.setLayoutManager(new LinearLayoutManager(RSVPFriendsEventActivity.this));
        interestedRv.setAdapter(interestedFriendsRecyclerAdapter);
        interestedRv.setItemAnimator(new DefaultItemAnimator());
        interestedFriendsRecyclerAdapter.notifyDataSetChanged();

        interestedFriendsRecyclerAdapter.setOnClick(new InterestedFriendsRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, AttendeeList item, InterestedFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {

                if (item.getStatus() != null && item.getStatus().equals("active")) {
                    if (item.getFriendstatus().equals("friend")) {
                        //NOTHING
                    } else if (item.getFriendstatus().equals("pending")) {
                        //NOTHING
                    } else if (item.getFriendstatus().equals("notfriend")) {
                        //TODO ADD AS FRIEND
                        Log.d(TAG, "add as friend called ");
                        addAsFriend(item);
                        currentInvitedAddedFriend = item.getFirstName();
                    }
                } else if (item.getStatus().equals("dummy")) {
                    Log.d(TAG, "dummy");
                }
            }
        });
    }

    private void addAsFriend(AttendeeList item) {
        if (isNetworkAvailable()) {
            int userId = item.getUserId();
            if (userId >= 0) {
                Log.d(TAG, "userId -" + userId);
                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
                startProgressBar();
            } else {
                Log.d(TAG, "USER ID =0");
                showSnackbar(closeLlBtn, getString(R.string.no_internet), Constant.ERROR);
            }
        } else {
            showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            Log.d(TAG, "NO NETWORK");
        }
    }

    @Subscribe
    public void onPostAddAsFriendJobCompleted(HttpResponseForAddFriend httpResponseForAddFriend) {
        if (httpResponseForAddFriend != null) {
            if (httpResponseForAddFriend.getStatus() != null && httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
                //showSnackbar(closeLlBtn, "Friend Request Sent Succesfully", Constant.SUCCESS);
                //refreshEventsData();
                getAllFriends(true);
                Log.d(TAG, "ADDED AS A FRIEND");
            } else {
                stopProgressBar();
                showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
                Log.d(TAG, "ADDED AS A FRIEND FAILED");
            }
        } else {
            stopProgressBar();
            showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            Log.d(TAG, "ADDED AS A FRIEND FAILED ELSE");
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.closeLlBtn:
                if(hasDataSetChanged){
                    Log.d(TAG, "hasDataSetChanged");
                    EventBus.getDefault().postSticky(new ReloadEventData(Constant.SUCCESS, null));
                }
                this.finish();
                break;
        }
    }

    public void refreshEventsData(){
        jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
    }

    @Subscribe
    public void onGetEventsWebJobCompletedEvent(GetEventsWebJobCompletedEvent getEventsWebJobCompletedEvent) {

        Log.d(TAG, "onGetEventsWebJobCompletedEvent 1");

        if (getEventsWebJobCompletedEvent != null) {

            if (getEventsWebJobCompletedEvent.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {
                String events = getEventsWebJobCompletedEvent.getEvents().toString();
                String attendance = getEventsWebJobCompletedEvent.getAttendance().toString();
                syncEventToDb(events, attendance);
                Log.d(TAG, "onGetEventsWebJobCompletedEvent 4");
            } else {
                stopProgressBar();
                showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
        }
    }

    public void syncEventToDb(String events, String attendance){
        jobManager.addJobInBackground(new EventsSyncJob(events, attendance));
    }

    @Subscribe
    public void onEventsSyncJobCompleted(EventsSyncJobCompleted eventsSyncJobCompleted) {
        Log.d(TAG, "onEventsSyncJobCompleted");

        if (eventsSyncJobCompleted != null) {
            if (eventsSyncJobCompleted.getStatus() != null && eventsSyncJobCompleted.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(closeLlBtn, "Friend Request Sent Succesfully", Constant.SUCCESS);
                hasDataSetChanged = true;
                getAllFriends(true);
            } else {
                stopProgressBar();
                showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void getAllFriends(boolean isWithProgress) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetMyAllFriendsWebJob(accessToken));
            if(isWithProgress) {
                startProgressBar();
            }
        } else {
            showSnackbar(closeLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyAllFriendsCompleted(FriendsAllResponse friendsAllResponse) {
        if (friendsAllResponse != null) {
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> friends = friendsAllResponse.getFriends();
                List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();
                List<FriendStatus> statusList = new ArrayList<>();

                for(Friends friend : friends){
                    FriendStatus friendStatus = new FriendStatus(friend.getId(), friend.getFriend_status());
                    statusList.add(friendStatus);
                }

                for(Pending_requests pendingRequest : pendingRequests){
                    FriendStatus friendStatus = new FriendStatus(pendingRequest.getUser_id(), "pending");
                    statusList.add(friendStatus);
                }

                setFrinendList(attendeeList, statusList);
                whiteView.setVisibility(View.GONE);
                stopProgressBar();

            } else {
                showSnackbar(closeLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(closeLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }
}
