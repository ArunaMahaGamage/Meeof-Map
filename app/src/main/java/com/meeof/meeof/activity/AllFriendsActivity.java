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
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.AcceptDeclineFriendRequestWebJob;
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

public class AllFriendsActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = AllFriendsActivity.class.getSimpleName();
    private AppCompatImageView back_button;
    private String accessToken;
    private Button findFriendsBtn;
    private LinearLayout noFriendsViewLl;
    private RecyclerView friendRequestsRv;
    private RecyclerView allFriendListRv;
    private LinearLayout friendListRvLl;
    private TextView numberOfFriendRequestsTv;
    private LinearLayout friendRequestsTv;
    private TextView searchFriendTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        setContentView(R.layout.activity_all_friends);

//        initViews();
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        findFriendsBtn = (Button) findViewById(R.id.findFriendsBtn);
        noFriendsViewLl = (LinearLayout) findViewById(R.id.noFriendsViewLl);
//        friendRequestsRv = (RecyclerView) findViewById(R.id.friendRequestsRv);
        allFriendListRv = (RecyclerView) findViewById(R.id.allFriendListRv);
        friendListRvLl = (LinearLayout) findViewById(R.id.friendListRvLl);
//        numberOfFriendRequestsTv = (TextView) findViewById(R.id.numberOfFriendRequestsTv);
//        friendRequestsTv = (LinearLayout) findViewById(R.id.friendRequestsLl);
        searchFriendTv = (TextView) findViewById(R.id.searchFriendTv);

        back_button.setOnClickListener(this);
        findFriendsBtn.setOnClickListener(this);
        noFriendsViewLl.setOnClickListener(this);
        searchFriendTv.addTextChangedListener(this);


//        getAllFriends();
    }

    private void getAllFriends() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetMyAllFriendsWebJob(accessToken));
        } else {
            //showSnackbar(back_button, getString(R.string.no_internet));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyAllFriendsCompleted(FriendsAllResponse friendsAllResponse) {
        if (friendsAllResponse != null) {
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> friends = friendsAllResponse.getFriends();
                List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();
                setDataToListFriends(friends);
                setDataToListPendingRequests(pendingRequests);
            } else {
                //showSnackbar(numberOfFriendRequestsTv, getString(R.string.oop_something_went_wrong));
            }
        } else {
            //showSnackbar(numberOfFriendRequestsTv, getString(R.string.oop_something_went_wrong));
        }
    }

    private void setDataToListPendingRequests(List<Pending_requests> pendingRequests) {
        updateFriendRequestsUI(pendingRequests.size());

        final PendingRequestsRecyclerAdapter friendsRecyclerAdapter = new PendingRequestsRecyclerAdapter(AllFriendsActivity.this, pendingRequests);
        friendRequestsRv.setLayoutManager(new LinearLayoutManager(AllFriendsActivity.this));
        friendRequestsRv.setAdapter(friendsRecyclerAdapter);
        friendRequestsRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();

        friendsRecyclerAdapter.setOnClick(new PendingRequestsRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, boolean isAccept, Pending_requests item) {
                int userId = item.getUser_id();
                if (isAccept) {
                    acceptDeclineFriendRequest(isAccept, userId);
                } else {
                    acceptDeclineFriendRequest(isAccept, userId);
                }
                friendsRecyclerAdapter.removeFriendById(userId);
            }
        });


    }

    private void updateFriendRequestsUI(int listSize) {
        if (listSize > 0) {
            friendRequestsTv.setVisibility(View.VISIBLE);
            friendListRvLl.setVisibility(View.VISIBLE);
            numberOfFriendRequestsTv.setText("(" + listSize + ")");
        } else {
            friendRequestsTv.setVisibility(View.GONE);
            friendRequestsRv.setVisibility(View.GONE);
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

        UserFriendsRecyclerAdapter friendsRecyclerAdapter = new UserFriendsRecyclerAdapter(AllFriendsActivity.this, friends);
        allFriendListRv.setLayoutManager(new LinearLayoutManager(AllFriendsActivity.this));
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
//                        Toast.makeText(AllFriendsActivity.this, "You Clicked " + position, Toast.LENGTH_LONG).show();
                        addAsFriend(item);
                    } else {
                        //TODO INVITE
//                        Toast.makeText(AllFriendsActivity.this, "You Clicked " + position, Toast.LENGTH_LONG).show();
                        inviteFriend(item);
                    }
                } else if (item.getStatus().equals("dummy")) {
                    //TODO INVITE
//                    Toast.makeText(AllFriendsActivity.this, "You Clicked " + position, Toast.LENGTH_LONG).show();
                    inviteFriend(item);
                } else {
                    //TODO INVITE
//                    Toast.makeText(AllFriendsActivity.this, "You Clicked " + position, Toast.LENGTH_LONG).show();
                    inviteFriend(item);
                }
            }
        });
    }

    private void inviteFriend(Friends item) {
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new GetInviteFriendWebJob(accessToken, item.getEmail()));
        } else {
            //showSnackbar(back_button, getString(R.string.no_internet));
        }
    }

    @Subscribe
    public void onGetInviteFriendsWebJob(HttpResponse httpResponseInviteFriend) {
        if (httpResponseInviteFriend != null) {
            if (httpResponseInviteFriend.getStatus()!=null&&httpResponseInviteFriend.getStatus().equals(Constant.SUCCESS)) {
                //
                //showSnackbar(back_button, "Successfully Invited as Friend");
            }
        } else {

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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        searchFriends(s.toString());
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
            if (searchFriendsQueryResponse.getStatus()!=null && searchFriendsQueryResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> tempFriendList = new ArrayList<>();
                List<com.meeof.meeof.model.search_query_dto.Data> listFriendData = searchFriendsQueryResponse.getData();
                setDataToListSearchFriends(listFriendData);
            }
        }
    }

    private void setDataToListSearchFriends(List<com.meeof.meeof.model.search_query_dto.Data> listFriendData) {
        FriendsSearchRecyclerAdapter friendsRecyclerAdapter = new FriendsSearchRecyclerAdapter(AllFriendsActivity.this, listFriendData);
        allFriendListRv.setLayoutManager(new LinearLayoutManager(AllFriendsActivity.this));
        allFriendListRv.setAdapter(friendsRecyclerAdapter);
        allFriendListRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();
    }

}
