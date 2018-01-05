package com.meeof.meeof.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.FriendsSearchRecyclerAdapter;
import com.meeof.meeof.adapter.IgnoredUsersRecyclerAdapter;
import com.meeof.meeof.adapter.UserFriendsRecyclerAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.model.ignored_members_dto.Ignored;
import com.meeof.meeof.model.ignored_members_dto.IgnoredMembersResponse;
import com.meeof.meeof.model.search_query_dto.Data;
import com.meeof.meeof.model.search_query_dto.SearchFriendsQueryResponse;
import com.meeof.meeof.model.updates_dto.HttpDeleteFriendResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteFriendWebJob;
import com.meeof.meeof.webjob.DeleteIgnoredUserWebJob;
import com.meeof.meeof.webjob.GetIgnoredMembersWebJob;
import com.meeof.meeof.webjob.GetInviteFriendWebJob;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;
import com.meeof.meeof.webjob.PostAddAsFriendWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class IgnoredMembersActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ManageFriendsActivity.class.getSimpleName();
    private AppCompatImageView back_button;
    private String accessToken;
    private LinearLayout editDoneBtnLl;
    private LinearLayout noFriendsViewLl;
    private RecyclerView ignoredFiendsRv;
    private boolean showHideDeleteButton;
    private IgnoredUsersRecyclerAdapter friendsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignored_members);

        initViews();
    }

    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        editDoneBtnLl = (LinearLayout) findViewById(R.id.editDoneBtnLl);
        ignoredFiendsRv = (RecyclerView) findViewById(R.id.ignoredFiendsRv);
        noFriendsViewLl=(LinearLayout)findViewById(R.id.noFriendsViewLl);


        back_button.setOnClickListener(this);
        editDoneBtnLl.setOnClickListener(this);

        getIgnoredMembers(true);
    }

    private void getIgnoredMembers(boolean isWithProgress) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetIgnoredMembersWebJob(accessToken));
            if(isWithProgress) {
                startProgressBar();
            }

        } else {
            showSnackbar(back_button, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backAcIvBtn:
                this.onBackPressed();
                break;
            case R.id.editDoneBtnLl:
                showHideDeleteButtons();
                break;
        }
    }

    private void showHideDeleteButtons(){
        Log.d(TAG,"showHideDeleteButtons :"+ showHideDeleteButton);
        if(showHideDeleteButton){
            showHideDeleteButton=false;
            if(friendsRecyclerAdapter!=null){
                friendsRecyclerAdapter.showdeleteButtons();
            }
        }else{
            showHideDeleteButton=true;
            if(friendsRecyclerAdapter!=null){
                friendsRecyclerAdapter.hidedeleteButtons();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetIgnoredListCompleted(IgnoredMembersResponse ignoredMembersResponse) {
        stopProgressBar();
        if (ignoredMembersResponse != null) {
            if (ignoredMembersResponse.getStatus() != null && ignoredMembersResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Ignored> ignored = ignoredMembersResponse.getIgnored();
                if(ignored.size()>0){
                    setDataToListFriends(ignored);
                    Log.d(TAG,"onGetMyAllFriendsCompleted :"+ignoredMembersResponse.getIgnored().toString());
                    noFriendsViewLl.setVisibility(View.GONE);
                    editDoneBtnLl.setVisibility(View.VISIBLE);
                    ignoredFiendsRv.setVisibility(View.VISIBLE);
                }else{
                    noFriendsViewLl.setVisibility(View.VISIBLE);
                    editDoneBtnLl.setVisibility(View.GONE);
                    ignoredFiendsRv.setVisibility(View.GONE);
                }
                //List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();

            } else {
                //showSnackbar(ignoredFiendsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                noFriendsViewLl.setVisibility(View.VISIBLE);
                editDoneBtnLl.setVisibility(View.GONE);
                ignoredFiendsRv.setVisibility(View.GONE);
            }
        } else {
            //showSnackbar(ignoredFiendsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            noFriendsViewLl.setVisibility(View.VISIBLE);
            editDoneBtnLl.setVisibility(View.GONE);
            ignoredFiendsRv.setVisibility(View.GONE);
        }
    }

    private void setDataToListFriends(List<Ignored> ignoreds) {

        if(friendsRecyclerAdapter==null){
            friendsRecyclerAdapter = new IgnoredUsersRecyclerAdapter(IgnoredMembersActivity.this, ignoreds);
            ignoredFiendsRv.setLayoutManager(new LinearLayoutManager(IgnoredMembersActivity.this));
            ignoredFiendsRv.setAdapter(friendsRecyclerAdapter);
            ignoredFiendsRv.setItemAnimator(new DefaultItemAnimator());
            friendsRecyclerAdapter.showdeleteButtons();
            friendsRecyclerAdapter.notifyDataSetChanged();
        }else{
            friendsRecyclerAdapter.updateList(ignoreds);
        }


        friendsRecyclerAdapter.setOnClick(new IgnoredUsersRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, Ignored item) {
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
                getIgnoredMembers(false);
            }
        });


        friendsRecyclerAdapter.setOnDeleteClick(new IgnoredUsersRecyclerAdapter.OnDeleteClicked() {
            @Override
            public void onDeleteItemClick(int position, Ignored item) {
                deleteIgnoredUser(item);
            }
        });
    }

    private void addAsFriend(Ignored item) {
        if (isNetworkAvailable()) {
            int userId = item.getId();
            if (userId >= 0) {
                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
            } else {
                showSnackbar(back_button, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(back_button, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostAddAsFriendJobCompleted(HttpResponseForAddFriend httpResponseForAddFriend) {
        if (httpResponseForAddFriend != null) {
            if (httpResponseForAddFriend.getStatus() != null && httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(back_button, getString(R.string.successfully_added_as_friend), Constant.SUCCESS);
            } else {
                showSnackbar(back_button, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(back_button, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    public void deleteIgnoredUser(Ignored item) {
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new DeleteIgnoredUserWebJob(accessToken, String.valueOf(item.getId())));
            startProgressBar();
        } else {
            showSnackbar(ignoredFiendsRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onDeleteFriendWebJobCompleted(HttpDeleteFriendResponse httpResponse) {
        stopProgressBar();
        if (httpResponse != null) {
            if (httpResponse.getStatus()!=null&&httpResponse.getStatus().equals(Constant.SUCCESS)) {
                getIgnoredMembers(false);
                showSnackbar(back_button, "Ignored Member Successfully Deleted", Constant.SUCCESS);
            }
        } else {
            Log.d(TAG,"onDeleteFriendWebJobCompleted");
            showSnackbar(back_button, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void inviteFriend(Ignored item) {
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new GetInviteFriendWebJob(accessToken, item.getEmail()));
        } else {
            showSnackbar(back_button, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetInviteFriendsWebJob(HttpResponse httpResponseInviteFriend) {
        stopProgressBar();

        if (httpResponseInviteFriend != null) {
            if (httpResponseInviteFriend.getStatus()!=null&&httpResponseInviteFriend.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(back_button, getString(R.string.successfully_invited_as_friend), Constant.SUCCESS);
            } else {
                showSnackbar(back_button, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(back_button, getString(R.string.oop_something_went_wrong), Constant.ERROR);
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
}
