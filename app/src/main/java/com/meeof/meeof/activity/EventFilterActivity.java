package com.meeof.meeof.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.EventFilterResponse;
import com.meeof.meeof.model.event_filter_get_dto.EventFilterGetModel;
import com.meeof.meeof.model.events_filter_dto.EventFilterModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetEventFilterWebJob;
import com.meeof.meeof.webjob.PostEventFilterWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Anuja Ranwalage on 10/18/17.
 */

public class EventFilterActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView closeAcIvBtn;
    private LinearLayout resetLLBtn;
    private LinearLayout applyfiltersLlBtn;
    private TextView addressLine1Tv;
    private TextView addressLine2Tv;
    private ImageView currentImageBtn;
    private ImageView homeImgBtn;
    private ImageView editImgBtn;
    private LinearLayout allLlBtn;
    private LinearLayout myFriendsLlBtn;
    private LinearLayout myInterestsLlBtn;
    private SeekBar distanceSeekBar;
    private TextView distanceMatrixTv;
    private LinearLayout yesBtnHideFriendsEventLlBtn;
    private LinearLayout noBtnHideFriendsEventLlBtn;
    private LinearLayout yesBtnShowEventsLlBtn;
    private LinearLayout noBtnShowEventsLlBtn;
    private LinearLayout yesBtnRememberMyFilterLlBtn;
    private LinearLayout noBtnRememberMyFilterLlBtn;
    private TextView allEventsTv;
    private TextView myFriendsTv;
    private TextView myInterestTv;
    private TextView hideFriendsYesTv;
    private TextView hideFriendsNoTv;
    private TextView showEventsLocationYesTv;
    private TextView showEventsLocationNoTv;
    private TextView rememberMyFilterYesTv;
    private TextView rememberMyFilterNoTv;


    private static String TAG = EventFilterActivity.class.getSimpleName();
    private AddressPlaceModel currentAddressPlace;

    private static final int PLACE_REQUEST_CODE = 9393;
    private final int EVENTS_ALL = 0;
    private final int EVENTS_MY_FRIENDS = 1;
    private final int EVENTS_MY_INTERESTS = 2;
    private int selectedEventsFrom = 3;

    private boolean hideFriendsEvents = false;
    private boolean showEventNearest = false;
    private boolean rememberMyFilter = false;
    private boolean isKM = true;
    private EventFilterModel eventModelObj = new EventFilterModel();
    private ProfileResponse profileResponse;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_filter);
        initViews();
        loadCurrentUser();
        setUserSelectedMatrix();
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        //getEventFilterInfo();

