package com.meeof.meeof.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.meeof.meeof.R;

import com.meeof.meeof.adapter.GridViewAdapter;
import com.meeof.meeof.custom.CustomFontTextView;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.PostAddUpdateModel;
import com.meeof.meeof.model.addUpdate.AddUpdateResponse;
import com.meeof.meeof.model.event_image_upload_dto.EventImageUploadResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.search_my_friend_dto.SendFriendBack;
import com.meeof.meeof.model.search_tag_dto.Data;
import com.meeof.meeof.model.search_tag_dto.SendTagBack;
import com.meeof.meeof.model.update_image_upload_dto.Photo;
import com.meeof.meeof.model.update_image_upload_dto.UpdateImageUploadResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.PostAddUpdateWebJob;
import com.meeof.meeof.webjob.UploadEventImageArrayWebJob;
import com.meeof.meeof.webjob.UploadUpdateImageArrayWebJob;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class AddUpdateBiginingActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = AddUpdateBiginingActivity.class.getSimpleName();
    private static final int PLACE_REQUEST_CODE = 9393;
    private static final int SELECT_COUNTRY_RC = 8778;
    private static final int UPLOAD_LIMIT_MULTI_SELECT_GALLERY = 1;

    private TextView userNameTv;
    private TextView locationTv;
    private TextView postTvBtn;

    private TextView hashTagsTv;
    private TextView friendsNameTv;

    private ImageView userImageIv;
    private ImageView deleteMultiIv;
    private LinearLayout addImagelv;
    private LinearLayout tagFriendslv;
    private LinearLayout addHashTaglv;
    private LinearLayout imageHidelv;
    private LinearLayout addMorePhotosLlBtn;

    private LinearLayout hashTagslv;
    private LinearLayout friendlv;

    private LinearLayout editHashtagsLlBtn;
    private LinearLayout editFriendsLlBtn;
    private LinearLayout editLocationLlBtn;


    private EditText discribeEt;
    private boolean isFromSettingsActivity;
    private boolean isEmptyPhoto;
    private String accessToken;
    private AppCompatImageView back_button;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<Photo> mGridData;

    private Gson gson;
    private ProfileResponse profileResponse;

    private ProgressDialog progressBar;

    private List<Data> selectedTags;
    private List<com.meeof.meeof.model.search_my_friend_dto.Data> selectedFriends;

    private double lat;
    private double lng;
    private String placeName;
    private String placeAddress;
    private String placeId;

    private ArrayList<Integer> images = new ArrayList<>();
    private ArrayList<Integer> tags = new ArrayList<>();
    private ArrayList<Integer> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_bigining);

        Log.d(TAG, "Inside Intent " + isFromSettingsActivity);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        initViews();
        getUserProfile();

    }

    private void initViews() {
        userNameTv = (TextView) findViewById(R.id.userNameTv);
        locationTv = (TextView) findViewById(R.id.locationTv);
        postTvBtn = (TextView) findViewById(R.id.postTvBtn);
        hashTagsTv = (TextView) findViewById(R.id.hashTagsTv);
        friendsNameTv = (TextView) findViewById(R.id.friendsNameTv);
        discribeEt = (EditText) findViewById(R.id.discribeEt);

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        userImageIv = (ImageView) findViewById(R.id.userImageIv);
        deleteMultiIv = (ImageView) findViewById(R.id.deleteMultiIv);
        addImagelv = (LinearLayout) findViewById(R.id.addImagelv);
        addHashTaglv = (LinearLayout) findViewById(R.id.addHashTaglv);
        tagFriendslv = (LinearLayout) findViewById(R.id.tagFriendslv);
        imageHidelv = (LinearLayout) findViewById(R.id.imageHidelv);
        addMorePhotosLlBtn = (LinearLayout) findViewById(R.id.addMorePhotosLlBtn);
        hashTagslv = (LinearLayout) findViewById(R.id.hashTagslv);
        friendlv = (LinearLayout) findViewById(R.id.friendlv);

        editHashtagsLlBtn = (LinearLayout) findViewById(R.id.editHashtagsLlBtn);
        editFriendsLlBtn = (LinearLayout) findViewById(R.id.editFriendsLlBtn);
        editLocationLlBtn = (LinearLayout) findViewById(R.id.editLocationLlBtn);

        imageHidelv.setVisibility(View.GONE);
        hashTagslv.setVisibility(View.GONE);
        friendlv.setVisibility(View.GONE);

        //OnClick
        back_button.setOnClickListener(this);
        postTvBtn.setOnClickListener(this);
        addMorePhotosLlBtn.setOnClickListener(this);

        addImagelv.setOnClickListener(this);
        tagFriendslv.setOnClickListener(this);
        addHashTaglv.setOnClickListener(this);

        editHashtagsLlBtn.setOnClickListener(this);
        editFriendsLlBtn.setOnClickListener(this);
        editLocationLlBtn.setOnClickListener(this);

        mGridView = (GridView) findViewById(R.id.gridImage);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(AddUpdateBiginingActivity.this, R.layout.item_grid_image, mGridData);
        mGridView.setAdapter(mGridAdapter);
    }

    private void getUserProfile() {
        gson = new Gson();
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);


        if (profileResponse.getData().getFirst_name() != null && profileResponse.getData().getLast_name() != null) {
            userNameTv.setText(profileResponse.getData().getFirst_name() + " " + profileResponse.getData().getLast_name());
        } else {
            if (profileResponse.getData().getFirst_name() != null) {
                userNameTv.setText(profileResponse.getData().getFirst_name());
            } else {
                userNameTv.setText(profileResponse.getData().getLast_name());
            }
        }

        locationTv.setText(profileResponse.getData().getAddress());

        String photoUrl = profileResponse.getData().getProfilephoto() == null || profileResponse.getData().getProfilephoto().trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                Constant.PROFILE_PIC_BASE_URL + profileResponse.getData().getProfilephoto();

        isEmptyPhoto = photoUrl.equals(Constant.DEFAULT_AVATAR_URL) ? true : false;


        if (isEmptyPhoto) {
            userImageIv.setImageDrawable(ContextCompat.getDrawable(AddUpdateBiginingActivity.this, R.drawable.ico_profile_edit_avatar));
        } else {

            isEmptyPhoto = true;
            Picasso.with(getApplicationContext()).load(photoUrl)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .fit()
                    .centerCrop()
                    .into(userImageIv);
        }
        Log.d(TAG, "profileResponse" + profileResponse);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backAcIvBtn:

                //Validate profileOriginalData With Filled Screen Data
                //if (hasUnsubmittedProfileChanges()) {
                //Profile data has unsubmitted changes
                //Todo Add Custom Conformation Dialog
                //Log.d(TAG, "Profile has unsubmitted changes");
                //showConfirmationAlert(this,"Discard Unsaved Data",  "CANCEL");
                //} else {
                this.onBackPressed();
                //}
                break;
            case R.id.addImagelv:
                galleryIntent();
                break;
            case R.id.addHashTaglv:
                tagHashTag();
                break;
            case R.id.tagFriendslv:
                tagFriends();
                break;
            case R.id.postTvBtn:
                retieveAddUpdateDetails();
                break;
            case R.id.addMorePhotosLlBtn:
                galleryIntent();
                break;
            case R.id.editHashtagsLlBtn:
                editTagHashTag();
                break;
            case R.id.editFriendsLlBtn:
                editTagHashFriend();
                break;
            case R.id.editLocationLlBtn:
                sendToPlacesAutocomplteActivity();
                Helper.clickGaurd(editLocationLlBtn);
                break;

        }
    }

    private void retieveAddUpdateDetails() {
        PostAddUpdateModel postAddUpdateModel = new PostAddUpdateModel();
//        sendToInviteFriendsToEventActivity();

        if ((placeId != null && placeName != null && placeAddress != null)) {
            if (discribeEt.getText().toString().trim().length() > 10) {
//                if (tags.length > 0) {
//                    if (friends.length > 0) {
//                        if (images.length > 0) {


                postAddUpdateModel.setLat(lat);
                postAddUpdateModel.setLng(lng);
                postAddUpdateModel.setPlaceID(placeId);
                postAddUpdateModel.setPlaceName(placeName);
                postAddUpdateModel.setAddniceaddress(placeAddress);
                postAddUpdateModel.setTxtupdate(discribeEt.getText().toString());
                postAddUpdateModel.setTags(tags);
                postAddUpdateModel.setToFriends(friends);
                postAddUpdateModel.setImages(images);


                postCreateEvent(postAddUpdateModel);


//                        } else {
//                            showSnackbar(editLocationLlBtn, "Your Images zero", Constant.ERROR);
//                        }
//                    } else {
//                        showSnackbar(editLocationLlBtn, "Your friends zero", Constant.ERROR);
//                    }
//                } else {
//                    showSnackbar(editLocationLlBtn, "Your tags zero", Constant.ERROR);
//                }
            } else {
                showSnackbar(editLocationLlBtn, "Please Enter a Longer Event Description", Constant.ERROR);
            }
        } else {
            showSnackbar(editLocationLlBtn, "Please Update the event Location", Constant.ERROR);
        }


    }


    private void postCreateEvent(PostAddUpdateModel postAddUpdateModel) {
//        sendToInviteFriendListActivity();
        if (isNetworkAvailable()) {
            Log.d(TAG, "postAddUpdateModel: " + postAddUpdateModel);
            jobManager.addJobInBackground(new PostAddUpdateWebJob(accessToken, postAddUpdateModel)); //TODO UNCOMMENT

            startProgressBar();
        } else {
            showSnackbar(editFriendsLlBtn, getString(R.string.no_internet), Constant.ERROR);
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

    private void onPlaceRequestResult(Intent data) {
        Log.d(TAG, "Inside PLACE_REQUEST_CODE");

        lat = Double.parseDouble(data.getStringExtra("lat").toString());
        lng = Double.parseDouble(data.getStringExtra("lng").toString());
        placeName = data.getStringExtra("place_name");
        placeAddress = data.getStringExtra("place_address");
        placeId = data.getStringExtra("place_id");
        locationTv.setText("" + placeName);

//        Double latitude = lat;
//        Double longitude = lng;
//
//        LatLng userLatLong = new LatLng(latitude, longitude);
    }

    private void editTagHashTag() {

        Intent intent = new Intent(this, SearchTagsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("BUNDLE_TAGS", (Serializable) selectedTags);
        intent.putExtra("FROM_FILTER", args);
        startActivity(intent);
    }

    private void editTagHashFriend() {

        Intent intent = new Intent(this, SearchFriendsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("BUNDLE_TAGS", (Serializable) selectedFriends);
        intent.putExtra("FROM_FILTER", args);
        startActivity(intent);
    }

    private void tagFriends() {
        Intent intent = new Intent(this, SearchFriendsActivity.class);
        startActivity(intent);
        tagFriendslv.setVisibility(View.GONE);
        friendlv.setVisibility(View.VISIBLE);
    }

    private void tagHashTag() {
        Intent intent = new Intent(this, SearchTagsActivity.class);
        startActivity(intent);
        addHashTaglv.setVisibility(View.GONE);
        hashTagslv.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPostTags(SendTagBack searchHashTagResponse) {
        Log.d(TAG, "onPostTags 1");
        if (searchHashTagResponse != null) {
            if (searchHashTagResponse.getStatus() != null && searchHashTagResponse.getStatus().equals(Constant.SUCCESS)) {

                Log.d(TAG, "onPostTags 2" + searchHashTagResponse.getData());
                selectedTags = searchHashTagResponse.getData();
                String tagsString = "";

                for (int i = 0; i < selectedTags.size(); i++) {
                    tagsString += "#" + selectedTags.get(i).getHashtag() + " ";
                    tags.add(selectedTags.get(i).getHashid());
                }
                //tags[]
                hashTagsTv.setText(tagsString);
                Log.d(TAG, "tags" + tags);

            } else {
                //showSnackbar(distanceMatrixTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            //showSnackbar(distanceMatrixTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onPostFriends(SendFriendBack searchHashTagResponse) {
        Log.d(TAG, "onPostTags 1");
        if (searchHashTagResponse != null) {
            if (searchHashTagResponse.getStatus() != null && searchHashTagResponse.getStatus().equals(Constant.SUCCESS)) {

                Log.d(TAG, "onPostTags 2" + searchHashTagResponse.getData());
                selectedFriends = searchHashTagResponse.getData();
                String tagsString = "";
                for (int i = 0; i < selectedFriends.size(); i++) {
                    tagsString += selectedFriends.get(i).getFirst_name() + " ";
                    friends.add(selectedFriends.get(i).getUser_id());


                }
                friendsNameTv.setText(tagsString);
                Log.d(TAG, "friends" + friends);


            } else {
                showSnackbar(hashTagsTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(hashTagsTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private Boolean hasUnsubmittedProfileChanges() {
        return false;
    }

    private void galleryIntent() {
        boolean isAllow = isStoragePermissionGranted();
        if (isAllow) {
            Intent intent = new Intent(this, AlbumSelectActivity.class);
            intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, UPLOAD_LIMIT_MULTI_SELECT_GALLERY); // set limit for image selection
            startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
            Log.v(TAG, "ImageUpdate " + intent.getExtras());

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Photo item;
        if (resultCode == Activity.RESULT_OK) {
            imageHidelv.setVisibility(View.VISIBLE);
            addImagelv.setVisibility(View.GONE);
            if (requestCode == ConstantsCustomGallery.REQUEST_CODE && data != null) {
                //The array list has the image paths of the selected images
                ArrayList<Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
                Set<String> paths = new HashSet<>();

                for (int i = 0; i < images.size(); i++) {

                    Uri uri = Uri.fromFile(new File(images.get(i).path));
                    Log.d(TAG, "ImagesUpload: " + images.get(i).path);
                    paths.add(uri.getPath());

                }
                if (images.size() < 10) {
                    addMorePhotosLlBtn.setVisibility(View.VISIBLE);
                } else {
                    addMorePhotosLlBtn.setVisibility(View.GONE);
                }
                uploadPhotos(paths, images.size());
            } else if (requestCode == PLACE_REQUEST_CODE) {

                onPlaceRequestResult(data);
                Log.e(TAG, "countryData : " + data);
            }
        }

    }

    private void uploadPhotos(Set<String> paths, int imageCount) {
        String[] images = paths.toArray(new String[paths.size()]);
        Log.d(TAG, "sdsdd: " + images);
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new UploadUpdateImageArrayWebJob(accessToken, images));

            String message = "Uploading photo. Please wait..";

            if (imageCount > 1) {
                message = "Uploading " + String.valueOf(imageCount) + " photos. " + "Please wait..";
            }
            //showProgressAlert(message);

            progressBar = ProgressDialog.show(this, "",
                    message, true);

        } else {
            showSnackbar(addMorePhotosLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadEventImageWebJob(UpdateImageUploadResponse updateImageUploadResponse) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopProgressBar();
                progressBar.dismiss();
            }
        });


        if (updateImageUploadResponse != null) {
            Photo item;
            if (updateImageUploadResponse.getStatus() != null && updateImageUploadResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(addMorePhotosLlBtn, getString(R.string.image_uploaded_successfully), Constant.SUCCESS);
                Log.d(TAG, "getMedia_id: " + updateImageUploadResponse.getPhoto().getMedia_id());

                images.add(updateImageUploadResponse.getPhoto().getMedia_id());


                Log.d(TAG, "getMedia_idi: " + images);


                item = new Photo();
                item.setFile_name(updateImageUploadResponse.getPhoto().getFile_name());
                mGridData.add(item);
                mGridAdapter.setGridData(mGridData);


                //refreshImageList();
            } else if (updateImageUploadResponse.getStatus() != null && updateImageUploadResponse.getStatus().equals(Constant.ERROR)) {
                showSnackbar(addMorePhotosLlBtn,
                        updateImageUploadResponse.getMessage() != null ? updateImageUploadResponse.getMessage() : getString(R.string.image_upload_failed), Constant.ERROR);
            } else {
                showSnackbar(addMorePhotosLlBtn, getString(R.string.image_upload_failed), Constant.SUCCESS);
            }
        } else {
            showSnackbar(addMorePhotosLlBtn, getString(R.string.image_upload_failed), Constant.SUCCESS);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostEventDetailsWebJobCompleted(AddUpdateResponse addUpdateResponse) {
        stopProgressBar();
        Log.d(TAG, "Inside onPostEventDetailsWebJob");
        if (addUpdateResponse != null) {
            Log.d(TAG, "Inside onPostEventDetailsWebJob not null");
            if (addUpdateResponse.getStatus() != null && addUpdateResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "Inside onPostEventDetailsWebJob not null success");
                showSnackbar(addMorePhotosLlBtn, getString(R.string.update_created_successfully), Constant.SUCCESS);
                this.onBackPressed();
                //getInviteInfo(eventId);
                //syncEvents();
            } else {
                showSnackbar(addMorePhotosLlBtn, getString(R.string.failed_to_create_update), Constant.ERROR);
            }
        } else {
            showSnackbar(addMorePhotosLlBtn, getString(R.string.failed_to_create_update), Constant.ERROR);
        }
    }
//    private void refreshImageList() {
//        getEventDetails(event.getEventid());
//    }

    @Override
    public void onBackPressed() {
        setResult(PlacesActivity.RESULT_OK);
        this.finish();
        super.onBackPressed();
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

}