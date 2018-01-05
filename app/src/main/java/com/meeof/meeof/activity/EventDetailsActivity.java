package com.meeof.meeof.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.PhotosEventRecyclerAdapter;
import com.meeof.meeof.helper.DBHelper;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.GetEventsWebJobCompletedEvent;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.ReloadEventData;
import com.meeof.meeof.model.ReportEventWebJobCompletedResponse;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.edit_event_dto.Photos;
import com.meeof.meeof.model.event_image_upload_dto.EventImageUploadResponse;
import com.meeof.meeof.model.event_permission_dto.EventPermissionResponse;
import com.meeof.meeof.model.event_permission_dto.Event_permission;
import com.meeof.meeof.model.events_dto.AttendeeList;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.interested_in_event_dto.InterestedEventResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.rsvp_response.RSVPResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.EventsSyncJob;
import com.meeof.meeof.webjob.GetEventInfoWebJob;
import com.meeof.meeof.webjob.GetEventPermissionsWebJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.PostIgnoreEventWebJob;
import com.meeof.meeof.webjob.PostInterestedInEventWebJob;
import com.meeof.meeof.webjob.PostLikeEventAsFriendWebJob;

import com.meeof.meeof.webjob.PostRSVPWebJob;
import com.meeof.meeof.webjob.PostReportEventWebJob;
import com.meeof.meeof.webjob.UploadEventImageArrayWebJob;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class EventDetailsActivity extends BaseActivity implements View.OnClickListener {

    private static final int RC_REFRESH_EVENTS_DATA = 9898;
    private static final String TAG = EventDetailsActivity.class.getSimpleName();
    private static final int UPLOAD_LIMIT_MULTI_SELECT_GALLERY = 10;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private LinearLayout invitedDetailsLlBtn;
    private Event event;
    private TextView eventActivityTypeTv;
    private TextView eventHostNameTv;
    private TextView noOfCommentTv;
    private TextView noOfLikesTv;
    private TextView eventNameTv;
    private TextView eventAudienceTv;
    private TextView eventActivityTv;
    private TextView eventDateTv;
    private TextView eventMonthTv;
    private TextView eventHostTv;
    private TextView eventStatusTv;
    private TextView eventLocationTv;
    private TextView eventPlaceNameTv;
    private TextView eventDetailedAddressTv;
    private TextView eventTotalDateTv;
    private ImageView publicPrivateIv;
    private TextView eventAudienceTv2;
    private TextView eventAudienceDetailsTv;
    private TextView noInvitedPplTv;
    private TextView noMaxAttendeesTv;
    private TextView eventDetailsTv;
    private TextView moreDetailsTv;
    private CheckBox noResponceCb;
    private CheckBox goingCb;
    private CheckBox notGoingCb;
    private LinearLayout addPhotosLlBtn;
    private CheckBox maybeCb;
    private TextView myEventStatusTv;
    private ImageView userImageBackgroundIv;
    private ImageView eventPosterIv;
    private LinearLayout mapLlBtn;
    private LinearLayout backLlBtn;
    private LinearLayout likeLlBtn;
    private LinearLayout moreLlBtn;
    private String accessToken;
    private ImageView hostProfilePicIv;
    private ImageView likeBtnIv;
    private boolean isCurrentlyLiked;
    private RecyclerView photosRv;
    private PhotosEventRecyclerAdapter photosEventAdapter;
    private LinearLayout moreCommentsLikesLlBtn;
    private LinearLayout seeAllCommentsLikesLlBtn;
    private ImageView going_people1_imageView;
    private ImageView going_people2_imageView;
    private ImageView going_people3_imageView;
    private LinearLayout eventAttendeesLl;
    private LinearLayout eventFullLl;
    private LinearLayout respondEventLl;
    private LinearLayout eventPlaceNameLl;
    private LinearLayout eventLocationLl;
    private LinearLayout eventDetailedAddressLl;
    private ProfileResponse profileResponse;
    private int rsvp;
    private boolean isDoneLoading;
    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_details_normal);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        profileResponse = retriveSavedProfileObject(sharedPreferences);

        Log.d(TAG, "Has Extra: " + this.getIntent().hasExtra("BUNDLE_EVENT"));
        if (this.getIntent().hasExtra("BUNDLE_EVENT")) {
            Bundle args = this.getIntent().getBundleExtra("BUNDLE_EVENT");
            event = (Event) args.getSerializable(Constant.SELECTED_EVENT_ITEM);
            Log.d(TAG, "Has Extra:Name  " + event.getFirst_name());
            Log.d(TAG, "Event:  " + event.toString());
        } else {
//            showSnackbar();
        }

        loadCurrentUser();
        initViews();

        getEventPermissions();
    }

    @Override
    protected void onResume() {
        getEventDetails(event.getEventid());
        if(isDoneLoading){
        }
        super.onResume();
    }


    private void loadCurrentUser() {
        Log.d(TAG,"loadCurrentUser 1");
        if (sharedPreferences != null) {
            Log.d(TAG,"loadCurrentUser 2");
            String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
            Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
            if (!profileObjectJsonStr.equals("")) {
                Gson gson = new Gson();
                profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);

                Log.d(TAG,"Profile user id: "+profileResponse.getData().getUser_id());

            }
        }
    }


    private void getEventPermissions() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventPermissionsWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(eventHostNameTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventPermissionsWebJob(EventPermissionResponse eventPermissionResponse) {
        if (eventPermissionResponse != null) {
            if (eventPermissionResponse.getStatus() != null && eventPermissionResponse.getStatus().equals(Constant.SUCCESS)) {
                if (eventPermissionResponse.getEvent_permission() != null) {
                    Event_permission eventPermission = eventPermissionResponse.getEvent_permission();
                    Log.d(TAG, "Permisssions for " + event.getEventid() + " is " + eventPermission.toString());
                    if (!eventPermission.getInteract()) {
                        addPhotosLlBtn.setVisibility(View.GONE);
                    } else {
                        addPhotosLlBtn.setVisibility(View.VISIBLE);
                    }

                    if (!eventPermission.getJoin()) {
                        respondEventLl.setVisibility(View.GONE);
                    } else {
                        respondEventLl.setVisibility(View.VISIBLE);
                    }
                    Log.d(TAG, "getIsFull rsvp 1 : "  + rsvp);

                    if (eventPermission.getIsFull()) {

                        if(rsvp == 0){
                            respondEventLl.setVisibility(View.GONE);
                        }else {
                            respondEventLl.setVisibility(View.VISIBLE);
                        }

                    } else {
                        respondEventLl.setVisibility(View.VISIBLE);
                    }

                    if (!eventPermission.getShowLocation()) {
                        eventPlaceNameTv.setText("Location Hidden by Host");
                        eventLocationLl.setVisibility(View.GONE);
                        eventDetailedAddressLl.setVisibility(View.GONE);
                    }
                } else {

                }
            } else {

            }
        } else {

        }
    }

    private void initViews() {
        publicPrivateIv = (ImageView) findViewById(R.id.publicPrivateIv);

        eventHostNameTv = (TextView) findViewById(R.id.eventHostNameTv);
        noOfCommentTv = (TextView) findViewById(R.id.noOfCommentTv);
        noOfLikesTv = (TextView) findViewById(R.id.noOfLikesTv);
        eventNameTv = (TextView) findViewById(R.id.eventNameTv);
        eventAudienceTv = (TextView) findViewById(R.id.eventAudienceTv);
        eventActivityTypeTv = (TextView) findViewById(R.id.eventActivityTypeTv);
        eventActivityTv = (TextView) findViewById(R.id.eventActivityTv);
        eventDateTv = (TextView) findViewById(R.id.eventDateTv);
        eventMonthTv = (TextView) findViewById(R.id.eventMonthTv);
        eventHostTv = (TextView) findViewById(R.id.eventHostTv);
        eventStatusTv = (TextView) findViewById(R.id.eventStatusTv);
        eventLocationTv = (TextView) findViewById(R.id.eventLocationTv);
        eventPlaceNameTv = (TextView) findViewById(R.id.eventPlaceNameTv);
        eventDetailedAddressTv = (TextView) findViewById(R.id.eventDetailedAddressTv);
        eventTotalDateTv = (TextView) findViewById(R.id.eventTotalDateTv);
        eventAudienceTv2 = (TextView) findViewById(R.id.eventAudienceTv2);
        eventAudienceDetailsTv = (TextView) findViewById(R.id.eventAudienceDetailsTv);
        noInvitedPplTv = (TextView) findViewById(R.id.noInvitedPplTv);
        noMaxAttendeesTv = (TextView) findViewById(R.id.noMaxAttendeesTv);
        eventDetailsTv = (TextView) findViewById(R.id.eventDetailsTv);
        moreDetailsTv = (TextView) findViewById(R.id.moreDetailsTv);
        userImageBackgroundIv = (ImageView) findViewById(R.id.userImageBackgroundIv);
        eventPosterIv = (ImageView) findViewById(R.id.eventPosterIv1);
        hostProfilePicIv = (ImageView) findViewById(R.id.hostProfilePicIv);
        mapLlBtn = (LinearLayout) findViewById(R.id.mapLlBtn);
        backLlBtn = (LinearLayout) findViewById(R.id.backLlBtn);
        likeLlBtn = (LinearLayout) findViewById(R.id.likeLlBtn);
        moreLlBtn = (LinearLayout) findViewById(R.id.moreLlBtn);
        likeBtnIv = (ImageView) findViewById(R.id.likeBtnIv);
        respondEventLl = (LinearLayout) findViewById(R.id.respondEventLl);
        eventAttendeesLl = (LinearLayout) findViewById(R.id.eventAttendeesLl);
        eventFullLl = (LinearLayout) findViewById(R.id.eventFullLl);
        eventPlaceNameLl=(LinearLayout)findViewById(R.id.eventPlaceNameLl);
        eventLocationLl=(LinearLayout)findViewById(R.id.eventLocationLl);
        eventDetailedAddressLl=(LinearLayout)findViewById(R.id.eventDetailedAddressLl);

        going_people1_imageView = (ImageView) findViewById(R.id.going_people1_imageView);
        going_people2_imageView = (ImageView) findViewById(R.id.going_people2_imageView);
        going_people3_imageView = (ImageView) findViewById(R.id.going_people3_imageView);

        seeAllCommentsLikesLlBtn = (LinearLayout) findViewById(R.id.seeAllCommentsLikesLlBtn);

        noResponceCb = (CheckBox) findViewById(R.id.noResponceCb);
        goingCb = (CheckBox) findViewById(R.id.goingCb);
        notGoingCb = (CheckBox) findViewById(R.id.notGoingCb);
        maybeCb = (CheckBox) findViewById(R.id.maybeCb);
        addPhotosLlBtn = (LinearLayout) findViewById(R.id.addPhotosLlBtn);

        addPhotosLlBtn = (LinearLayout) findViewById(R.id.addPhotosLlBtn);
        invitedDetailsLlBtn = (LinearLayout) findViewById(R.id.invitedDetailsLlBtn);
        myEventStatusTv = (TextView) findViewById(R.id.myEventStatusTv);

        photosRv = (RecyclerView) findViewById(R.id.photosRv);

        moreLlBtn=(LinearLayout)findViewById(R.id.moreLlBtn);

        invitedDetailsLlBtn.setOnClickListener(this);
        likeLlBtn.setOnClickListener(this);
        mapLlBtn.setOnClickListener(this);
        backLlBtn.setOnClickListener(this);
        addPhotosLlBtn.setOnClickListener(this);
        seeAllCommentsLikesLlBtn.setOnClickListener(this);

        noResponceCb.setOnClickListener(this);
        goingCb.setOnClickListener(this);
        notGoingCb.setOnClickListener(this);
        maybeCb.setOnClickListener(this);
        moreLlBtn.setOnClickListener(this);

        setDataToUI();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invitedDetailsLlBtn:
                sendToInvitedFriendsEventActivity();
                Helper.clickGaurd(invitedDetailsLlBtn);
                break;
            case R.id.likeLlBtn:
                isCurrentlyLiked = !isCurrentlyLiked;
                likeEvent();
                Log.d(TAG, "onClick() " + isCurrentlyLiked);
                break;
            case R.id.mapLlBtn:
                startNavigationtoPlace();
                break;
            case R.id.backLlBtn:
                this.onBackPressed();
                break;
            case R.id.addPhotosLlBtn:
//                uploadPhotos();
                galleryIntent();
                break;
            case R.id.seeAllCommentsLikesLlBtn:
                sendToEventSocialActivity();
                Helper.clickGaurd(seeAllCommentsLikesLlBtn);
                break;
            case R.id.noResponceCb:
                sendRSVPResponse(2); //No Response
                break;
            case R.id.goingCb:
                sendRSVPResponse(3); //Going
                break;
            case R.id.notGoingCb:
                sendRSVPResponse(4); //Not Going
                break;
            case R.id.maybeCb:
                sendRSVPResponse(5); //Maybe
                break;
            case R.id.moreLlBtn:
                showPopUpMenu(false, moreLlBtn,event);
                break;
        }
    }

    private void showPopUpMenu(final boolean isHost, View anchor, final Event event) {
        final PopupMenu popup = new PopupMenu(this, anchor);
        //Inflating the Popup using xml file
        if (isHost) {
            popup.getMenuInflater().inflate(R.menu.popup_menu_host, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.popup_menu_non_host, popup.getMenu());
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case R.id.interestItm:
                            interestedInEvent(event);
                            break;
                        case R.id.ignoreItm:
                            ignoreEvent(event);
                            break;
                        case R.id.reportItm:
                            reportEvent(event);
                            break;

                }
                return true;
            }


        });
        popup.show();//showing popup menu
    }

    private void interestedInEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostInterestedInEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(moreLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void ignoreEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostIgnoreEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(moreLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostIgnoreEventWebJobCompleted(HttpResponseData httpResponseData) {
        if (httpResponseData != null) {
            if (httpResponseData.getStatus() != null && httpResponseData.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(moreLlBtn, getString(R.string.ignore_event_successful), Constant.SUCCESS);
            } else {
                showSnackbar(moreLlBtn, getString(R.string.ignore_event_unsucessful), Constant.ERROR);
            }
        } else {
            showSnackbar(moreLlBtn, getString(R.string.ignore_event_unsucessful), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostInterestedInEventWebJobCompleted(InterestedEventResponse interestedEventResponse) {
        if (interestedEventResponse != null) {
            if (interestedEventResponse.getStatus() != null && interestedEventResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(moreLlBtn, getString(R.string.interest_event_successful), Constant.SUCCESS);
            } else {
                showSnackbar(moreLlBtn, getString(R.string.interest_event_unsuccessful), Constant.ERROR);
            }
        } else {
            showSnackbar(moreLlBtn, getString(R.string.interest_event_unsuccessful), Constant.ERROR);
        }
    }

    private void reportEvent(Event event) {
        if (isNetworkAvailable()) {
            Log.d(TAG,"Access token report event :"+accessToken);
            jobManager.addJobInBackground(new PostReportEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(moreLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostReportEventWebJobCompleted(ReportEventWebJobCompletedResponse reportEventWebJobCompletedResponse) {
        if (reportEventWebJobCompletedResponse != null) {
            if (reportEventWebJobCompletedResponse.getStatus() != null && reportEventWebJobCompletedResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(moreLlBtn, getString(R.string.report_event_successful), Constant.SUCCESS);
            } else {
                showSnackbar(moreLlBtn, getString(R.string.retport_event_unsuccessful), Constant.ERROR);
            }
        } else {
            showSnackbar(moreLlBtn, getString(R.string.retport_event_unsuccessful), Constant.ERROR);
        }
    }


    private void sendRSVPResponse(int rsvpStatus) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostRSVPWebJob(accessToken, event.getEventid(), rsvpStatus));
        } else {
            showSnackbar(maybeCb, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostRSVPWebJob(RSVPResponse rsvpResponse) {
        if (rsvpResponse != null) {
            if (rsvpResponse.getStatus() != null && rsvpResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(maybeCb, getString(R.string.RSVP_captured), Constant.SUCCESS);
                setMyEventStatus(rsvpResponse.getRequest());
            } else {
                showSnackbar(maybeCb, getString(R.string.did_not_capture_RSVP), Constant.ERROR);
            }
        } else {
            showSnackbar(maybeCb, getString(R.string.did_not_capture_RSVP), Constant.ERROR);
        }
    }

    private void sendToEventSocialActivity() {
        Intent intent = new Intent(this, EventSocialActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) this.event);
        intent.putExtra("BUNDLE_EVENT", args);
        startActivity(intent);
    }

    private void galleryIntent() {
        boolean isAllow = isStoragePermissionGranted();
        if (isAllow) {
            Intent intent = new Intent(this, AlbumSelectActivity.class);
            intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, UPLOAD_LIMIT_MULTI_SELECT_GALLERY); // set limit for image selection
            startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);

        }
    }




    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    galleryIntent();
                } else {
                    showSnackbar(eventHostNameTv, "Permission not granted", Constant.ERROR);
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

            if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
                //The array list has the image paths of the selected images
                ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
                Set<String> paths = new HashSet<>();
                for (int i = 0; i < images.size(); i++) {
                    Uri uri = Uri.fromFile(new File(images.get(i).path));
                    Log.d(TAG, "Images: " + images.get(i).path);
                    paths.add(uri.getPath());
                }

                uploadPhotos(paths, images.size());
            }
        }
    }

    private void uploadPhotos(Set<String> paths, int imageCount) {
        String[] images = paths.toArray(new String[paths.size()]);
        if (isNetworkAvailable()) {
            Log.d(TAG,"uploadPhotos "+images.toString());
            jobManager.addJobInBackground(new UploadEventImageArrayWebJob(accessToken, images, event.getEventid()));

            String message = "Uploading photo. Please wait..";

            if(imageCount > 1){
                message = "Uploading " + String.valueOf(imageCount) + " photos. " + "Please wait..";
            }
            //showProgressAlert(message);

            progressBar = ProgressDialog.show(this, "",
                    message, true);

        } else {
            showSnackbar(eventHostNameTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onUploadEventImageWebJob(EventImageUploadResponse eventImageUploadResponse) {
        isDoneLoading = true;
        Log.d(TAG,"onUploadEventImageWebJob ");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopProgressBar();
                progressBar.dismiss();
            }
        });

        if (eventImageUploadResponse != null) {
            if (eventImageUploadResponse.getStatus() != null && eventImageUploadResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(addPhotosLlBtn, getString(R.string.image_uploaded_successfully), Constant.SUCCESS);
                refreshImageList();
            } else if (eventImageUploadResponse.getStatus() != null && eventImageUploadResponse.getStatus().equals(Constant.ERROR)) {
                showSnackbar(addPhotosLlBtn,
                        eventImageUploadResponse.getMessage() != null ? eventImageUploadResponse.getMessage() : getString(R.string.image_upload_failed), Constant.ERROR);
            } else {
                showSnackbar(addPhotosLlBtn, getString(R.string.image_upload_failed), Constant.SUCCESS);
            }
        } else {
            showSnackbar(addPhotosLlBtn, getString(R.string.image_upload_failed), Constant.ERROR);
        }
    }

    private void refreshImageList() {
        getEventDetails(event.getEventid());
    }

    @Override
    public void onBackPressed() {
        setResult(PlacesActivity.RESULT_OK);
        this.finish();
        super.onBackPressed();
    }

    private void startNavigationtoPlace() {

        double lat = event.getLatitude();
        double lang = event.getLongitude();
        String labelLocation = event.getTitle();

        //String packageName = "com.google.android.apps.maps";
        //String query = "google.navigation:q=" + lat + "," + lang;

        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + lat  + ">,<" + lang + ">?q=<" + lat  + ">,<" + lang + ">(" + labelLocation + ")"));
        //intent.setPackage(packageName);
        startActivity(intent);
    }

    private void likeEvent() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostLikeEventAsFriendWebJob(accessToken, event.getEventid()));
            startProgressBar();
        } else {
            showSnackbar(likeBtnIv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostLikeEventAsFriendWebJobCompleted(HttpResponseLikeUnlike httpResponseLike) {
        if (httpResponseLike != null) {
            if (httpResponseLike.getStatus() != null && httpResponseLike.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "@Subscribe success00: " + isCurrentlyLiked);
                setLikeButtonImage(httpResponseLike.is_like());
                noOfLikesTv.setText(String.valueOf(httpResponseLike.getLike_count()));
                Log.d(TAG, "@Subscribe success01: " + isCurrentlyLiked);

                //Helper.syncDbWithEvents(accessToken, this);
                jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
            } else {
                stopProgressBar();
                Log.d(TAG, "@Subscribe error1: " + isCurrentlyLiked);
                isCurrentlyLiked = !isCurrentlyLiked;
                setLikeButtonImage(isCurrentlyLiked);
                Log.d(TAG, "@Subscribe error2: " + isCurrentlyLiked);
            }
        } else {
            stopProgressBar();
            Log.d(TAG, "@Subscribe error11: " + isCurrentlyLiked);
            isCurrentlyLiked = !isCurrentlyLiked;
            setLikeButtonImage(isCurrentlyLiked);
            Log.d(TAG, "@Subscribe error22: " + isCurrentlyLiked);
        }
    }

    private void syncDbWithEvents() {

    }

    private void setDataToUI() {


            if (event != null) {
                eventHostNameTv.setText(event.getFirst_name());
                noOfCommentTv.setText(String.valueOf(String.valueOf(event.getCountComments())));
                noOfLikesTv.setText(String.valueOf(String.valueOf(event.getCountLikes())));
                eventNameTv.setText(event.getTitle());
                eventAudienceTv.setText(getEventAudience(event.getType()));

                setLikeButtonImage(event.isLike());
                isCurrentlyLiked = event.isLike();
                eventActivityTypeTv.setText(event.getTierName());
                eventActivityTv.setText(event.getInterestName());

                setDates(event.getStart_date(), event.getEnd_date());

                if (event.getLive() == 1) {
                    eventStatusTv.setText("Live");
                } else if (event.getLive() == 2) {
                    eventStatusTv.setText("Cancelled");
                } else {
                    eventStatusTv.setText("Event is Over");
                }

                eventStatusTv.setTextColor(event.getLive() == 1 ? ContextCompat.getColor(this, R.color.interstGreen) : ContextCompat.getColor(this, R.color.red));
                rsvp = 0;
                if (event.getAttendeeList() != null) {
                    for (AttendeeList attendeeList : event.getAttendeeList()) {
                        if (profileResponse.getData().getUser_id() == attendeeList.getUserId()) {
                            rsvp = attendeeList.getRsvp();
                            Log.d(TAG, attendeeList.toString());
                            break;
                        }
                    }
                }


                Log.d(TAG, "RSVP d:" + rsvp);

                eventHostTv.setVisibility(View.GONE);

                if (event.getLive() == 0) {

                } else if (event.getLive() == 1) {
                    eventHostTv.setVisibility(View.GONE);

//            Log.d(TAG, "Evetnt live");
//
//            Log.d(TAG + "  getMyRsvp ", item.getRsvp().toString());
//            Log.d(TAG + "  isHost ", String.valueOf(item.isHost()));

                    if (event.isHost()) {

                        eventHostTv.setVisibility(View.VISIBLE);
                        eventHostTv.setText("HOST / ");


                    } else if (rsvp == 3) {

                        Log.d(TAG + " inside going-- ", event.getRsvp().toString());


                        eventHostTv.setText("Going / ");
                        eventHostTv.setTextColor(Color.parseColor("#4C8C5F"));
                        eventHostTv.setVisibility(View.VISIBLE);


                    } else if (rsvp == 5) {

                        Log.d(TAG + " inside maybe-- ", event.getRsvp().toString());


                        eventHostTv.setText("MayBe / ");
                        eventHostTv.setTextColor(Color.parseColor("#B79428"));
                        eventHostTv.setVisibility(View.VISIBLE);

                    } else if (rsvp == 4) {

                        Log.d(TAG + " inside notgoing --", event.getRsvp().toString());


                        eventHostTv.setText("Not Going / ");
                        eventHostTv.setTextColor(Color.parseColor("#ff0000"));
                        eventHostTv.setVisibility(View.VISIBLE);

                    } else if (rsvp == 2) {

                        Log.d(TAG + " inside interested --", event.getRsvp().toString());


                        eventHostTv.setText("Interested / ");
                        eventHostTv.setTextColor(Color.parseColor("#727272"));
                        eventHostTv.setVisibility(View.VISIBLE);

                    } else if (rsvp == 1) {

                        Log.d(TAG + " inside invited --", event.getRsvp().toString());


                        eventHostTv.setText("Invited / ");
                        eventHostTv.setTextColor(Color.parseColor("#727272"));
                        eventHostTv.setVisibility(View.VISIBLE);
                    } else {
                        eventHostTv.setVisibility(View.GONE);
                    }
                }


                //eventLocationTv.setText(event.getPlaceName()+" "+event.getLocation()+" "+event.getDetailedaddress() );
                Log.d(TAG, "placename: " + event.getPlaceName());
                Log.d(TAG, "location: " + event.getLocation());
                Log.d(TAG, "detailed address: " + event.getDetailedaddress());

                if (event.getPlaceName() != null) {
                    eventPlaceNameTv.setText(event.getPlaceName().toString());
                } else {
                    eventPlaceNameLl.setVisibility(View.GONE);
                }

                if (event.getLocation() != null) {
                    eventLocationLl.setVisibility(View.VISIBLE);
                    eventLocationTv.setText(event.getLocation().toString());
                }

                if (event.getDetailedaddress() != null) {
                    eventDetailedAddressLl.setVisibility(View.VISIBLE);
                    eventDetailedAddressTv.setText(event.getDetailedaddress().toString());
                }

                publicPrivateIv.setImageDrawable(getAudienceDrawable(event.getType()));

                eventAudienceTv2.setText(getEventAudience(event.getType()));
                //eventAudienceDetailsTv.setText(getAudienceDescription(event.getType()) + "");


                if(event.getAttendeeList()!=null){
                    if (event.getMax_attendees() <= event.getAttendeeList().size()) {
                        eventAttendeesLl.setVisibility(View.GONE);
                        eventFullLl.setVisibility(View.VISIBLE);
                    } else {
                        eventAttendeesLl.setVisibility(View.VISIBLE);
                        eventFullLl.setVisibility(View.GONE);
                        noInvitedPplTv.setText(String.valueOf(event.getAttendeeList().size()));
                        noMaxAttendeesTv.setText(String.valueOf(event.getMax_attendees()));
                    }
                }


                eventDetailsTv.setText(event.getDescription());
                //setMyEventStatus(event.getRsvp());

                setMyEventStatus(String.valueOf(rsvp));

                String photoUrlPoster = Constant.EVENT_POSTER_BASE_URL + event.getEvent_poster();
                String photoUrlProfilePic = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                        Constant.PROFILE_PIC_BASE_URL + event.getProfilephoto();

//            userImageBackgroundIv
//                    eventPosterIv

                Picasso.with(getApplicationContext())
                        .load(photoUrlProfilePic)
                        .placeholder(R.drawable.ico_profile_edit_avatar)
                        .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                        .into(hostProfilePicIv);

                Picasso.with(getApplicationContext())
                        .load(photoUrlPoster)
                        .placeholder(R.drawable.ico_profile_edit_avatar)
                        .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                        .into(userImageBackgroundIv);

                Log.d(TAG, "Event details picasso");
                Picasso.with(getApplicationContext())
                        .load(photoUrlPoster)
                        .centerCrop()
                        .fit()
                        .placeholder(R.drawable.ico_profile_edit_avatar)
                        .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                        .into(eventPosterIv);


//            noResponceCb
//                    goingCb
//            notGoingCb
//                    maybeCb
//            addPhotosLlBtn
//                    invitedDetailsLlBtn
                if (event.getAttendeeList() != null) {
                    setEventAttendees(event);
                }


                getEventDetails(event.getEventid());

            }
    }

    private void getEventDetails(int eventid) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventInfoWebJob(accessToken, eventid));
        } else {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventInfoWebJob(EventInfoResponse eventInfoResponse) {
        Log.d(TAG, "onGetEventInfoWebJob()");
        if (eventInfoResponse != null) {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse!=null");
            if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null");
                setDataToPhotoList(eventInfoResponse.getData().get(0).getPhotos());
            } else {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE");
                showSnackbar(likeBtnIv, getString(R.string.failed_to_load_images), Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE ELSE");
            showSnackbar(likeBtnIv, getString(R.string.failed_to_load_images), Constant.ERROR);
        }
    }

    private void setDataToPhotoList(List<Photos> photos) {
        photosEventAdapter = new PhotosEventRecyclerAdapter(this, photos);
        photosRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosRv.setAdapter(photosEventAdapter);
        photosRv.setItemAnimator(new DefaultItemAnimator());
        photosEventAdapter.notifyDataSetChanged();

        photosEventAdapter.setOnClick(new PhotosEventRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, boolean isAccept, Photos item) {
                sendToEventPhotosListActivity();
            }


        });
    }

    private void sendToEventPhotosListActivity() {
        Intent intent = new Intent(this, EventPhotosListActivity.class);
        intent.putExtra(Constant.EVENT_ID, this.event.getEventid());
        startActivity(intent);
    }

    private void setLikeButtonImage(boolean isLike) {
        Log.d(TAG, "setLikeButtonImage() " + isLike);
        Drawable drawable = null;
        if (isLike) {
            drawable = ContextCompat.getDrawable(this, R.drawable.ico_event_like_active);
        } else {
            drawable = ContextCompat.getDrawable(this, R.drawable.ico_event_like_normal);
        }
        likeBtnIv.setImageDrawable(drawable);
    }

    private void setDates(String start_day, String end_date1) {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateStart = null;
        Date dateEnd = null;
        try {
            dateStart = format.parse(start_day);
            dateEnd = format.parse(start_day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dateStart);

        Calendar cal = Calendar.getInstance();
        cal.setTime(dateStart);

        SimpleDateFormat monthDate = new SimpleDateFormat("MMMM");
        String monthName = monthDate.format(c.getTime());
        SimpleDateFormat monthDateShort = new SimpleDateFormat("MMM");
        String monthNameShort = monthDateShort.format(c.getTime());
        monthNameShort = WordUtils.capitalize(monthNameShort.toLowerCase());

        SimpleDateFormat monthDateEnd = new SimpleDateFormat("MMMM");
        String monthNameEnd = monthDate.format(cal.getTime());
        SimpleDateFormat monthDateShortEnd = new SimpleDateFormat("MMM");
        String monthNameShortEnd = monthDateShort.format(cal.getTime());
        monthNameShortEnd = monthNameShort.toLowerCase();

        int day = c.get(Calendar.DATE);
        int year = c.get(Calendar.YEAR);
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        int am_pm = c.get(Calendar.AM_PM);

        int dayEnd = c.get(Calendar.DATE);
        int yearEnd = c.get(Calendar.YEAR);
        int hourEnd = c.get(Calendar.HOUR);
        int minuteEnd = c.get(Calendar.MINUTE);
        int am_pmEnd = c.get(Calendar.AM_PM);

        eventDateTv.setText(String.valueOf(day));
        eventMonthTv.setText(monthNameShort);

        String dateString = dayEnd + " " + monthNameEnd + " @ " + hourEnd + "." + minuteEnd + " " + am_pmEnd;
        Log.d(TAG, "Complete Date: " + dateString);
        eventTotalDateTv.setText(dateString);

    }


    private void setMyEventStatus(String rsvp) {
        int rsvpInt = -1;
        Log.d(TAG,"setMyEventStatus rsvp="+rsvp);
        try {
            rsvpInt = Integer.parseInt(rsvp);
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d(TAG,"setMyEventStatus exception: "+ ex.getClass().getCanonicalName());
        }
        switch (rsvpInt) {
            case 1:
                noResponceCb.setChecked(true);
                goingCb.setChecked(false);
                notGoingCb.setChecked(false);
                maybeCb.setChecked(false);
                myEventStatusTv.setText("No Response");
                break;
            case 2:
                noResponceCb.setChecked(true);
                goingCb.setChecked(false);
                notGoingCb.setChecked(false);
                maybeCb.setChecked(false);
                myEventStatusTv.setText("No Response");
                break;
            case 3:
                goingCb.setChecked(true);
                noResponceCb.setChecked(false);
                notGoingCb.setChecked(false);
                maybeCb.setChecked(false);
                myEventStatusTv.setText("Going");
                break;
            case 4:
                notGoingCb.setChecked(true);
                goingCb.setChecked(false);
                noResponceCb.setChecked(false);
                maybeCb.setChecked(false);
                myEventStatusTv.setText("Not Going");
                break;
            case 5:
                maybeCb.setChecked(true);
                notGoingCb.setChecked(false);
                goingCb.setChecked(false);
                noResponceCb.setChecked(false);
                myEventStatusTv.setText("Maybe");
                break;
            default:
                noResponceCb.setChecked(true);
                notGoingCb.setChecked(false);
                goingCb.setChecked(false);
                maybeCb.setChecked(false);
                myEventStatusTv.setText("No Response");
                break;
        }

    }


    private String getAudienceDescription(int type) {
        Log.d(TAG, "getAudienceDescription() type: " + type);
        switch (type) {
            case 0:
                return "Anyone in your neighbourhood and friends in meeof," +
                        "can see it. It can be shared freely by anyone";
            case 1:
                return "Only people invited by name can see it. No possibility" +
                        "by anyone to share it further";
            case 2:
                return "Only your friends in meeof can see it, with the option" +
                        "for the organizer to share on social networks";
            default:
                return "Default";
        }
    }

    private Drawable getAudienceDrawable(int type) {
        switch (type) {
            case 0:
                return ContextCompat.getDrawable(this, R.drawable.ico_eventnew_public_normal);
            case 1:
                return ContextCompat.getDrawable(this, R.drawable.ico_eventnew_friends_normal);
            case 2:
                return ContextCompat.getDrawable(this, R.drawable.ico_eventnew_private_normal);
            default:
                return ContextCompat.getDrawable(this, R.drawable.ico_eventnew_public_normal);
        }
    }

    private String getEventAudience(int type) {
        switch (type) {
            case 0:
                return "Public";
            case 1:
                return "Friends";
            case 2:
                return "Private";
            default:
                return "N/A";
        }
    }

    private void setEventAttendees(Event event) {
        int profileCount = event.getAttendeeList().size();
        String imgeUrl1 = "";
        String imgeUrl2 = "";
        String imgeUrl3 = "";
        if (profileCount == 0) {
            going_people1_imageView.setVisibility(View.GONE);
            going_people2_imageView.setVisibility(View.GONE);
            going_people3_imageView.setVisibility(View.GONE);
        } else if (profileCount == 1) {
            going_people1_imageView.setVisibility(View.VISIBLE);
            going_people2_imageView.setVisibility(View.GONE);
            going_people3_imageView.setVisibility(View.GONE);
            imgeUrl1 = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + event.getAttendeeList().get(0).getProfilephoto();
            Picasso.with(this.getApplicationContext())
                    .load(imgeUrl1)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                    .into(going_people1_imageView);
        } else if (profileCount == 2) {
            going_people1_imageView.setVisibility(View.VISIBLE);
            going_people2_imageView.setVisibility(View.VISIBLE);
            going_people3_imageView.setVisibility(View.GONE);
            imgeUrl1 = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + event.getAttendeeList().get(0).getProfilephoto();
            Picasso.with(this.getApplicationContext())
                    .load(imgeUrl1)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                    .into(going_people1_imageView);
            imgeUrl2 = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + event.getAttendeeList().get(1).getProfilephoto();
            Picasso.with(this.getApplicationContext())
                    .load(imgeUrl2)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                    .into(going_people2_imageView);

        } else if (profileCount >= 3) {
            going_people1_imageView.setVisibility(View.VISIBLE);
            going_people2_imageView.setVisibility(View.VISIBLE);
            going_people3_imageView.setVisibility(View.VISIBLE);
            imgeUrl1 = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + event.getAttendeeList().get(0).getProfilephoto();
            Picasso.with(this.getApplicationContext())
                    .load(imgeUrl1)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                    .into(going_people1_imageView);
            imgeUrl2 = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + event.getAttendeeList().get(1).getProfilephoto();
            Picasso.with(this.getApplicationContext())
                    .load(imgeUrl2)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                    .into(going_people2_imageView);
            imgeUrl3 = event.getProfilephoto() == null || event.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + event.getAttendeeList().get(2).getProfilephoto();
            Picasso.with(this.getApplicationContext())
                    .load(imgeUrl3)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar))
                    .into(going_people3_imageView);

        }
    }

    private void sendToInvitedFriendsEventActivity() {
        Intent intent = new Intent(this, RSVPFriendsEventActivity.class);
        if (event != null) {
            Bundle args = new Bundle();
            args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) event);
            intent.putExtra("BUNDLE_EVENT", args);
        }
        startActivityForResult(intent, RC_REFRESH_EVENTS_DATA);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                showSnackbar(going_people1_imageView, "check eror 1", Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(going_people1_imageView, "check error 2", Constant.ERROR);
        }
    }

    public void syncEventToDb(String events, String attendance){
        jobManager.addJobInBackground(new EventsSyncJob(events, attendance));
    }

    @Subscribe
    public void onEventsSyncJobCompleted(EventsSyncJobCompleted eventsSyncJobCompleted) {
        Log.d(TAG, "onEventsSyncJobCompleted");
        stopProgressBar();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.BACKGROUND)
    public void onReloadEvenData(ReloadEventData reloadEventData) {
        Log.d(TAG, "onReloadEvenData");
        try{
            Log.d(TAG, "onReloadEvenData original getEventid "  + event.getEventid());

            if(reloadEventData.getEvent() != null){
                event = reloadEventData.getEvent();
            }else{
                event = DBHelper.getInstance(this).getEventByEventId(String.valueOf(event.getEventid()));
            }

            Log.d(TAG, "onReloadEvenData getCountLikes "  + event.getCountLikes());
            Log.d(TAG, "onReloadEvenData getCountComments "  + event.getCountComments());
            Log.d(TAG, "onReloadEvenData getAttendeeList "  + event.getAttendeeList().size());
            Log.d(TAG, "onReloadEvenData getEventid "  + event.getEventid());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setDataToUI();
                }
            });
        }catch (IllegalStateException e){
            e.printStackTrace();
            showSnackbar(eventHostNameTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
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
}
