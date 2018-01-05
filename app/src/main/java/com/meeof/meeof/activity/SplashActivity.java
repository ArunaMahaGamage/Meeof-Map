package com.meeof.meeof.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.RefreshResponse;
import com.meeof.meeof.model.UpdateFilterGetModel;
import com.meeof.meeof.model.UpdateFilterModel;
import com.meeof.meeof.model.event_filter_get_dto.EventFilterGetModel;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventFilterWebJob;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.GetUpdateFilterWebJob;
import com.meeof.meeof.webjob.UserLoginRefreshWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private ImageView meeofImg;
    private Boolean serverResponded;
    private EventFilterModel eventModelObj;
    private UpdateFilterModel updateFilterModel;
    private ProfileResponse profileResponse;
    private String accessToken;
    private AddressPlaceModel currentAddressPlace;
    private boolean isKM = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        meeofImg = (ImageView) findViewById(R.id.meeofImg);

        serverResponded = false;
        String refreshToken = sharedPreferences.getString(Constant.REFRESH_TOKEN, "");

        printFbHashKey();

        if (refreshToken != null && !refreshToken.equals("")) {
            Log.d(TAG, "refresh not null");
            loginWithRefresh(refreshToken);
        } else {
            Log.d(TAG, "refresh  null");
            goToGetStartedActivity();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }



    private void loginWithRefresh(String refreshToken) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new UserLoginRefreshWebJob(refreshToken));
            startProgressBar();
        } else {
            showSnackbar(meeofImg, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onLoginRefreshJobsCompleted(RefreshResponse refreshResponse) {

        stopProgressBar();
        if (refreshResponse != null) {
            if (refreshResponse.getAccessToken() != null && refreshResponse.getRefreshToken() != null && refreshResponse.getTokenType() != null) {
                String accessToken = refreshResponse.getAccessToken();
                String refreshToken = refreshResponse.getRefreshToken();
                String tokenType = refreshResponse.getTokenType();

                Log.d(TAG + "ACCESS TOKEN ", tokenType + " " + accessToken);
                Log.d(TAG + "REFRESH TOKEN ", refreshToken);

                sharedEditor.putString(Constant.ACCESS_TOKEN, tokenType + " " + accessToken);
                sharedEditor.putString(Constant.REFRESH_TOKEN, refreshToken);
                sharedEditor.apply();
                getUserProfile(tokenType + " " + accessToken);


            } else {
                goToGetStartedActivity();
            }
        } else {
            goToGetStartedActivity();
        }
    }

    private void getUserProfile(String accessToken) {
        if (isNetworkAvailable()) {
            Log.wtf(TAG, "getUserProfile 1");
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
        } else {
            showSnackbar(meeofImg, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetUserProfileWebJobCompleted(final ProfileResponse profileResponse) {

        if(!serverResponded){

            serverResponded = true;

            Log.wtf(TAG,"onGetUserProfileWebJobCompleted 1");
            if (profileResponse != null) {
                Log.wtf(TAG,"onGetUserProfileWebJobCompleted 2");
                if (profileResponse.getStatus() != null && profileResponse.getStatus().equals(Constant.SUCCESS)) {
                    Log.wtf(TAG,"onGetUserProfileWebJobCompleted 5");

                    if(profileResponse.getData().getTrack_current_location() == 1){
                        saveContentBasedOnType(Constant.CURRENT_LOCATION);
                    }else {
                        saveContentBasedOnType(Constant.HOME);
                    }

                    if(profileResponse.getData().getIs_first_login()==1){
                        Log.d(TAG,"First Time User");
                        stopProgressBar();
                        sendToEditProfileActivity(); //IF first time user send to EditProfile Activity//

                    }else{
                        Log.d(TAG,"Not First Time User");
                        eventModelObj = new EventFilterModel();
                        updateFilterModel=new UpdateFilterModel();
                        loadCurrentUser();
                        setUserSelectedMatrix();
                        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
                        getEventFilterInfo();
                        getUpdateFilterInfo();
                    }

                } else {
                    stopProgressBar();

                    Log.d(TAG,"onGetUserProfileWebJobCompleted 4");
                    goToGetStartedActivity();
                }
            } else {
                stopProgressBar();

                Log.d(TAG,"onGetUserProfileWebJobCompleted 3");
                goToGetStartedActivity();
            }
        }
    }

    private void saveContentBasedOnType(String contentBasedType) {

        Log.wtf(TAG,"saveContentBasedOnType: " + contentBasedType);

        if(contentBasedType.equals(Constant.CURRENT_LOCATION)) {
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, true);
            sharedEditor.putBoolean(Constant.HOME, false);
        }else if(contentBasedType.equals(Constant.HOME)){
            sharedEditor.putBoolean(Constant.HOME, true);
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, false);
        }else {
            sharedEditor.putBoolean(Constant.CURRENT_LOCATION, true);
            sharedEditor.putBoolean(Constant.HOME, false);
        }
        sharedEditor.apply();
    }

    private void loadCurrentUser() {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            currentAddressPlace = new AddressPlaceModel();
            initAddressPlaceModel(profileResponse.getData().getLatitude(), profileResponse.getData().getLongitude(), profileResponse.getData().getPlaceName(), profileResponse.getData().getAddress(), profileResponse.getData().getPlaceID());
        }
    }

    private void initAddressPlaceModel(double latitude, double longitude, String placeName, String address, String placeId) {
        currentAddressPlace = new AddressPlaceModel();
        currentAddressPlace.setLat("" + latitude);
        currentAddressPlace.setLng("" + longitude);
        currentAddressPlace.setPlaceName(placeName);
        currentAddressPlace.setAddniceaddress(address);
        currentAddressPlace.setPlaceID(placeId);
        eventModelObj.setNiceaddress(address);
        eventModelObj.setLng("" + longitude);
        eventModelObj.setLat("" + latitude);

        updateFilterModel.setNiceaddress(address);
        updateFilterModel.setLng(""+longitude);
        updateFilterModel.setLat(""+latitude);
    }

    private void setUserSelectedMatrix() {
        if (profileResponse != null) {
            if (profileResponse.getData().getMatrix().equals("0")) {
                isKM = true;
                eventModelObj.setMatrix("0");
                updateFilterModel.setMatrix("0");
            }
            if (profileResponse.getData().getMatrix().equals("1")) {
                isKM = false;
                eventModelObj.setMatrix("1");
                updateFilterModel.setMatrix("1");
            }
        }
    }

    private void getEventFilterInfo() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventFilterWebJob(accessToken));
        } else {
            stopProgressBar();
            showSnackbar(meeofImg, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void getUpdateFilterInfo() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetUpdateFilterWebJob(accessToken));
        } else {
            stopProgressBar();
            showSnackbar(meeofImg, getString(R.string.no_internet), Constant.ERROR);
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

        sendToDashboard(); //ELSE send to Dashboard Activity//
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetUpdateFilterInfoCompleted(UpdateFilterGetModel updateFilterGetModel) {
        Log.i(TAG, "EVENT Listned " + updateFilterGetModel.toString());

        if (updateFilterGetModel.getData() != null) {
            Log.wtf(TAG, "onGetEventFilterInfoCompleted");
            updateFilterModel.setLocation(updateFilterGetModel.getData().getLocation().toString());
            updateFilterModel.setMasterfilter(updateFilterGetModel.getData().getMasterfilter().toString());
            updateFilterModel.setAcceptabledistance(updateFilterGetModel.getData().getAcceptabledistance().toString());
            updateFilterModel.setMatrix(updateFilterGetModel.getData().getMatrix().toString());
            updateFilterModel.setHideoutsidefriends(updateFilterGetModel.getData().getHideoutsidefriends().toString());
            updateFilterModel.setSortbyproximity(updateFilterGetModel.getData().getSortbyproximity().toString());
            updateFilterModel.setRememberfilter(updateFilterGetModel.getData().getRememberfilter().toString());
            updateFilterModel.setFriends(updateFilterGetModel.getData().getFriends().toString());
            updateFilterModel.setHideolderupdates(updateFilterGetModel.getData().getHideolderupdates().toString());


            if(updateFilterGetModel.getData().getNiceaddress() != null){
                updateFilterModel.setNiceaddress(updateFilterGetModel.getData().getNiceaddress().toString());
            }else{
                updateFilterModel.setNiceaddress("");
            }

            saveObjectToSharedPref(sharedEditor, this.updateFilterModel, Constant.UPDATE_FILTER_OBJ);
        }

        sendToDashboard(); //ELSE send to Dashboard Activity//
    }


    private EventFilterModel retriveSavedFilterObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.EVENT_FILTER_OBJ, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            EventFilterModel profileResponse = gson.fromJson(profileObjectJsonStr, EventFilterModel.class);
            return profileResponse;
        }
        return null;
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

    private void sendToInviteFriendsToEventActivity() {
        Intent intent = new Intent(this, InviteFriendsToEventActivity.class);
        startActivity(intent);
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


}