//        toggleforShowEvent();
//        toggleForHideFriend();
//        toggleForShowEventsNearestLocation();
//        toggleForRememberMyFilter();

        double currentLat = sharedPreferences.getFloat(Constant.lastKnownLatitudeService,0);
        double currentLng =sharedPreferences.getFloat(Constant.lastKnownLongitudeService, 0);

        if(retriveSavedFilterObject(sharedPreferences)!=null){
            eventModelObj = retriveSavedFilterObject(sharedPreferences);
        }else if(Helper.getEventFilterDefaultModel(currentLat,currentLng)!=null){
            eventModelObj = Helper.getEventFilterDefaultModel(currentLat,currentLng);
        }else {
            eventModelObj.setNiceaddress(profileResponse.getData().getAddress());
            eventModelObj.setLocation("1");
            eventModelObj.setMasterfilter("0");
            eventModelObj.setAcceptabledistance("15");
            eventModelObj.setMatrix((isKM ? Constant.KM : Constant.MILES));
            eventModelObj.setHideoutsidefriends("0");
            eventModelObj.setSortbyproximity("0");
            eventModelObj.setRememberfilter("0");
        }


        togleViewsToModelObject(eventModelObj);
    }




    private void initViews() {
        closeAcIvBtn = (ImageView) findViewById(R.id.closeAcIvBtn);
        currentImageBtn = (ImageView) findViewById(R.id.currentImageBtn);
        homeImgBtn = (ImageView) findViewById(R.id.homeImgBtn);
        editImgBtn = (ImageView) findViewById(R.id.editImgBtn);
        applyfiltersLlBtn = (LinearLayout) findViewById(R.id.applyfiltersLlBtn);
        allLlBtn = (LinearLayout) findViewById(R.id.allLlBtn);
        myFriendsLlBtn = (LinearLayout) findViewById(R.id.myFriendsLlBtn);
        myInterestsLlBtn = (LinearLayout) findViewById(R.id.myInterestsLlBtn);
        yesBtnHideFriendsEventLlBtn = (LinearLayout) findViewById(R.id.yesBtnHideFriendsEventLlBtn);
        noBtnHideFriendsEventLlBtn = (LinearLayout) findViewById(R.id.noBtnHideFriendsEventLlBtn);
        yesBtnShowEventsLlBtn = (LinearLayout) findViewById(R.id.yesBtnShowEventsLlBtn);
        noBtnShowEventsLlBtn = (LinearLayout) findViewById(R.id.noBtnShowEventsLlBtn);
        yesBtnRememberMyFilterLlBtn = (LinearLayout) findViewById(R.id.yesBtnRememberMyFilterLlBtn);
        noBtnRememberMyFilterLlBtn = (LinearLayout) findViewById(R.id.noBtnRememberMyFilterLlBtn);
        resetLLBtn = (LinearLayout) findViewById(R.id.resetLLBtn);
        addressLine1Tv = (TextView) findViewById(R.id.addressLine1Tv);
        addressLine2Tv = (TextView) findViewById(R.id.addressLine2Tv);
        distanceMatrixTv = (TextView) findViewById(R.id.distanceMatrixTv);
        distanceSeekBar = (SeekBar) findViewById(R.id.distanceSeekBar);
        allEventsTv = (TextView) findViewById(R.id.allEventsTv);
        myFriendsTv = (TextView) findViewById(R.id.myFriendsTv);
        myInterestTv = (TextView) findViewById(R.id.myInterestTv);
        hideFriendsYesTv = (TextView) findViewById(R.id.hideFriendsYesTv);
        hideFriendsNoTv = (TextView) findViewById(R.id.hideFriendsNoTv);
        showEventsLocationYesTv = (TextView) findViewById(R.id.showEventsLocationYesTv);
        showEventsLocationNoTv = (TextView) findViewById(R.id.showEventsLocationNoTv);
        rememberMyFilterYesTv = (TextView) findViewById(R.id.rememberMyFilterYesTv);
        rememberMyFilterNoTv = (TextView) findViewById(R.id.rememberMyFilterNoTv);

        currentImageBtn.setOnClickListener(this);
        homeImgBtn.setOnClickListener(this);
        editImgBtn.setOnClickListener(this);
        allLlBtn.setOnClickListener(this);
        myFriendsLlBtn.setOnClickListener(this);
        myInterestsLlBtn.setOnClickListener(this);
        yesBtnHideFriendsEventLlBtn.setOnClickListener(this);
        noBtnHideFriendsEventLlBtn.setOnClickListener(this);
        yesBtnShowEventsLlBtn.setOnClickListener(this);
        noBtnShowEventsLlBtn.setOnClickListener(this);
        yesBtnRememberMyFilterLlBtn.setOnClickListener(this);
        noBtnRememberMyFilterLlBtn.setOnClickListener(this);
        applyfiltersLlBtn.setOnClickListener(this);
        distanceSeekBar.setOnSeekBarChangeListener(this);
        resetLLBtn.setOnClickListener(this);
        closeAcIvBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.currentImageBtn):
                Log.i(TAG, "currentImageBtn");
                currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current_selected));
                homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home));
                editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit));
                eventModelObj.setLocation("0");
                break;
            case (R.id.homeImgBtn):
                Log.i(TAG, "homeImgBtn");
                currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current));
                homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home_selected));
                editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit));
                displayHomeAddress();
                eventModelObj.setLocation("1");
                break;
            case (R.id.editImgBtn):
                currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current));
                homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home));
                editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit_selected));
                sendToPlacesAutocomplteActivity();
                eventModelObj.setLocation("2");
                Helper.clickGaurd(editImgBtn);
                break;
            case (R.id.allLlBtn):
                selectedEventsFrom = EVENTS_ALL;
                toggleforShowEvent();
                eventModelObj.setMasterfilter("0");
                break;
            case (R.id.myFriendsLlBtn):
                selectedEventsFrom = EVENTS_MY_FRIENDS;
                toggleforShowEvent();
                eventModelObj.setMasterfilter("1");
                break;
            case (R.id.myInterestsLlBtn):
                selectedEventsFrom = EVENTS_MY_INTERESTS;
                toggleforShowEvent();
                eventModelObj.setMasterfilter("2");
                break;
            case (R.id.yesBtnHideFriendsEventLlBtn):
                hideFriendsEvents = true;
                toggleForHideFriend();
                eventModelObj.setHideoutsidefriends("1");
                break;
            case (R.id.noBtnHideFriendsEventLlBtn):
                hideFriendsEvents = false;
                toggleForHideFriend();
                eventModelObj.setHideoutsidefriends("0");
                break;
            case (R.id.yesBtnShowEventsLlBtn):
                showEventNearest = true;
                toggleForShowEventsNearestLocation();
                eventModelObj.setSortbyproximity("1");
                break;
            case (R.id.noBtnShowEventsLlBtn):
                showEventNearest = false;
                toggleForShowEventsNearestLocation();
                eventModelObj.setSortbyproximity("0");
                break;
            case (R.id.yesBtnRememberMyFilterLlBtn):
                rememberMyFilter = true;
                toggleForRememberMyFilter();
                eventModelObj.setRememberfilter("1");
                break;
            case (R.id.noBtnRememberMyFilterLlBtn):
                rememberMyFilter = false;
                toggleForRememberMyFilter();
                eventModelObj.setRememberfilter("0");
                break;
            case (R.id.applyfiltersLlBtn):
                postEventFilterInfo();
                break;
            case (R.id.resetLLBtn):
                getEventFilterInfo();
                break;
            case R.id.closeAcIvBtn:
                this.finish();
                break;
        }

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

    private void displayHomeAddress() {

        if (profileResponse != null) {
            addressLine2Tv.setText(profileResponse.getData().getAddress());
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

    private void sendToPlacesAutocomplteActivity() {
        String countryName = sharedPreferences.getString(Constant.CURRENT_COUNTRY_NAME, "Singapore");
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("country", countryName);
        startActivityForResult(intent, PLACE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == PLACE_REQUEST_CODE) {
                onPlaceRequestResult(data);


            }
        }
    }

    private void onPlaceRequestResult(Intent data) {
        Log.d(TAG, "Inside PLACE_REQUEST_CODE");

        String lat = data.getStringExtra("lat");
        String lng = data.getStringExtra("lng");
        String placeName = data.getStringExtra("place_name");
        String placeAddress = data.getStringExtra("place_address");
        String placeId = data.getStringExtra("place_id");

        currentAddressPlace = new AddressPlaceModel();
        currentAddressPlace.setLat(lat);
        currentAddressPlace.setLng(lng);
        currentAddressPlace.setPlaceName(placeName);
        currentAddressPlace.setAddniceaddress(placeAddress);
        currentAddressPlace.setPlaceID(placeId);


        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);

        LatLng userLatLong = new LatLng(latitude, longitude);

        addressLine2Tv.setText("" + placeName);
        eventModelObj.setNiceaddress("" + placeName);
        eventModelObj.setLng(lng);
        eventModelObj.setLat(lat);

    }

    private void toggleforShowEvent() {
        if (selectedEventsFrom == EVENTS_ALL) {
            allLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            myFriendsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            myInterestsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            allEventsTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            myFriendsTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            myInterestTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        }
        if (selectedEventsFrom == EVENTS_MY_FRIENDS) {
            allLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            myFriendsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            myInterestsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            allEventsTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            myFriendsTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            myInterestTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        }
        if (selectedEventsFrom == EVENTS_MY_INTERESTS) {
            allLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            myFriendsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            myInterestsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            allEventsTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            myFriendsTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            myInterestTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));

        }

        if (selectedEventsFrom == 3) {
            allLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            myFriendsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            myInterestsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            allEventsTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            myFriendsTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            myInterestTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));

        }

    }

    private void toggleForHideFriend() {
        if (hideFriendsEvents) {
            yesBtnHideFriendsEventLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            noBtnHideFriendsEventLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            hideFriendsYesTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            hideFriendsNoTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        } else {
            yesBtnHideFriendsEventLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            noBtnHideFriendsEventLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            hideFriendsYesTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            hideFriendsNoTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }

    }

    private void toggleForShowEventsNearestLocation() {
        if (showEventNearest) {
            yesBtnShowEventsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            noBtnShowEventsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            showEventsLocationYesTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            showEventsLocationNoTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        } else {
            yesBtnShowEventsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            noBtnShowEventsLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            showEventsLocationYesTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            showEventsLocationNoTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }

    }

    private void toggleForRememberMyFilter() {
        if (rememberMyFilter) {
            yesBtnRememberMyFilterLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            noBtnRememberMyFilterLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            rememberMyFilterYesTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            rememberMyFilterNoTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        } else {
            yesBtnRememberMyFilterLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            noBtnRememberMyFilterLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            rememberMyFilterYesTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            rememberMyFilterNoTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Log.d("DEBUG", "Progress is: " + progress);

        if (!isKM) {
            Log.d(TAG, "Progress Miles : " + progress);
            Log.d(TAG, "Progress Miles : " + getMilesKmValueForSeek(Constant.MILES, progress));
            eventModelObj.setAcceptabledistance("" + getMilesKmValueForSeek(Constant.MILES, progress));
        } else {
            Log.d(TAG, "Progress Km : " + progress);
            getMilesKmValueForSeek(Constant.KM, progress);
            Log.d(TAG, "Progress Km : " + getMilesKmValueForSeek(Constant.KM, progress));
            eventModelObj.setAcceptabledistance("" + getMilesKmValueForSeek(Constant.KM, progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private double getMilesKmValueForSeek(String matrix, int progress) {
        String distaceTvValue = "";
        if (matrix.equals(Constant.KM)) {
            int progressKm = progress;
            Log.d(TAG, "progressKm: " + progressKm);
            if (progress == 1) {
                distaceTvValue = "100m";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.1;
            } else if (progressKm == 2) {
                distaceTvValue = "250m";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.25;
            } else if (progressKm == 3) {
                distaceTvValue = "500m";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.5;
            } else if (progressKm == 4) {
                distaceTvValue = "750m";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.75;
            } else if (progressKm == 5) {
                distaceTvValue = "1KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 1.0;
            } else if (progressKm == 6) {
                distaceTvValue = "2KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 2.0;
            } else if (progressKm == 7) {
                distaceTvValue = "5KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 5;
            } else if (progressKm == 8) {
                distaceTvValue = "10KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 10;
            } else if (progressKm == 9) {
                distaceTvValue = "15KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 15;
            } else if (progressKm == 10) {
                distaceTvValue = "20KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 20;
            } else if (progressKm == 11) {
                distaceTvValue = "25KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 25;
            } else if (progressKm == 12) {
                distaceTvValue = "30KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 30;
            } else if (progressKm == 13) {
                distaceTvValue = "40KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 40;
            } else if (progressKm == 14) {
                distaceTvValue = "50KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 50;
            } else if (progressKm == 15) {
                distaceTvValue = "100KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 100;
            } else {
                distaceTvValue = "100m";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.1;
            }
        } else {
            int progressMiles = progress;
            Log.d(TAG, "progressMiles: " + progressMiles);
            if (progress == 1) {
                distaceTvValue = "330 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.033;
            } else if (progressMiles == 2) {
                distaceTvValue = "820 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.082;
            } else if (progressMiles == 3) {
                distaceTvValue = "1640 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.1640;
            } else if (progressMiles == 4) {
                distaceTvValue = "2460 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.2460;
            } else if (progressMiles == 5) {
                distaceTvValue = "0.5 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.5;
            } else if (progressMiles == 6) {
                distaceTvValue = "1 Mile";
                distanceMatrixTv.setText(distaceTvValue);
                return 1;
            } else if (progressMiles == 7) {
                distaceTvValue = "3 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 3;
            } else if (progressMiles == 8) {
                distaceTvValue = "6 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 6;
            } else if (progressMiles == 9) {
                distaceTvValue = "10 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 10;
            } else if (progressMiles == 10) {
                distaceTvValue = "13 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 13;
            } else if (progressMiles == 11) {
                distaceTvValue = "16 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 16;
            } else if (progressMiles == 12) {
                distaceTvValue = "20 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 20;
            } else if (progressMiles == 13) {
                distaceTvValue = "26 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 26;
            } else if (progressMiles == 14) {
                distaceTvValue = "30 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 30;
            } else if (progressMiles == 15) {
                distaceTvValue = "60 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 60;
            } else {
                distaceTvValue = "330 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 0.033;
            }

        }
    }

    private int setMilesKmValueForSeek(String matrix, Double distance) {
        String distaceTvValue = "";
        if (matrix.equals(Constant.KM)) {

            Log.d(TAG, "distance: " + distance);
            if (distance == 0.1) {
                distaceTvValue = "100m";
                distanceMatrixTv.setText(distaceTvValue);
                return 1;
            } else if (distance == 0.25) {
                distaceTvValue = "250m";
                distanceMatrixTv.setText(distaceTvValue);
                return 2;
            } else if (distance == 0.5) {
                distaceTvValue = "500m";
                distanceMatrixTv.setText(distaceTvValue);
                return 3;
            } else if (distance == 0.75) {
                distaceTvValue = "750m";
                distanceMatrixTv.setText(distaceTvValue);
                return 4;
            } else if (distance == 1.0) {
                distaceTvValue = "1KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 5;
            } else if (distance == 2.0) {
                distaceTvValue = "2KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 6;
            } else if (distance == 5) {
                distaceTvValue = "5KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 7;
            } else if (distance == 10) {
                distaceTvValue = "10KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 8;
            } else if (distance == 15) {
                distaceTvValue = "15KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 9;
            } else if (distance == 20) {
                distaceTvValue = "20KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 10;
            } else if (distance == 25) {
                distaceTvValue = "25KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 11;
            } else if (distance == 30) {
                distaceTvValue = "30KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 12;
            } else if (distance == 40) {
                distaceTvValue = "40KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 13;
            } else if (distance == 50) {
                distaceTvValue = "50KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 14;
            } else if (distance == 100) {
                distaceTvValue = "100KM";
                distanceMatrixTv.setText(distaceTvValue);
                return 15;
            } else {
                distaceTvValue = "100m";
                distanceMatrixTv.setText(distaceTvValue);
                return 1;
            }
        } else {

            Log.d(TAG, "distance: " + distance);
            if (distance == 0.033) {
                distaceTvValue = "330 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 1;
            } else if (distance == 0.082) {
                distaceTvValue = "820 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 2;
            } else if (distance == 0.1640) {
                distaceTvValue = "1640 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 3;
            } else if (distance == 0.2460) {
                distaceTvValue = "2460 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 4;
            } else if (distance == 0.5) {
                distaceTvValue = "0.5 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 5;
            } else if (distance == 1) {
                distaceTvValue = "1 Mile";
                distanceMatrixTv.setText(distaceTvValue);
                return 6;
            } else if (distance == 3) {
                distaceTvValue = "3 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 7;
            } else if (distance == 6) {
                distaceTvValue = "6 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 8;
            } else if (distance == 10) {
                distaceTvValue = "10 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 9;
            } else if (distance == 13) {
                distaceTvValue = "13 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 10;
            } else if (distance == 16) {
                distaceTvValue = "16 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 11;
            } else if (distance == 20) {
                distaceTvValue = "20 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 12;
            } else if (distance == 26) {
                distaceTvValue = "26 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 13;
            } else if (distance == 30) {
                distaceTvValue = "30 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 14;
            } else if (distance == 60) {
                distaceTvValue = "60 Miles";
                distanceMatrixTv.setText(distaceTvValue);
                return 15;
            } else {
                distaceTvValue = "330 ft";
                distanceMatrixTv.setText(distaceTvValue);
                return 1;
            }
        }
    }

    private void postEventFilterInfo() {
        Log.i(TAG, "ACCESS TOKEN " + accessToken);

        if(rememberMyFilter){
            jobManager.addJobInBackground(new PostEventFilterWebJob(accessToken, eventModelObj));
        }else{
            saveObjectToSharedPref(sharedEditor, this.eventModelObj, Constant.EVENT_FILTER_OBJ);
            this.finish();
        }
    }

    @Subscribe
    public void onPostEventFilterInfo(EventFilterResponse eventFilterResponse) {
        if (eventFilterResponse != null) {
            if (eventFilterResponse.getStatus() != null && eventFilterResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(distanceMatrixTv, getString(R.string.filter_applied_successfully), Constant.SUCCESS);
                if (this.eventModelObj != null && this.eventModelObj.getAcceptabledistance() != null) {
                    saveObjectToSharedPref(sharedEditor, this.eventModelObj, Constant.EVENT_FILTER_OBJ);
                }
                this.finish();
            } else {
                showSnackbar(distanceMatrixTv, getString(R.string.unable_to_apply_filter), Constant.ERROR);
            }
        } else {
            showSnackbar(distanceMatrixTv, getString(R.string.unable_to_apply_filter), Constant.ERROR);
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

    private void getEventFilterInfo() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventFilterWebJob(accessToken));
        } else {
            showSnackbar(distanceMatrixTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetEventFilterInfoCompleted(EventFilterGetModel eventFilterGetModel) {
        Log.i(TAG, "EVENT Listned " + eventFilterGetModel.toString());
        if (eventFilterGetModel.getData() != null) {
            eventModelObj.setLocation(eventFilterGetModel.getData().getLocation().toString());
            eventModelObj.setMasterfilter(eventFilterGetModel.getData().getMasterfilter().toString());
            eventModelObj.setAcceptabledistance(eventFilterGetModel.getData().getAcceptabledistance().toString());
            eventModelObj.setMatrix(eventFilterGetModel.getData().getMatrix().toString());
            eventModelObj.setHideoutsidefriends(eventFilterGetModel.getData().getHideoutsidefriends().toString());
            eventModelObj.setSortbyproximity(eventFilterGetModel.getData().getSortbyproximity().toString());
            eventModelObj.setRememberfilter(eventFilterGetModel.getData().getRememberfilter().toString());
            eventModelObj.setNiceaddress(eventFilterGetModel.getData().getNiceaddress().toString());
            togleViewsToModelObject(eventModelObj);

        }else if(retriveSavedFilterObject(sharedPreferences)!=null){
            togleViewsToModelObject(retriveSavedFilterObject(sharedPreferences));
        }
        else {
            eventModelObj.setNiceaddress(profileResponse.getData().getAddress());
            eventModelObj.setLocation("1");
            eventModelObj.setMasterfilter("0");
            eventModelObj.setAcceptabledistance("5");
            eventModelObj.setMatrix("0");
            eventModelObj.setHideoutsidefriends("0");
            eventModelObj.setSortbyproximity("0");
            eventModelObj.setRememberfilter("0");
            togleViewsToModelObject(eventModelObj);
        }
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


    private void togleViewsToModelObject(EventFilterModel eventFilterModel) {
        String location = eventFilterModel.getLocation().toString();
        if (location.equals("1")) {
            currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current));
            homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home_selected));
            editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit));
        } else if (location.equals("0")) {
            currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current_selected));
            homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home));
            editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit));
        } else if (location.equals("2")) {
            currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current));
            homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home));
            editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit_selected));

        }
        String masterFilter = eventFilterModel.getMasterfilter().toString();
        if (masterFilter.equals("0")) {
            selectedEventsFrom = EVENTS_ALL;
            toggleforShowEvent();
        } else if (masterFilter.equals("1")) {
            selectedEventsFrom = EVENTS_MY_FRIENDS;
            toggleforShowEvent();
        } else if (masterFilter.equals("2")) {
            selectedEventsFrom = EVENTS_MY_INTERESTS;
            toggleforShowEvent();
        }

        String hideoutsidefriends = eventFilterModel.getHideoutsidefriends().toString();

        if (hideoutsidefriends.equals("1")) {
            hideFriendsEvents = true;
            toggleForHideFriend();
        } else if (hideoutsidefriends.equals("0")) {
            hideFriendsEvents = false;
            toggleForHideFriend();
        }

        String sortbyproximity = eventFilterModel.getSortbyproximity().toString();

        if (sortbyproximity.equals("1")) {
            showEventNearest = true;
            toggleForShowEventsNearestLocation();
        } else if (sortbyproximity.equals("0")) {
            showEventNearest = false;
            toggleForShowEventsNearestLocation();
        }

        String rememberfilter = eventFilterModel.getRememberfilter().toString();
        if (rememberfilter.equals("1")) {
            rememberMyFilter = true;
            toggleForRememberMyFilter();
        } else if (rememberfilter.equals("0")) {
            rememberMyFilter = false;
            toggleForRememberMyFilter();
        }

        if(eventFilterModel.getNiceaddress() != null && eventFilterModel.getNiceaddress().length() > 0){
            addressLine2Tv.setText(eventFilterModel.getNiceaddress().toString());
        }

        distanceSeekBar.setProgress(setMilesKmValueForSeek((isKM ? Constant.KM : Constant.MILES), Double.parseDouble(eventFilterModel.getAcceptabledistance().toString())));
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


