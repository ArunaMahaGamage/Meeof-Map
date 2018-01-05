package com.meeof.meeof.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.EventFilterResponse;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.LoginResponse;
import com.meeof.meeof.model.event_filter_get_dto.EventFilterGetModel;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventFilterWebJob;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.PostEventFilterWebJob;
import com.meeof.meeof.webjob.UserLoginEmailWebJob;
import com.meeof.meeof.webjob.VerificationWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VerificationActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private EditText codeET;
    private LinearLayout submitBtn;
    private RelativeLayout resendCodeRlBtn;
    private TextView emailTV;
    private String email, password, accessToken;
    private String TAG = "VerificationActivity";
    private RelativeLayout backIvBtn;
    private RelativeLayout emailAddressETClearBtn;
    private LinearLayout codeEditTextBg;
    private TextView verificationTextTopTV;
    private boolean isFromOptionsMenu;
    private Boolean serverResponded;
    private ProfileResponse profileResponse;

    private EventFilterModel eventModelObj;
    private EventFilterModel defaultEventModelObj;

    private boolean isKM = true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        Intent intent = getIntent();
        isFromOptionsMenu = intent.getBooleanExtra(Constant.IS_FROM_OPTIONS_MENU, false);
        email = intent.getStringExtra(Constant.USER_EMAIL);
        password = intent.getStringExtra(Constant.PASSWORD);
        serverResponded = false;
        intViews();
    }


    private void intViews() {
        codeET = (EditText) findViewById(R.id.codeET);
        submitBtn = (LinearLayout) findViewById(R.id.submitLLBtn);
        resendCodeRlBtn = (RelativeLayout) findViewById(R.id.resendCodeRlBtn);
        submitBtn = (LinearLayout) findViewById(R.id.submitLLBtn);
        emailTV = (TextView) findViewById(R.id.emailTV);
        backIvBtn = (RelativeLayout) findViewById(R.id.backIvBtn);
        verificationTextTopTV = (TextView) findViewById(R.id.verificationTextTopTV);

        resendCodeRlBtn = (RelativeLayout) findViewById(R.id.resendCodeRlBtn);
        emailAddressETClearBtn = (RelativeLayout) findViewById(R.id.emailAddressETClearBtn);
        codeEditTextBg = (LinearLayout) findViewById(R.id.codeEditTextBg);

        if (!isFromOptionsMenu) {
            emailTV.setText(getString(R.string.verify_txt_code_to) + " " + email);

        } else {
            verificationTextTopTV.setText(getString(R.string.verifiation_top_text));
            emailTV.setText(getString(R.string.verifiation_bottom_text));
        }

        resendCodeRlBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
        backIvBtn.setOnClickListener(this);

        emailTV.setOnFocusChangeListener(this);
        emailAddressETClearBtn.setOnClickListener(this);
        codeET.setOnFocusChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitLLBtn:
                String code = codeET.getText().toString();
                if (isCodeValid() && !code.isEmpty()) {
                    submitCode(code);
                } else {
                    showSnackbar(backIvBtn, getString(R.string.invalid_code), Constant.ERROR);
                }
                break;

            case R.id.emailAddressETClearBtn:
                codeET.setText("");
                break;

            case R.id.resendCodeRlBtn:
                sendToResendCode();
                Helper.clickGaurd(resendCodeRlBtn);
                break;

            case R.id.backIvBtn:
                this.onBackPressed();
                break;
        }
    }

    private void sendToResendCode() {
        Intent intent = new Intent(this, ResendActivationCodeActivity.class);
        this.startActivity(intent);
    }

    private void submitCode(String code) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new VerificationWebJob(code));
            startProgressBar();
        } else {
            showSnackbar(emailTV, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onVerificationJobCompleted(HttpResponse httpResponse) {

        Log.wtf(TAG, "Inside onVerificationJobCompleted");

        if ((httpResponse.getStatus() != null && httpResponse.getMessage() != null)
                && (!httpResponse.getStatus().isEmpty()) && !httpResponse.getMessage().isEmpty()) {
            Log.wtf(TAG, "Inside onVerificationJobCompleted 7");

            if (httpResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.wtf(TAG, "Inside onVerificationJobCompleted 2");

                if (!isFromOptionsMenu) {

                    Log.wtf(TAG, "Inside onVerificationJobCompleted 4");
                    String emailShared = sharedPreferences.getString(Constant.USER_EMAIL, null);
                    String passwordShared = sharedPreferences.getString(Constant.PASSWORD, null);

                    if (emailShared != null && passwordShared != null) {
                        Log.wtf(TAG, "Inside onVerificationJobCompleted 5");
                        loginUser(emailShared, passwordShared);
                    } else {
                        Log.wtf(TAG, "Inside onVerificationJobCompleted 6");
                        stopProgressBar();
                        sendToEmailLogin();
                    }

                } else {
                    Log.wtf(TAG, "Inside onVerificationJobCompleted 3");
                    stopProgressBar();
                    sendToEmailLogin();
                }

            } else {
                stopProgressBar();
                showSnackbar(emailTV, getString(R.string.invalid_error_messasge_code), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(emailTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void sendToEmailLogin() {
        Intent intent = new Intent(this, LoginWithEmailActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "onFocusChange");
        switch (v.getId()) {
            case R.id.codeET:
                if (hasFocus)
                    enableEmailET();
                else {
                    disableEmailET();
                    hideKeyboard(codeET);
                }

                break;
        }
    }

    private Boolean isCodeValid() {
        return codeET.getText().toString().trim().length() > 0 ? true : false;
    }

    private void enableEmailET() {
        codeEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
    }

    private void disableEmailET() {
        codeEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
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

    public void hideKeyboard(View view) {
        Log.d(TAG, "hideKeyboard");
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void loginUser(String email, String password) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new UserLoginEmailWebJob(email, password));
        } else {
            stopProgressBar();
            showSnackbar(emailTV, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onUserLoginJobsCompleted(LoginResponse loginResponse) {
        /**Both Socail and Email logins come here**/

        Log.wtf(TAG, "onUserLoginJobsCompleted");

        if (loginResponse != null) {
            if (loginResponse.getAccessToken() != null && loginResponse.getRefreshToken() != null && loginResponse.getTokenType() != null) {
                String accessToken = loginResponse.getAccessToken();
                String refreshToken = loginResponse.getRefreshToken();
                String tokenType = loginResponse.getTokenType();

                Log.wtf("ACCESS TOKEN ", tokenType + " " + accessToken);
                Log.wtf("REFRESH TOKEN ", refreshToken);

                sharedEditor.putString(Constant.ACCESS_TOKEN, tokenType + " " + accessToken);
                sharedEditor.putString(Constant.REFRESH_TOKEN, refreshToken);

                if (loginResponse.getSocialType() != null) {
                    sharedEditor.putString(Constant.LOGGED_IN_WITH, loginResponse.getSocialType());
                } else {
                    sharedEditor.putString(Constant.LOGGED_IN_WITH, Constant.GRANT_TYPE_EMAIL);
                }

                sharedEditor.apply();
                getUserProfile(tokenType+" "+accessToken);

            } else {
                stopProgressBar();
                showSnackbar(emailTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(emailTV, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void getUserProfile(String accessToken) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
        } else {
            showSnackbar(emailTV, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetUserProfileWebJobCompleted(final ProfileResponse profileResponse) {

        if(!serverResponded) {
            serverResponded = true;

            Log.d(TAG,"onGetUserProfileWebJobCompleted");

            if (profileResponse != null) {
                if (profileResponse.getStatus() != null && profileResponse.getStatus().equals(Constant.SUCCESS)) {

                    this.profileResponse = profileResponse;
                    eventModelObj = new EventFilterModel();
                    setUserSelectedMatrix();
                    saveContentBasedOnType();
                    defaultEventModelObj =  eventModelObj;
                    accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

                    if(this.profileResponse.getData().getIs_first_login() == 1){
                        setDefaultEventFilter();
                        postEventFilterInfo(defaultEventModelObj);
                    }else{
                        getEventFilterInfo();
                    }

                } else {
                    stopProgressBar();
                    showSnackbar(emailTV,getString(R.string.login_failed),Constant.ERROR);
                }
            } else {
                stopProgressBar();
                showSnackbar(emailTV,getString(R.string.login_failed),Constant.ERROR);
            }
        }
    }

    private void setUserSelectedMatrix() {
        if (profileResponse != null) {
            if (profileResponse.getData().getMatrix().equals("0")) {
                isKM = true;
                eventModelObj.setMatrix("0");
            }
            if (profileResponse.getData().getMatrix().equals("1")) {
                isKM = false;
                eventModelObj.setMatrix("1");
            }
        }
    }

    private void saveContentBasedOnType() {

        if(profileResponse.getData().getTrack_current_location() == 1){
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, true);
            sharedEditor.putBoolean(Constant.HOME, false);
        }else {
            sharedEditor.putBoolean(Constant.HOME, true);
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, false);
        }
        sharedEditor.apply();
    }

    private void setDefaultEventFilter() {
        defaultEventModelObj.setNiceaddress(profileResponse.getData().getAddress());
        defaultEventModelObj.setLat(String.valueOf(profileResponse.getData().getLatitude()));
        defaultEventModelObj.setLng(String.valueOf(profileResponse.getData().getLongitude()));
        defaultEventModelObj.setNiceaddress(profileResponse.getData().getAddress());
        defaultEventModelObj.setLocation("1");
        defaultEventModelObj.setMasterfilter("0");
        defaultEventModelObj.setAcceptabledistance("5");
        defaultEventModelObj.setHideoutsidefriends("0");
        defaultEventModelObj.setSortbyproximity("0");
        defaultEventModelObj.setRememberfilter("0");
    }


    private void postEventFilterInfo(EventFilterModel eventModelObj) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostEventFilterWebJob(accessToken, eventModelObj));
        }else{
            stopProgressBar();
            showSnackbar(emailTV, getString(R.string.login_failed), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostEventFilterInfo(EventFilterResponse eventFilterResponse) {
        if (eventFilterResponse != null) {
            if (eventFilterResponse.getStatus() != null && eventFilterResponse.getStatus().equals(Constant.SUCCESS)) {
                getEventFilterInfo();
            } else {
                stopProgressBar();
                showSnackbar(emailTV, getString(R.string.login_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(emailTV, getString(R.string.login_failed), Constant.ERROR);
        }
    }


    private void getEventFilterInfo() {
        Log.wtf(TAG,"getEventFilterInfo");
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventFilterWebJob(accessToken));
        } else {
            stopProgressBar();
            showSnackbar(emailTV, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetEventFilterInfoCompleted(EventFilterGetModel eventFilterGetModel) {
        Log.i(TAG, "EVENT Listned " + eventFilterGetModel.toString());

        if (eventFilterGetModel.getData() != null) {
            Log.wtf(TAG, "onGetEventFilterInfoCompleted");
            eventModelObj.setLocation(eventFilterGetModel.getData().getLocation().toString());
            eventModelObj.setMasterfilter(eventFilterGetModel.getData().getMasterfilter().toString());
            eventModelObj.setAcceptabledistance(eventFilterGetModel.getData().getAcceptabledistance().toString());
            eventModelObj.setMatrix(eventFilterGetModel.getData().getMatrix().toString());
            eventModelObj.setHideoutsidefriends(eventFilterGetModel.getData().getHideoutsidefriends().toString());
            eventModelObj.setSortbyproximity(eventFilterGetModel.getData().getSortbyproximity().toString());
            eventModelObj.setRememberfilter(eventFilterGetModel.getData().getRememberfilter().toString());
            eventModelObj.setNiceaddress("");
//            eventModelObj.setNiceaddress(eventFilterGetModel.getData().getNiceaddress().toString());

            saveObjectToSharedPref(sharedEditor, this.eventModelObj, Constant.EVENT_FILTER_OBJ);
        }

        if(profileResponse.getData().getIs_first_login() == 1){
            Log.d(TAG, "First Time User");
            sendToEditProfileActivity(); //IF first time user send to EditProfile Activity//
        }else{
            sendToDashboard(); //ELSE send to Dashboard Activity//
        }

    }

    private void saveObjectToSharedPref(SharedPreferences.Editor sharedEditor, Object object, String objectSaveName) {
        try {
            Gson gson = new Gson();
            String objectJson = gson.toJson(object);
            sharedEditor.putString(objectSaveName, objectJson);
            sharedEditor.apply();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void sendToEditProfileActivity() {
        this.finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        this.startActivity(intent);
    }


    private void sendToDashboard() {
        this.finish();
        Intent intent = new Intent(this, DashboardActivity.class);
        this.startActivity(intent);
    }


}
