package com.meeof.meeof.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.notification_settings_response.GetSettingsNotificationResponse;
import com.meeof.meeof.model.profile.Data;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.CopyEventWebJob;
import com.meeof.meeof.webjob.GetSettingsNotificationPreference;
import com.meeof.meeof.webjob.SaveNotificationSettingsWebJob;
import com.meeof.meeof.webjob.UpdateUserLocationTrackingStatusWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by ransikadesilva on 10/11/17.
 */

public class SettingsActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private LinearLayout manageFriendsLlBtn;
    private LinearLayout findFriendsLlBtn;
    private LinearLayout updateProfileLlBtn;
    private LinearLayout ignoredMembersLlBtn;
    private LinearLayout changePasswordLlBtn;
    private LinearLayout eventsInterestedInLlBtn;
    private LinearLayout igonoredEventsLlBtn;
    private SwitchCompat notifyOnCommentsSw;
    private SwitchCompat notifyOnLikesSw;
    private LinearLayout logoutLlBtn;
    private RelativeLayout profileLlBtn;
    private AppCompatImageView back_button;
    private LinearLayout deleteAccountLlBtn;
    private TextView userNameTv;
    private LinearLayout interestsLlBtn;
    private LinearLayout trackLL;
    private LinearLayout homeLL;
    private ImageView locationIv;
    private ImageView homeIv;
    private TextView trackTv;
    private TextView homeTv;
    private TextView updateMethodTV;
    private  String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        intitViews();
    }

    private void intitViews() {

        manageFriendsLlBtn = (LinearLayout) findViewById(R.id.manageFriendsLlBtn);
        findFriendsLlBtn = (LinearLayout) findViewById(R.id.findFriendsLlBtn);
        updateProfileLlBtn = (LinearLayout) findViewById(R.id.updateProfileLlBtn);
        ignoredMembersLlBtn = (LinearLayout) findViewById(R.id.ignoredMembersLlBtn);
        changePasswordLlBtn = (LinearLayout) findViewById(R.id.changePasswordLlBtn);
        eventsInterestedInLlBtn = (LinearLayout) findViewById(R.id.eventsInterestedInLlBtn);
        igonoredEventsLlBtn = (LinearLayout) findViewById(R.id.igonoredEventsLlBtn);
        notifyOnCommentsSw = (SwitchCompat) findViewById(R.id.notifyOnCommentsSw);
        notifyOnLikesSw = (SwitchCompat) findViewById(R.id.notifyOnCommentsSw);
        logoutLlBtn = (LinearLayout) findViewById(R.id.logoutLlBtn);
        profileLlBtn = (RelativeLayout) findViewById(R.id.profileRlBtn);
        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        deleteAccountLlBtn = (LinearLayout) findViewById(R.id.deleteAccountLlBtn);
        userNameTv = (TextView) findViewById(R.id.userNameTv);
        interestsLlBtn = (LinearLayout) findViewById(R.id.interestsLlBtn);
        updateMethodTV = (TextView) findViewById(R.id.updateMethodTV);

        trackLL = (LinearLayout) findViewById(R.id.trackLL);
        homeLL = (LinearLayout) findViewById(R.id.homeLL);
        locationIv = (ImageView) findViewById(R.id.locationIv);
        homeIv = (ImageView) findViewById(R.id.homeIv);

        trackTv = (TextView) findViewById(R.id.trackTv);
        homeTv = (TextView) findViewById(R.id.homeTv);
        notifyOnCommentsSw= (SwitchCompat) findViewById(R.id.notifyOnCommentsSw);
        notifyOnLikesSw= (SwitchCompat) findViewById(R.id.notifyOnLikesSw);

        if(sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false)){
            toggleAction(trackLL);
        }

        if(sharedPreferences.getBoolean(Constant.HOME, false)){
            toggleAction(homeLL);
        }


        back_button.setOnClickListener(this);
        manageFriendsLlBtn.setOnClickListener(this);
        findFriendsLlBtn.setOnClickListener(this);
        updateProfileLlBtn.setOnClickListener(this);
        igonoredEventsLlBtn.setOnClickListener(this);
        changePasswordLlBtn.setOnClickListener(this);
        eventsInterestedInLlBtn.setOnClickListener(this);
        igonoredEventsLlBtn.setOnClickListener(this);
        notifyOnCommentsSw.setOnClickListener(this);
        notifyOnLikesSw.setOnClickListener(this);
        logoutLlBtn.setOnClickListener(this);
        profileLlBtn.setOnClickListener(this);
        deleteAccountLlBtn.setOnClickListener(this);
        userNameTv.setOnClickListener(this);
        interestsLlBtn.setOnClickListener(this);
        trackLL.setOnClickListener(this);
        homeLL.setOnClickListener(this);
        ignoredMembersLlBtn.setOnClickListener(this);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        notifyOnCommentsSw.setOnCheckedChangeListener(this);
        notifyOnLikesSw.setOnCheckedChangeListener(this);

        updateUI();
        GetSettingsNotificationPreference();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.findFriendsLlBtn:
                sendToInviteFriendsActivity();
                break;
            case R.id.manageFriendsLlBtn:
                sendToManageFriendsActivity();
                break;
            case R.id.profileRlBtn:
                sendToProfileActivity();
                break;
            case R.id.backAcIvBtn:
                this.onBackPressed();
                break;
            case R.id.deleteAccountLlBtn:
                sendToDeleteAccountActivity();
                break;
            case R.id.logoutLlBtn:
                showAlertLogout();
                break;
            case R.id.updateProfileLlBtn:
                sendToUpdateProfile();
                break;
            case R.id.interestsLlBtn:
                sendToInterestsActivity();
                break;
            case R.id.changePasswordLlBtn:
                sendToChangePasswordActivity();
                break;
            case R.id.userNameTv:
                break;
            case R.id.trackLL:
                toggleAction(trackLL);
                saveContentBasedOnType(Constant.CURRENT_LOCATION);
                trackLL.setEnabled(false);
                homeLL.setEnabled(true);

                updateMethodTV.setText("Meeof will update the content when you are moving around");
                //call api to update user track location

                jobManager.addJobInBackground(new UpdateUserLocationTrackingStatusWebJob(accessToken,1));


                break;
            case R.id.homeLL:
                toggleAction(homeLL);
                saveContentBasedOnType(Constant.HOME);
                homeLL.setEnabled(false);
                trackLL.setEnabled(true);

                updateMethodTV.setText("Meeof will show content based on your home profile address");
                //call api to update user track location
                jobManager.addJobInBackground(new UpdateUserLocationTrackingStatusWebJob(accessToken,0));

                break;
            case R.id.ignoredMembersLlBtn:
                sendToIgnoredMembersActivity();
                break;
            case R.id.eventsInterestedInLlBtn:
                sendToInterestedEventsActivity();
                break;
            case R.id.igonoredEventsLlBtn:
                sendToIgnoredEventsActivity();
                break;
        }
    }

    @Subscribe
    public void onUpdateUserLocationTrackingStatusWebJobComplete(HttpResponse defaultResponse) {
        stopProgressBar();
        if (defaultResponse != null) {
            if (defaultResponse.getStatus() != null && defaultResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG,"onUpdateUserLocationTrackingStatusWebJobComplete: Success: ");

            } else {
                Log.d(TAG,"onUpdateUserLocationTrackingStatusWebJobComplete: Fail: ");
            }
        } else {

        }
    }

    private void saveContentBasedOnType(String contentBasedType) {
        if(contentBasedType.equals(Constant.CURRENT_LOCATION)) {
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, true);
            sharedEditor.putBoolean(Constant.HOME, false);
        }else if(contentBasedType.equals(Constant.HOME)){
            sharedEditor.putBoolean(Constant.HOME, true);
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, false);
        }

        sharedEditor.apply();
    }

    private void toggleAction(LinearLayout clickedLl) {
        if (clickedLl.getId() == R.id.trackLL) {
            locationIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_current_location_on));
            homeIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home_off));

            trackLL.setBackground(ContextCompat.getDrawable(this, R.drawable.location_button));
            homeLL.setBackground(ContextCompat.getDrawable(this, R.drawable.location_button_white));

            trackTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            homeTv.setTextColor(ContextCompat.getColor(this, R.color.usualTextColor));

        } else if (clickedLl.getId() == R.id.homeLL) {
            homeIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_home_on));
            locationIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_current_location_off));

            homeLL.setBackground(ContextCompat.getDrawable(this, R.drawable.location_button));
            trackLL.setBackground(ContextCompat.getDrawable(this, R.drawable.location_button_white));

            homeTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            trackTv.setTextColor(ContextCompat.getColor(this, R.color.usualTextColor));

        }
    }

    private void updateUI() {
        ProfileResponse profileResponse = retriveSavedProfileObject();
        if (profileResponse != null) {
            Data profData = profileResponse.getData();
            userNameTv.setText(profData.getFirst_name() + " " + (profData.getLast_name() != null ? profData.getLast_name() : ""));
        }
    }

    private ProfileResponse retriveSavedProfileObject() {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            return profileResponse;
        }
        return null;
    }

    private void sendToChangePasswordActivity() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void sendToInterestsActivity() {
        Intent intent = new Intent(this, InterestsActivity.class);
        intent.putExtra(Constant.IS_FROM_SETTINGS, true);
        startActivity(intent);
    }

    private void sendToUpdateProfile() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra(Constant.IS_FROM_SETTINGS, true);
        startActivity(intent);
    }

    private void sendToDeleteAccountActivity() {
        Intent intent = new Intent(this, DeleteAccountActivity.class);
        startActivity(intent);
    }

    private void sendToProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(Constant.IS_MY_PROFILE, true);
        startActivity(intent);
    }

    private void sendToManageFriendsActivity() {
        Intent intent = new Intent(this, ManageFriendsActivity.class);
        startActivity(intent);
    }

    private void sendToInviteFriendsActivity() {
        Intent intent = new Intent(this, InviteFriendsActivity.class);
        startActivity(intent);
    }

    private void sendToIgnoredMembersActivity() {
        Intent intent = new Intent(this, IgnoredMembersActivity.class);
        startActivity(intent);
    }

    private void sendToInterestedEventsActivity() {
        Intent intent = new Intent(this, IgnoredInterestedActivity.class);
        intent.putExtra(Constant.IS_INTERESTED_EVENTS, true);
        startActivity(intent);
    }

    private void sendToIgnoredEventsActivity() {
        Intent intent = new Intent(this, IgnoredInterestedActivity.class);
        intent.putExtra(Constant.IS_INTERESTED_EVENTS, false);
        startActivity(intent);
    }

    private void showAlertLogout() {
        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
        alertDialog.setTitle("Logout");
        alertDialog.setMessage("Are you sure you want to Logout?");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutUser();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void logoutUser() {
        sharedEditor.putString(Constant.ACCESS_TOKEN, "");
        sharedEditor.putString(Constant.REFRESH_TOKEN, "");
        sharedEditor.apply();
        disconnectFromFacebook();

        this.finish();

        Intent intent = new Intent(this, GetStartedActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.notifyOnCommentsSw:
                //startProgressBar();
                SetSettingsNotificationPreference();
                break;
            case R.id.notifyOnLikesSw:
                //startProgressBar();
                SetSettingsNotificationPreference();
                break;
        }
    }

    private void GetSettingsNotificationPreference(){
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetSettingsNotificationPreference(accessToken));
        } else {
            showSnackbar(notifyOnLikesSw, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void SetSettingsNotificationPreference(){
        if (isNetworkAvailable()) {
            String notifiyCommentsString=notifyOnCommentsSw.isChecked()?"on":"off";
            String notifiyLikeString=notifyOnLikesSw.isChecked()?"on":"off";
            jobManager.addJobInBackground(new SaveNotificationSettingsWebJob(accessToken,notifiyCommentsString,notifiyLikeString,"on"));
        } else {
            showSnackbar(notifyOnLikesSw, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSaveNotifcationSettings(GetSettingsNotificationResponse getSettingsNotificationResponse) {
        //stopProgressBar();
        if (getSettingsNotificationResponse != null) {
            if (getSettingsNotificationResponse.getStatus() != null && getSettingsNotificationResponse.getStatus().equals(Constant.SUCCESS)) {
                notifyOnLikesSw.setChecked(getSettingsNotificationResponse.getData().getNotify_like()==1);
                notifyOnCommentsSw.setChecked(getSettingsNotificationResponse.getData().getNotify_comment()==1);
                Log.d(TAG,"onUpdateNotifcationSettings: Success: ");
            } else {
                Log.d(TAG,"onUpdateNotifcationSettings: Fail: ");
            }
        } else {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateNotifcationSettings(HttpResponse httpResponse) {
        //stopProgressBar();
        if (httpResponse != null) {
            if (httpResponse.getStatus() != null && httpResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG,"onUpdateNotifcationSettings: Success: ");
            } else {
                Log.d(TAG,"onUpdateNotifcationSettings: Fail: ");
            }
        } else {

        }
        GetSettingsNotificationPreference();
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
