package com.meeof.meeof.activity;

import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.IgnoredInterestedEventsAdapter;
import com.meeof.meeof.model.interested_ignored_event_reponse_dto.Events;
import com.meeof.meeof.model.interested_ignored_event_reponse_dto.InterestedIgnoredEventResponse;
import com.meeof.meeof.model.updates_dto.HttpDeleteFriendResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteIgnoreEventWebJob;
import com.meeof.meeof.webjob.DeleteInterestedEventWebJob;
import com.meeof.meeof.webjob.GetIgnoredEventsWebJob;
import com.meeof.meeof.webjob.GetInterestedEventsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class IgnoredInterestedActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = IgnoredInterestedActivity.class.getSimpleName();
    private AppCompatImageView back_button;
    private String accessToken;
    private LinearLayout editDoneBtnLl;
    private LinearLayout noIgnoredEventsViewLl;
    private LinearLayout noInterestedEventsViewLl;
    private RecyclerView ignoredInterestedEventsRv;
    private boolean showHideDeleteButton;
    private boolean isInterestedEvents;
    private TextView titleTv;

    private IgnoredInterestedEventsAdapter ignoredInterestedEventsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignored_interested);

        if (getIntent().hasExtra("IS_INTERESTED_EVENTS")) {
            isInterestedEvents = this.getIntent().getBooleanExtra(Constant.IS_INTERESTED_EVENTS, false);
        }

        initViews();
    }

    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        editDoneBtnLl = (LinearLayout) findViewById(R.id.editDoneBtnLl);
        ignoredInterestedEventsRv = (RecyclerView) findViewById(R.id.ignoredInterestedEventsRv);
        noInterestedEventsViewLl=(LinearLayout)findViewById(R.id.noInterestedEventsViewLl);
        noIgnoredEventsViewLl=(LinearLayout)findViewById(R.id.noIgnoredEventsViewLl);
        titleTv=(TextView)findViewById(R.id.titleTv);

        if(isInterestedEvents){
            titleTv.setText("Events Interested In");
        }else{
            titleTv.setText("Ignored Events");
        }


        back_button.setOnClickListener(this);
        editDoneBtnLl.setOnClickListener(this);

        getEvents(true);
    }

    private void getEvents(boolean isWithProgress) {
        if (isNetworkAvailable()) {
            if(isInterestedEvents){
                jobManager.addJobInBackground(new GetInterestedEventsWebJob(accessToken));
            }else{
                jobManager.addJobInBackground(new GetIgnoredEventsWebJob(accessToken));
            }

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
            if(ignoredInterestedEventsAdapter!=null){
                ignoredInterestedEventsAdapter.showdeleteButtons();
            }
        }else{
            showHideDeleteButton=true;
            if(ignoredInterestedEventsAdapter!=null){
                ignoredInterestedEventsAdapter.hidedeleteButtons();
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetIgnoredInterestedListCompleted(InterestedIgnoredEventResponse interestedIgnoredEventResponse) {
        stopProgressBar();
        Log.d(TAG,"onGetIgnoredInterestedListCompleted");
        if (interestedIgnoredEventResponse != null) {
            if (interestedIgnoredEventResponse.getStatus() != null && interestedIgnoredEventResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Events> events = interestedIgnoredEventResponse.getEvents();
                if(events.size()>0){
                    setDataToListEvents(events);
                    Log.d(TAG,"onGetIgnoredInterestedListCompleted list:"+interestedIgnoredEventResponse.getEvents().toString());
                    editDoneBtnLl.setVisibility(View.VISIBLE);
                    ignoredInterestedEventsRv.setVisibility(View.VISIBLE);
                    noInterestedEventsViewLl.setVisibility(View.GONE);
                    noIgnoredEventsViewLl.setVisibility(View.GONE);
                }else{
                    Log.d(TAG,"onGetIgnoredInterestedListCompleted no events:");
                    editDoneBtnLl.setVisibility(View.GONE);
                    ignoredInterestedEventsRv.setVisibility(View.GONE);
                    if(isInterestedEvents){
                        noInterestedEventsViewLl.setVisibility(View.VISIBLE);
                        noIgnoredEventsViewLl.setVisibility(View.GONE);
                    }else{
                        noInterestedEventsViewLl.setVisibility(View.GONE);
                        noIgnoredEventsViewLl.setVisibility(View.VISIBLE);
                    }
                }
                //List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();

            } else {
                //showSnackbar(ignoredFiendsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                Log.d(TAG,"onGetIgnoredInterestedListCompleted not success:");
                editDoneBtnLl.setVisibility(View.GONE);
                ignoredInterestedEventsRv.setVisibility(View.GONE);
                if(isInterestedEvents){
                    noInterestedEventsViewLl.setVisibility(View.VISIBLE);
                    noIgnoredEventsViewLl.setVisibility(View.GONE);
                }else{
                    noInterestedEventsViewLl.setVisibility(View.GONE);
                    noIgnoredEventsViewLl.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //showSnackbar(ignoredFiendsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            Log.d(TAG,"onGetIgnoredInterestedListCompleted null:");
            editDoneBtnLl.setVisibility(View.GONE);
            ignoredInterestedEventsRv.setVisibility(View.GONE);
            if(isInterestedEvents){
                noInterestedEventsViewLl.setVisibility(View.VISIBLE);
                noIgnoredEventsViewLl.setVisibility(View.GONE);
            }else{
                noInterestedEventsViewLl.setVisibility(View.GONE);
                noIgnoredEventsViewLl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setDataToListEvents(List<Events> events) {

        if(ignoredInterestedEventsAdapter==null){
            ignoredInterestedEventsAdapter = new IgnoredInterestedEventsAdapter(IgnoredInterestedActivity.this, events);
            ignoredInterestedEventsRv.setLayoutManager(new LinearLayoutManager(IgnoredInterestedActivity.this));
            ignoredInterestedEventsRv.setAdapter(ignoredInterestedEventsAdapter);
            ignoredInterestedEventsRv.setItemAnimator(new DefaultItemAnimator());
            ignoredInterestedEventsAdapter.showdeleteButtons();
            ignoredInterestedEventsAdapter.notifyDataSetChanged();
        }else{
            ignoredInterestedEventsAdapter.updateList(events);
        }


        ignoredInterestedEventsAdapter.setOnDeleteClick(new IgnoredInterestedEventsAdapter.OnDeleteClicked() {
            @Override
            public void onDeleteItemClick(int position, Events item) {
                deleteIgnoredUser(item);
            }
        });
    }

    public void deleteIgnoredUser(Events item) {
        if (isNetworkAvailable()) {
            if(isInterestedEvents){
                jobManager.addJobInBackground(new DeleteInterestedEventWebJob(accessToken, String.valueOf(item.getEventid())));
            }else{
                jobManager.addJobInBackground(new DeleteIgnoreEventWebJob(accessToken, String.valueOf(item.getEventid())));
            }
            startProgressBar();
        } else {
            showSnackbar(ignoredInterestedEventsRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onDeleteFriendWebJobCompleted(HttpDeleteFriendResponse httpResponse) {
        stopProgressBar();
        if (httpResponse != null) {
            if (httpResponse.getStatus()!=null&&httpResponse.getStatus().equals(Constant.SUCCESS)) {
                getEvents(false);
                if(isInterestedEvents){
                    showSnackbar(back_button, "Interested event successfully deleted", Constant.SUCCESS);
                }else{
                    showSnackbar(back_button, "Ignored event successfully deleted", Constant.SUCCESS);
                }

            }
        } else {
            Log.d(TAG,"onDeleteFriendWebJobCompleted");
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
