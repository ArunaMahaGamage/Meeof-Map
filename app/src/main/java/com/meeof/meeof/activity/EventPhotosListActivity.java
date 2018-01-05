package com.meeof.meeof.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.PhotosEventFullScreenRecyclerAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.edit_event_dto.Data;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.edit_event_dto.Photos;
import com.meeof.meeof.model.event_permission_dto.EventPermissionResponse;
import com.meeof.meeof.model.event_permission_dto.Event_permission;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.CopyEventWebJob;
import com.meeof.meeof.webjob.DeleteAllImagesWebJob;
import com.meeof.meeof.webjob.DeleteEventImageWebJob;
import com.meeof.meeof.webjob.GetEventInfoWebJob;
import com.meeof.meeof.webjob.GetEventPermissionsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransikadesilva on 10/26/17.
 */

public class EventPhotosListActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = EventPhotosListActivity.class.getSimpleName();
    private TextView photosNumberTv;
    private RecyclerViewPager photosRv;
    private String accessToken;
    private PhotosEventFullScreenRecyclerAdapter photosEventAdapter;
    private int eventId;
    private TextView currentPicNumberTxt;
    private TextView totalPicNumberTxt;
    private LinearLayout filtersLl;
    private LinearLayout reportLlBtn;
    private LinearLayout saveLlBtn;
    private LinearLayout downloadLlBTn;
    private LinearLayout cancelLLBtn;
    private LinearLayout deleteLlBtn;
    private LinearLayout deleteAllLlBtn;
    private LinearLayout bottomMenuLl;
    private LinearLayout popoupLl;
    private List<Photos> imageList;
    private LinearLayout closeLlBtn;
    private Data eventData;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_photo);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        initViews();
        Intent intent = getIntent();
        Log.d(TAG, "has extra has: " + intent.hasExtra(Constant.EVENT_ID));
        if (intent.hasExtra(Constant.EVENT_ID)) {
            eventId = intent.getIntExtra(Constant.EVENT_ID, 0);
        }

        Log.d(TAG, "has extra: " + eventId);
        if (eventId != 0) {
            Log.d(TAG, "calling getEvent(): " + eventId);
            getEventDetails(eventId);
        } else {
            showSnackbar(photosNumberTv, getString(R.string.unable_to_load_photos), Constant.ERROR);
        }

        getEventPermissions();
        userId=0;
    }


    private void getEventPermissions() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventPermissionsWebJob(accessToken, eventId));
        } else {
            showSnackbar(photosNumberTv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventPermissionsWebJob(EventPermissionResponse eventPermissionResponse) {
        if (eventPermissionResponse != null) {
            if (eventPermissionResponse.getStatus() != null && eventPermissionResponse.getStatus().equals(Constant.SUCCESS)) {
                if (eventPermissionResponse.getEvent_permission() != null) {
                    Event_permission eventPermission = eventPermissionResponse.getEvent_permission();
                    Log.d(TAG,"Permisssions for "+eventId+" is "+eventPermission.toString());

                    if (!eventPermission.getInteract()) {
                        saveLlBtn.setVisibility(View.GONE);
                        downloadLlBTn.setVisibility(View.GONE);
                    } else {
                        saveLlBtn.setVisibility(View.VISIBLE);
                        downloadLlBTn.setVisibility(View.VISIBLE);
                    }

                } else {

                }
            } else {

            }
        } else {

        }
    }


    private void initViews() {
        photosNumberTv = (TextView) findViewById(R.id.photosNumberTv);
        photosRv = (RecyclerViewPager) findViewById(R.id.photosRv);

        currentPicNumberTxt = (TextView) findViewById(R.id.currentPicNumberTxt);
        totalPicNumberTxt = (TextView) findViewById(R.id.totalPicNumberTxt);

        filtersLl = (LinearLayout) findViewById(R.id.filtersLl);
        reportLlBtn = (LinearLayout) findViewById(R.id.reportLlBtn);
        saveLlBtn = (LinearLayout) findViewById(R.id.saveLlBtn);
        downloadLlBTn = (LinearLayout) findViewById(R.id.downloadLlBTn);
        cancelLLBtn = (LinearLayout) findViewById(R.id.cancelLLBtn);
        bottomMenuLl = (LinearLayout) findViewById(R.id.bottomMenuLl);
        popoupLl = (LinearLayout) findViewById(R.id.popoupLl);
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        deleteLlBtn=(LinearLayout) findViewById(R.id.deleteLlBtn);
        deleteAllLlBtn=(LinearLayout) findViewById(R.id.deleteAllLlBtn);

        filtersLl.setOnClickListener(this);
        reportLlBtn.setOnClickListener(this);
        saveLlBtn.setOnClickListener(this);
        downloadLlBTn.setOnClickListener(this);
        cancelLLBtn.setOnClickListener(this);
        closeLlBtn.setOnClickListener(this);
        deleteLlBtn.setOnClickListener(this);
        deleteAllLlBtn.setOnClickListener(this);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(photosRv);

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
                if(eventInfoResponse.getData().get(0).getPhotos().size()<1){
                    onBackPressed();
                }
                setDataToPhotoList(eventInfoResponse.getData().get(0).getPhotos());
                imageList = eventInfoResponse.getData().get(0).getPhotos();
                totalPicNumberTxt.setText(String.valueOf(eventInfoResponse.getData().get(0).getPhotos().size()));
                eventData=eventInfoResponse.getData().get(0);
            } else {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE");
                showSnackbar(photosRv, getString(R.string.failed_to_load_images), Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE ELSE");
            showSnackbar(photosRv, getString(R.string.failed_to_load_images), Constant.ERROR);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventDeleteImageWebJob(HttpResponse httpResponse) {
        Log.d(TAG, "onGetEventInfoWebJob()");
        if (httpResponse != null) {
            stopProgressBar();
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse!=null");
            if (httpResponse.getStatus() != null && httpResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(photosRv, "Image Deleted Successfully", Constant.SUCCESS);
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null");
                getEventDetails(eventId);
            } else {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE");
                showSnackbar(photosRv, "Failed to delete all Image", Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE ELSE");
            showSnackbar(photosRv, "Failed to delete all Images", Constant.ERROR);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventDeleteAllImagesWebJob(HttpResponseData httpResponse) {
        Log.d(TAG, "onGetEventInfoWebJob()");
        if (httpResponse != null) {
            stopProgressBar();
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse!=null");
            if (httpResponse.getStatus() != null && httpResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(photosRv, "All Images Deleted Successfully", Constant.SUCCESS);
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null");
                Helper.delay(1500, new Helper.DelayCallBack() {
                    @Override
                    public void postDelay() {
                        onBackPressed();
                    }
                });
            } else {
                Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE");
                showSnackbar(photosRv, "Failed to delete Image", Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onGetEventInfoWebJob() eventInfoResponse.getStatus()!=null ELSE ELSE");
            showSnackbar(photosRv, "Failed to delete Image", Constant.ERROR);
        }

    }

    private void setDataToPhotoList(List<Photos> photos) {
        photosEventAdapter = new PhotosEventFullScreenRecyclerAdapter(this, photos);
        photosRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosRv.setAdapter(photosEventAdapter);
        photosRv.setItemAnimator(new DefaultItemAnimator());
        photosEventAdapter.notifyDataSetChanged();

        photosEventAdapter.setOnClick(new PhotosEventFullScreenRecyclerAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, boolean isAccept, Photos item) {

            }
        });

        photosRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                currentPicNumberTxt.setText(String.valueOf(photosRv.getCurrentPosition() + 1));

            }
        });

    }

    private class DownloadImages extends AsyncTask<List<String>, Void, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressBar();
        }

        @Override
        protected List<String> doInBackground(List<String>... params) {
            List<String> eventData = new ArrayList<>();
            for (String str : params[0]) {
                try {
                    java.net.URL url = new java.net.URL(str);
                    HttpURLConnection connection = (HttpURLConnection) url
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    Bitmap myBitmap = BitmapFactory.decodeStream(input);

                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                            "/MeeofEvents";
                    File dir = new File(file_path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, "meeof_event_" + System.currentTimeMillis() + ".jpg");
                    FileOutputStream fOut = new FileOutputStream(file);

                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, fOut);
                    fOut.flush();
                    fOut.close();

                    Log.d(TAG, file.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }


            Log.i(TAG, "onCreateView " + eventData.size());
            return eventData;
        }

        @Override
        protected void onPostExecute(List<String> eventData) {
            super.onPostExecute(eventData);
            stopProgressBar();
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filtersLl:
                showHideBottomMenu(true);
                break;
            case R.id.reportLlBtn:
                reportPhoto();
                break;
            case R.id.saveLlBtn:
                saveThisPhoto();
                break;
            case R.id.deleteLlBtn:
                deleteImage();
                showHideBottomMenu(false);
                break;
            case R.id.deleteAllLlBtn:
                deleteAllImages();
                showHideBottomMenu(false);
                break;
            case R.id.downloadLlBTn:
                saveAllPhotos();
                break;
            case R.id.cancelLLBtn:
                showHideBottomMenu(false);
                break;
            case R.id.closeLlBtn:
                this.finish();
                break;
        }
    }

    private void deleteImage() {
        if (isNetworkAvailable()) {
            startProgressBar();
            jobManager.addJobInBackground(new DeleteEventImageWebJob(accessToken,eventId+"",
                    eventData.getPhotos().get(photosRv.getCurrentPosition()).getMedia_id()+""));
        } else {
            showSnackbar(photosRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void deleteAllImages() {
        if (isNetworkAvailable()) {
            startProgressBar();
            for(Photos photo:eventData.getPhotos()){
                Log.d(TAG,"Delete photo media Id: "+photo.getMedia_id());
                jobManager.addJobInBackground(new DeleteAllImagesWebJob(accessToken,eventId+"",
                        eventData.getPhotos()));
            }
        } else {
            showSnackbar(photosRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void saveAllPhotos() {
        List<String> imageListStr = new ArrayList<>();
        for (Photos photo : imageList) {
            imageListStr.add(photo.getFile_name() == null || photo.getFile_name().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                    Constant.EVENT_IMAGES_BASE_URL + photo.getFile_name());
        }
        if (isStoragePermissionGranted()) {
            new DownloadImages().execute(imageListStr);
        }
    }

    private void saveThisPhoto() {
        List<String> imageListStr = new ArrayList<>();
        int postion = photosRv.getCurrentPosition();

        imageListStr.add(imageList.get(postion).getFile_name() == null || imageList.get(postion).getFile_name().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                Constant.EVENT_IMAGES_BASE_URL + imageList.get(postion).getFile_name());

        if (isStoragePermissionGranted()) {
            new DownloadImages().execute(imageListStr);
        }
    }


    private void reportPhoto() {

    }

    private void showHideBottomMenu(boolean isShow) {

        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            userId=profileResponse.getData().getUser_id();
        }

        //delete button show or hide here
        if(eventData.getUser_id()==userId){
            Log.d(TAG,"isHost");
            deleteAllLlBtn.setVisibility(View.VISIBLE);
            deleteLlBtn.setVisibility(View.VISIBLE);

        }else {
            deleteAllLlBtn.setVisibility(View.GONE);
            if(eventData.getPhotos().get(photosRv.getCurrentPosition()).getUser_id().equals(userId+"")){
                Log.d(TAG,"image uploaded by user");
                deleteLlBtn.setVisibility(View.VISIBLE);


            }else{
                Log.d(TAG,"image uploaded by someone else");
                deleteLlBtn.setVisibility(View.GONE);

            }
        }

        if (isShow) {
            popoupLl.setVisibility(View.VISIBLE);
        } else {
            popoupLl.setVisibility(View.GONE);
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
                    List<String> imageListStr = new ArrayList<>();
                    for (Photos photo : imageList) {
                        imageListStr.add(photo.getFile_name() == null || photo.getFile_name().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                Constant.EVENT_IMAGES_BASE_URL + photo.getFile_name());
                    }
                    new DownloadImages().execute(imageListStr);
                } else {
                    showSnackbar(popoupLl, "Permission not granted", Constant.ERROR);
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
                break;
        }
    }
}
