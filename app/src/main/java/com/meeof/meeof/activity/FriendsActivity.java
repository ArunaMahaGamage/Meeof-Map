package com.meeof.meeof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.FriendsSearchRecyclerAdapter;
import com.meeof.meeof.adapter.PendingRequestsRecyclerAdapter;
import com.meeof.meeof.adapter.UserFriendsRecyclerAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.model.search_query_dto.SearchFriendsQueryResponse;
import com.meeof.meeof.model.updates_dto.HttpDeleteFriendResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.AcceptDeclineFriendRequestWebJob;
import com.meeof.meeof.webjob.DeleteFriendWebJob;
import com.meeof.meeof.webjob.GetInviteFriendWebJob;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;
import com.meeof.meeof.webjob.GetSearchMyFriendsInterestsWebJob;
import com.meeof.meeof.webjob.PostAddAsFriendWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransikadesilva on 10/11/17.
 */

public class FriendsActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private String accessToken;
    private TextView titleUserFriends;
    private AppCompatImageView back_button;
    private Button findFriendsBtn;
    private LinearLayout noFriendsViewLl;
    private RecyclerView allFriendListRv;
    private String currentInvitedAddedFriend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        setContentView(R.layout.activity_all_friends);

        initViews();
        getAllFriends(true);
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        noFriendsViewLl = (LinearLayout) findViewById(R.id.noFriendsViewLl);
        allFriendListRv = (RecyclerView) findViewById(R.id.allFriendListRv);
        findFriendsBtn = (Button) findViewById(R.id.findFriendsBtn);

        back_button.setOnClickListener(this);
        findFriendsBtn.setOnClickListener(this);
        noFriendsViewLl.setOnClickListener(this);

    }

    private void getAllFriends(boolean isWithProgress) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetMyAllFriendsWebJob(accessToken));
            if (isWithProgress) {
                startProgressBar();
            }
        } else {
            showSnackbar(back_button, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyAllFriendsCompleted(FriendsAllResponse friendsAllResponse) {
        stopProgressBar();
        if (friendsAllResponse != null) {
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> friends = friendsAllResponse.getFriends();
                List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();
                if (friends.size() > 0) {
                    setDataToListFriends(friends);
                } else {
                    noFriendsViewLl.setVisibility(View.VISIBLE);
                }
//                setDataToListPendingRequests(pendingRequests);
            } else {
                showSnackbar(back_button, getString(R.string.unable_to_retrive_friends), Constant.ERROR);
            }
        } else {
            showSnackbar(back_button, getString(R.string.unable_to_retrive_friends), Constant.ERROR);
        }
    }

    private void acceptDeclineFriendRequest(boolean isAccept, int userId) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new AcceptDeclineFriendRequestWebJob(accessToken, userId, isAccept));
        } else {
            //showSnackbar(back_button, getString(R.string.no_internet));
        }
    }

    @Subscribe
    public void onAcceptDeclineFriendRequestWebJobCompleted(HttpResponse acceptDecResponse) {
        if (acceptDecResponse != null) {
            if (acceptDecResponse.getStatus() != null && acceptDecResponse.getStatus().equals(Constant.SUCCESS)) {

            } else {
                //showSnackbar(back_button, getString(R.string.oop_something_went_wrong));
            }
        } else {
            //showSnackbar(back_button, getString(R.string.oop_something_went_wrong));
        }
    }

    private void setDataToListFriends(List<Friends> friends) {

        UserFriendsRecyclerAdapter friendsRecyclerAdapter = new UserFriendsRecyclerAdapter(FriendsActivity.this, friends);
        allFriendListRv.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
        allFriendListRv.setAdapter(friendsRecyclerAdapter);
        allFriendListRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();

        friendsRecyclerAdapter.setOnClick(new UserFriendsRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, Friends item) {
                if (item.getStatus().equals("active")) {
                    if (item.getFriend_status().equals("friend")) {
                        //NOTHING
                    } else if (item.getFriend_status().equals("pending")) {
                        //NOTHING
                    } else if (item.getFriend_status().equals("none")) {
                        //TODO ADD AS FRIEND
                        addAsFriend(item);
                        currentInvitedAddedFriend = item.getFirst_name();
                    } else {
                        //TODO INVITE
                        inviteFriend(item);
                        currentInvitedAddedFriend = item.getFirst_name();
                    }
                } else if (item.getStatus().equals("dummy")) {
                    //TODO INVITE
                    inviteFriend(item);
                    currentInvitedAddedFriend = item.getFirst_name();
                } else {
                    //TODO INVITE
                    inviteFriend(item);
                    currentInvitedAddedFriend = item.getFirst_name();
                }
                getAllFriends(false);
            }
        });

        friendsRecyclerAdapter.setOnDeleteClick(new UserFriendsRecyclerAdapter.OnDeleteClicked() {
            @Override
            public void onDeleteItemClick(int position, Friends item) {
                deleteFriend(item);
            }
        });
    }

    private void inviteFriend(Friends item) {
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new GetInviteFriendWebJob(accessToken, item.getEmail()));
                startProgressBar();
        } else {
            showSnackbar(noFriendsViewLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    public void deleteFriend(Friends item) {
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new DeleteFriendWebJob(accessToken, String.valueOf(item.getId())));
                startProgressBar();
        } else {
            showSnackbar(noFriendsViewLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetInviteFriendsWebJob(HttpResponse httpResponseInviteFriend) {
        stopProgressBar();
        if (httpResponseInviteFriend != null) {
            if (httpResponseInviteFriend.getStatus()!=null&&httpResponseInviteFriend.getStatus().equals(Constant.SUCCESS)) {
                //
                if(currentInvitedAddedFriend!=null){
                    showSnackbar(back_button, getString(R.string.successfully_invited_as_friend_to)+" "+currentInvitedAddedFriend, Constant.SUCCESS);
                }else{
                    showSnackbar(back_button, getString(R.string.successfully_invited_as_friend), Constant.SUCCESS);
                }
            }
        } else {

        }
    }

    @Subscribe
    public void onDeleteFriendWebJobCompleted(HttpDeleteFriendResponse httpResponse) {
        stopProgressBar();
        if (httpResponse != null) {
            if (httpResponse.getStatus()!=null&&httpResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(back_button, getString(R.string.Friend_deleted), Constant.SUCCESS);
            }
        } else {
            showSnackbar(back_button, getString(R.string.Friend_deleted_failed), Constant.ERROR);
        }
    }

    private void addAsFriend(Friends item) {
        if (isNetworkAvailable()) {
            int userId = item.getId();
            if (userId >= 0) {
                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
            } else {
                //CANT SEND FRIEND REQUEST
            }
        } else {
            //showSnackbar(back_button, getString(R.string.no_internet));
        }
    }

    @Subscribe
    public void onPostAddAsFriendJobCompleted(HttpResponseForAddFriend httpResponseForAddFriend) {
        if (httpResponseForAddFriend != null) {
            if (httpResponseForAddFriend.getStatus() != null && httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
                //showSnackbar(back_button, "Successfully Added as friend");
            } else {
                //TODO SHOW SNACKBAR
            }
        } else {
            //TODO SHOW SNACKBAR
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backAcIvBtn:
                this.onBackPressed();
                break;
            case R.id.findFriendsBtn:
                sendToInviteFriendsActivity();
                Helper.clickGaurd(findFriendsBtn);
                break;
        }
    }

    private void sendToInviteFriendsActivity() {
        Intent intent = new Intent(this, InviteFriendsActivity.class);
        startActivity(intent);
    }

    private void searchFriends(String s) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetSearchMyFriendsInterestsWebJob(accessToken, s, ""));
        } else {
            //showSnackbar(findFriendsBtn, getString(R.string.no_internet));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetSearchMeeofFriendsWebJobCompleted(SearchFriendsQueryResponse searchFriendsQueryResponse) {
        if (searchFriendsQueryResponse != null) {
            if (searchFriendsQueryResponse.getStatus()!=null&&searchFriendsQueryResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> tempFriendList = new ArrayList<>();
                List<com.meeof.meeof.model.search_query_dto.Data> listFriendData = searchFriendsQueryResponse.getData();

                setDataToListSearchFriends(listFriendData);
            }
        }
    }

    private void setDataToListSearchFriends(List<com.meeof.meeof.model.search_query_dto.Data> listFriendData) {
        FriendsSearchRecyclerAdapter friendsRecyclerAdapter = new FriendsSearchRecyclerAdapter(FriendsActivity.this, listFriendData);
        allFriendListRv.setLayoutManager(new LinearLayoutManager(FriendsActivity.this));
        allFriendListRv.setAdapter(friendsRecyclerAdapter);
        allFriendListRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
