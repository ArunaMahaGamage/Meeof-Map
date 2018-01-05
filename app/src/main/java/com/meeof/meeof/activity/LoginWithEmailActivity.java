package com.meeof.meeof.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.EventFilterResponse;
import com.meeof.meeof.model.LoginResponse;
import com.meeof.meeof.model.event_filter_get_dto.EventFilterGetModel;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventFilterWebJob;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.PostEventFilterWebJob;
import com.meeof.meeof.webjob.SocialRegistrationWebJob;
import com.meeof.meeof.webjob.UpdateOneSignalIdWebJob;
import com.meeof.meeof.webjob.UserLoginEmailWebJob;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginWithEmailActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {


    LinearLayout googleBtn;
    LinearLayout facebookBtn;
    LinearLayout otherOptionsBtn;
    private LinearLayout loginLlBtn;
    private EditText emailET;
    private EditText passwordET;
    private ImageView backIvBtn;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String googleToken;
    private final String TAG = "LoginWithEmailActivity";
    private RelativeLayout showPasswordRlBtn;
    private RelativeLayout hidePasswordRlBtn;
    private RelativeLayout emailAddressETClearBtn;
    private LinearLayout termsConditionsLlBtn;
    private String googleAuthToken;
    private String googleId;
    private LinearLayout emailAddressEditTextBg;
    private LinearLayout passwordEditTextBg;
    private LinearLayout forgotPasswordTvLl;


    private EventFilterModel eventModelObj;
    private EventFilterModel defaultEventModelObj;

    private ProfileResponse profileResponse;
    private String accessToken;
    private AddressPlaceModel currentAddressPlace;
    private boolean isKM = true;
    private Boolean serverResponded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_with_email);
        intViews();
        serverResponded = false;
        setUpGoogleSignIn();
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public String fbToken;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("ACCESS TOKEN FB", loginResult.getAccessToken().getToken().toString());
                fbToken = loginResult.getAccessToken().getToken().toString();
                Log.d(TAG, "fbToken : " + fbToken);

                signInWithToken(fbToken, Constant.GRANT_TYPE_FB);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "loginButton onError: " + error.toString());
            }
        });
    }

    private void loginEmailUser(String email, String password) {
        if (isNetworkAvailable()) {

            OSPermissionSubscriptionState state = OneSignal.getPermissionSubscriptionState();
            state.getPermissionStatus();

            Log.i(TAG, "OSPermissionSubscriptionState:state " + state);
            String userId = "";

            if(state.getPermissionStatus().getEnabled()){
                userId = state.getSubscriptionStatus().getUserId();
            }

            jobManager.addJobInBackground(new UserLoginEmailWebJob(email, password));
            startProgressBar();
        } else {
            showToast(googleBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void signInWithToken(String socialToken, String grantType) {

        if (isNetworkAvailable()) {

            OSPermissionSubscriptionState state = OneSignal.getPermissionSubscriptionState();
            state.getPermissionStatus();

            Log.d(TAG, "OSPermissionSubscriptionState:state " + state);
            String userId = "";

            if(state.getPermissionStatus().getEnabled()){
                userId = state.getSubscriptionStatus().getUserId();
            }

            jobManager.addJobInBackground(new SocialRegistrationWebJob(socialToken, grantType, userId));
            startProgressBar();

        } else {
            showToast(googleBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onUserLoginJobsCompleted(LoginResponse loginResponse) {
        /**Both Socail and Email logins come here**/
        Log.i(TAG,"onUserLoginJobsCompleted");

        if (loginResponse != null) {

            if(loginResponse.getStatus() != null && loginResponse.getStatus().equalsIgnoreCase(Constant.SUCCESS)){

                if (loginResponse.getAccessToken() != null && loginResponse.getRefreshToken() != null && loginResponse.getTokenType() != null) {
                    String accessToken = loginResponse.getAccessToken();
                    String refreshToken = loginResponse.getRefreshToken();
                    String tokenType = loginResponse.getTokenType();

                    Log.i("ACCESS TOKEN ", tokenType + " " + accessToken);
                    Log.i("REFRESH TOKEN ", refreshToken);

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
                    showToast(googleBtn, getString(R.string.login_failed), Constant.ERROR);
                }
            }else{
                stopProgressBar();

                if(loginResponse.getMessage() != null && loginResponse.getMessage().length() > 0){
                    showToast(googleBtn, loginResponse.getMessage(), Constant.ERROR);
                }else{
                    showToast(googleBtn, getString(R.string.login_failed), Constant.ERROR);
                }
            }
        } else {
            stopProgressBar();
            showToast(googleBtn, getString(R.string.login_failed), Constant.ERROR);
        }
    }

    private void getUserProfile(String accessToken) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
        } else {
            stopProgressBar();
            showSnackbar(googleBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetUserProfileWebJobCompleted(final ProfileResponse profileResponse) {
        Log.wtf(TAG,"onGetUserProfileWebJobCompleted");

        if(!serverResponded){

            serverResponded = true;

            if (profileResponse != null) {
                if (profileResponse.getStatus() != null && profileResponse.getStatus().equals(Constant.SUCCESS)) {

                    this.profileResponse = profileResponse;
                    eventModelObj = new EventFilterModel();
                    setUserSelectedMatrix();
                    saveContentBasedOnType();
                    defaultEventModelObj =  eventModelObj;
                    accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

                    Log.wtf(TAG,"onGetUserProfileWebJobCompleted 2");
//                    profileResponse.getData().setIs_first_login(1);

                    if(this.profileResponse.getData().getIs_first_login() == 1){
                        setDefaultEventFilter();
                        postEventFilterInfo(defaultEventModelObj);
                    }else{
                        getEventFilterInfo();
                    }

                } else {
                    stopProgressBar();
                    goToGetStartedActivity();
                }
            } else {
                stopProgressBar();
                goToGetStartedActivity();
            }
        }
    }

    private void postEventFilterInfo(EventFilterModel eventModelObj) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostEventFilterWebJob(accessToken, eventModelObj));
        }else{
            stopProgressBar();
            showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostEventFilterInfo(EventFilterResponse eventFilterResponse) {
        if (eventFilterResponse != null) {
            if (eventFilterResponse.getStatus() != null && eventFilterResponse.getStatus().equals(Constant.SUCCESS)) {
                getEventFilterInfo();
            } else {
                stopProgressBar();
                showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
        }
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



    private void getEventFilterInfo() {
        Log.wtf(TAG,"getEventFilterInfo");
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventFilterWebJob(accessToken));
        } else {
            stopProgressBar();
            showSnackbar(googleBtn, getString(R.string.no_internet), Constant.ERROR);
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

            if(eventFilterGetModel.getData().getNiceaddress() != null){
                eventModelObj.setNiceaddress(eventFilterGetModel.getData().getNiceaddress().toString());
            }else{
                eventModelObj.setNiceaddress("");
            }

            saveObjectToSharedPref(sharedEditor, this.eventModelObj, Constant.EVENT_FILTER_OBJ);
        }

        if(profileResponse.getData().getIs_first_login() == 1){
            Log.d(TAG, "First Time User");
            sendToEditProfileActivity(); //IF first time user send to EditProfile Activity//
        }else{
            sendToDashboard(); //ELSE send to Dashboard Activity//
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

    private void goToGetStartedActivity() {
        Intent intent = new Intent(this, GetStartedActivity.class);
        startActivity(intent);
        finish();
    }



    private void intViews() {
        googleBtn = (LinearLayout) findViewById(R.id.googleBtn);
        facebookBtn = (LinearLayout) findViewById(R.id.facebookBtn);
        otherOptionsBtn = (LinearLayout) findViewById(R.id.otherBtn);

        loginLlBtn = (LinearLayout) findViewById(R.id.loginLlBtn);

        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton); //FACEBOOK BUTTON

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        backIvBtn = (ImageView) findViewById(R.id.backIvBtn);

        termsConditionsLlBtn = (LinearLayout) findViewById(R.id.termsConditionsLlBtn);

        showPasswordRlBtn = (RelativeLayout) findViewById(R.id.showPasswordRlBtn);
        hidePasswordRlBtn = (RelativeLayout) findViewById(R.id.hidePasswordRlBtn);
        emailAddressETClearBtn = (RelativeLayout) findViewById(R.id.emailAddressETClearBtn);

        emailAddressEditTextBg = (LinearLayout) findViewById(R.id.emailAddressEditTextBg);
        passwordEditTextBg = (LinearLayout) findViewById(R.id.passwordEditTextBg);
        forgotPasswordTvLl = (LinearLayout) findViewById(R.id.forgotPasswordTvLl);
        Typeface mFont = Typeface.createFromAsset(this.getAssets(), "Hind-Medium.ttf");
        passwordET.setTypeface(mFont);

        googleBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        otherOptionsBtn.setOnClickListener(this);
        loginLlBtn.setOnClickListener(this);
        backIvBtn.setOnClickListener(this);
        emailAddressETClearBtn.setOnClickListener(this);
        showPasswordRlBtn.setOnClickListener(this);
        hidePasswordRlBtn.setOnClickListener(this);
        termsConditionsLlBtn.setOnClickListener(this);
        forgotPasswordTvLl.setOnClickListener(this);

        passwordET.setOnFocusChangeListener(this);
        emailET.setOnFocusChangeListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.googleBtn:
                signIn();
                break;

            case R.id.facebookBtn:
                loginButton.performClick();
                break;

            case R.id.otherBtn:
                showOtherOptions(this);
                break;

            case R.id.loginLlBtn:
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if (isEmailValid()) {
                    if (isPasswordValid()) {
                        Log.i(TAG, "Inside registerBtn");
                        String email = emailET.getText().toString();
                        String password = passwordET.getText().toString();
                        loginEmailUser(email, password);
                    } else {
                        showSnackbar(googleBtn, getString(R.string.non_valid_password), Constant.ERROR);
                    }
                } else {
                    showSnackbar(googleBtn, getString(R.string.non_valid_email), Constant.ERROR);
                }
                break;
            case R.id.backIvBtn:
                this.onBackPressed();
                break;

            case R.id.emailAddressETClearBtn:
                emailET.setText("");
                break;

            case R.id.showPasswordRlBtn:
                Log.d(TAG, "showPasswordRlBtn");
                passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordET.setSelection(passwordET.getText().length());
                showPasswordRlBtn.setVisibility(View.GONE);
                hidePasswordRlBtn.setVisibility(View.VISIBLE);
                break;

            case R.id.hidePasswordRlBtn:
                Log.d(TAG, "hidePasswordRlBtn");
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordET.setSelection(passwordET.getText().length());
                showPasswordRlBtn.setVisibility(View.VISIBLE);
                hidePasswordRlBtn.setVisibility(View.GONE);
                break;

            case R.id.termsConditionsLlBtn:
                sendToTermsConditions();
                Helper.clickGaurd(termsConditionsLlBtn);
                break;

            case R.id.forgotPasswordTvLl:
                sendToForgetPasswordActivity();
                Helper.clickGaurd(forgotPasswordTvLl);
                break;
        }
    }

    private void sendToForgetPasswordActivity() {
        Intent intent = new Intent(this,ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        Log.d(TAG, "onFocusChange");

        switch (view.getId()) {
            case R.id.emailET:
                if (b)
                    enableEmailET();
                break;

            case R.id.passwordET:
                if (b)
                    enablePasswordET();
                break;
        }
    }

    private void sendToTermsConditions() {
        Intent intent = new Intent(this, TermsAndPrivacyActivity.class);
        startActivity(intent);
    }

    private void enableEmailET() {
        Log.d(TAG, "enableEmailET");
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
        passwordEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    private void enablePasswordET() {
        Log.d(TAG, "enablePasswordET");
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        passwordEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_GOOGLE_SIGN_IN) {
                Log.d(TAG, "Inside onActivityResult RC_GOOGLE_SIGN_IN");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                Log.d(TAG, "Google result: " + result.getStatus().toString());
                if (result.isSuccess()) {
                    Log.d(TAG, "Inside onActivityResult isSuccess");

                    GoogleSignInAccount account = result.getSignInAccount();
                    googleToken = account.getIdToken(); //job to send

                    googleAuthToken = account.getServerAuthCode();
                    Log.d(TAG, "googleAuthToken : " + googleAuthToken);

                    googleId = account.getId();
                    Log.d(TAG, "googleId : " + googleId);

                    signInWithToken(googleToken, Constant.GRANT_TYPE_GOOGLE);

                    Log.d(TAG, "googleToken :" + googleToken);

                    Log.d(" GOOG ID", googleToken);

                    Log.d(TAG, "google token : " + googleToken);


                } else {
                    showSnackbar(otherOptionsBtn,getString(R.string.google_login_failed),Constant.ERROR);
                }
            } else if(requestCode==RC_FACEBOOk_SIGN_IN){ //facebook
                Log.d(TAG,"requestCode :"+requestCode);
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }else{
                showSnackbar(otherOptionsBtn,getString(R.string.login_failed),Constant.ERROR);
            }
        }else{
            showSnackbar(otherOptionsBtn,getString(R.string.login_failed),Constant.ERROR);
        }
    }



    private void sendToDashboard() {
        this.finish();
        Intent intent = new Intent(this, DashboardActivity.class);
        this.startActivity(intent);
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

    private Boolean isEmailValid() {
        return emailET.getText().toString().matches(Constant.EMAIL_REGEX) ? true : false;
    }

    private Boolean isPasswordValid() {
        return passwordET.getText().toString().length() > 0 ? true : false;
    }

}
