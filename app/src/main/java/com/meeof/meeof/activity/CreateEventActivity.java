package com.meeof.meeof.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.dialog.DatePickerDialogFragment;
import com.meeof.meeof.dialog.TimePickerDialogFragment;
import com.meeof.meeof.fragment.CustomMapFragment;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.CreateEventGooglePlaceIdResponse;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.GetEventsWebJobCompletedEvent;
import com.meeof.meeof.model.PostEventModel;
import com.meeof.meeof.model.UploadEventPosterResponse;
import com.meeof.meeof.model.date_time_dto.DateModel;
import com.meeof.meeof.model.date_time_dto.TimeModel;
import com.meeof.meeof.model.edit_event_dto.Data;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.events.CreateEventResponse;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.interests.InterestsBaseItem;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.GetImageFromGallery;
import com.meeof.meeof.util.Utility;
import com.meeof.meeof.webjob.CreateEventGetPlaceIdWebJob;
import com.meeof.meeof.webjob.EventsSyncJob;
import com.meeof.meeof.webjob.GetEventInfoWebJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.GetInterestsListWebJob;
import com.meeof.meeof.webjob.PostEventDetailsWebJob;
import com.meeof.meeof.webjob.UploadEventPosterImageWebJob;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Anuja Ranwalage on 10/18/17.
 */

public class CreateEventActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, TextWatcher{
    private static final String TAG = CreateEventActivity.class.getSimpleName();
    private static final int SELECT_ACTIVITY_REQUEST_CODE = 3843;
    private final int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int PLACE_REQUEST_CODE = 9393;

    public static LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interestsCategories;

    private ImageView eventPosterIv;
    private ImageView cameraIv;
    private ImageView cropIv;
    private ImageView deleteIv;
    private ImageView publicIv;
    private ImageView passwordIv;
    private ImageView friendsIv;
    private TextView eventActivtyTv;
    private DatePicker meeofDp;
    private DatePicker multipleDp;
    private TimePicker startTp;
    private TimePicker endTp;
    private TextView eventLocationTv;
    private EditText discribeEt;
    private EditText addressEt;
    private SwitchCompat hideLocationSwitch;
    private Spinner noOfParticipantsSpinner;
    private SwitchCompat approvalSwitch;
    private LinearLayout cropLl;
    private LinearLayout deleteLl;
    private SwitchCompat participantSwitch;
    private CheckBox publicCb;
    private CheckBox privateCb;
    private CheckBox friendsCb;
    private LinearLayout startDateLlBtn;
    private TextView startDateTv;
    private LinearLayout endDateLlBtn;
    private TextView endDateTv;
    private LinearLayout startTimeLlBtn;
    private TextView startTimeTv;
    private LinearLayout endTimeLlBtn;
    private TextView endTimeTv;
    private EditText eventTitleEt;
    private LinearLayout eventActivtyLl;
    private LinearLayout nextLlBtn;
    private LinearLayout publicCbLl;
    private LinearLayout privateCbLl;
    private LinearLayout friendsCbLl;

    private GoogleMap mGoogleMap;

    private int clickedDateTv;
    private int clickedTimeTv;
    private int actionForPermission;
    private int mDownloadId;
    private String accessToken;
    private boolean isImageInImageView;
    private Uri inViewImageUri = null;
    private String serverImageUrl;
    private DownloadManager mDownloadManager;
    private JSONObject selectedActivity;
    private JSONArray interestsCategoriesArr;
    private ArrayList<String> maxParticipantsList;

    private int eventAudiance;
    private int eventId;

    private double lat;
    private double lng;
    private String placeName;
    private String placeAddress;
    private String placeId;

    private boolean isHideLocation;
    private boolean isApproveBeforeJoin;
    private int selectedActivityId; //eventid":76
    private boolean isEditEvent;
    private AddressPlaceModel currentAddressPlace;

    private Geocoder geocoder;
    private ScrollView userProfileScrollView;
    private AppCompatImageView back_button;
    private LinearLayout informParticipantsLl;
    private boolean isNotifyParticipants;
    private TextInputLayout discribeEtTil;
    private TextView audienceTv1;
    private TextView audienceTv2;
    private LinearLayout cameraIvLl;
    private int currentProfileUserCountryId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        mDownloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        mDownloadId = 0;

        initViews();
        getInterestsList();

