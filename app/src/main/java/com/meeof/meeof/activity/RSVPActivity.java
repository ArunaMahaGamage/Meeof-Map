//package com.meeof.meeof.activity;
//
//import android.os.Bundle;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.View;
//import android.widget.LinearLayout;
//
//import com.meeof.meeof.R;
//import com.meeof.meeof.adapter.GoingFriendRecyclerAdapter;
//import com.meeof.meeof.adapter.MaybeFriendsRecyclerAdapter;
//import com.meeof.meeof.adapter.NotGoingFriendsRecyclerAdapter;
//import com.meeof.meeof.model.HttpResponseForAddFriend;
//import com.meeof.meeof.model.events_dto.AttendeeList;
//import com.meeof.meeof.model.events_dto.Event;
//import com.meeof.meeof.util.Constant;
//import com.meeof.meeof.webjob.PostAddAsFriendWebJob;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Visni on 10/24/2017.
// */
//
//public class RSVPActivity extends BaseActivity implements View.OnClickListener {
//    private static final String TAG = RSVPActivity.class.getSimpleName();
//    private RecyclerView goingRv, notGoingRv, maybeGoingRv, invitedRv, interestedRv;
//    List<AttendeeList> attendeeList;
//    private Event event;
//    private String accessToken;
//    private String currentInvitedAddedFriend;
//    private LinearLayout closeLlBtn;
//    private LinearLayout notGoingLl, goingLl, maybeLl;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_rsvp);
//
//        initViews();
//        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
//        if (this.getIntent().hasExtra("BUNDLE_EVENT")) {
//            Log.d(TAG, "extrass not null");
//            Bundle args = this.getIntent().getBundleExtra("BUNDLE_EVENT");
//            event = (Event) args.getSerializable(Constant.SELECTED_EVENT_ITEM);
//            attendeeList = event.getAttendeeList();
//            setFrinendList(attendeeList);
//        } else {
//            Log.d(TAG, "extrass null");
//        }
//
//
//    }
//
//
//    private void setFrinendList(List<AttendeeList> attendeeList) {
//
//        List<AttendeeList> goingList = new ArrayList<>();
//        List<AttendeeList> notGoingList = new ArrayList<>();
//        List<AttendeeList> maybeList = new ArrayList<>();
//        List<AttendeeList> invitedList = new ArrayList<>();
//        List<AttendeeList> interestedList = new ArrayList<>();
//
//        for (AttendeeList list : attendeeList) {
//            if (list.getRsvp() == 1) {
//                invitedList.add(list);
//            } else if (list.getRsvp() == 2) {
//                interestedList.add(list);
//            } else if (list.getRsvp() == 3) {
//                goingList.add(list);
//            } else if (list.getRsvp() == 4) {
//                notGoingList.add(list);
//            } else if (list.getRsvp() == 5) {
//                maybeList.add(list);
//            }
//        }
//
//
//        Log.d(TAG, "Going list size - " + goingList.size());
//
//        if (goingList.size() == 0) {
//            goingLl.setVisibility(View.GONE);
//        }
//        if (notGoingList.size() == 0) {
//            notGoingLl.setVisibility(View.GONE);
//        }
//        if (maybeList.size() == 0) {
//            maybeLl.setVisibility(View.GONE);
//        }
//
//        setDataToGoing(goingList);
//        setDataToNotGoing(notGoingList);
//        setDataToMaybeGoing(maybeList);
//        //setDataToInvited(invitedList);
//        //setDataToInterested(interestedList);
//    }
//
//    private void initViews() {
//        goingRv = (RecyclerView) findViewById(R.id.goingRv);
//        notGoingRv = (RecyclerView) findViewById(R.id.notGoingRv);
//        maybeGoingRv = (RecyclerView) findViewById(R.id.maybeGoingRv);
//        invitedRv = (RecyclerView) findViewById(R.id.invitedRv);
//        interestedRv = (RecyclerView) findViewById(R.id.interestedRv);
//        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
//        goingLl = (LinearLayout) findViewById(R.id.goingLl);
//        notGoingLl = (LinearLayout) findViewById(R.id.notGoingLl);
//        maybeLl = (LinearLayout) findViewById(R.id.maybeLl);
//        closeLlBtn.setOnClickListener(this);
//    }
//
//
//    private void setDataToGoing(List<AttendeeList> goingList) {
//
//        //delete going GoingFriendsRecyclerAdapter(s)
//
//        if (goingList != null) {
//            GoingFriendRecyclerAdapter goingFriendsRecyclerAdapter = new GoingFriendRecyclerAdapter(RSVPActivity.this, goingList);
//            goingRv.setLayoutManager(new LinearLayoutManager(RSVPActivity.this));
//            goingRv.setAdapter(goingFriendsRecyclerAdapter);
//            goingRv.setItemAnimator(new DefaultItemAnimator());
//            goingFriendsRecyclerAdapter.notifyDataSetChanged();
//
//
//            goingFriendsRecyclerAdapter.setOnClick(new GoingFriendRecyclerAdapter.OnItemClicked() {
//
//                @Override
//                public void onItemClick(int position, AttendeeList item, GoingFriendRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {
//
//                    if (item.getStatus() != null && item.getStatus().equals("active")) {
//                        if (item.getFriendstatus().equals("friend")) {
//                            //NOTHING
//                        } else if (item.getFriendstatus().equals("pending")) {
//                            //NOTHING
//                        } else if (item.getFriendstatus().equals("notfriend")) {
//                            //TODO ADD AS FRIEND
//                            addAsFriend(item);
//                            currentInvitedAddedFriend = item.getFirstName();
//                        }
//                    } else if (item.getStatus().equals("dummy")) {
//                        Log.d(TAG, "dummy");
//                    }
//                }
//            });
//        } else {
//            Log.d(TAG, "going list null");
//        }
//
//
//    }
//
//    private void setDataToNotGoing(List<AttendeeList> notGoingList) {
//
//        if (notGoingList != null) {
//            NotGoingFriendsRecyclerAdapter notGoingFriendsRecyclerAdapter = new NotGoingFriendsRecyclerAdapter(RSVPActivity.this, notGoingList);
//            notGoingRv.setLayoutManager(new LinearLayoutManager(RSVPActivity.this));
//            notGoingRv.setAdapter(notGoingFriendsRecyclerAdapter);
//            notGoingRv.setItemAnimator(new DefaultItemAnimator());
//            notGoingFriendsRecyclerAdapter.notifyDataSetChanged();
//
//            notGoingFriendsRecyclerAdapter.setOnClick(new NotGoingFriendsRecyclerAdapter.OnItemClicked() {
//                @Override
//                public void onItemClick(int position, AttendeeList item, NotGoingFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {
//
//                    if (item.getStatus() != null && item.getStatus().equals("active")) {
//                        if (item.getFriendstatus().equals("friend")) {
//                            //NOTHING
//                        } else if (item.getFriendstatus().equals("pending")) {
//                            //NOTHING
//                        } else if (item.getFriendstatus().equals("notfriend")) {
//                            //TODO ADD AS FRIEND
//                            addAsFriend(item);
//                            currentInvitedAddedFriend = item.getFirstName();
//                        }
//                    } else {
//                        Log.d(TAG, "dummy");
//                    }
//                }
//            });
//
//        } else {
//            Log.d(TAG, "notgoing list null");
//        }
//
//    }
//
//    private void setDataToMaybeGoing(List<AttendeeList> maybeList) {
//
//        MaybeFriendsRecyclerAdapter maybeFriendsRecyclerAdapter = new MaybeFriendsRecyclerAdapter(RSVPActivity.this, maybeList);
//        maybeGoingRv.setLayoutManager(new LinearLayoutManager(RSVPActivity.this));
//        maybeGoingRv.setAdapter(maybeFriendsRecyclerAdapter);
//        maybeGoingRv.setItemAnimator(new DefaultItemAnimator());
//        maybeFriendsRecyclerAdapter.notifyDataSetChanged();
//
//        maybeFriendsRecyclerAdapter.setOnClick(new MaybeFriendsRecyclerAdapter.OnItemClicked() {
//            @Override
//            public void onItemClick(int position, AttendeeList item, MaybeFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {
//
//                if (item.getStatus() != null && item.getStatus().equals("active")) {
//                    if (item.getFriendstatus().equals("friend")) {
//                        //NOTHING
//                    } else if (item.getFriendstatus().equals("pending")) {
//                        //NOTHING
//                    } else if (item.getFriendstatus().equals("notfriend")) {
//                        //TODO ADD AS FRIEND
//                        addAsFriend(item);
//                        currentInvitedAddedFriend = item.getFirstName();
//                    }
//                } else if (item.getStatus().equals("dummy")) {
//                    Log.d(TAG, "dummy");
//                }
//            }
//        });
//    }
//
//
//    private void addAsFriend(AttendeeList item) {
//        if (isNetworkAvailable()) {
//            int userId = item.getUserId();
//            if (userId >= 0) {
//                Log.d(TAG, "userId -" + userId);
//                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
//            } else {
//                Log.d(TAG, "USER ID =0");
//                showSnackbar(closeLlBtn, getString(R.string.no_internet), Constant.ERROR);
//            }
//        } else {
//            showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
//            Log.d(TAG, "NO NETWORK");
//        }
//    }
//
//    @Subscribe
//    public void onPostAddAsFriendJobCompleted(HttpResponseForAddFriend httpResponseForAddFriend) {
//        if (httpResponseForAddFriend != null) {
//            if (httpResponseForAddFriend.getStatus() != null && httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
//                showSnackbar(closeLlBtn, "Friend Request Send Succesfully", Constant.SUCCESS);
//                Log.d(TAG, "ADDED AS A FRIEND");
//            } else {
//                showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
//                Log.d(TAG, "ADDED AS A FRIEND FAILED");
//            }
//        } else {
//            showSnackbar(closeLlBtn, getString(R.string.friend_request_failed), Constant.ERROR);
//            Log.d(TAG, "ADDED AS A FRIEND FAILED ELSE");
//        }
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//
//            case R.id.closeLlBtn:
//                this.finish();
//                break;
//        }
//    }
//}
