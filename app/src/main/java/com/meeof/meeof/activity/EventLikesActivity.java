package com.meeof.meeof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.EventLikesAdapter;
import com.meeof.meeof.adapter.UserFriendsRecyclerAdapter;
import com.meeof.meeof.model.edit_event_dto.AttendeeList;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.edit_event_dto.Likes;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventInfoWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by ransikadesilva on 10/26/17.
 */

public class EventLikesActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = EventLikesActivity.class.getSimpleName();
    private String accessToken;
    private LinearLayout closeLlBtn;
    private RecyclerView commentsRv;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_likes);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        initViews();

        Intent intent = getIntent();
        if(intent.hasExtra(Constant.EVENT_ID)){
            eventId = intent.getIntExtra(Constant.EVENT_ID,0);
        }

        getInviteFriendsInfo(eventId);
    }

    private void getInviteFriendsInfo(int eventId) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventInfoWebJob(accessToken, eventId));
            Log.d(TAG, "Inside getInviteInfo");
        } else {
            showSnackbar(closeLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventInfoWebJobCompleted(EventInfoResponse eventInfoResponse) {
        if (eventInfoResponse != null) {
            if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Likes> likesList = eventInfoResponse.getData().get(0).getLikes();
                if(likesList.size()>0){
                    setEventInviteDetailsToUI(likesList);
                }else{
                    Log.d(TAG,"No Likes For this Event");
                }
                Log.d(TAG, "Inside Subscribe onGetEventInfoWebJobCompleted");
            } else if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.ERROR)) {
                showSnackbar(closeLlBtn, getString(R.string.unable_to_retrieve_event_details), Constant.ERROR);
            }
        } else {
            showSnackbar(closeLlBtn, getString(R.string.unable_to_retrieve_event_details), Constant.ERROR);
        }
    }

    private void setEventInviteDetailsToUI(List<Likes> likes) {
        EventLikesAdapter friendsRecyclerAdapter = new EventLikesAdapter(EventLikesActivity.this, likes);
        commentsRv.setLayoutManager(new LinearLayoutManager(EventLikesActivity.this));
        commentsRv.setAdapter(friendsRecyclerAdapter);
        commentsRv.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();
    }


    private void initViews() {
        closeLlBtn = (LinearLayout)findViewById(R.id.closeLlBtn);
        commentsRv = (RecyclerView)findViewById(R.id.commentsRv);

        closeLlBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.closeLlBtn:
                this.finish();
                break;
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
