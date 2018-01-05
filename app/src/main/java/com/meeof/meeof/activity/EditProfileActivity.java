package com.meeof.meeof.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.meeof.meeof.R;
import com.meeof.meeof.fragment.CustomMapFragment;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.Country;
import com.meeof.meeof.model.EditProfileGooglePlaceIdResponse;
import com.meeof.meeof.model.EditProfileInfo;
import com.meeof.meeof.model.GooglePlaceIdResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.ProfileSaveResponse;
import com.meeof.meeof.model.UpdateProfilePictureResponse;
import com.meeof.meeof.model.countries_dto.CountriesResponse;
import com.meeof.meeof.model.countries_dto.Data;
import com.meeof.meeof.model.image_upload_dto.ImageUploadResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.GetImageFromGallery;
import com.meeof.meeof.webjob.EditProfileGetPlaceIdWebJob;
import com.meeof.meeof.webjob.GetCountriesListWebJob;
import com.meeof.meeof.webjob.GetPlaceIdWebJob;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.GetSocialAccountPictureWebJob;
import com.meeof.meeof.webjob.PostEditProfileInfoWebJob;
import com.meeof.meeof.webjob.UpdateProfilePictureWebJob;
import com.meeof.meeof.webjob.UploadImageWebJob;
import com.rabbitmq.client.AMQP;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class EditProfileActivity extends BaseActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnCameraMoveStartedListener {

    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private static final int PLACE_REQUEST_CODE = 9393;
    private static final int SELECT_COUNTRY_RC = 8778;
    private LinearLayout facebookPictureLlBtn;
    private LinearLayout googlePictureBtn;
    private LinearLayout cameraOpenLlBtn;
    private LinearLayout cropLlBtn;
    private LinearLayout deletePhotoLlBtn;
    private LinearLayout uploadFromGaleryLlBtn;
    private EditText editNameET;
    private EditText changeEmailET;
    private TextView selectCountryTv;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private ImageView profilePhotoIv;
    private LinearLayout maleLlBtn;
    private LinearLayout femaleLlBtn;
    private LinearLayout kmLlBtn;
    private LinearLayout milesLlBtn;
    private RelativeLayout emptyMapll;
    private LinearLayout mainView;
    private boolean isMale;
    private boolean isKM;
    private TextView maleTV;
    private TextView femaleTV;
    private TextView kmTV;
    private TextView milesTV;
    private SeekBar distanceSB;
    private TextView distanceTV;
    private TextView addressEditText;
    private GoogleMap mGoogleMap;
    private String accessToken;
    private TextView loadFromTV;
    private ImageView facebookImageView;
    private ImageView googleImageView;
    private TextView addressET;
    private boolean isImageInImageView;
    private TextView saveTvBtn;
    boolean isImageFromSocial;
    private String serverImageUrl;
    private AddressPlaceModel currentAddressPlace;
    private ProfileResponse currentProfileResponse;
    private AppCompatImageView back_button;
    private boolean isEmptyPhoto;
    private static final int GET_FROM_GALLERY = 3;
    private DownloadManager mDownloadManager;
    public long mDownloadId;
    private int currentProfileUserCountryId;
    private int actionForPermission;
    private boolean isFromSettingsActivity;
    private ScrollView userProfileScrollView;

    private Geocoder geocoder;
    private boolean hasGender;

    private com.meeof.meeof.model.profile.Data profileOriginalData;
    private String originalCountryName = "";
    private String changedPlaceAddress = "";
    private String socialProfileImg;
    private String userContryCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        if (getIntent().hasExtra(Constant.IS_FROM_SETTINGS)) {
            isFromSettingsActivity = this.getIntent().getBooleanExtra(Constant.IS_FROM_SETTINGS, false);
        }

        Log.d(TAG, "Inside Intent " + isFromSettingsActivity);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        initViews();
        getUserProfile(true);

        mDownloadManager = (DownloadManager) this.getSystemService(DOWNLOAD_SERVICE);
        mDownloadId = 0;
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mainView = (LinearLayout) findViewById(R.id.mainView);
        facebookPictureLlBtn = (LinearLayout) findViewById(R.id.facebookPictureLlBtn);
        googlePictureBtn = (LinearLayout) findViewById(R.id.googlePictureBtn);
        cameraOpenLlBtn = (LinearLayout) findViewById(R.id.cameraOpenLlBtn);
        cropLlBtn = (LinearLayout) findViewById(R.id.cropLlBtn);
        deletePhotoLlBtn = (LinearLayout) findViewById(R.id.deletePhotoLlBtn);
        uploadFromGaleryLlBtn = (LinearLayout) findViewById(R.id.uploadFromGaleryLlBtn);
        editNameET = (EditText) findViewById(R.id.editNameET);
        changeEmailET = (EditText) findViewById(R.id.changeEmailET);
        selectCountryTv = (TextView) findViewById(R.id.selectCountryTV);
        selectCountryTv.setTypeface(null, Typeface.NORMAL);
        profilePhotoIv = (ImageView) findViewById(R.id.profilePhotoIv);
        addressEditText = (TextView) findViewById(R.id.addressET);
        loadFromTV = (TextView) findViewById(R.id.loadFromTV);
        facebookImageView = (ImageView) findViewById(R.id.facebook_imageView);
        googleImageView = (ImageView) findViewById(R.id.google_imageView);
        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        maleLlBtn = (LinearLayout) findViewById(R.id.maleLlBtn);
        femaleLlBtn = (LinearLayout) findViewById(R.id.femaleLlBtn);
        kmLlBtn = (LinearLayout) findViewById(R.id.kmLlBtn);
        emptyMapll = (RelativeLayout) findViewById(R.id.emptyMapll);
        milesLlBtn = (LinearLayout) findViewById(R.id.milesLlBtn);
        maleTV = (TextView) findViewById(R.id.maleTV);
        femaleTV = (TextView) findViewById(R.id.femaleTV);
        kmTV = (TextView) findViewById(R.id.kmTV);
        milesTV = (TextView) findViewById(R.id.milesTV);
        saveTvBtn = (TextView) findViewById(R.id.saveTvBtn);
        distanceSB = (SeekBar) findViewById(R.id.distanceSB);
        distanceTV = (TextView) findViewById(R.id.distanceTV);
        addressET = (TextView) findViewById(R.id.addressET);
        addressET.setTypeface(null, Typeface.NORMAL);
        userProfileScrollView = (ScrollView) findViewById(R.id.user_profile_scrollView);

        if (isFromSettingsActivity) {
            saveTvBtn.setText("Save");
            back_button.setVisibility(View.VISIBLE);
        }else{
            back_button.setVisibility(View.GONE);
        }

        googleImageView.setVisibility(View.INVISIBLE);
        googlePictureBtn.setEnabled(false);
        facebookImageView.setVisibility(View.INVISIBLE);
        facebookPictureLlBtn.setEnabled(false);
        loadFromTV.setVisibility(View.INVISIBLE);
        facebookPictureLlBtn.setVisibility(View.GONE);
        googlePictureBtn.setVisibility(View.GONE);

        selectCountryTv.setOnClickListener(this);
        cameraOpenLlBtn.setOnClickListener(this);
        uploadFromGaleryLlBtn.setOnClickListener(this);
        cropLlBtn.setOnClickListener(this);
        deletePhotoLlBtn.setOnClickListener(this);
        editNameET.setOnClickListener(this);
        changeEmailET.setOnClickListener(this);
        back_button.setOnClickListener(this);
        googlePictureBtn.setOnClickListener(this);
        facebookPictureLlBtn.setOnClickListener(this);
        maleLlBtn.setOnClickListener(this);
        femaleLlBtn.setOnClickListener(this);
        kmLlBtn.setOnClickListener(this);
        milesLlBtn.setOnClickListener(this);
        addressEditText.setOnClickListener(this);
        saveTvBtn.setOnClickListener(this);
        distanceSB.setOnSeekBarChangeListener(this);

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.mapFragment);
//        mapFragment.getMapAsync(this);


