package com.meeof.meeof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.EventInviteFriendsRecyclerAdapter;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ransikadesilva on 10/19/17.
 */

public class InviteFriendsToEventListActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = InviteFriendsToEventListActivity.class.getSimpleName();
    private LinearLayout backLlBtn;
    private LinearLayout doneLlBtn;
    private EditText searchEt;
    private RecyclerView inviteFriendsRv;
    private String accessToken;
    private List<Friends> tempInvitedFriends = new ArrayList<>();
    private EventInviteFriendsRecyclerAdapter friendsRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends_list);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
//        accessToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjA3NzdhNTI5NGZmYTE4MTg0MjU4YWRiYjQzNmQzOTE1MGY3NGI1MGQ1NjZjNzBlMGRkOTI2NDgwMDc1ZjBiZGIyNmVmZjdhOWM4ZDgwMjYzIn0.eyJhdWQiOiIxIiwianRpIjoiMDc3N2E1Mjk0ZmZhMTgxODQyNThhZGJiNDM2ZDM5MTUwZjc0YjUwZDU2NmM3MGUwZGQ5MjY0ODAwNzVmMGJkYjI2ZWZmN2E5YzhkODAyNjMiLCJpYXQiOjE1MDg0ODY4NDEsIm5iZiI6MTUwODQ4Njg0MSwiZXhwIjoxNTQwMDIyODQxLCJzdWIiOiIxNTQiLCJzY29wZXMiOltdfQ.M6_VVlZ02HAHfb9oY6DTwp3KaWEMHlotnAonfFJUcbjdqBiOuj40xoHvzVEGiBlC5dYtiLyDm9NaFDEnWn0VLUs1HoWCj6n-R3s4h71QsKkFAfdsv20NX1AYDSlyoVvuECp48zYvCeLVRb321mWCcPzhIcqK8MKL7hNMDxLfI4MVtuYKWIyYVSLAJcn7Dn7CJJ9V8zjU2X_057jOoGJ0ukMmYsSU1dBtJRj-M1WwBFdJWNfGnPeipQ0VVlo3h86daXRfxSHykrp_WwmstYE3Oa7LaWxPI7p4dy6trweMVaglV01qWIHiBnnbFAotQK241qMWc4FBFvidcNGRm9ryy5WtyQejiY8WjHSbXglYoOA5I83BRzYGjPl9tjLPEnzr8MfexwlubY1xplEW_PlPQMRst91QcZT5OjDHZyP_qSTfdCCgaM_EOowPXWNOr9YdhicBJw6sm-B8kd-pygOzmXuxxmGCXM1qNZ0gYjC9BMX9_ygmSq74hRZ1ONwsGqz4yML6jdkb4whQHSiC0KgeQaIVKD-MWB8RtHxyQpkA9cH_rX0DPNoDvanZGla_Ymsr2Yi84_FJrDVNzzST2-eg1WssW6tKaUG33cKcNSQwruE83fl2T7mod1blx9qYOVgYJlKVXtpo3GAaNoZ9h7e6d4h6LEXyCZYA7LwLqz0q4Z4";

        Bundle args = this.getIntent().getBundleExtra("BUNDLE_B");
        Log.d(TAG,"Received friend has bundle "+getIntent().hasExtra("BUNDLE_B"));
        Set<Friends> friendsList = (HashSet<Friends>) args.getSerializable(Constant.SELECTED_FRIENDS_FOR_EVENT);
        for(Friends friend:friendsList){
            Log.d(TAG,"Received friend :"+friend.getFirst_name());
        }
        tempInvitedFriends.addAll(friendsList);

        initViews();
        getAllFriends();

    }

    private void initViews() {
        backLlBtn = (LinearLayout) findViewById(R.id.backLlBtn);
        doneLlBtn = (LinearLayout) findViewById(R.id.doneLlBtn);
        searchEt = (EditText) findViewById(R.id.searchTv);
        inviteFriendsRv = (RecyclerView) findViewById(R.id.inviteFriendsRv);

        backLlBtn.setOnClickListener(this);
        doneLlBtn.setOnClickListener(this);
        searchEt.addTextChangedListener(this);
    }


    private void getAllFriends() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetMyAllFriendsWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(backLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyAllFriendsWebJobCompleted(FriendsAllResponse friendsAllResponse) {
        Log.d(TAG, "Inside onPostEventDetailsWebJob");
        stopProgressBar();
        if (friendsAllResponse != null) {
            Log.d(TAG, "Inside onPostEventDetailsWebJob not null");
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "Inside onPostEventDetailsWebJob not null success");
                List<Friends> friends = friendsAllResponse.getFriends();
                Log.d(TAG, "List Size: " + friends.size());
                List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();
                setDataToListFriends(friends, tempInvitedFriends);

            } else {
                showSnackbar(backLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(backLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void setDataToListFriends(List<Friends> friends, List<Friends> invitedFriends) {

        friendsRecyclerAdapter = new EventInviteFriendsRecyclerAdapter(this, friends,invitedFriends);
        inviteFriendsRv.setLayoutManager(new LinearLayoutManager(this));
        inviteFriendsRv.setAdapter(friendsRecyclerAdapter);
        inviteFriendsRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();

        friendsRecyclerAdapter.setOnClick(new EventInviteFriendsRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, Friends item, boolean isChecked) {
                if (isChecked) {
                    Log.d(TAG, "Clicked :" + item.getFirst_name());
//                    tempInvitedFriends.add(position, item);
                    tempInvitedFriends.add(item);
                } else {
                    tempInvitedFriends.remove(item);
//                    Log.d(TAG, "Clicked :" + item.getFirst_name());
//                    if (tempInvitedFriends.size() > position) {
//                        Log.d(TAG, "Clicked :" + item.getFirst_name());
//                        if (tempInvitedFriends.get(position) == null) {
//                            tempInvitedFriends.remove(position);
//                        }
//                    }
                }
            }

        });

//        checkAlreadySelected(friends);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backLlBtn:
                this.onBackPressed();
                break;
            case R.id.doneLlBtn:
                sendSelectedFriendList();
                break;
        }
    }

    private void sendSelectedFriendList() {
        Intent data = new Intent();

        Bundle args = new Bundle();
        for (Friends friend : getSelectedFriendList()) {
            Log.d(TAG, "This dot finish: " + friend.getFirst_name());
        }
        args.putSerializable(Constant.SELECTED_FRIENDS_FOR_EVENT, (Serializable) getSelectedFriendList());

        data.putExtra("BUNDLE", args);

        setResult(PlacesActivity.RESULT_OK, data);
        finish();
    }

    private List<Friends> getSelectedFriendList() {
        Log.d(TAG,"Inside getSelectedFriendList()");
        List<Friends> selectedFriendList = new ArrayList<>();
        for (int i = 0; i < tempInvitedFriends.size(); i++) {
            if (tempInvitedFriends.get(i) != null) {
                Log.d(TAG,"Inside getSelectedFriendList() "+tempInvitedFriends.get(i));
                selectedFriendList.add(tempInvitedFriends.get(i));
            }
        }
        return selectedFriendList;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (friendsRecyclerAdapter != null) {
            friendsRecyclerAdapter.filter(s.toString());
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