        Log.d(TAG, "Has extra IS_EDIT_EVENT: " + intent.hasExtra(Constant.IS_EDIT_EVENT));
        Log.d(TAG, "Has extra IS_EDIT_EVENT: bool:  " + intent.getBooleanExtra(Constant.IS_EDIT_EVENT, false));
        if (intent.hasExtra(Constant.IS_EDIT_EVENT) && intent.getBooleanExtra(Constant.IS_EDIT_EVENT, false)) {
            isEditEvent = intent.getBooleanExtra(Constant.IS_EDIT_EVENT, false);
            int eventId = intent.getIntExtra(Constant.EDIT_EVENT_EVENT_ID, 0);
            getInviteInfo(eventId);
            Log.d(TAG, "Inside HasExtra");

        } else {
            informParticipantsLl.setVisibility(View.GONE);
        }
    }

    private void initViews() {
        eventPosterIv = (ImageView) findViewById(R.id.eventPosterIv);
        cameraIv = (ImageView) findViewById(R.id.cameraIv);
        cropIv = (ImageView) findViewById(R.id.cropIv);
        deleteIv = (ImageView) findViewById(R.id.deleteIv);
        back_button = (AppCompatImageView) findViewById(R.id.back_button);
        nextLlBtn = (LinearLayout) findViewById(R.id.nextLlBtn);

        publicIv = (ImageView) findViewById(R.id.publicIv);
        passwordIv = (ImageView) findViewById(R.id.passwordIv);
        friendsIv = (ImageView) findViewById(R.id.friendsIv);
        eventTitleEt = (EditText) findViewById(R.id.eventTitleEt);
        eventActivtyTv = (TextView) findViewById(R.id.eventActivtyTv);

        cropLl = (LinearLayout) findViewById(R.id.cropLl);
        deleteLl = (LinearLayout) findViewById(R.id.deleteLl);

        meeofDp = (DatePicker) findViewById(R.id.meeofDp);
        multipleDp = (DatePicker) findViewById(R.id.MultipleDp);
        startTp = (TimePicker) findViewById(R.id.startTp);
        endTp = (TimePicker) findViewById(R.id.endTp);
        eventLocationTv = (TextView) findViewById(R.id.eventLocationTv);
        addressEt = (EditText) findViewById(R.id.addressEt);
        discribeEt = (EditText) findViewById(R.id.discribeEt);
        noOfParticipantsSpinner = (Spinner) findViewById(R.id.noOfParticipantsSpinner);
        discribeEtTil = (TextInputLayout) findViewById(R.id.discribeEtTil);

        hideLocationSwitch = (SwitchCompat) findViewById(R.id.hideLocationSwitch);
        approvalSwitch = (SwitchCompat) findViewById(R.id.approvalSwitch);
        participantSwitch = (SwitchCompat) findViewById(R.id.participantSwitch);

        audienceTv1 = (TextView) findViewById(R.id.audienceTv1);
        audienceTv2 = (TextView) findViewById(R.id.audienceTv2);

        publicCb = (CheckBox) findViewById(R.id.publicCb);
        privateCb = (CheckBox) findViewById(R.id.privateCb);
        friendsCb = (CheckBox) findViewById(R.id.friendsCb);


        startDateLlBtn = (LinearLayout) findViewById(R.id.startDateLlBtn);
        startDateTv = (TextView) findViewById(R.id.startDateTv);
        endDateLlBtn = (LinearLayout) findViewById(R.id.endDateLlBtn);
        endDateTv = (TextView) findViewById(R.id.endDateTv);
        startTimeLlBtn = (LinearLayout) findViewById(R.id.startTimeLlBtn);
        startTimeTv = (TextView) findViewById(R.id.startTimeTv);
        endTimeLlBtn = (LinearLayout) findViewById(R.id.endTimeLlBtn);
        endTimeTv = (TextView) findViewById(R.id.endTimeTv);
        eventActivtyLl = (LinearLayout) findViewById(R.id.eventActivtyLl);
        userProfileScrollView = (ScrollView) findViewById(R.id.user_profile_scrollView);
        informParticipantsLl = (LinearLayout) findViewById(R.id.informParticipantsLl);

        publicCbLl= (LinearLayout) findViewById(R.id.publicCbLl);
        privateCbLl= (LinearLayout) findViewById(R.id.privateCbLl);
        friendsCbLl= (LinearLayout) findViewById(R.id.friendsCbLl);
        cameraIvLl=(LinearLayout) findViewById(R.id.cameraIvLl);

        registerForContextMenu(cameraIvLl);


        publicCb.setOnCheckedChangeListener(this);
        privateCb.setOnCheckedChangeListener(this);
        friendsCb.setOnCheckedChangeListener(this);
        hideLocationSwitch.setOnCheckedChangeListener(this);
        approvalSwitch.setOnCheckedChangeListener(this);
        participantSwitch.setOnCheckedChangeListener(this);

        eventPosterIv.setOnClickListener(this);
        cameraIv.setOnClickListener(this);
        cropIv.setOnClickListener(this);
        deleteIv.setOnClickListener(this);
        eventLocationTv.setOnClickListener(this);
        eventActivtyLl.setOnClickListener(this);

        startDateLlBtn.setOnClickListener(this);
        endDateLlBtn.setOnClickListener(this);
        startTimeLlBtn.setOnClickListener(this);
        endTimeLlBtn.setOnClickListener(this);
        back_button.setOnClickListener(this);
        discribeEt.addTextChangedListener(this);
        nextLlBtn.setOnClickListener(this);
        publicCbLl.setOnClickListener(this);
        privateCbLl.setOnClickListener(this);
        friendsCbLl.setOnClickListener(this);

        cameraIvLl.setOnClickListener(this);

        startDateTv.addTextChangedListener(DatePickerWatcher);
        startTimeTv.addTextChangedListener(TimePickerWatcher);


        maxParticipantsList = new ArrayList<>();
        maxParticipantsList.add("No Limit");
        for (int i = 1; i <= 1000; i++) {
            if (i <= 50) {
                maxParticipantsList.add(i + "");
            } else if (i <= 100) {
                if (i % 10 == 0) {
                    maxParticipantsList.add(i + "");
                }
            } else {
                if (i % 100 == 0) {
                    maxParticipantsList.add(i + "");
                }
            }
        }

        publicCb.performClick();

        initActivitiesSpinner(noOfParticipantsSpinner, maxParticipantsList);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);


        CustomMapFragment mapFragment = (CustomMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);

        mapFragment.setListener(new CustomMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                userProfileScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });


    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        getMenuInflater().inflate(R.menu.popup_menu_image_options, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        Log.d(TAG,"Context menu open");
        switch (item.getItemId()) {
            case R.id.menu_camera:
                actionForPermission = 2;
                cameraIntent();
                return true;
            case R.id.menu_gallery:
                galleryIntent();
                actionForPermission = 1;
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eventPosterIv:
                cameraIvLl.showContextMenu();
                break;
            case R.id.cropIv:
                actionForPermission = 3;
                cropImage();
                break;
            case R.id.deleteIv:
                deletePhoto();
                break;
            case R.id.startDateLlBtn:
                clickedDateTv = 1;
                openDatePickerDialog();
                break;
            case R.id.endDateLlBtn:
                clickedDateTv = 2;
                openDatePickerDialog();
                break;
            case R.id.startTimeLlBtn:
                clickedTimeTv = 1;
                openTimePickerDialog();
                break;
            case R.id.endTimeLlBtn:
                clickedTimeTv = 2;
                openTimePickerDialog();
                break;
            case R.id.eventLocationTv:
                sendToPlacesAutocomplteActivity();
                Helper.clickGaurd(eventLocationTv);
                break;
            case R.id.nextLlBtn:
                retieveEventDetails();
                break;
            case R.id.eventActivtyLl:
                sendToSelectActivity();
                Helper.clickGaurd(eventActivtyLl);
                break;
            case R.id.back_button:
                this.finish();
                break;
            case R.id.publicCbLl:
                changeCheckBox(publicCb);
                break;
            case R.id.privateCbLl:
                changeCheckBox(privateCb);
                break;
            case R.id.friendsCbLl:
                changeCheckBox(friendsCb);
                break;
            case R.id.cameraIvLl:
                cameraIvLl.showContextMenu();
                break;
            case R.id.cameraIv:
                cameraIvLl.showContextMenu();
                break;
        }

    }

    private void changeCheckBox(CheckBox checkBox){
        if(checkBox.isChecked()){
            checkBox.setChecked(false);
        }else {
            checkBox.setChecked(true);
        }
    }

    TextWatcher DatePickerWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //String[] dateSplitString=charSequence.toString().split("/");
            //multipleDp.updateDate(Integer.parseInt(dateSplitString[2]),Integer.parseInt(dateSplitString[1]),Integer.parseInt(dateSplitString[0]));
            endDateTv.setText(charSequence.toString());
        }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    TextWatcher TimePickerWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.d(TAG,"Time: "+charSequence.toString());

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Log.d(TAG,"END TIME: "+endTimeTv.getText());
            if(endTimeTv.getText().equals("End Time")){
                endTimeTv.setText("00:00");
            }

            try {
                Date startTime = sdf.parse(charSequence.toString());
                Date endTime = sdf.parse(endTimeTv.getText().toString());

                Log.d(TAG,"Start time: "+startTime.toString());
                Log.d(TAG,"End time: "+endTime.toString());
                if(startTime.compareTo(endTime)>0){
                    Log.d(TAG,"Start time greater");
                    //End time is lesser, so increase it to startime + 2 hours
                    //if start time is 11.30, end time cant be 1.30, need to fix it. Rest is done.
                    if(DateUtils.addHours(startTime,2).getDate()>startTime.getDate()){
                        endTimeTv.setText("00:00");
                    }else{
                        Date end= DateUtils.addHours(startTime,2);
                        endTimeTv.setText(new SimpleDateFormat("HH:mm").format(end).toString());
                    }
                }else{
                    Log.d(TAG,"Start time lesser");
                    //End time greater, so do nothing
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    private void getInviteInfo(int eventId) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventInfoWebJob(accessToken, eventId));
            Log.d(TAG, "Inside getInviteInfo");
        } else {
            showSnackbar(eventPosterIv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventInfoWebJobCompleted(EventInfoResponse eventInfoResponse) {
        if (eventInfoResponse != null) {
            if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                setEventDetailsToUI(eventInfoResponse.getData());
                Log.d(TAG, "Inside Subscribe onGetEventInfoWebJobCompleted");
            } else if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.ERROR)) {
                showSnackbar(eventPosterIv, getString(R.string.unable_to_retrieve_event_details), Constant.ERROR);
            }
        } else {
            showSnackbar(eventPosterIv, getString(R.string.unable_to_retrieve_event_details), Constant.ERROR);
        }
    }


    private void syncEvents(){
        jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
        startProgressBar();
    }

    @Subscribe
    public void onGetEventsWebJobCompletedEvent(GetEventsWebJobCompletedEvent getEventsWebJobCompletedEvent) {

        Log.d(TAG, "onGetEventsWebJobCompletedEvent 1");

        if (getEventsWebJobCompletedEvent != null) {

            if (getEventsWebJobCompletedEvent.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {
                String events = getEventsWebJobCompletedEvent.getEvents().toString();
                String attendance = getEventsWebJobCompletedEvent.getAttendance().toString();
                syncEventToDb(events, attendance);
                Log.d(TAG, "onGetEventsWebJobCompletedEvent 4");
            } else {
                stopProgressBar();
                showSnackbar(eventPosterIv, "check eror 1", Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(eventPosterIv, "check error 2", Constant.ERROR);
        }
    }

    public void syncEventToDb(String events, String attendance){
        jobManager.addJobInBackground(new EventsSyncJob(events, attendance));
    }

    @Subscribe
    public void onEventsSyncJobCompleted(EventsSyncJobCompleted eventsSyncJobCompleted) {
        Log.d(TAG, "onEventsSyncJobCompleted");
        stopProgressBar();
        Event event = DBHelper.getInstance(this).getEventByEventId(String.valueOf(eventId));
        sendToInviteFriendsToEventActivity(event);
        finish();
    }



    private void setEventDetailsToUI(List<Data> data) {
        Log.d(TAG, "Inside setEventDetailsToUI");
        String eventPoster = data.get(0).getEvent_poster();
        String title = data.get(0).getTitle();
        String interestName = data.get(0).getInterestName();
        String firstName = data.get(0).getFirst_name();
        String profilePhoto = data.get(0).getProfilephoto();

        int categoryId = data.get(0).getCategory_id();
        selectedActivityId = categoryId;

        int eventId = data.get(0).getEventid();
        this.eventId = eventId;

        String location = data.get(0).getLocation();
        this.placeAddress = location;

        String placeName = data.get(0).getPlaceName();
        this.placeName = placeName;

        String placeID = data.get(0).getPlaceID();
        this.placeId = placeID;

        double longitude = data.get(0).getLongitude();
        this.lng = longitude;

        double latitude = data.get(0).getLatitude();
        this.lat = latitude;

        int type = data.get(0).getType();
        eventAudiance = type;

        int isHideLocation = data.get(0).getIs_hide_location();
        this.isHideLocation = isHideLocation == 1 ? true : false;

        int organizerId = data.get(0).getOrganizer_id();
        String description = data.get(0).getDescription();
        String startDate = data.get(0).getStart_date();
        String endDate = data.get(0).getEnd_date();
        int maxAttendees = data.get(0).getMax_attendees();
        String detailedAddress = data.get(0).getDetailedaddress();

//        eventPosterIv

        informParticipantsLl.setVisibility(View.VISIBLE);

        String photoUrl = eventPoster == null || eventPoster.trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                Constant.EVENT_POSTER_BASE_URL + eventPoster;

        Log.d(TAG,"Photo url: "+eventPoster);
        if(eventPoster==null){
            isImageInImageView=false;
        }else{
            isImageInImageView=true;
            cropLl.setVisibility(View.VISIBLE);
            deleteLl.setVisibility(View.VISIBLE);
        }

        Picasso.with(getApplicationContext())
                .load(photoUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                .into(eventPosterIv);

        if (title != null) {
            eventTitleEt.setText(title);
        } else {
            eventTitleEt.setText("");
        }

        if (interestName != null) {
            eventActivtyTv.setText(interestName);
        } else {
            eventActivtyTv.setText("");
        }
/**Start Date Start Time**/
        if (startDate != null) {
            String startDateStr = startDate.split("\\s+")[0];
            String startTimeStr = startDate.split("\\s+")[1];

            if (startDateStr != null) {
                startDateTv.setText(formatDate(startDateStr, false));
            } else {
                startDateTv.setText("");
            }

            if (startTimeStr != null) {
                startTimeTv.setText(startTimeStr);
            } else {
                startTimeTv.setText("");
            }

        } else {
            startDateTv.setText("");
            startTimeTv.setText("");
        }
        /**Start Date Start Time**/
/**End date End Time**/
        if (endDate != null) {
            String endDateStr = endDate.split("\\s+")[0];
            String endTimeStr = endDate.split("\\s+")[1];

            if (endDateStr != null) {
                endDateTv.setText(formatDate(endDateStr, false));
            } else {
                endDateTv.setText("");
            }

            if (endTimeStr != null) {
                endTimeTv.setText(endTimeStr);
            } else {
                endTimeTv.setText("");
            }

        } else {
            endDateTv.setText("");
            endTimeTv.setText("");
        }
/**End date End Time**/

        if (location != null) {
            eventLocationTv.setText(placeName);
        } else {
            eventLocationTv.setText("");
        }

        if (placeAddress != null) {
            addressEt.setText(placeAddress);
        } else {
            addressEt.setText("");
        }

        if (description != null) {
            discribeEt.setText(description);
        } else {
            discribeEt.setText("");
        }

        if (maxAttendees != 0) {
            int maxAttendeesIndex = 0;
            for (int i = 0; i < maxParticipantsList.size(); i++) {
                Log.d(TAG, "No of Attendees Loop : " + maxParticipantsList.get(i) + " : " + maxAttendees);
                if (maxParticipantsList.get(i).equals(String.valueOf(maxAttendees))) {
                    Log.d(TAG, "No of Attendees: " + maxParticipantsList.get(i) + " : " + maxAttendees);
                    maxAttendeesIndex = i;
                }
            }
            noOfParticipantsSpinner.setSelection(maxAttendeesIndex);
        } else {
            noOfParticipantsSpinner.setSelection(0);
        }

        switch (type) {
            case 0:
                publicCb.setChecked(true);
                break;
            case 1:
                privateCb.setChecked(true);
                break;
            case 3:
                friendsCb.setChecked(true);
                break;
        }

        if (latitude != 0 && longitude != 0) {
            LatLng locationLatLng = new LatLng(latitude, longitude);
            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions().position(locationLatLng));
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, Constant.MAP_ZOOM_LEVEL));
        }

        if (isHideLocation == 1) {
            hideLocationSwitch.setChecked(true);
        } else {
            hideLocationSwitch.setChecked(false);
        }

        if (detailedAddress != null) {
            addressEt.setText(detailedAddress);
        } else {
            addressEt.setText("");
        }
    }

    private void getInterestsList() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "ACCESS TOKEN: " + accessToken);
            jobManager.addJobInBackground(new GetInterestsListWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(nextLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetInterestsWebJobCompleted(JSONObject interestsJObj) {
        stopProgressBar();
        if (interestsJObj != null) {
            try {
                if (interestsJObj.get("status") != null && interestsJObj.get("status").equals(Constant.SUCCESS)) {
                    if (interestsJObj.get("data") != null) {
                        try {
                            processInterestsJSON(interestsJObj.getJSONObject("data"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                        }
                    } else {
                        showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                    }
                } else {
                    showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void processInterestsJSON(JSONObject interestsData) throws JSONException, IOException {
        Log.d(TAG, "Interests::: " + interestsData.toString());
        ArrayList<String> mainCategories = new ArrayList<>();
        ArrayList<String> mainCategoriesTitles = new ArrayList<>();

        LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interests = new LinkedHashMap<>();
        interestsCategories = new LinkedHashMap<>();
        interestsCategoriesArr = new JSONArray();

        JSONObject inputJSON = interestsData;
        Iterator<String> keysIterator = inputJSON.keys();


        while (keysIterator.hasNext()) {
            String gridKey = (String) keysIterator.next(); //NEED
            mainCategories.add(gridKey);
            mainCategoriesTitles.add(inputJSON.getJSONObject(gridKey).getString("name"));

            Log.d(TAG, " inputJSON: " + inputJSON);
            Log.d(TAG, " inputJSON.getJSONObject(gridKey): " + inputJSON.getJSONObject(gridKey));

            JSONObject tempDict = new JSONObject();
            tempDict.put(gridKey, inputJSON.getJSONObject(gridKey));

            interestsCategoriesArr.put(tempDict);
        }

        Log.d(TAG, "final interestsCategories: " + interestsCategories.toString());
        Log.d(TAG, "final interestsCategoriesArr subcategories array: " + interestsCategoriesArr.length());

    }


    private void sendToSelectActivity() {
        Intent intent = new Intent(this, SearchInterests.class);
        intent.putExtra(Constant.SELECTED_ACTIVITY_EVENT, selectedActivity != null ? selectedActivity.toString() : null);
        intent.putExtra("interestsCategoriesArr", interestsCategoriesArr.toString());
        intent.putExtra(Constant.IS_FROM_CREATE_EVENT, true);
        startActivityForResult(intent, SELECT_ACTIVITY_REQUEST_CODE);
    }

    private void retieveEventDetails() {
        PostEventModel postEventModel = new PostEventModel();
//        sendToInviteFriendsToEventActivity();
        if (eventId != 0) {
            if ((placeId != null && placeName != null && placeAddress != null)) {
                if (!startDateTv.getText().toString().trim().equals("DD MM YY") &&
                        !endDateTv.getText().toString().trim().equals("DD MM YY")) {
                    if (!startTimeTv.getText().toString().trim().equals("Start Time") &&
                            !endTimeTv.getText().toString().trim().equals("End Time")) {
                        if (selectedActivityId != 0) {
                            if (discribeEt.getText().toString().trim().length() > 10) {
                                if (!isDatesInPast()) {
                                    if (!isEndDateBeforeStartDate()) {
                                        if (isStartTimeBeforeEndTime()) {
                                            if(isSquarePoster()) {

                                                postEventModel.setEventid(eventId);
                                                postEventModel.setAudience(eventAudiance);
                                                postEventModel.setTitle(eventTitleEt.getText().toString());
                                                postEventModel.setActivity(selectedActivityId);
                                                postEventModel.setDatestartdate(formatDate(startDateTv.getText().toString(), true));
                                                postEventModel.setDateenddate(formatDate(endDateTv.getText().toString(), true));
                                                postEventModel.setDatestartFtime(startTimeTv.getText().toString());
                                                postEventModel.setDateendtime(endTimeTv.getText().toString());
                                                postEventModel.setLat(lat);
                                                postEventModel.setLng(lng);
                                                postEventModel.setPlaceID(placeId);
                                                postEventModel.setPlaceName(placeName);
                                                postEventModel.setAddniceaddress(placeAddress);
                                                postEventModel.setDetailedaddress(addressEt.getText().toString());
                                                postEventModel.setDescription(discribeEt.getText().toString());
                                                postEventModel.setMaxnumbers(noOfParticipantsSpinner.getSelectedItem().toString());
                                                postEventModel.setHidelocation((isHideLocation ? "1" : ""));
                                                postEventModel.setApprovaltojoin((isApproveBeforeJoin ? "1" : ""));
                                                postEventModel.setInformparticipants(isNotifyParticipants ? "on" : "off");

                                                postCreateEvent(postEventModel);

                                            }else{
                                                showSnackbar(eventPosterIv, "Event poster needs to be cropped into a square image", Constant.ERROR);
                                            }
                                        } else {
                                            showSnackbar(eventPosterIv, "Your Event End Time Should be a Time After Start Time", Constant.ERROR);
                                        }
                                    } else {
                                        showSnackbar(eventPosterIv, "Your Event End Date Should be on the same date or date After Start Date", Constant.ERROR);
                                    }
                                } else {
                                    showSnackbar(eventPosterIv, "You Cannot Add an Date from Past", Constant.ERROR);
                                }
                            } else {
                                showSnackbar(eventPosterIv, "Please Enter a Longer Event Description", Constant.ERROR);
                            }
                        } else {
                            showSnackbar(eventPosterIv, "Please Select an Activity", Constant.ERROR);
                        }
                    } else {
                        showSnackbar(eventPosterIv, "Please Set Event Duration", Constant.ERROR);
                    }
                } else {
                    showSnackbar(eventPosterIv, "Please Set Event Dates", Constant.ERROR);
                }
            } else {
                showSnackbar(eventPosterIv, "Please Update the event Location", Constant.ERROR);
            }
        } else {
            showSnackbar(eventPosterIv, "Please Upload an Event Poster", Constant.ERROR);
        }
    }


    private boolean isDatesInPast() {
        try {
            boolean isStartDatePast = new SimpleDateFormat("dd/MM/yyyy").parse(startDateTv.getText().toString().trim()).before(new Date());
            boolean isEndDatePast = new SimpleDateFormat("dd/MM/yyyy").parse(endDateTv.getText().toString().trim()).before(new Date());
            Log.d(TAG, "(isDatesInPast) Date: " + startDateTv.getText().toString().trim() + " is in past: " + isStartDatePast);
            Log.d(TAG, "(isDatesInPast) Date: " + endDateTv.getText().toString().trim() + " is in past: " + isEndDatePast);
            if (isEndDatePast && isStartDatePast) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "(isDatesInPast) : " + e.getMessage());
        }
        return true;
    }

    private boolean isEndDateBeforeStartDate() {
        try {
            Date startDatePast = new SimpleDateFormat("dd/MM/yyyy").parse(startDateTv.getText().toString().trim());
            Date endDatePast = new SimpleDateFormat("dd/MM/yyyy").parse(endDateTv.getText().toString().trim());
            Log.d(TAG, "(isDatesInPast) Date: " + startDateTv.getText().toString().trim() + " is in past: " + startDatePast);
            Log.d(TAG, "(isDatesInPast) Date: " + endDateTv.getText().toString().trim() + " is in past: " + endDatePast);

            Log.d(TAG, "startDatePast: " + startDatePast.toString());
            Log.d(TAG, "endDatePast: " + endDatePast.toString());

//            if (endDatePast.compareTo(startDatePast) == 0 || endDatePast.before(startDatePast)) {
//                return true;
//            } else {
//                return false;
//            }

            if(endDatePast.compareTo(startDatePast)>=0){
              Log.d(TAG,"event end date 1");
                return false;

            }else {
                Log.d(TAG,"event end date 2");
                return true;

            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "(isDatesInPast) : " + e.getMessage());
        }
        return true;
    }

    private boolean isStartTimeBeforeEndTime() {
        try {
            Date startDatePast = new SimpleDateFormat("dd/MM/yyyy").parse(startDateTv.getText().toString().trim());
            Date endDatePast = new SimpleDateFormat("dd/MM/yyyy").parse(endDateTv.getText().toString().trim());
            Log.d(TAG, "(isDatesInPast) Date: " + startDateTv.getText().toString().trim() + " is in past: " + startDatePast);
            Log.d(TAG, "(isDatesInPast) Date: " + endDateTv.getText().toString().trim() + " is in past: " + endDatePast);

            Date startTime = new SimpleDateFormat("HH:mm").parse(startTimeTv.getText().toString().trim());
            Date endTime = new SimpleDateFormat("HH:mm").parse(endTimeTv.getText().toString().trim());

            Log.d(TAG, "startDatePast: " + startDatePast.toString());
            Log.d(TAG, "endDatePast: " + endDatePast.toString());
            Log.d(TAG, "startTime: " + startTime.toString());
            Log.d(TAG, "endTime: " + endTime.toString());

            if (endDatePast.compareTo(startDatePast) == 0) {
                if (startTime.before(endTime)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "(isDatesInPast) : " + e.getMessage());
        }
        return true;
    }

    private String formatDate(String date, boolean isSlash) {
        if (isSlash) {
            String day = date.split("/")[0];
            String month = date.split("/")[1];
            String year = date.split("/")[2];

            return year + "/" + month + "/" + day;
        } else {
            String year = date.split("-")[0];
            String month = date.split("-")[1];
            String day = date.split("-")[2];

            return day + "/" + month + "/" + year;
        }
    }

    private void postCreateEvent(PostEventModel postEventModel) {
//        sendToInviteFriendListActivity();
        if (isNetworkAvailable()) {
            Log.d(TAG,"postCreateEvent event id: "+eventId);
            jobManager.addJobInBackground(new PostEventDetailsWebJob(accessToken, postEventModel)); //TODO UNCOMMENT

            startProgressBar();
        } else {
            showSnackbar(eventPosterIv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostEventDetailsWebJobCompleted(final CreateEventResponse createEventResponse) {
        stopProgressBar();
        Log.d(TAG, "Inside onPostEventDetailsWebJob");
        if (createEventResponse != null) {
            Log.d(TAG, "Inside onPostEventDetailsWebJob not null");
            if (createEventResponse.getStatus() != null && createEventResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "Inside onPostEventDetailsWebJob not null success");
                showSnackbar(eventPosterIv, getString(R.string.event_created_successfully), Constant.SUCCESS);
                //getInviteInfo(eventId);
                syncEvents();
            } else {
                showSnackbar(eventPosterIv, getString(R.string.failed_to_create_event), Constant.ERROR);
            }
        } else {
            showSnackbar(eventPosterIv, getString(R.string.failed_to_create_event), Constant.ERROR);
        }
    }

    private void sendToInviteFriendsToEventActivity(Event event) {
        Intent intent = new Intent(this, InviteFriendsToEventActivity.class);
        if (isEditEvent) {
            intent.putExtra(Constant.IS_EDIT_EVENT, true);
        }
        Bundle args = new Bundle();
        args.putSerializable(Constant.EVENT, (Serializable) event);
        intent.putExtra("BUNDLE_EVENT", args);
        intent.putExtra(Constant.EDIT_EVENT_EVENT_ID, eventId);
        intent.putExtra(Constant.EDIT_EVENT_TITLE, eventTitleEt.getText().toString());
        stopProgressBar();
        startActivity(intent);
    }



    private void uploadImageMultiPart(Uri currentImageUri) {
        if (currentImageUri != null) {
            if (isNetworkAvailable()) {
                Log.d(TAG, "uploadImageMultiPart");
                if(isEditEvent){
                    jobManager.addJobInBackground(new UploadEventPosterImageWebJob(accessToken, new File(currentImageUri.toString()).getAbsolutePath(),eventId+""));
                }else{
                    jobManager.addJobInBackground(new UploadEventPosterImageWebJob(accessToken, new File(currentImageUri.toString()).getAbsolutePath(),""));
                }
                startProgressBar();
            } else {
                showSnackbar(endTimeTv, getString(R.string.no_internet), Constant.ERROR);
            }
        } else {
            showSnackbar(endTimeTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadImageWebJobCompleted(UploadEventPosterResponse imageUploadResponse) {
        stopProgressBar();

        Log.d(TAG, "onUploadImageWebJobCompleted");

        if (imageUploadResponse != null) {
            if (imageUploadResponse.getSmallurl() != null && imageUploadResponse.getSmallurl().length() > 0) {
                serverImageUrl = imageUploadResponse.getNormalurl();
                eventId = imageUploadResponse.getEventid();
                showSnackbar(eventPosterIv, "Image Uploaded Successfully", Constant.SUCCESS);
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        picassoImageTarget(serverImageUrl, false);
                    }
                });

            } else {
                showSnackbar(endTimeTv, getString(R.string.image_upload_failed), Constant.ERROR);
                isImageInImageView = false;
                inViewImageUri = null;
                showHideCropDelete(isImageInImageView);
                eventPosterIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar));
                eventId = 0;
            }
        } else {
            showSnackbar(endTimeTv, getString(R.string.image_upload_failed), Constant.ERROR);
            isImageInImageView = false;
            inViewImageUri = null;
            showHideCropDelete(isImageInImageView);
            eventPosterIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar));
            eventId = 0;
        }
    }

    private void picassoImageTarget(String url, final boolean isSocialNetImage) {
        String fileName = System.currentTimeMillis() + ".jpg";
        downloadImage(url, fileName);
    }


    private void sendToPlacesAutocomplteActivity() {
        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);

        Log.wtf(TAG, "Profile Response: " + profileResponse.toString());

        String userCountry = null;
        try {
            profileResponse.getData().getCountry().get(0).getEnglish_name();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Intent intent = new Intent(this, PlacesActivity.class);
//        intent.putExtra("country", userCountry != null ? userCountry : "singapore");
        intent.putExtra("country", "");
        startActivityForResult(intent, PLACE_REQUEST_CODE);
    }

    private void openTimePickerDialog() {
        TimePickerDialogFragment timePickerDialogFragment = new TimePickerDialogFragment();
        timePickerDialogFragment.show(getFragmentManager(), "datepicker");
    }

    @Subscribe
    public void onTimeSelected(TimeModel timeModel) {
        if (timeModel != null) {
            if (clickedTimeTv == 1) {
                startTimeTv.setText(timeModel.getHour() + ":" + timeModel.getMinute());
            } else if (clickedTimeTv == 2) {
                endTimeTv.setText(timeModel.getHour() + ":" + timeModel.getMinute());
            }
        }
    }

    private ProfileResponse retriveSavedProfileObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            return profileResponse;
        }
        return null;
    }

    private void openDatePickerDialog() {
        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();
        datePickerDialogFragment.show(getFragmentManager(), "datepicker");
    }

    @Subscribe
    public void onDateSelected(DateModel dateModel) {
        if (dateModel != null) {
            if (clickedDateTv == 1) {
                startDateTv.setText(dateModel.getDay() + "/" + (dateModel.getMonth() + 1) + "/" + dateModel.getYear());
            } else if (clickedDateTv == 2) {
                endDateTv.setText(dateModel.getDay() + "/" + (dateModel.getMonth() + 1) + "/" + dateModel.getYear());
            }
        }
    }


    private void galleryIntent() {
        boolean isAllow = isStoragePermissionGranted();
        if (isAllow) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
        }
    }

    private void showHideCropDelete(boolean isShow) {
        if (isShow) {
            cropLl.setVisibility(View.VISIBLE);
            deleteLl.setVisibility(View.VISIBLE);
        } else {
            cropLl.setVisibility(View.GONE);
            deleteLl.setVisibility(View.GONE);
        }

    }

    private void cameraIntent() {
        boolean isAllow = isStoragePermissionGranted();
        boolean isAllowCamera = Utility.checkCameraPermission(this);
        if (isAllowCamera && isAllow) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            showSnackbar(eventPosterIv, "Please Allow External Storage Permission", Constant.ERROR);
        }
    }

    private void cropImage() {

        boolean isAllow = isStoragePermissionGranted();

        if (isAllow) {
            if (isImageInImageView) {
                BitmapDrawable drawable = (BitmapDrawable) eventPosterIv.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Uri profileImageUrl = getImageUri(this, bitmap);
                //CropImage.activity(profileImageUrl).start(this);
                if(bitmap.getWidth()==bitmap.getHeight()){
                    Log.d(TAG,"Square aspect ratio");

                }
                CropImage.activity(profileImageUrl).setAspectRatio(1,1).setFixAspectRatio(true).start(this);
            } else {
                showSnackbar(eventPosterIv, "Please select a image for profile picture", Constant.ERROR);
                Log.d(TAG, "isImageInImageView false ");
            }
        } else {
            showSnackbar(eventPosterIv, "Permission not granted", Constant.ERROR);
        }

    }

    private boolean isSquarePoster(){
        if (isImageInImageView) {
            BitmapDrawable drawable = (BitmapDrawable) eventPosterIv.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            if(bitmap.getWidth()==bitmap.getHeight()){
                Log.d(TAG,"Square aspect ratio");
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }


    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "profileimage", null);
        return Uri.parse(path);
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void deletePhoto() {
        isImageInImageView = false;
        inViewImageUri = null;
        showHideCropDelete(isImageInImageView);
        eventPosterIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar));
        eventId = 0;
        if (isNetworkAvailable()) {
//            jobManager.addJobInBackground(new UpdateProfilePictureWebJob(accessToken, ""));
//            startProgressBar();
        } else {
            showSnackbar(eventPosterIv, getString(R.string.no_internet), Constant.ERROR);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (actionForPermission) {
                        case 1:
                            galleryIntent();
                            break;
                        case 2: //STORAGE
                            boolean isAllowCamera = Utility.checkCameraPermission(this);
                            if (isAllowCamera) {
                                cameraIntent();
                            }
                            break;
                        case Utility.MY_PERMISSIONS_CAMERA:
                            boolean isAllow = isStoragePermissionGranted();
                            if (isAllow) {
                                cameraIntent();
                            }
                            break;
                        case 3:
                            cropImage();
                            break;
                        default:
                            showSnackbar(eventPosterIv, "Permission not granted", Constant.ERROR);
                            break;
                    }
                } else {
                    showSnackbar(eventPosterIv, "Permission not granted", Constant.ERROR);
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);

            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                onCropImageResult(data);

            } else if (requestCode == PLACE_REQUEST_CODE) {
                onPlaceRequestResult(data);
            } else if (requestCode == SELECT_ACTIVITY_REQUEST_CODE) {
                try {
                    selectedActivity = new JSONObject(data.getStringExtra(Constant.SELECTED_ACTIVITY_EVENT));
                    selectedActivityId = selectedActivity.getInt("id");
                    Log.d(TAG, "Selected Activity: " + selectedActivity.toString());
                    eventActivtyTv.setText(selectedActivity.getString("name").toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onPlaceRequestResult(Intent data) {
        Log.d(TAG, "Inside PLACE_REQUEST_CODE");

        lat = Double.parseDouble(data.getStringExtra("lat").toString());
        lng = Double.parseDouble(data.getStringExtra("lng").toString());
        placeName = data.getStringExtra("place_name");
        placeAddress = data.getStringExtra("place_address");
        placeId = data.getStringExtra("place_id");


        Double latitude = lat;
        Double longitude = lng;

        LatLng userLatLong = new LatLng(latitude, longitude);

        eventLocationTv.setText("" + placeName);

        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(userLatLong));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, Constant.MAP_ZOOM_LEVEL));
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String userPostImagePath = GetImageFromGallery.getPath(this, selectedImageUri);

        Uri currentImageUri = Uri.fromFile(new File(userPostImagePath));
        Log.d(TAG, "Path SelectFromGallery: " + currentImageUri.getPath());
        String path = currentImageUri.getPath();

        sharedEditor.putString(Constant.IMAGE_SET_PATH, path).apply();

        File sourceFile = new File(path);
        Log.d(TAG, "isExists: " + sourceFile.exists());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(userPostImagePath, options);

        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        Bitmap selectedImageBitMap = BitmapFactory.decodeFile(userPostImagePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        selectedImageBitMap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();
        Base64.encodeToString(bytes, Base64.DEFAULT);

        String pathImg = MediaStore.Images.Media.insertImage(this.getContentResolver(), selectedImageBitMap, "meeof_pp", null);
        updateProfileImageView(selectedImageBitMap);

        isImageInImageView = true;
        inViewImageUri = Uri.parse(pathImg);
        showHideCropDelete(isImageInImageView);
        cropIv.setVisibility(View.VISIBLE);
        deleteIv.setVisibility(View.VISIBLE);

        uploadImageMultiPart(inViewImageUri);
    }

    private void onCaptureImageResult(Intent data) {
        Uri selectedImageUri = data.getData();
        Bitmap selectedImageBitMap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        selectedImageBitMap.compress(Bitmap.CompressFormat.JPEG, 70, bytes);

        File destination = new File(Environment.getExternalStorageDirectory().getPath(),
                System.currentTimeMillis() + ".jpg");

        bytes.toByteArray();
        Base64.encodeToString((bytes.toByteArray()), Base64.DEFAULT);

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri currentImageUri = Uri.fromFile(destination);
        Log.d(TAG, "Path SelectFromCamera: " + currentImageUri.getPath());
        String path = currentImageUri.getPath();
        sharedEditor.putString(Constant.IMAGE_SET_PATH, path).apply();

        updateProfileImageView(selectedImageBitMap);
        File sourceFile = new File(path);
        Log.d(TAG, "isExists: " + sourceFile.exists());

        inViewImageUri = currentImageUri;
        isImageInImageView = true;
        showHideCropDelete(isImageInImageView);
        cropIv.setVisibility(View.VISIBLE);
        deleteIv.setVisibility(View.VISIBLE);

        uploadImageMultiPart(inViewImageUri);
    }

    private void onCropImageResult(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        Uri resultUri = result.getUri();
        saveImageData(resultUri);
    }

    private void saveImageData(Uri imageUri) {
        inViewImageUri = imageUri;
        String imagePath = GetImageFromGallery.getPath(this, imageUri);
        String path = imageUri.getPath();
        sharedEditor.putString(Constant.IMAGE_SET_PATH, path).apply();

        File sourceFile = new File(path);
        Log.d(TAG, "isExists: " + sourceFile.exists());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;

        Bitmap imageBitMap = BitmapFactory.decodeFile(imagePath, options);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitMap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();
        Base64.encodeToString(bytes, Base64.DEFAULT);
        updateProfileImageView(imageBitMap);

        uploadImageMultiPart(inViewImageUri);

    }

    private void updateProfileImageView(Bitmap selectedImageBitMap) {
        eventPosterIv.setImageBitmap(selectedImageBitMap);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.publicCb:
                if (isChecked) {
                    Log.d(TAG, "inside publicCb");
                    eventAudiance = 0;
                    audienceTv1.setText(getAudienceDescription(eventAudiance)[0]);
                    audienceTv2.setText(getAudienceDescription(eventAudiance)[1]);
                    updateViewCheckBox(eventAudiance);
                }
                break;
            case R.id.friendsCb:
                if (isChecked) {
                    Log.d(TAG, "inside friendsCb");
                    eventAudiance = 1;
                    audienceTv1.setText(getAudienceDescription(eventAudiance)[0]);
                    audienceTv2.setText(getAudienceDescription(eventAudiance)[1]);
                    privateCb.setChecked(false);
                    publicCb.setChecked(false);
                    updateViewCheckBox(eventAudiance);
                }
                break;
            case R.id.privateCb:
                if (isChecked) {
                    Log.d(TAG, "inside privateCb");
                    eventAudiance = 2;
                    audienceTv1.setText(getAudienceDescription(eventAudiance)[0]);
                    audienceTv2.setText(getAudienceDescription(eventAudiance)[1]);
                    updateViewCheckBox(eventAudiance);
                }
                break;
            case R.id.hideLocationSwitch:
                if (isChecked) {
                    isHideLocation = true;
                }
                break;
            case R.id.approvalSwitch:
                if (isChecked) {
                    isApproveBeforeJoin = true;
                }
                break;
            case R.id.participantSwitch:
                if (isChecked) {
                    isNotifyParticipants = true;
                }

        }
    }

    private void updateViewCheckBox(int audiance) {
        switch (audiance) {
            case 0:
                privateCb.setChecked(false);
                friendsCb.setChecked(false);
                publicIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_public_selected));
                passwordIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_private_normal));
                friendsIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_friends_normal));
                break;
            case 1:
                publicCb.setChecked(false);
                privateCb.setChecked(false);
                friendsIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_friends_selected));
                publicIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_public_normal));
                passwordIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_private_normal));
                break;
            case 2:
                publicCb.setChecked(false);
                friendsCb.setChecked(false);
                passwordIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_private_selected));
                publicIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_public_normal));
                friendsIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_eventnew_friends_normal));
                break;
        }
    }

    private boolean initActivitiesSpinner(final Spinner spinner, final List<String> listSpinner) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateEventActivity.this, android.R.layout.simple_spinner_item, listSpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });


        return true;
    }


    private void downloadImage(String imageUrl, String fileName) {
        Log.d(TAG, "downloadAndPlay imageUrl: : " + imageUrl);
        Log.d(TAG, "downloadAndPlay fileName: " + fileName);

        BroadcastReceiver receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    try {
                        Log.d(TAG, "downloadAndPlay image: downloading");
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(mDownloadId);
                        Cursor c = mDownloadManager.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                                CreateEventActivity.this.unregisterReceiver(this);

                                final String uriString = c.getString(c.getColumnIndex(
                                        DownloadManager.COLUMN_LOCAL_URI));

                                Log.d(TAG, "downloadAndPlay videoUrl:3 " + uriString);

                                CreateEventActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Uri tempUri = Uri.parse(uriString);
                                        eventPosterIv.setImageURI(tempUri);
                                        inViewImageUri = tempUri;
                                        isImageInImageView = true;
                                        stopProgressBar();
                                    }
                                });
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, e.toString());
                    }
                }
            }
        };
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
    }


    @Override
    public void onMapClick(LatLng latLng) {
        Log.d(TAG, "onMapClick");

        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constant.MAP_ZOOM_LEVEL));

        List<Address> addresses = getAddressFromLatLong(latLng.latitude, latLng.longitude);

        if (addresses != null && addresses.size() > 0) {
            if (addresses.get(0) != null) {

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.d(TAG, "onMapClick: addresses" + addresses.get(0));


                currentAddressPlace = new AddressPlaceModel();
                currentAddressPlace.setLat(String.valueOf(latLng.latitude));
                currentAddressPlace.setLng(String.valueOf(latLng.longitude));
                currentAddressPlace.setPlaceName(knownName);
                currentAddressPlace.setAddniceaddress(address);

                eventLocationTv.setText(address);
                retrievePlaceIdForLatLong(latLng);

            } else {
                mGoogleMap.clear();
                showSnackbar(eventPosterIv, getString(R.string.unable_to_retrieve_address_line_from_this_location), Constant.ERROR);
            }
        } else {
            mGoogleMap.clear();
            showSnackbar(eventPosterIv, getString(R.string.unable_to_retrieve_address_line_from_this_location), Constant.ERROR);
        }
    }


    public void retrievePlaceIdForLatLong(LatLng latLng) {
        jobManager.addJobInBackground(new CreateEventGetPlaceIdWebJob(latLng.latitude, latLng.longitude));
    }


    @Subscribe
    public void googlePlaceIdResponse(final CreateEventGooglePlaceIdResponse googlePlaceIdResponse) {
        Log.d(TAG, "googlePlaceIdResponse");
        stopProgressBar();
        if (googlePlaceIdResponse != null) {
            if (googlePlaceIdResponse.getStatus() != null && googlePlaceIdResponse.getStatus().contains(Constant.SUCCESS)) {

                try {
                    JSONArray data = googlePlaceIdResponse.getData();
                    JSONObject placeObj = (JSONObject) data.get(0);

                    String placeId = placeObj.getString("place_id");

                    if (placeId != null && placeId.length() > 0) {
                        currentAddressPlace.setPlaceID(placeId);
                        lat = Double.parseDouble(currentAddressPlace.getLat());
                        lng = Double.parseDouble(currentAddressPlace.getLng());
                        placeName = currentAddressPlace.getPlaceName();
                        placeAddress = currentAddressPlace.getAddniceaddress();
                        placeId = currentAddressPlace.getPlaceID();
                    } else {
                        currentAddressPlace = null;
                        lat = 0;
                        lng = 0;
                        placeName = null;
                        placeAddress = null;
                        placeId = null;
                    }

                    Log.d(TAG, "googlePlaceIdResponse placeObj: " + placeObj);
                    Log.d(TAG, "googlePlaceIdResponse place id: " + placeId);


                } catch (JSONException e) {
                    e.printStackTrace();
                    showSnackbar(eventPosterIv, getString(R.string.google_place_id_failed), Constant.ERROR);
                    currentAddressPlace = null;
                }


            } else {
                showSnackbar(eventPosterIv, getString(R.string.google_place_id_failed), Constant.ERROR);
                currentAddressPlace = null;
            }
        } else {
            showSnackbar(eventPosterIv, getString(R.string.google_place_id_failed), Constant.ERROR);
            currentAddressPlace = null;
        }
    }


    private String[] getAudienceDescription(int type) {
        Log.d(TAG, "getAudienceDescription() type: " + type);
        String[] text = new String[2];
        switch (type) {
            case 0:
                return new String[]{"Anyone in your neighbourhood and friends in meeof,",
                        "can see it. It can be shared freely by anyone"};
            case 1:
                return new String[]{"Only people invited by name can see it. No possibility",
                        "by anyone to share it further"};
            case 2:
                return new String[]{"Only your friends in meeof can see it, with the option",
                        "for the organizer to share on social networks"};
            default:
                return new String[]{"Default"};
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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (count <= 10) {
            discribeEtTil.setError(count + "/10");
        } else {
            discribeEtTil.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.toString().length() <= 10) {
            discribeEtTil.setError("Event Description should be more than 10 Characters");
        } else {
            discribeEtTil.setError(null);
        }

    }

}