//        userProfileScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                Log.wtf(TAG, "userProfileScrollView dragged");
//                // DO SOMETHING WITH THE SCROLL COORDINATES
//            }
//        });



        CustomMapFragment mapFragment = (CustomMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);

        mapFragment.getMapAsync(this);

        mapFragment.setListener(new CustomMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                //hey userProfileScrollView, do not intercept the map view when it is touched
                userProfileScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cameraOpenLlBtn:
                actionForPermission = 1;
                cameraIntent();
                break;
            case R.id.uploadFromGaleryLlBtn:
                actionForPermission = 2;
                galleryIntent();
                break;
            case R.id.cropLlBtn:
                actionForPermission = 3;
                cropImage();
                break;
            case R.id.deletePhotoLlBtn:
                deletePhoto();
                break;
            case R.id.selectCountryTV:
                sendToSelectCountryActivity();
                break;
            case R.id.googlePictureBtn:
                getSocialPictureUrl(Constant.GRANT_TYPE_GOOGLE);
                break;
            case R.id.facebookPictureLlBtn:
                getSocialPictureUrl(Constant.GRANT_TYPE_FB);
                break;
            case R.id.maleLlBtn:
                MaleFemaleToggle(Constant.MALE);
                break;
            case R.id.femaleLlBtn:
                MaleFemaleToggle(Constant.FEMALE);
                break;
            case R.id.milesLlBtn:
                KmMilesToggle(Constant.MILES);
                break;
            case R.id.kmLlBtn:
                KmMilesToggle(Constant.KM);
                break;
            case R.id.addressET:
                if (selectCountryTv.getText().equals("")) {
                    showSnackbar(selectCountryTv, "Please select a Country first", Constant.ERROR);
                } else {
                    sendToPlacesAutocomplteActivity();
                }
                break;

            case R.id.saveTvBtn:
                Log.wtf(TAG,"saveTvBtn pressed");
                Log.wtf(TAG,"saveTvBtn pressed hasGender: " + hasGender);
                Log.wtf(TAG,"saveTvBtn pressed addressET: " + addressET.getText());

                if (!selectCountryTv.getText().equals("") && !addressET.getText().equals("") && hasGender) {
                    Log.wtf(TAG,"saveTvBtn pressed 2");
                    postEditProfileInfo();
                } else {
                    Log.wtf(TAG,"saveTvBtn pressed 3");
                    showSnackbar(selectCountryTv, "Please fill all fields in your profile", Constant.ERROR);
                }
                break;

            case R.id.backAcIvBtn:

                //Validate profileOriginalData With Filled Screen Data
                if (hasUnsubmittedProfileChanges()){
                    //Profile data has unsubmitted changes
                    //Todo Add Custom Conformation Dialog
                    Log.d(TAG,"Profile has unsubmitted changes");
                    showConfirmationAlert(this,"Discard Unsaved Data",  "CANCEL");
                }
                else{
                    this.onBackPressed();
                }



                break;

        }
    }

    public void showConfirmationAlert(Context c, String title, final String type) {
        try {
            final Dialog dialog = new Dialog(c);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_discard_unsaved_changes_confirmation);
            dialog.setCancelable(false);


            Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            Button confirmBtn = (Button) dialog.findViewById(R.id.confirmBtn);


            Log.d(TAG,"postEditProfileInfo0");

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"postEditProfileInfo1");
                    dialog.cancel();

                    if (!selectCountryTv.getText().equals("") && !addressET.getText().equals("") && hasGender) {
                        postEditProfileInfo();
                    } else {
                        showSnackbar(selectCountryTv, "Please fill all fields in your profile", Constant.ERROR);
                    }

                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"postEditProfileInfo2");

                    dialog.cancel();
                    EditProfileActivity.this.onBackPressed();

                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToSelectCountryActivity() {
        Intent intent = new Intent(this, SelectCountryActivity.class);
        if (selectCountryTv.getText().toString() != null && selectCountryTv.getText().length() > 0) {
            intent.putExtra("SELECTED_COUNTRY", selectCountryTv.getText().toString());
        }
        startActivityForResult(intent, SELECT_COUNTRY_RC);
    }


    private void postEditProfileInfo() {
        Log.wtf(TAG,"postEditProfileInfo3");
        String[] selectedCountry = ((String) selectCountryTv.getText()).split("\\s+");
        String selectedCountryAlphaCode = selectedCountry[selectedCountry.length - 1].trim();

        Log.wtf(TAG,"postEditProfileInfo4");

        if (isValidName()) {
            Log.wtf(TAG,"postEditProfileInfo5");
            if (isEmailValid()) {
                Log.wtf(TAG,"postEditProfileInfo6");
                String email = changeEmailET.getText().toString();
                String fullName = editNameET.getText().toString();

                String address;
                String placeId;
                String placeName;
                String latitude;
                String longitude;

                int gender = isMale ? 1 : 0;
                String matrix = isKM ? "0" : "1";
                Log.wtf(TAG,"postEditProfileInfo7: " + isKM);
                Log.wtf(TAG,"postEditProfileInfo matrix: " + matrix);
                Log.wtf(TAG,"postEditProfileInfo getProgress: " +  distanceSB.getProgress());

                String acceptedDistance;

                if(isKM){
                    acceptedDistance = String.valueOf(getMilesKmValueForSeek(Constant.KM, distanceSB.getProgress()));
                }else{
                    acceptedDistance = String.valueOf(getMilesKmValueForSeek(Constant.MILES, distanceSB.getProgress()));
                }



                if (currentAddressPlace != null) {
                    address = currentAddressPlace.getAddniceaddress();
                    placeId = currentAddressPlace.getPlaceID();
                    placeName = currentAddressPlace.getPlaceName();
                    latitude = currentAddressPlace.getLat();
                    longitude = currentAddressPlace.getLng();

                } else {
                    address = currentProfileResponse.getData().getAddress();
                    placeId = currentProfileResponse.getData().getPlaceID();
                    placeName = currentProfileResponse.getData().getPlaceName();
                    latitude = currentProfileResponse.getData().getLatitude() + "";
                    longitude = currentProfileResponse.getData().getLongitude() + "";
                }

                EditProfileInfo editProfileInfo = new EditProfileInfo();

                editProfileInfo.setCountry(selectedCountryAlphaCode);
                editProfileInfo.setEmail(email);
                editProfileInfo.setFullName(fullName);
                editProfileInfo.setAddress(address);
                editProfileInfo.setPlaceId(placeId);
                editProfileInfo.setPlaceName(placeName);
                editProfileInfo.setLat(latitude);
                editProfileInfo.setLng(longitude);
                editProfileInfo.setGender(gender);
                editProfileInfo.setMatrix(matrix);
                editProfileInfo.setAcceptedDistance(acceptedDistance);

                Log.wtf(TAG,"postEditProfileInfo7");

                Log.wtf(TAG + "Entered gerder--", String.valueOf(editProfileInfo.getGender()));

                if (isNetworkAvailable()) {
                    Log.wtf(TAG,"postEditProfileInfo8");
                    jobManager.addJobInBackground(new PostEditProfileInfoWebJob(accessToken, editProfileInfo));
                    startProgressBar();
                } else {
                    showSnackbar(profilePhotoIv, getString(R.string.no_internet), Constant.ERROR);
                }
            } else {
                showSnackbar(profilePhotoIv, getString(R.string.non_valid_email), Constant.ERROR);
            }
        } else {
            showSnackbar(profilePhotoIv, getString(R.string.invalid_name), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostEditProfileInfoWebJobCompleted(ProfileSaveResponse profileSaveResponse) {
        Log.wtf(TAG,"postEditProfileInfo9");

        stopProgressBar();
        if (profileSaveResponse != null) {
            if (profileSaveResponse.getStatus() != null && profileSaveResponse.getStatus() != null) {

                if(profileSaveResponse.getStatus().equalsIgnoreCase(Constant.SUCCESS)){

                    //showSnackbar(profilePhotoIv, profileSaveResponse.getMessage().replaceAll("(<br/>)+$", "").toString(), Constant.SUCCESS);
                    if(profileSaveResponse.getMessage().contains("have changed your email address")){
                        showSnackbar(profilePhotoIv, profileSaveResponse.getMessage().replaceAll("(<br/>)+$", "").toString(), Constant.SUCCESS);
                    }else{
                        showSnackbar(profilePhotoIv, "We have updated your profile", Constant.SUCCESS);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Helper.delay(3000, new Helper.DelayCallBack() {
                                @Override
                                public void postDelay() {
                                    Log.d(TAG, "sendToInterestsActivity ");
                                    if (isFromSettingsActivity) {
                                        EditProfileActivity.this.finish();
                                    } else {
                                        sendToInterestsActivity();
                                    }
                                }
                            });
                        }
                    });

                } else {

                    if(profileSaveResponse.getMessage() != null){
                        showSnackbar(profilePhotoIv, profileSaveResponse.getMessage(), Constant.ERROR);
                    }else{
                        showSnackbar(profilePhotoIv, Constant.ERROR, Constant.ERROR);
                    }
                }
            } else {
                showSnackbar(profilePhotoIv, Constant.ERROR, Constant.ERROR);
            }
        } else {
            showSnackbar(profilePhotoIv, Constant.ERROR, Constant.ERROR);
        }
    }

    private void sendToInterestsActivity() {
        Intent intent = new Intent(this, InterestsActivity.class);
        startActivity(intent);
    }

    private void getUserProfile(boolean shouldLoadWithProgress) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
            if (shouldLoadWithProgress) {
                startProgressBar();
            }
        } else {
            showSnackbar(profilePhotoIv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetuserProfileWebJobCompleted(final ProfileResponse profileResponse) {
        stopProgressBar();
        if (profileResponse != null) {

            if (profileResponse.getStatus() != null && profileResponse.getStatus().equals(Constant.SUCCESS)) {
                currentProfileResponse = profileResponse;

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isFacebookLogged = profileResponse.getFacebook() != null ? true : false;
                        boolean isGoogleLogged = profileResponse.getGoogle() != null ? true : false;
                        activateSocialPhotoButton(isFacebookLogged, isGoogleLogged);
                    }
                });

                currentProfileUserCountryId = profileResponse.getData().getCountry_id();
                setDataToEditProfile(profileResponse.getData());
                getCountryList();
            } else {
                showSnackbar(profilePhotoIv, getString(R.string.profile_info_receiving_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(profilePhotoIv, getString(R.string.profile_info_receiving_failed), Constant.ERROR);
        }
    }

    private Boolean hasUnsubmittedProfileChanges(){

        Log.d(TAG,"hasUnsubmittedProfileChanges: OriginalID: "+ profileOriginalData.getAddress() + " ChangedID: "+changedPlaceAddress);
        String matrix = isKM ? "0" : "1";

        String matrix2 = isKM ? Constant.KM : Constant.MILES;
        int gender = isMale ? 1 : 0;

        if(!profileOriginalData.getFirst_name().contentEquals(editNameET.getText().toString())){
            Log.wtf(TAG,"getFirst_name true");
            return true;
        }

        if(!profileOriginalData.getEmail().contentEquals(changeEmailET.getText().toString())){
            Log.wtf(TAG,"getEmail true");
            return true;
        }

        if(!originalCountryName.contentEquals(selectCountryTv.getText().toString())){
            Log.wtf(TAG,"originalCountryName true");
            return true;
        }

        if(profileOriginalData.getGender() != gender){
            Log.wtf(TAG,"getGender true");
            return true;
        }

        if(!profileOriginalData.getMatrix().equalsIgnoreCase(matrix)){
            Log.wtf(TAG,"getMatrix true");
            Log.wtf(TAG,"getMatrix : " + matrix);
            Log.wtf(TAG,"getMatrix profileOriginalData: " + profileOriginalData.getMatrix());
            return true;
        }

        if(profileOriginalData.getPlaceName() != null) {
           if(!profileOriginalData.getPlaceName().contentEquals(addressET.getText().toString())){
               Log.wtf(TAG,"getPlaceName true");
               Log.wtf(TAG,"getPlaceName profileOriginalData: " + profileOriginalData.getPlaceName());
               Log.wtf(TAG,"getPlaceName addressET: " + addressET.getText().toString());
               return true;
           }
        }else{
            if(!profileOriginalData.getAddress().contentEquals(addressET.getText().toString())){
                Log.wtf(TAG,"getAddress true");
                Log.wtf(TAG,"getAddress profileOriginalData: " + profileOriginalData.getAddress());
                Log.wtf(TAG,"getAddress addressET: " + addressET.getText().toString());
                return true;
            }
        }


        if(profileOriginalData.getAccept_distance() != getMilesKmValueForSeek(matrix2, distanceSB.getProgress())){
            Log.wtf(TAG,"getAccept_distance true");
            return true;
        }


        Log.d(TAG,"hasUnsubmittedProfileChanges return false");
        return false;
    }

    private void setDataToEditProfile(final com.meeof.meeof.model.profile.Data data) {

        profileOriginalData = data;

        Log.wtf(TAG,"ProfileOriginalData: "+profileOriginalData.getPlaceName());

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (data.getFirst_name() != null) {
                    if (data.getLast_name() != null) {
                        editNameET.setText(data.getFirst_name() + " " + data.getLast_name());
                    } else {
                        editNameET.setText(data.getFirst_name());
                        editNameET.setSelection(editNameET.getText().length());
                    }
                } else {
                    editNameET.setText("");
                }

                if (data.getEmail() != null) {
                    changeEmailET.setText(data.getEmail());
                } else {
                    changeEmailET.setText("");
                }

                if (data.getAddress() != null) {
                    addressET.setText(data.getAddress());
                } else {
                    addressET.setText("");
                }

                Log.d(TAG + "gender-", String.valueOf(data.getGender()));
                if (data.getGender() != 2) {

                    if (data.getGender() == 1) {
                        MaleFemaleToggle(Constant.MALE);
                        // maleLlBtn.callOnClick();
                    } else if (data.getGender() == 0) {
                        MaleFemaleToggle(Constant.FEMALE);
                        // femaleLlBtn.callOnClick();
                    }

                } else {
                    hasGender = false;
                    MaleFemaleToggle(Constant.MALE);
                }

                if (data.getMatrix().equals("0")) {
                    kmLlBtn.callOnClick();
                } else if (data.getMatrix().equals("1")) {
                    milesLlBtn.callOnClick();
                }


                if(data.getPlaceName() != null){
                    addressET.setText(data.getPlaceName());
                }else if(data.getAddress() != null && data.getAddress().length() > 0){
                    addressET.setText(data.getAddress());
                }

                String photoUrl = data.getProfilephoto() == null || data.getProfilephoto().trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                        Constant.PROFILE_PIC_BASE_URL + data.getProfilephoto();

                isEmptyPhoto = photoUrl.equals(Constant.DEFAULT_AVATAR_URL) ? true : false;

                Log.wtf(TAG, "First Logging profile pic: " + photoUrl);
                Log.wtf(TAG, "First Logging profile pic:isEmptyPhoto " + isEmptyPhoto);
                Log.wtf(TAG, "First Logging profile pic 2: " + data.getProfilephoto());
                Log.wtf(TAG, "First Logging profile getFacebook: " +  currentProfileResponse.getFacebook());

                if (isEmptyPhoto) {

                    if(socialProfileImg != null && socialProfileImg.length() > 0){

                        isImageInImageView = true;

                        Picasso.with(getApplicationContext()).load(socialProfileImg)
                                .placeholder(R.drawable.ico_profile_edit_avatar)
                                .fit()
                                .centerCrop()
                                .into(profilePhotoIv);

                    }else{
                        deletePhotoLlBtn.setVisibility(View.GONE);
                        cropLlBtn.setVisibility(View.GONE);
                        profilePhotoIv.setImageDrawable(ContextCompat.getDrawable(EditProfileActivity.this, R.drawable.ico_profile_edit_avatar));
                    }

                } else {

                    isImageInImageView = true;
                    cropLlBtn.setVisibility(View.VISIBLE);
                    deletePhotoLlBtn.setVisibility(View.VISIBLE);

                    Picasso.with(getApplicationContext()).load(photoUrl)
                            .placeholder(R.drawable.ico_profile_edit_avatar)
                            .fit()
                            .centerCrop()
                            .into(profilePhotoIv);
                }

                Log.wtf(TAG, "distanceSB getMax: " + distanceSB.getMax());
                Log.wtf(TAG, "getAccept_distance: " + data.getAccept_distance());

                if(data.getAccept_distance() == 0){
                    //default value is 5Km
                    distanceSB.setProgress(setMilesKmValueForSeek(Constant.KM, Double.valueOf(5)));
                }else{
                    if(isKM){
                        distanceSB.setProgress(setMilesKmValueForSeek(Constant.KM, data.getAccept_distance()));
                    }else{
                        distanceSB.setProgress(setMilesKmValueForSeek(Constant.MILES, data.getAccept_distance()));
                    }
                }

                if (data.getLatitude() != 0 && data.getLongitude() != 0) {
                    LatLng location = new LatLng(data.getLatitude(), data.getLongitude());
                    mGoogleMap.clear();
                    mGoogleMap.addMarker(new MarkerOptions().position(location));
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, Constant.MAP_ZOOM_LEVEL));
                }

                if(addressET.getText().toString().trim().length() > 0){
                    emptyMapll.setVisibility(View.GONE);
                }else{
                    emptyMapll.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    private void getCountryList() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetCountriesListWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(addressEditText, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCountriesJobCompleted(CountriesResponse countriesResponse) {
        stopProgressBar();
        if (countriesResponse != null) {
            if (countriesResponse.getStatus() != null) {
                if (countriesResponse.getStatus().equals(Constant.SUCCESS)) {
                    final List<Data> countryList = countriesResponse.getData();
                    final List<Country> countriesWithAlphaCode = new ArrayList<>();
                    for (Data data : countryList) {
                        Country country = new Country(data.getenglish_name(), data.getalpha2_code());
                        countriesWithAlphaCode.add(country);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "currentProfileUserCountryId: " + currentProfileUserCountryId);
                            Log.d(TAG, "currentProfileUserCountry size: " + countriesWithAlphaCode.size());

                            if(currentProfileUserCountryId > 0){

                                Country country = countriesWithAlphaCode.get(currentProfileUserCountryId -1);
                                selectCountryTv.setText(country.getEnglishName() + " " + country.getAlpha2Code());
                                originalCountryName = country.getEnglishName() + " " + country.getAlpha2Code();
                                userContryCode = country.getAlpha2Code();

                                Log.wtf(TAG,"onGetCountriesJobCompleted: userContryCode: "+ userContryCode);
                                Log.wtf(TAG,"onGetCountriesJobCompleted: updatedCountryName: "+ originalCountryName);
                            }
                        }
                    });

                } else {
                    showSnackbar(saveTvBtn, getString(R.string.country_list_load_failed), Constant.ERROR);
                }
            } else {
                showSnackbar(saveTvBtn, getString(R.string.country_list_load_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(saveTvBtn, getString(R.string.country_list_load_failed), Constant.ERROR);
        }
    }

    private void activateSocialPhotoButton(boolean isFacebookLogged, boolean isGoogleLogged) {
        String loggedInWith = sharedPreferences.getString(Constant.LOGGED_IN_WITH, "").trim();
        Log.wtf(TAG, "Logged In with: " + loggedInWith);


        if (isGoogleLogged) {
            googleImageView.setVisibility(View.VISIBLE);
            googlePictureBtn.setEnabled(true);
            loadFromTV.setVisibility(View.VISIBLE);
            googlePictureBtn.setVisibility(View.VISIBLE);
            socialProfileImg = currentProfileResponse.getGoogle();

        }
        if (isFacebookLogged) {
            facebookImageView.setVisibility(View.VISIBLE);
            facebookPictureLlBtn.setEnabled(true);
            loadFromTV.setVisibility(View.VISIBLE);
            facebookPictureLlBtn.setVisibility(View.VISIBLE);
            socialProfileImg = currentProfileResponse.getFacebook();
        }
    }


    private void uploadImageMultiPart(Uri currentImageUri) {
        if (currentImageUri != null) {
            if (isNetworkAvailable()) {
                Log.d(TAG, "uploadImageMultiPart");
                jobManager.addJobInBackground(new UploadImageWebJob(accessToken, new File(currentImageUri.toString()).getAbsolutePath()));
                startProgressBar();
            } else {
                showSnackbar(saveTvBtn, getString(R.string.no_internet), Constant.ERROR);
            }
        } else {
            showSnackbar(saveTvBtn, getString(R.string.image_upload_failed), Constant.ERROR);
        }
    }

    @Subscribe
    public void onUploadImageWebJobCompleted(ImageUploadResponse imageUploadResponse) {
        stopProgressBar();

        Log.d(TAG, "onUploadImageWebJobCompleted");

        if (imageUploadResponse != null) {
            if (imageUploadResponse.getStatus() != null && imageUploadResponse.getStatus().equals(Constant.SUCCESS)) {
                serverImageUrl = imageUploadResponse.getData().getOriginal().getNormalurl();
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        picassoImageTarget(serverImageUrl, false);

                    }
                });

            } else {
                showSnackbar(profilePhotoIv, getString(R.string.image_upload_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(profilePhotoIv, getString(R.string.image_upload_failed), Constant.ERROR);
        }
    }

    private void getSocialPictureUrl(String socialAccount) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetSocialAccountPictureWebJob(accessToken, socialAccount));
            startProgressBar();
        } else {
            showSnackbar(distanceSB, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void getSocialAccountPictureWebJob(HttpResponseData imageUrlResponse) {

        Log.d(TAG, " getSocialAccountPictureWebJob");

        if (imageUrlResponse != null) {
            if (imageUrlResponse.getStatus() != null && imageUrlResponse.getStatus().equals(Constant.SUCCESS)) {
                final String imageUrl = imageUrlResponse.getData().replaceAll("(?<!https:)\\/\\/", "/");
                Log.d(TAG, "Google/Facebook image: " + imageUrl);
                final Bitmap[] bitmapImage = {null};

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cropLlBtn.setVisibility(View.VISIBLE);
                        deletePhotoLlBtn.setVisibility(View.VISIBLE);
                        picassoImageTarget(imageUrl, true);
                        updateProfilePicture(imageUrl);
                    }
                });

            } else {
                stopProgressBar();
                showSnackbar(cameraOpenLlBtn, getString(R.string.profile_picture_update_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(cameraOpenLlBtn, getString(R.string.profile_picture_update_failed), Constant.ERROR);
        }
    }

    private void sendToPlacesAutocomplteActivity() {

        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("country", userContryCode);

        if (addressET.getText().toString() != null && addressET.getText().length() > 0) {
            intent.putExtra("SELECTED_LOCATION", addressET.getText().toString());
        }

        startActivityForResult(intent, PLACE_REQUEST_CODE);
    }

    private void deletePhoto() {

        isImageInImageView = false;
        isImageFromSocial = false;
        socialProfileImg = "";

        profilePhotoIv.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_profile_edit_avatar));

        if(!isEmptyPhoto){

            if (isNetworkAvailable()) {
                jobManager.addJobInBackground(new UpdateProfilePictureWebJob(accessToken, ""));
                startProgressBar();
            } else {
                showSnackbar(profilePhotoIv, getString(R.string.no_internet), Constant.ERROR);
            }
        }
    }

    private void updateProfilePicture(String photoUrl) {
        Log.d(TAG, "updateProfilePicture");
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new UpdateProfilePictureWebJob(accessToken, photoUrl));
            startProgressBar();
        } else {
            showSnackbar(profilePhotoIv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onUpdateProfilePictureWebJob(final UpdateProfilePictureResponse updateProfilePictureResponse) {
        Log.wtf(TAG, "onUpdateProfilePictureWebJob");

        if (updateProfilePictureResponse != null) {
            if (updateProfilePictureResponse.getStatus() != null && updateProfilePictureResponse.getStatus().contains(Constant.SUCCESS)) {
                if (updateProfilePictureResponse.getNormalurl() != null && updateProfilePictureResponse.getNormalurl().length() > 0) {
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (Boolean.parseBoolean(updateProfilePictureResponse.getStatus().split("_")[1])) {

                                getUserProfile(false);
                            }
                        }
                    });

                } else {
                    stopProgressBar();
                    showSnackbar(profilePhotoIv, getString(R.string.profile_picture_update_failed), Constant.ERROR);
                }
            } else {
                stopProgressBar();
                showSnackbar(profilePhotoIv, getString(R.string.profile_picture_update_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(profilePhotoIv, getString(R.string.profile_picture_update_failed), Constant.ERROR);
        }
    }

    private void cropImage() {

        boolean isAllow = isStoragePermissionGranted();

        if (isAllow) {
            if (isImageInImageView) {
                BitmapDrawable drawable = (BitmapDrawable) profilePhotoIv.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                Uri profileImageUrl = getImageUri(this, bitmap);
                CropImage.activity(profileImageUrl).start(this);
            } else {
                showSnackbar(profilePhotoIv, "Please select a image for profile picture", Constant.ERROR);
                Log.d(TAG, "isImageInImageView false ");
            }
        }

    }

    private void picassoImageTarget(String url, final boolean isSocialNetImage) {
        String fileName = System.currentTimeMillis() + ".jpg";
        downloadImage(url, fileName);
    }


    /**
     * Add image to imageview form file path
     **/
    private void cameraIntent() {
        boolean isAllow = isStoragePermissionGranted();
        if (isAllow) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAMERA);
        } else {
            showSnackbar(profilePhotoIv, "Please Allow External Storage Permission", Constant.ERROR);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf(TAG, "OnActivityResult1 ");
        if (resultCode == Activity.RESULT_OK) {
            Log.wtf(TAG, "OnActivityResult2 : " + requestCode);

            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);

            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                onCropImageResult(data);

            } else if (requestCode == PLACE_REQUEST_CODE) {
                onPlaceRequestResult(data);
                emptyMapll.setVisibility(View.GONE);

            } else if (requestCode == SELECT_COUNTRY_RC && data != null) {
                selectCountryTv.setText(data.getStringExtra("country"));
                Log.wtf(TAG, "OnActivityResult3: "+data.getStringExtra("country"));
            }
        }
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

        isImageInImageView = true;
        isImageFromSocial = false;
        cropLlBtn.setVisibility(View.VISIBLE);
        deletePhotoLlBtn.setVisibility(View.VISIBLE);

        uploadImageMultiPart(currentImageUri);
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

        updateProfileImageView(selectedImageBitMap);

        isImageInImageView = true;
        isImageFromSocial = false;

        cropLlBtn.setVisibility(View.VISIBLE);
        deletePhotoLlBtn.setVisibility(View.VISIBLE);

        uploadImageMultiPart(currentImageUri);

    }

    private void onCropImageResult(Intent data) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        Uri resultUri = result.getUri();
        saveImageData(resultUri);
    }


    private void onPlaceRequestResult(Intent data) {
        Log.d(TAG, "Inside PLACE_REQUEST_CODE");

        String lat = data.getStringExtra("lat");
        String lng = data.getStringExtra("lng");
        String placeName = data.getStringExtra("place_name");
        String placeAddress = data.getStringExtra("place_address");
        String placeId = data.getStringExtra("place_id");

        //Set Changed Place ID
        changedPlaceAddress = placeAddress;

        currentAddressPlace = new AddressPlaceModel();
        currentAddressPlace.setLat(lat);
        currentAddressPlace.setLng(lng);
        currentAddressPlace.setPlaceName(placeName);
        currentAddressPlace.setAddniceaddress(placeAddress);
        currentAddressPlace.setPlaceID(placeId);

        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lng);

        LatLng userLatLong = new LatLng(latitude, longitude);

        addressET.setText(placeName);

        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(userLatLong));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLong, Constant.MAP_ZOOM_LEVEL));
    }

    private void updateProfileImageView(Bitmap selectedImageBitMap) {
        profilePhotoIv.setImageBitmap(selectedImageBitMap);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    switch (actionForPermission) {
                        case 1:
                            cameraIntent();
                            break;
                        case 2:
                            galleryIntent();
                            break;
                        case 3:
                            cropImage();
                            break;
                        default:
                            showSnackbar(cropLlBtn, "Permission not granted", Constant.ERROR);
                            break;
                    }

                } else {
                    showSnackbar(cropLlBtn, "Permission not granted", Constant.ERROR);
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
                break;
        }
    }


    private void MaleFemaleToggle(String femMale) {
        if (femMale.equals(Constant.FEMALE)) {
            hasGender = true;
            isMale = false;
            femaleLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_selected));
            femaleTV.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            maleLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_unselected));
            maleTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        } else {
            hasGender = true;
            isMale = true;
            maleLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_selected));
            maleTV.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            femaleLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_unselected));
            femaleTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));
        }

    }

    private void KmMilesToggle(String mileKm) {
        if (mileKm.equals(Constant.MILES)) {
            isKM = false;
            kmLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_unselected));
            milesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_selected));
            milesTV.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            kmTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));

            distanceSB.setProgress(5);
            distanceTV.setText("3 Miles");

        } else {
            isKM = true;
            kmLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_selected));
            milesLlBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.profile_view_button_unselected));
            kmTV.setTextColor(ContextCompat.getColor(this, R.color.whiteColor));
            milesTV.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryBackground));

            distanceSB.setProgress(5);
            distanceTV.setText("5 KM");
        }


    }

    private boolean isValidName() {
        return editNameET.getText().toString().trim().length() > 0 ? true : false;
    }

    private Boolean isEmailValid() {
        return changeEmailET.getText().toString().matches(Constant.EMAIL_REGEX) ? true : false;
    }


    private Uri openFileUri(String path) {
        return Uri.parse(new File(path).toString());
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d("DEBUG", "Progress is: " + progress);

        if (!isKM) {
            Log.wtf(TAG, "Progress Miles : " + progress);
            Log.d(TAG, "Progress Miles : " + getMilesKmValueForSeek(Constant.MILES, progress));
        } else {
            Log.wtf(TAG, "Progress Km : " + progress);
            getMilesKmValueForSeek(Constant.KM, progress);
            Log.d(TAG, "Progress Km : " + getMilesKmValueForSeek(Constant.KM, progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnPoiClickListener(this);
        mGoogleMap.setMapType(1);
        //mGoogleMap.setBuildingsEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

        mGoogleMap.setOnCameraMoveStartedListener(this);

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_stype_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
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

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 70, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "profileimage", null);
        return Uri.parse(path);
    }

    private void saveImageData(Uri imageUri) {
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
        uploadImageMultiPart(imageUri);
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
                                EditProfileActivity.this.unregisterReceiver(this);

                                final String uriString = c.getString(c.getColumnIndex(
                                        DownloadManager.COLUMN_LOCAL_URI));

                                Log.d(TAG, "downloadAndPlay videoUrl:3 " + uriString);

                                EditProfileActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Uri tempUri = Uri.parse(uriString);
                                        profilePhotoIv.setImageURI(tempUri);
                                        isImageInImageView = true;
                                        stopProgressBar();
                                    }
                                });
                            }else {
                                stopProgressBar();
                            }
                        }else {
                            stopProgressBar();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        stopProgressBar();
                        Log.d(TAG, e.toString());
                    }
                }
            }
        };


        EditProfileActivity.this.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setTitle(EditProfileActivity.this.getResources().getString(R.string.app_name));
        request.setDescription(imageUrl);
        request.setDestinationInExternalFilesDir(EditProfileActivity.this, Environment.DIRECTORY_DOWNLOADS, fileName);

        mDownloadId = mDownloadManager.enqueue(request);
    }

    private double getMilesKmValueForSeek(String matrix, int progress) {
        String distaceTvValue = "";
        if (matrix.equals(Constant.KM)) {
            int progressKm = progress;
            Log.wtf(TAG, "progressKm: " + progressKm);
            if (progress == 1) {
                distaceTvValue = "100m";
                distanceTV.setText(distaceTvValue);
                return 0.1;
            } else if (progressKm == 2) {
                distaceTvValue = "250m";
                distanceTV.setText(distaceTvValue);
                return 0.25;
            } else if (progressKm == 3) {
                distaceTvValue = "500m";
                distanceTV.setText(distaceTvValue);
                return 0.5;
            } else if (progressKm == 4) {
                distaceTvValue = "750m";
                distanceTV.setText(distaceTvValue);
                return 0.75;
            } else if (progressKm == 5) {
                distaceTvValue = "1KM";
                distanceTV.setText(distaceTvValue);
                return 1;
            } else if (progressKm == 6) {
                distaceTvValue = "2KM";
                distanceTV.setText(distaceTvValue);
                return 2;
            } else if (progressKm == 7) {
                distaceTvValue = "5KM";
                distanceTV.setText(distaceTvValue);
                return 5;
            } else if (progressKm == 8) {
                distaceTvValue = "10KM";
                distanceTV.setText(distaceTvValue);
                return 10;
            } else if (progressKm == 9) {
                distaceTvValue = "15KM";
                distanceTV.setText(distaceTvValue);
                return 15;
            } else if (progressKm == 10) {
                distaceTvValue = "20KM";
                distanceTV.setText(distaceTvValue);
                return 20;
            } else if (progressKm == 11) {
                distaceTvValue = "25KM";
                distanceTV.setText(distaceTvValue);
                return 25;
            } else if (progressKm == 12) {
                distaceTvValue = "30KM";
                distanceTV.setText(distaceTvValue);
                return 30;
            } else if (progressKm == 13) {
                distaceTvValue = "40KM";
                distanceTV.setText(distaceTvValue);
                return 40;
            } else if (progressKm == 14) {
                distaceTvValue = "50KM";
                distanceTV.setText(distaceTvValue);
                return 50;
            } else if (progressKm == 15) {
                distaceTvValue = "100KM";
                distanceTV.setText(distaceTvValue);
                return 100;
            } else {
                distaceTvValue = "100m";
                distanceTV.setText(distaceTvValue);
                return 0.1;
            }
        } else {
            int progressMiles = progress;
            Log.wtf(TAG, "progressMiles: " + progressMiles);
            if (progress == 1) {
                distaceTvValue = "330 ft";
                distanceTV.setText(distaceTvValue);
                return 0.033;
            } else if (progressMiles == 2) {
                distaceTvValue = "820 ft";
                distanceTV.setText(distaceTvValue);
                return 0.082;
            } else if (progressMiles == 3) {
                distaceTvValue = "1640 ft";
                distanceTV.setText(distaceTvValue);
                return 0.1640;
            } else if (progressMiles == 4) {
                distaceTvValue = "2460 ft";
                distanceTV.setText(distaceTvValue);
                return 0.2460;
            } else if (progressMiles == 5) {
                distaceTvValue = "0.5 Miles";
                distanceTV.setText(distaceTvValue);
                return 0.5;
            } else if (progressMiles == 6) {
                distaceTvValue = "1 Mile";
                distanceTV.setText(distaceTvValue);
                return 1;
            } else if (progressMiles == 7) {
                distaceTvValue = "3 Miles";
                distanceTV.setText(distaceTvValue);
                return 3;
            } else if (progressMiles == 8) {
                distaceTvValue = "6 Miles";
                distanceTV.setText(distaceTvValue);
                return 6;
            } else if (progressMiles == 9) {
                distaceTvValue = "10 Miles";
                distanceTV.setText(distaceTvValue);
                return 10;
            } else if (progressMiles == 10) {
                distaceTvValue = "13 Miles";
                distanceTV.setText(distaceTvValue);
                return 13;
            } else if (progressMiles == 11) {
                distaceTvValue = "16 Miles";
                distanceTV.setText(distaceTvValue);
                return 16;
            } else if (progressMiles == 12) {
                distaceTvValue = "20 Miles";
                distanceTV.setText(distaceTvValue);
                return 20;
            } else if (progressMiles == 13) {
                distaceTvValue = "26 Miles";
                distanceTV.setText(distaceTvValue);
                return 26;
            } else if (progressMiles == 14) {
                distaceTvValue = "30 Miles";
                distanceTV.setText(distaceTvValue);
                return 30;
            } else if (progressMiles == 15) {
                distaceTvValue = "60 Miles";
                distanceTV.setText(distaceTvValue);
                return 60;
            } else {
                distaceTvValue = "330 ft";
                distanceTV.setText(distaceTvValue);
                return 0.033;
            }
        }
    }

    private int setMilesKmValueForSeek(String matrix, Double distance) {
        String distaceTvValue = "";

        if (matrix.equals(Constant.KM)) {
            Log.wtf(TAG, "distance: " + distance);
            if (distance == 0.1) {
                distaceTvValue = "100m";
                distanceTV.setText(distaceTvValue);
                return 1;
            } else if (distance == 0.25) {
                distaceTvValue = "250m";
                distanceTV.setText(distaceTvValue);
                return 2;
            } else if (distance == 0.5) {
                distaceTvValue = "500m";
                distanceTV.setText(distaceTvValue);
                return 3;
            } else if (distance == 0.75) {
                distaceTvValue = "750m";
                distanceTV.setText(distaceTvValue);
                return 4;
            } else if (distance == 1.0) {
                distaceTvValue = "1KM";
                distanceTV.setText(distaceTvValue);
                return 5;
            } else if (distance == 2.0) {
                distaceTvValue = "2KM";
                distanceTV.setText(distaceTvValue);
                return 6;
            } else if (distance == 5) {
                distaceTvValue = "5KM";
                distanceTV.setText(distaceTvValue);
                return 7;
            } else if (distance == 10) {
                distaceTvValue = "10KM";
                distanceTV.setText(distaceTvValue);
                return 8;
            } else if (distance == 15) {
                distaceTvValue = "15KM";
                distanceTV.setText(distaceTvValue);
                return 9;
            } else if (distance == 20) {
                distaceTvValue = "20KM";
                distanceTV.setText(distaceTvValue);
                return 10;
            } else if (distance == 25) {
                distaceTvValue = "25KM";
                distanceTV.setText(distaceTvValue);
                return 11;
            } else if (distance == 30) {
                distaceTvValue = "30KM";
                distanceTV.setText(distaceTvValue);
                return 12;
            } else if (distance == 40) {
                distaceTvValue = "40KM";
                distanceTV.setText(distaceTvValue);
                return 13;
            } else if (distance == 50) {
                distaceTvValue = "50KM";
                distanceTV.setText(distaceTvValue);
                return 14;
            } else if (distance == 100) {
                distaceTvValue = "100KM";
                distanceTV.setText(distaceTvValue);
                return 15;
            } else {
                distaceTvValue = "100m";
                distanceTV.setText(distaceTvValue);
                return 1;
            }
        } else {

            Log.d(TAG, "distance: " + distance);
            if (distance == 0.033) {
                distaceTvValue = "330 ft";
                distanceTV.setText(distaceTvValue);
                return 1;
            } else if (distance == 0.082) {
                distaceTvValue = "820 ft";
                distanceTV.setText(distaceTvValue);
                return 2;
            } else if (distance == 0.1640) {
                distaceTvValue = "1640 ft";
                distanceTV.setText(distaceTvValue);
                return 3;
            } else if (distance == 0.2460) {
                distaceTvValue = "2460 ft";
                distanceTV.setText(distaceTvValue);
                return 4;
            } else if (distance == 0.5) {
                distaceTvValue = "0.5 Miles";
                distanceTV.setText(distaceTvValue);
                return 5;
            } else if (distance == 1) {
                distaceTvValue = "1 Mile";
                distanceTV.setText(distaceTvValue);
                return 6;
            } else if (distance == 3) {
                distaceTvValue = "3 Miles";
                distanceTV.setText(distaceTvValue);
                return 7;
            } else if (distance == 6) {
                distaceTvValue = "6 Miles";
                distanceTV.setText(distaceTvValue);
                return 8;
            } else if (distance == 10) {
                distaceTvValue = "10 Miles";
                distanceTV.setText(distaceTvValue);
                return 9;
            } else if (distance == 13) {
                distaceTvValue = "13 Miles";
                distanceTV.setText(distaceTvValue);
                return 10;
            } else if (distance == 16) {
                distaceTvValue = "16 Miles";
                distanceTV.setText(distaceTvValue);
                return 11;
            } else if (distance == 20) {
                distaceTvValue = "20 Miles";
                distanceTV.setText(distaceTvValue);
                return 12;
            } else if (distance == 26) {
                distaceTvValue = "26 Miles";
                distanceTV.setText(distaceTvValue);
                return 13;
            } else if (distance == 30) {
                distaceTvValue = "30 Miles";
                distanceTV.setText(distaceTvValue);
                return 14;
            } else if (distance == 60) {
                distaceTvValue = "60 Miles";
                distanceTV.setText(distaceTvValue);
                return 15;
            } else {
                distaceTvValue = "330 ft";
                distanceTV.setText(distaceTvValue);
                return 1;
            }
        }
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
                Log.d(TAG, "onMapClick: knownName" + knownName);


                currentAddressPlace = new AddressPlaceModel();
                currentAddressPlace.setLat(String.valueOf(latLng.latitude));
                currentAddressPlace.setLng(String.valueOf(latLng.longitude));
                currentAddressPlace.setPlaceName(knownName);
                currentAddressPlace.setAddniceaddress(address);

                addressET.setText(address);
                retrievePlaceIdForLatLong(latLng);

            } else {
                mGoogleMap.clear();
                showSnackbar(distanceTV, getString(R.string.unable_to_retrieve_address_line_from_this_location), Constant.ERROR);
            }
        } else {
            mGoogleMap.clear();
            showSnackbar(distanceTV, getString(R.string.unable_to_retrieve_address_line_from_this_location), Constant.ERROR);
        }

    }


    public void retrievePlaceIdForLatLong(LatLng latLng) {
        jobManager.addJobInBackground(new EditProfileGetPlaceIdWebJob(latLng.latitude, latLng.longitude));
    }


    @Subscribe
    public void googlePlaceIdResponse(final EditProfileGooglePlaceIdResponse googlePlaceIdResponse) {
        Log.wtf(TAG, "googlePlaceIdResponse");
        stopProgressBar();
        if (googlePlaceIdResponse != null) {
            if (googlePlaceIdResponse.getStatus() != null && googlePlaceIdResponse.getStatus().contains(Constant.SUCCESS)) {

                try {
                    JSONArray data = googlePlaceIdResponse.getData();
                    JSONObject placeObj = (JSONObject) data.get(0);


                    if(placeObj.has("place_id")){

                        String placeId = placeObj.getString("place_id");
                        if (placeId != null && placeId.length() > 0) {
                            currentAddressPlace.setPlaceID(placeId);
                        } else {
                            currentAddressPlace = null;
                        }

                        Log.wtf(TAG, "googlePlaceIdResponse placeObj: " + placeObj);
                        Log.wtf(TAG, "googlePlaceIdResponse place id: " + placeId);

                    }else{
                        currentAddressPlace = null;
                    }

                    if(addressET.getText().toString().trim().length() > 0){
                        emptyMapll.setVisibility(View.GONE);
                    }else{
                        emptyMapll.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    showSnackbar(profilePhotoIv, getString(R.string.google_place_id_failed), Constant.ERROR);
                    currentAddressPlace = null;
                }

            } else {
                showSnackbar(profilePhotoIv, getString(R.string.google_place_id_failed), Constant.ERROR);
                currentAddressPlace = null;
            }
        } else {
            showSnackbar(profilePhotoIv, getString(R.string.google_place_id_failed), Constant.ERROR);
            currentAddressPlace = null;
        }
    }


    @Override
    public void onPoiClick(final PointOfInterest pointOfInterest) {

//        Toast.makeText(getApplicationContext(), "Clicked: " +
//                        pointOfInterest.name + "\nPlace ID:" + pointOfInterest.placeId +
//                        "\nLatitude:" + pointOfInterest.latLng.latitude +
//                        " Longitude:" + pointOfInterest.latLng.longitude,
//                Toast.LENGTH_SHORT).show();

        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(pointOfInterest.latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pointOfInterest.latLng, Constant.MAP_ZOOM_LEVEL));

        List<Address> addresses = getAddressFromLatLong(pointOfInterest.latLng.latitude, pointOfInterest.latLng.longitude);

        if (addresses != null && addresses.size() > 0) {
            if (addresses.get(0) != null) {

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                Log.d(TAG, "onPoiClick: addresses" + addresses.get(0));

                currentAddressPlace = new AddressPlaceModel();
                currentAddressPlace.setLat(String.valueOf(pointOfInterest.latLng.latitude));
                currentAddressPlace.setLng(String.valueOf(pointOfInterest.latLng.longitude));
                currentAddressPlace.setPlaceName(pointOfInterest.name);
                currentAddressPlace.setAddniceaddress(address);
                currentAddressPlace.setPlaceID(pointOfInterest.placeId);

                addressET.setText(pointOfInterest.name);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        emptyMapll.setVisibility(View.GONE);
                    }
                });

                stopProgressBar();

            } else {
                mGoogleMap.clear();
                stopProgressBar();
                showSnackbar(distanceTV, getString(R.string.unable_to_retrieve_address_line_from_this_location), Constant.ERROR);
            }
        } else {
            mGoogleMap.clear();
            stopProgressBar();
            showSnackbar(distanceTV, getString(R.string.unable_to_retrieve_address_line_from_this_location), Constant.ERROR);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {

//        if (i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
//            Toast.makeText(this, "The user gestured on the map.",
//                    Toast.LENGTH_SHORT).show();
//            //userProfileScrollView.requestDisallowInterceptTouchEvent(false);
//
//        } else if (i == GoogleMap.OnCameraMoveStartedListener
//                .REASON_API_ANIMATION) {
//            Toast.makeText(this, "The user tapped something on the map.",
//                    Toast.LENGTH_SHORT).show();
//        } else if (i == GoogleMap.OnCameraMoveStartedListener
//                .REASON_DEVELOPER_ANIMATION) {
//            Toast.makeText(this, "The app moved the camera.",
//                    Toast.LENGTH_SHORT).show();
//        }
    }

}
