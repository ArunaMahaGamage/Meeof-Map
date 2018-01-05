package com.meeof.meeof.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.UpdateFilterGetModel;
import com.meeof.meeof.model.UpdateFilterModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.search_tag_dto.Data;
import com.meeof.meeof.model.search_tag_dto.SearchHashTagResponse;
import com.meeof.meeof.model.search_tag_dto.SendTagBack;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetUpdateFilterWebJob;
import com.meeof.meeof.webjob.PostUpdateFilterWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateFilterActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private ImageView closeAcIvBtn;
    private LinearLayout resetLLBtn;
    private LinearLayout applyfiltersLlBtn;
    private TextView addressLine1Tv;
    private TextView addressLine2Tv;
    private ImageView currentImageBtn;
    private ImageView homeImgBtn;
    private ImageView editImgBtn;

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

    private LinearLayout yesBtnShowUpdatesLlBtn;
    private LinearLayout noBtnShowUpdatesLlBtn;
    private LinearLayout yesBtnHideOlderUpdatesLlBtn;
    private LinearLayout noBtnHideOlderUpdatesLlBtn;

    private TextView yesBtnShowUpdatesLlBtnTV;
    private TextView noBtnShowUpdatesLlBtnTv;
    private TextView yesBtnHideOlderUpdatesLlBtnTv;
    private TextView noBtnHideOlderUpdatesLlBtnTv;

    private LinearLayout addEditTags;

    private TextView tagsTv;



    private static String TAG = UpdateFilterActivity.class.getSimpleName();
    private AddressPlaceModel currentAddressPlace;

    private static final int PLACE_REQUEST_CODE = 9393;
    private final int EVENTS_ALL = 0;
    private final int EVENTS_MY_FRIENDS = 1;
    private final int EVENTS_MY_INTERESTS = 2;
    private int selectedEventsFrom = 3;


    private boolean isKM = true;
    private UpdateFilterModel updateFilterModel = new UpdateFilterModel();
    private ProfileResponse profileResponse;
    private String accessToken;





    private boolean showOnlyUpdatesFromMyFriends=false;
    private boolean hideEventBeyondAcceptableDistance=false;
    private boolean showEventNearest = false;
    private boolean hideUpdatesOlderThanAWeek = false;
    private boolean rememberMyFilter = false;

    private List<Data> selectedTags=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_filter);
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
            Log.d(TAG,"retriveSavedFilterObject(sharedPreferences)!=null");
            updateFilterModel = retriveSavedFilterObject(sharedPreferences);
        }else if(Helper.getEventFilterDefaultModel(currentLat,currentLng)!=null){
            Log.d(TAG,"Helper.getEventFilterDefaultModel(currentLat,currentLng)!=null");
            updateFilterModel = Helper.getUpdateFilterDefaultModel(currentLat,currentLng);
        }else {
            Log.d(TAG,"retriveSavedFilterObject else");
            updateFilterModel.setNiceaddress(profileResponse.getData().getAddress());
            updateFilterModel.setLocation("1");
            updateFilterModel.setMasterfilter("0");
            updateFilterModel.setAcceptabledistance("15");
            updateFilterModel.setMatrix((isKM ? Constant.KM : Constant.MILES));
            updateFilterModel.setFriends("0");
            updateFilterModel.setSortbyproximity("0");
            updateFilterModel.setRememberfilter("0");
        }


        togleViewsToModelObject(updateFilterModel);
    }




    private void initViews() {
        closeAcIvBtn = (ImageView) findViewById(R.id.closeAcIvBtn);
        currentImageBtn = (ImageView) findViewById(R.id.currentImageBtn);
        homeImgBtn = (ImageView) findViewById(R.id.homeImgBtn);
        editImgBtn = (ImageView) findViewById(R.id.editImgBtn);
        applyfiltersLlBtn = (LinearLayout) findViewById(R.id.applyfiltersLlBtn);

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

        yesBtnShowUpdatesLlBtn=(LinearLayout) findViewById(R.id.yesBtnShowUpdatesLlBtn);
        noBtnShowUpdatesLlBtn=(LinearLayout) findViewById(R.id.noBtnShowUpdatesLlBtn);
        yesBtnHideOlderUpdatesLlBtn=(LinearLayout) findViewById(R.id.yesBtnHideOlderUpdatesLlBtn);
        noBtnHideOlderUpdatesLlBtn=(LinearLayout) findViewById(R.id.noBtnHideOlderUpdatesLlBtn);

        yesBtnShowUpdatesLlBtnTV = (TextView) findViewById(R.id.yesBtnShowUpdatesLlBtnTV);
        noBtnShowUpdatesLlBtnTv = (TextView) findViewById(R.id.noBtnShowUpdatesLlBtnTv);
        yesBtnHideOlderUpdatesLlBtnTv = (TextView) findViewById(R.id.yesBtnHideOlderUpdatesLlBtnTv);
        noBtnHideOlderUpdatesLlBtnTv = (TextView) findViewById(R.id.noBtnHideOlderUpdatesLlBtnTv);

        addEditTags=(LinearLayout)findViewById(R.id.addEditTags);
        tagsTv=(TextView)findViewById(R.id.tagsTv);

        currentImageBtn.setOnClickListener(this);
        homeImgBtn.setOnClickListener(this);
        editImgBtn.setOnClickListener(this);

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

        yesBtnShowUpdatesLlBtn.setOnClickListener(this);
        noBtnShowUpdatesLlBtn.setOnClickListener(this);
        yesBtnHideOlderUpdatesLlBtn.setOnClickListener(this);
        noBtnHideOlderUpdatesLlBtn.setOnClickListener(this);

        addEditTags.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.currentImageBtn):
                Log.i(TAG, "currentImageBtn");
                currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current_selected));
                homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home));
                editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit));
                updateFilterModel.setLocation("0");
                break;
            case (R.id.homeImgBtn):
                Log.i(TAG, "homeImgBtn");
                currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current));
                homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home_selected));
                editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit));
                displayHomeAddress();
                updateFilterModel.setLocation("1");
                break;
            case (R.id.editImgBtn):
                currentImageBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_current));
                homeImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_home));
                editImgBtn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventfilter_loc_edit_selected));
                sendToPlacesAutocomplteActivity();
                updateFilterModel.setLocation("2");
                Helper.clickGaurd(editImgBtn);
                break;
            case R.id.yesBtnShowUpdatesLlBtn:
                showOnlyUpdatesFromMyFriends = true;
                toggleShowOnlyUpdatesFromMyFriends();
                updateFilterModel.setFriends("1");  //check and update
                break;
            case R.id.noBtnShowUpdatesLlBtn:
                showOnlyUpdatesFromMyFriends = false;
                toggleShowOnlyUpdatesFromMyFriends();
                updateFilterModel.setFriends("0");  //check and update
                break;
            case (R.id.yesBtnHideFriendsEventLlBtn):
                hideEventBeyondAcceptableDistance = true;
                toggleHideFriendsBeyondAcceptableDistance();
                updateFilterModel.setHideoutsidefriends("1");
                break;
            case (R.id.noBtnHideFriendsEventLlBtn):
                hideEventBeyondAcceptableDistance = false;
                toggleHideFriendsBeyondAcceptableDistance();
                updateFilterModel.setHideoutsidefriends("0");
                break;
            case (R.id.yesBtnShowEventsLlBtn):
                showEventNearest = true;
                toggleForShowEventsNearestLocation();
                updateFilterModel.setSortbyproximity("1");
                break;
            case (R.id.noBtnShowEventsLlBtn):
                showEventNearest = false;
                toggleForShowEventsNearestLocation();
                updateFilterModel.setSortbyproximity("0");
                break;
            case R.id.yesBtnHideOlderUpdatesLlBtn:
                hideUpdatesOlderThanAWeek = true;
                toggleHideUpdatesOlderThanAWeek();
                updateFilterModel.setHideolderupdates("1");  //check and update
                break;
            case R.id.noBtnHideOlderUpdatesLlBtn:
                hideUpdatesOlderThanAWeek = false;
                toggleHideUpdatesOlderThanAWeek();
                updateFilterModel.setHideolderupdates("0");  //check and update
                break;
            case (R.id.yesBtnRememberMyFilterLlBtn):
                rememberMyFilter = true;
                toggleForRememberMyFilter();
                updateFilterModel.setRememberfilter("1");
                break;
            case (R.id.noBtnRememberMyFilterLlBtn):
                rememberMyFilter = false;
                toggleForRememberMyFilter();
                updateFilterModel.setRememberfilter("0");
                break;
            case (R.id.applyfiltersLlBtn):
                postEventFilterInfo();
                break;
            case (R.id.resetLLBtn):
                getUpdateFilterInfo();
                break;
            case R.id.closeAcIvBtn:
                this.finish();
                break;
            case R.id.addEditTags:
                sendToTagsActivity();
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

            Log.d(TAG,"loadCurrentUser getLatitude :"+profileResponse.getData().getLatitude());
            Log.d(TAG,"loadCurrentUser getLongitude :"+profileResponse.getData().getLongitude());
            Log.d(TAG,"loadCurrentUser getPlaceName:"+profileResponse.getData().getPlaceName());
            Log.d(TAG,"loadCurrentUser getAddress:"+profileResponse.getData().getAddress());

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

        Log.d(TAG,"initAddressPlaceModel getLatitude :"+latitude);
        Log.d(TAG,"initAddressPlaceModel getLongitude :"+longitude);
        Log.d(TAG,"initAddressPlaceModel getPlaceName:"+placeName);
        Log.d(TAG,"initAddressPlaceModel getAddress:"+address);

        updateFilterModel.setNiceaddress(address);
        updateFilterModel.setLng("" + longitude);
        updateFilterModel.setLat("" + latitude);
    }

    private void setUserSelectedMatrix() {
        if (profileResponse != null) {
            if (profileResponse.getData().getMatrix().equals("0")) {
                isKM = true;
                updateFilterModel.setMatrix("0");
            }
            if (profileResponse.getData().getMatrix().equals("1")) {
                isKM = false;
                updateFilterModel.setMatrix("1");
            }
        }
    }

    private void sendToPlacesAutocomplteActivity() {
        String countryName = sharedPreferences.getString(Constant.CURRENT_COUNTRY_NAME, "Singapore");
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("country", countryName);
        startActivityForResult(intent, PLACE_REQUEST_CODE);
    }

    private void sendToTagsActivity() {
        Intent intent = new Intent(this, SearchTagsActivity.class);

        Bundle bundle = new Bundle();
        Bundle args = new Bundle();
        args.putSerializable("BUNDLE_TAGS", (Serializable) selectedTags);
        intent.putExtra("FROM_FILTER", args);

        startActivity(intent);

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
        currentAddressPlace.setAddniceaddress(lng);
        currentAddressPlace.setPlaceID(placeId);


        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);

        LatLng userLatLong = new LatLng(latitude, longitude);

        Log.d(TAG,"onPlaceRequestResult getLatitude :"+lat);
        Log.d(TAG,"onPlaceRequestResult getLongitude :"+lng);
        Log.d(TAG,"onPlaceRequestResult getPlaceName:"+placeName);
        Log.d(TAG,"onPlaceRequestResult getAddress:"+lng);

        addressLine2Tv.setText("" + placeName);
        updateFilterModel.setNiceaddress("" + placeName);
        updateFilterModel.setLng(lng);
        updateFilterModel.setLat(lat);

    }


    private void toggleHideFriendsBeyondAcceptableDistance() {
        if (hideEventBeyondAcceptableDistance) {
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

    private void toggleShowOnlyUpdatesFromMyFriends() {
        if (showOnlyUpdatesFromMyFriends) {
            yesBtnShowUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            noBtnShowUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            yesBtnShowUpdatesLlBtnTV.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            noBtnShowUpdatesLlBtnTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        } else {
            yesBtnShowUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            noBtnShowUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            yesBtnShowUpdatesLlBtnTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            noBtnShowUpdatesLlBtnTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }

    }

    private void toggleHideUpdatesOlderThanAWeek() {
        if (hideUpdatesOlderThanAWeek) {
            yesBtnHideOlderUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            noBtnHideOlderUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            yesBtnHideOlderUpdatesLlBtnTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            noBtnHideOlderUpdatesLlBtnTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        } else {
            yesBtnHideOlderUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_unselect));
            noBtnHideOlderUpdatesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.button_select));
            yesBtnHideOlderUpdatesLlBtnTv.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
            noBtnHideOlderUpdatesLlBtnTv.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        Log.d("DEBUG", "Progress is: " + progress);

        if (!isKM) {
            Log.d(TAG, "Progress Miles : " + progress);
            Log.d(TAG, "Progress Miles : " + getMilesKmValueForSeek(Constant.MILES, progress));
            updateFilterModel.setAcceptabledistance("" + getMilesKmValueForSeek(Constant.MILES, progress));
        } else {
            Log.d(TAG, "Progress Km : " + progress);
            getMilesKmValueForSeek(Constant.KM, progress);
            Log.d(TAG, "Progress Km : " + getMilesKmValueForSeek(Constant.KM, progress));
            updateFilterModel.setAcceptabledistance("" + getMilesKmValueForSeek(Constant.KM, progress));
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
            jobManager.addJobInBackground(new PostUpdateFilterWebJob(accessToken, updateFilterModel));
        }else{

            saveObjectToSharedPref(sharedEditor, this.updateFilterModel, Constant.UPDATE_FILTER_OBJ);
            this.finish();
        }
    }

    @Subscribe
    public void onPostUpdateilterInfo(HttpResponse httpResponse) {
        if (httpResponse != null) {
            if (httpResponse.getStatus() != null && httpResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(distanceMatrixTv, getString(R.string.filter_applied_successfully), Constant.SUCCESS);
                if (this.updateFilterModel != null && this.updateFilterModel.getAcceptabledistance() != null) {
                    saveObjectToSharedPref(sharedEditor, this.updateFilterModel, Constant.UPDATE_FILTER_OBJ);
                }
                this.finish();
            } else {
                showSnackbar(distanceMatrixTv, getString(R.string.unable_to_apply_filter), Constant.ERROR);
            }
        } else {
            showSnackbar(distanceMatrixTv, getString(R.string.unable_to_apply_filter), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onPostTags(SendTagBack searchHashTagResponse) {
        Log.d(TAG,"onPostTags 1");
        if (searchHashTagResponse != null) {
            if (searchHashTagResponse.getStatus() != null && searchHashTagResponse.getStatus().equals(Constant.SUCCESS)) {

                Log.d(TAG,"onPostTags 2"+searchHashTagResponse.getData());
                selectedTags=searchHashTagResponse.getData();
                String tagsString="";
                for(Data data:selectedTags){
                    tagsString+="#"+data.getHashtag()+" ";
                }
                tagsTv.setText(tagsString);

            } else {
                //showSnackbar(distanceMatrixTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            //showSnackbar(distanceMatrixTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
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

    private void getUpdateFilterInfo() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetUpdateFilterWebJob(accessToken));
        } else {
            showSnackbar(distanceMatrixTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetUpdatetFilterInfoCompleted(UpdateFilterGetModel updateFilterGetModel) {
        Log.i(TAG, "EVENT Listned " + updateFilterGetModel.toString());
        if (updateFilterGetModel.getData() != null) {
            updateFilterModel.setLocation(updateFilterGetModel.getData().getLocation().toString());
            updateFilterModel.setMasterfilter(updateFilterGetModel.getData().getMasterfilter().toString());
            updateFilterModel.setAcceptabledistance(updateFilterGetModel.getData().getAcceptabledistance().toString());
            updateFilterModel.setMatrix(updateFilterGetModel.getData().getMatrix().toString());
            updateFilterModel.setHideoutsidefriends(updateFilterGetModel.getData().getHideoutsidefriends().toString());
            updateFilterModel.setSortbyproximity(updateFilterGetModel.getData().getSortbyproximity().toString());
            updateFilterModel.setRememberfilter(updateFilterGetModel.getData().getRememberfilter().toString());
            updateFilterModel.setNiceaddress(updateFilterGetModel.getData().getNiceaddress().toString());
            updateFilterModel.setFriends(updateFilterGetModel.getData().getFriends().toString());
            updateFilterModel.setHideolderupdates(updateFilterGetModel.getData().getHideolderupdates().toString());
            togleViewsToModelObject(updateFilterModel);

        }else if(retriveSavedFilterObject(sharedPreferences)!=null){
            togleViewsToModelObject(retriveSavedFilterObject(sharedPreferences));
        }
        else {
            updateFilterModel.setNiceaddress(profileResponse.getData().getAddress());
            updateFilterModel.setLocation("1");
            updateFilterModel.setMasterfilter("0");
            updateFilterModel.setAcceptabledistance("5");
            updateFilterModel.setMatrix("0");
            updateFilterModel.setHideoutsidefriends("0");
            updateFilterModel.setSortbyproximity("0");
            updateFilterModel.setRememberfilter("0");
            updateFilterModel.setFriends("0");
            updateFilterModel.setHideolderupdates("0");
            togleViewsToModelObject(updateFilterModel);
        }
    }

    private UpdateFilterModel retriveSavedFilterObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.UPDATE_FILTER_OBJ, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            UpdateFilterModel profileResponse = gson.fromJson(profileObjectJsonStr, UpdateFilterModel.class);
            return profileResponse;
        }
        return null;
    }


    private void togleViewsToModelObject(UpdateFilterModel updateFilterModel) {
        String location = updateFilterModel.getLocation().toString();
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
        String masterFilter = updateFilterModel.getMasterfilter().toString();
//        if (masterFilter.equals("0")) {
//            selectedEventsFrom = EVENTS_ALL;
//            toggleforShowEvent();
//        } else if (masterFilter.equals("1")) {
//            selectedEventsFrom = EVENTS_MY_FRIENDS;
//            toggleforShowEvent();
//        } else if (masterFilter.equals("2")) {
//            selectedEventsFrom = EVENTS_MY_INTERESTS;
//            toggleforShowEvent();
//        }

        String hideoutsidefriends = updateFilterModel.getHideoutsidefriends().toString();






        if(updateFilterModel.getNiceaddress() != null && updateFilterModel.getNiceaddress().length() > 0){
            addressLine2Tv.setText(updateFilterModel.getNiceaddress().toString());
        }

        distanceSeekBar.setProgress(setMilesKmValueForSeek((isKM ? Constant.KM : Constant.MILES), Double.parseDouble(updateFilterModel.getAcceptabledistance().toString())));



        String sortbyproximity = updateFilterModel.getSortbyproximity().toString();



        //1
        if(updateFilterModel.getFriends().equals("1")){
            showOnlyUpdatesFromMyFriends = true;
            toggleShowOnlyUpdatesFromMyFriends();
        }else{
            showOnlyUpdatesFromMyFriends = false;
            toggleShowOnlyUpdatesFromMyFriends();
        }

        //2
        if(updateFilterModel.getHideoutsidefriends().equals("1")){
            hideEventBeyondAcceptableDistance = true;
            toggleHideFriendsBeyondAcceptableDistance();
        }else{
            hideEventBeyondAcceptableDistance = false;
            toggleHideFriendsBeyondAcceptableDistance();
        }

        //3
        if (sortbyproximity.equals("1")) {
            showEventNearest = true;
            toggleForShowEventsNearestLocation();
        } else if (sortbyproximity.equals("0")) {
            showEventNearest = false;
            toggleForShowEventsNearestLocation();
        }

        //4
        if(updateFilterModel.getHideolderupdates().equals("1")){
            hideUpdatesOlderThanAWeek = true;
            toggleHideUpdatesOlderThanAWeek();
        }else{
            hideUpdatesOlderThanAWeek = false;
            toggleHideUpdatesOlderThanAWeek();
        }

        //5
        String rememberfilter = updateFilterModel.getRememberfilter().toString();
        if (rememberfilter.equals("1")) {
            rememberMyFilter = true;
            toggleForRememberMyFilter();
        } else if (rememberfilter.equals("0")) {
            rememberMyFilter = false;
            toggleForRememberMyFilter();
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
