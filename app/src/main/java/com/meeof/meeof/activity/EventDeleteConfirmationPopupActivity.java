package com.meeof.meeof.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.DeleteEventWebJobResponse;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteEventWebJob;
import com.meeof.meeof.webjob.GetEventInfoWebJob;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ransikadesilva on 10/31/17.
 */

public class EventDeleteConfirmationPopupActivity extends BaseActivityPopup implements View.OnClickListener {

    private static final String TAG = EventDeleteConfirmationPopupActivity.class.getSimpleName();
    private Button cancelBtn;
    private Button confirmBtn;
    private String accessToken;
    private int eventId;
    private ImageView eventIv;
    private TextView eventNameTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_event_confirmation);

        Intent intent = getIntent();
        if(intent.hasExtra(Constant.EVENT_ID)){
            eventId = intent.getIntExtra(Constant.EVENT_ID, 0);
        }
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        initViews();

        getEventDetails(eventId);
    }

    private void initViews() {
        cancelBtn = (Button) findViewById(R.id.cancelBtn);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);
        eventIv = (ImageView) findViewById(R.id.eventIv);
        eventNameTv = (TextView) findViewById(R.id.eventNameTv);

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelBtn:
                this.finish();
                break;
            case R.id.confirmBtn:
                deleteEvent(eventId);
                break;
        }
    }


    private void getEventDetails(int eventid) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventInfoWebJob(accessToken, eventid));
            startProgressBar();
        } else {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventInfoWebJob(EventInfoResponse eventInfoResponse) {
        stopProgressBar();
        Log.d(TAG, "onGetEventInfoWebJob()");
        if (eventInfoResponse != null) {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse!=null");
            if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null");
                setDataToUI(eventInfoResponse);
            } else {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE");
//                showSnackbar(cancelBtn, getString(R.string), Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE ELSE");
//            showSnackbar(cancelBtn, getString(R.string.failed_to_load_images), Constant.ERROR);
        }
    }

    private void setDataToUI(EventInfoResponse eventInfoResponse) {
        if (eventInfoResponse.getData() != null) {
            if (eventInfoResponse.getData().get(0) != null) {
                eventNameTv.setText(eventInfoResponse.getData().get(0).getTitle()+"");

                String eventPoster = eventInfoResponse.getData().get(0).getEvent_poster();
                String photoUrl = eventPoster == null || eventPoster.trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                        Constant.EVENT_POSTER_BASE_URL + eventPoster;

                Picasso.with(getApplicationContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.ico_profile_edit_avatar)
                        .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                        .into(eventIv);

            }
        }

    }


    private void deleteEvent(int eventid) {
        if (eventid != 0) {
            if (isNetworkAvailable()) {
                jobManager.addJobInBackground(new DeleteEventWebJob(accessToken, eventid));
                startProgressBar();
            } else {
                showSnackbar(cancelBtn, getString(R.string.no_internet), Constant.ERROR);
            }
        } else {
            showSnackbar(cancelBtn, getString(R.string.unable_to_delete_event), Constant.ERROR);
        }
    }


    @Subscribe
    public void onDeleteEventWebJob(DeleteEventWebJobResponse deleteEventWebJobdResponse) {
        stopProgressBar();
        if (deleteEventWebJobdResponse != null) {
            if (deleteEventWebJobdResponse.getStatus() != null && deleteEventWebJobdResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(cancelBtn, getString(R.string.event_delete_successfully), Constant.SUCCESS);
            } else {
                showSnackbar(cancelBtn, getString(R.string.event_delete_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(cancelBtn, getString(R.string.event_delete_failed), Constant.ERROR);
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
