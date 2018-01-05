package com.meeof.meeof.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;
import com.meeof.meeof.R;
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
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class GetStartedActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout googleBtn;
    private LinearLayout facebookBtn;
    private LinearLayout otherOptionsBtn;
    private String TAG = GetStartedActivity.class.getSimpleName();
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private String googleToken;
    private LinearLayout termsConditionsLlBtn;
    private String googleAuthToken;
    private String googleId;
    private Boolean serverResponded;


    private EventFilterModel eventModelObj;
    private EventFilterModel defaultEventModelObj;
    private ProfileResponse profileResponse;
    private String accessToken;
    private AddressPlaceModel currentAddressPlace;
    private boolean isKM = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_get_started);
        serverResponded = false;
        intViews();
        setUpGoogleSignIn();
        setUpFacebookSignIn();

    }

    private void setUpFacebookSignIn() {
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
                showSnackbar(otherOptionsBtn, getString(R.string.facebook_login_failed), Constant.ERROR);
            }

        });

    }

    private void signInWithToken(final String socialToken, final String grantType) {
        if (isNetworkAvailable()) {


            final OSPermissionSubscriptionState state = OneSignal.getPermissionSubscriptionState();
            state.getPermissionStatus();

            Log.d(TAG, "OSPermissionSubscriptionState:state " + state);
            String userId = "";

            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    String oneSignalId = userId;
                    jobManager.addJobInBackground(new SocialRegistrationWebJob(socialToken, grantType, userId));
                    startProgressBar();
                }
            });

            if (state.getPermissionStatus().getEnabled()) {
                userId = state.getSubscriptionStatus().getUserId();
            }


        } else {
            showSnackbar(googleBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onSocialRegistrationJobCompleted(LoginResponse loginResponse) {
        stopProgressBar();
        if (loginResponse != null) {
            if (loginResponse.getAccessToken() != null && loginResponse.getRefreshToken() != null && loginResponse.getTokenType() != null) {

                String accessToken = loginResponse.getAccessToken();
                String refreshToken = loginResponse.getRefreshToken();
                String tokenType = loginResponse.getTokenType();

                Log.d("ACCESS TOKEN ", tokenType + " " + accessToken);
                Log.d("REFRESH TOKEN ", refreshToken);

                sharedEditor.putString(Constant.ACCESS_TOKEN, tokenType + " " + accessToken);
                sharedEditor.putString(Constant.REFRESH_TOKEN, refreshToken);
                sharedEditor.putString(Constant.LOGGED_IN_WITH, loginResponse.getSocialType());
                sharedEditor.apply();

                getUserProfile(tokenType + " " + accessToken);

            } else {
                showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
        }
    }

    private void getUserProfile(String accessToken) {
        if (isNetworkAvailable()) {
            Log.d(TAG, "getUserProfile:" );
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
        } else {
            showSnackbar(googleBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetUserProfileWebJobCompleted(final ProfileResponse profileResponse) {

        Log.wtf(TAG, "onGetUserProfileWebJobCompleted: " + profileResponse.getStatus());

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
                    Log.wtf(TAG, "event model obj1: " +  eventModelObj.getMatrix());
                    Log.wtf(TAG, "event model obj2: " +  eventModelObj.getLocation());

                    //profileResponse.getData().setIs_first_login(1);

                    if(this.profileResponse.getData().getIs_first_login() == 1){
                        setDefaultEventFilter();
                        Log.wtf(TAG, "default event model obj1: " +  defaultEventModelObj.getLocation());
                        Log.wtf(TAG, "default event model obj2: " +  defaultEventModelObj.getMatrix());
                        postEventFilterInfo(defaultEventModelObj);
                    }else{
                        getEventFilterInfo();
                    }

                } else {
                    stopProgressBar();
                    showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
                }
            } else {
                stopProgressBar();
                showSnackbar(googleBtn, getString(R.string.login_failed), Constant.ERROR);
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

    private void setDefaultEventFilter(){
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

    private void getEventFilterInfo() {
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


    private void sendToDashboard() {
        this.finish();
        Intent intent = new Intent(this, DashboardActivity.class);
        this.startActivity(intent);
    }

    private void intViews() {
        googleBtn = (LinearLayout) findViewById(R.id.googleBtn);
        facebookBtn = (LinearLayout) findViewById(R.id.facebookBtn);
        otherOptionsBtn = (LinearLayout) findViewById(R.id.otherBtn);

        termsConditionsLlBtn = (LinearLayout) findViewById(R.id.termsConditionsLlBtn);

        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton); //FACEBOOK BUTTON
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        googleBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        otherOptionsBtn.setOnClickListener(this);
        termsConditionsLlBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.googleBtn:
                signIn();
                break;

            case R.id.facebookBtn:
                try {
                    disconnectFromFacebook();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                loginButton.performClick();
                break;

            case R.id.otherBtn:
                showOtherOptions(GetStartedActivity.this);
                break;

            case R.id.termsConditionsLlBtn:
                sendToTermsConditions();
                break;

        }
    }

    private void sendToTermsConditions() {
        Intent intent = new Intent(this, TermsAndPrivacyActivity.class);
        startActivity(intent);
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
                    showSnackbar(otherOptionsBtn, getString(R.string.login_failed), Constant.ERROR);
                }
            } else if (requestCode == RC_FACEBOOk_SIGN_IN) { //facebook
                Log.d(TAG, "requestCode :" + requestCode);
                callbackManager.onActivityResult(requestCode, resultCode, data);
            } else {
                showSnackbar(otherOptionsBtn, getString(R.string.login_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(otherOptionsBtn, getString(R.string.login_failed), Constant.ERROR);
        }
    }

    @Override
    public void showOtherOptions(Context context) {
        super.showOtherOptions(context);
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
