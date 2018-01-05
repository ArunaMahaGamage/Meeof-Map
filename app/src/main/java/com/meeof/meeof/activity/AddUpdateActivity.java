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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.internal.Validate;
import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.GridViewAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.PostEditUpdateModel;
import com.meeof.meeof.model.editUpdate.EditUpdateResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.search_my_friend_dto.SendFriendBack;
import com.meeof.meeof.model.search_tag_dto.Data;
import com.meeof.meeof.model.search_tag_dto.SendTagBack;
import com.meeof.meeof.model.update_image_dto.UpdateImageResponse;
import com.meeof.meeof.model.update_image_upload_dto.Photo;
import com.meeof.meeof.model.update_image_upload_dto.UpdateImageUploadResponse;
import com.meeof.meeof.model.updates_all_dto.Array_updates;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetUpdateImageWebJob;
import com.meeof.meeof.webjob.PostEditUpdateWebJob;
import com.meeof.meeof.webjob.UploadUpdateImageArrayWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;
import in.myinnos.awesomeimagepicker.models.Image;

public class AddUpdateActivity extends BaseActivity implements View.OnClickListener {
    private String accessToken;
    private Array_updates array_updates;
    private String TAG = AddUpdateActivity.class.getSimpleName();
    private static final int PLACE_REQUEST_CODE = 9393;
    private static final int SELECT_COUNTRY_RC = 8778;
    private static final int UPLOAD_LIMIT_MULTI_SELECT_GALLERY = 1;

    private TextView userNameTv;
    private TextView locationTv;
    private TextView hashTagsTv;
    private TextView friendsNameTv;

    private LinearLayout editLocationLlBtn;
    private LinearLayout editHashtagsLlBtn;
    private LinearLayout editFriendsLlBtn;
    private LinearLayout addMorePhotosLlBtn;
    private ImageView backAcIvBtn;
    private TextView postTvBtn;

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private ArrayList<Photo> mGridData;

    private double lat;
    private double lng;
    private String placeName;
    private String placeAddress;
    private String placeId;
    private String title="";
    private int updateid;

    private Photo item;
    private ProgressDialog progressBar;
    private List<Data> selectedTags=new ArrayList<>();
    private List<com.meeof.meeof.model.search_my_friend_dto.Data> selectedFriends=new ArrayList<>();

    private ArrayList<Integer> images = new ArrayList<>();
    private ArrayList<Integer> tags = new ArrayList<>();
    private ArrayList<Integer> friends = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update);


        initViews();
        Intent intent = getIntent();
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        if (this.getIntent().hasExtra("FROM_EDIT")) {
            Bundle args = this.getIntent().getBundleExtra("FROM_EDIT");
            array_updates = (Array_updates) args.getSerializable("UPDATE_ITEMS");
            Log.d(TAG, "Has Extra:Name  " + array_updates);
            addDetailsView(array_updates);

        }
    }

    private void addDetailsView(Array_updates array_updates) {
        userNameTv.setText(array_updates.getFirst_name());
        locationTv.setText(array_updates.getLocation());
        hashTagsTv.setText(array_updates.getTags());
        friendsNameTv.setText(array_updates.getFriends());
        title=array_updates.getTitle();
        updateid=array_updates.getUpdateid();
        try {
            String tagS="";
            JSONArray jsonArray=new JSONArray(array_updates.getTags());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String tag=jsonObject.getString("hashtag");
                String tagid=jsonObject.getString("hashid");
                tags.add(Integer.valueOf(tagid));
                if(tagS.equals("")){
                    tagS="#"+tag;
                }else{
                    tagS+=" #"+tag;
                }
                Log.d(TAG,"TAGS 2: "+tag);
            }
            hashTagsTv.setText(tagS);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"TAGS 1: ");
            hashTagsTv.setText("");
        }

        try {
            String frien="";
            JSONArray jsonArray=new JSONArray(array_updates.getFriends());
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String friend=jsonObject.getString("first_name");
                String friendId=jsonObject.getString("user_id");

                friends.add(Integer.valueOf(friendId));
                if(frien.equals("")){
                    frien=friend+", ";
                }else{
                    frien+=friend+", ";
                }
                Log.d(TAG,"FRIENDS 2: "+friend);
            }
            friendsNameTv.setText(frien);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG,"FRIENDS 1: ");
            friendsNameTv.setText("");
        }


        //Image Setter
        for(int i=0;i<array_updates.getPhotocount();i++){
            //images.add(array_updates.getPhoto_arrays().)
            item = new Photo();
            item.setFile_name(array_updates.getPhoto_arrays().get(i));
            mGridData.add(item);
            mGridAdapter.setGridData(mGridData);
        }
        if (isNetworkAvailable()) {
            //Log.d(TAG, "postEditUpdateModel: " + postEditUpdateModel);
            jobManager.addJobInBackground(new GetUpdateImageWebJob(accessToken, updateid));
            //TODO UNCOMMENT
            //startProgressBar();
        } else {
            showSnackbar(editFriendsLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }

    }

    private void initViews() {
        userNameTv = (TextView) findViewById(R.id.userNameTv);
        locationTv = (TextView) findViewById(R.id.locationTv);
        hashTagsTv = (TextView) findViewById(R.id.hashTagsTv);
        friendsNameTv = (TextView) findViewById(R.id.friendsNameTv);
        mGridView = (GridView) findViewById(R.id.gridImage);


        editLocationLlBtn = (LinearLayout) findViewById(R.id.editLocationLlBtn);
        editHashtagsLlBtn = (LinearLayout) findViewById(R.id.editHashtagsLlBtn);
        editFriendsLlBtn = (LinearLayout) findViewById(R.id.editFriendsLlBtn);
        addMorePhotosLlBtn = (LinearLayout) findViewById(R.id.addMorePhotosLlBtn);
        backAcIvBtn = (ImageView) findViewById(R.id.backAcIvBtn);
        postTvBtn = (TextView) findViewById(R.id.postTvBtn);


        //Set OnClick
        editLocationLlBtn.setOnClickListener(this);
        editHashtagsLlBtn.setOnClickListener(this);
        editFriendsLlBtn.setOnClickListener(this);
        addMorePhotosLlBtn.setOnClickListener(this);
        backAcIvBtn.setOnClickListener(this);
        postTvBtn.setOnClickListener(this);

        //Initialize with empty data
        mGridData = new ArrayList<>();
        mGridAdapter = new GridViewAdapter(AddUpdateActivity.this, R.layout.item_grid_image, mGridData);
        mGridView.setAdapter(mGridAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backAcIvBtn:

                // Validate profileOriginalData With Filled Screen Data
                //if (hasUnsubmittedProfileChanges()) {
                //Profile data has unsubmitted changes
                //Todo Add Custom Conformation Dialog
                // Log.d(TAG, "Profile has unsubmitted changes");
                //showConfirmationAlert(this,"Discard Unsaved Data",  "CANCEL");
                //} else {
                this.onBackPressed();
                //}
                break;
            case R.id.editLocationLlBtn:
                sendToPlacesAutocomplteActivity();
                Helper.clickGaurd(editLocationLlBtn);
                break;
            case R.id.editHashtagsLlBtn:
                editTagHashTag();
                break;
            case R.id.editFriendsLlBtn:
                editTagHashFriend();
                break;
            case R.id.addMorePhotosLlBtn:
                galleryIntent();
                break;
            case R.id.postTvBtn:
                retieveEditUpdateDetails();
                break;
        }
    }
    private void retieveEditUpdateDetails() {
        PostEditUpdateModel postAddUpdateModel = new PostEditUpdateModel();
//        sendToInviteFriendsToEventActivity();

        if ((placeId != null && placeName != null && placeAddress != null)) {
//            if (discribeEt.getText().toString().trim().length() > 10) {
//                if (tags.length > 0) {
//                    if (friends.length > 0) {
//                        if (images.length > 0) {


            postAddUpdateModel.setUpdateid(String.valueOf(updateid));
            postAddUpdateModel.setLat(lat);
            postAddUpdateModel.setLng(lng);
            postAddUpdateModel.setPlaceID(placeId);
            postAddUpdateModel.setPlaceName(placeName);
            postAddUpdateModel.setAddniceaddress(placeAddress);
            postAddUpdateModel.setTxtupdate(title);
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
//            } else {
//                showSnackbar(editLocationLlBtn, "Please Enter a Longer Event Description", Constant.ERROR);
//            }
        } else {
            showSnackbar(editLocationLlBtn, "Please Update the event Location", Constant.ERROR);
        }


    }


    private void postCreateEvent(PostEditUpdateModel postEditUpdateModel) {
//        sendToInviteFriendListActivity();
        if (isNetworkAvailable()) {
            Log.d(TAG, "postEditUpdateModel: " + postEditUpdateModel);
            jobManager.addJobInBackground(new PostEditUpdateWebJob(accessToken, postEditUpdateModel));
            //TODO UNCOMMENT

            startProgressBar();
        } else {
            showSnackbar(editFriendsLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void editTagHashTag() {

        Intent intent = new Intent(AddUpdateActivity.this, SearchTagsActivity.class);
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
    public void onPostEventDetailsWebJobCompleted(EditUpdateResponse editUpdateResponse) {
        stopProgressBar();
        Log.d(TAG, "Inside onPostEventDetailsWebJob");
        if (editUpdateResponse != null) {
            Log.d(TAG, "Inside onPostEventDetailsWebJob not null");
            if (editUpdateResponse.getStatus() != null && editUpdateResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "Inside onPostEventDetailsWebJob not null success");
                showSnackbar(addMorePhotosLlBtn, getString(R.string.update_updated_successfully), Constant.SUCCESS);
                //getInviteInfo(eventId);
                //syncEvents();
            } else {
                showSnackbar(addMorePhotosLlBtn, getString(R.string.failed_to_update_event), Constant.ERROR);
            }
        } else {
            showSnackbar(addMorePhotosLlBtn, getString(R.string.failed_to_update_event), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateImageResponseCompleted(UpdateImageResponse updateImageResponse) {
        stopProgressBar();
        Log.d(TAG, "Inside updateImageResponse");
        if (updateImageResponse != null) {
            Log.d(TAG, "Inside updateImageResponse not null");
            if (updateImageResponse.getStatus() != null && updateImageResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "Inside updateImageResponse not null success");

                for(com.meeof.meeof.model.update_image_dto.Data data: updateImageResponse.getData()){
                    images.add(data.getMedia_id());
                }

                //showSnackbar(addMorePhotosLlBtn, getString(R.string.update_updated_successfully), Constant.SUCCESS);
                //getInviteInfo(eventId);
                //syncEvents();
            } else {
                //showSnackbar(addMorePhotosLlBtn, getString(R.string.failed_to_update_event), Constant.ERROR);
            }
        } else {
            //showSnackbar(addMorePhotosLlBtn, getString(R.string.failed_to_update_event), Constant.ERROR);
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
