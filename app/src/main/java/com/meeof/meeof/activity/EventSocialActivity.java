package com.meeof.meeof.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.CommentAdapter;
import com.meeof.meeof.adapter.PhotosEventRecyclerAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.CopyEventWebJobResponse;
import com.meeof.meeof.model.EventLikeData;
import com.meeof.meeof.model.EventsSyncJobCompleted;
import com.meeof.meeof.model.GetEventsWebJobCompletedEvent;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseData;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.ReloadEventData;
import com.meeof.meeof.model.ReportEventWebJobCompletedResponse;
import com.meeof.meeof.model.comment_dto.HttpResponseComments;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.edit_event_dto.Photos;
import com.meeof.meeof.model.event_comment_dto.CommentData;
import com.meeof.meeof.model.event_comment_dto.EventCommentGet;
import com.meeof.meeof.model.event_permission_dto.EventPermissionResponse;
import com.meeof.meeof.model.event_permission_dto.Event_permission;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.interested_in_event_dto.InterestedEventResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.Utility;
import com.meeof.meeof.webjob.CopyEventWebJob;
import com.meeof.meeof.webjob.EventsSyncJob;
import com.meeof.meeof.webjob.GetCommentsWebJob;
import com.meeof.meeof.webjob.GetEventInfoWebJob;
import com.meeof.meeof.webjob.GetEventPermissionsWebJob;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.PostCommentWebJob;
import com.meeof.meeof.webjob.PostIgnoreEventWebJob;
import com.meeof.meeof.webjob.PostInterestedInEventWebJob;
import com.meeof.meeof.webjob.PostLikeEventAsFriendWebJob;
import com.meeof.meeof.webjob.PostReportEventWebJob;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.meeof.meeof.util.Utility.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by ransikadesilva on 10/25/17.
 */

public class EventSocialActivity extends CommentsBaseActivity implements View.OnClickListener {

    private ImageView backIv;
    private TextView eventNameTv;
    private ImageView searchIv;
    private TextView currentLocTv;
    private TextView eventNameHeaderTv;
    private TextView eventStatusTv;
    private ImageView moreIv;
    private ImageView calenderIv;
    private ImageView likeImageView;
    private TextView profileViewTv;
    private TextView eventTypeTv;
    private ListView eventSocialLv;
    private EditText typeCommentEt;
    private LinearLayout commentBtnLl;
    private Event event;
    private static final String TAG = EventSocialActivity.class.getSimpleName();
    private TextView commentCountTv;
    private TextView noOfLikesTv;
    //private RecyclerView commentsRv;
    private LinearLayout moreCommentsLlBtn;
    private LinearLayout moreLikeLlBtn;
    private String accessToken;
    private RecyclerView photosRv;
    private PhotosEventRecyclerAdapter photosEventAdapter;
    private TextView currentLocationTv;
    private Location mLastLocation;
    private LinearLayout commentTvLl, likeBtn;
    private CommentAdapter commentAdapter;
    private TextView comment1Tv;
    private TextView comment2Tv;
    private LinearLayout commentEditTextLl;
    private ImageView comment1AvatarIv;
    private ImageView comment2AvatarIv;
    private LinearLayout comment1Ll;
    private LinearLayout comment2Ll;
    private TextView CommentEventTv;
    private LinearLayout moreLlBtn;
    private LinearLayout seeMoreCommentsTvLl;

    private String imageUrl;
    private boolean hasDataSetChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_social);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        hasDataSetChanged = false;

        initViews();
        if (this.getIntent().hasExtra("BUNDLE_EVENT")) {
            Bundle args = this.getIntent().getBundleExtra("BUNDLE_EVENT");
            event = (Event) args.getSerializable(Constant.SELECTED_EVENT_ITEM);
            Log.d(TAG, "EVENTSSS " + event.toString());
        }
        setupViews();

        getComments(true);

        imageUrl="";

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {

                    Log.d(TAG, "Inside HandleMsg: " + msg.toString());

                    if(msg.getData().getString("event_comment") != null){

                        String message = msg.getData().getString("event_comment");
                        Log.d(TAG, "comment Message: " + message);
                        JSONObject messageObject = new JSONObject(message);

                        if (messageObject != null) {
                            CommentData commentData = getStringObject(messageObject.getString("event_comment"));

                            int zoneId =messageObject.getJSONObject("event_comment").getInt("zone_id");
                            Log.d(TAG, "commentData: " + commentData.toString());

                            if(zoneId==event.getEventid()){
                                comment2Tv.setText(comment1Tv.getText());
                                comment1Tv.setText(commentData.getContent());
                            }

                            String imgeUrl1 = commentData.getProfilephoto() == null || commentData.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                    "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + commentData.getProfilephoto();
                            Picasso.with(getApplicationContext())
                                    .load(imgeUrl1)
                                    .placeholder(R.drawable.siloet_img)
                                    .error(ContextCompat.getDrawable(getApplicationContext(), R.drawable.siloet_img))
                                    .into(comment1AvatarIv);

                            Picasso.with(getApplicationContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.siloet_img)
                                    .error(ContextCompat.getDrawable(getApplicationContext(), R.drawable.siloet_img))
                                    .into(comment2AvatarIv);

                            //Change here
                            /*
                            if (zoneId == event.getEventid()) {
                                commentAdapter.addItem(commentData);
                                commentAdapter.notifyDataSetChanged();
                                commentsRv.smoothScrollToPosition(1);
                            }
                            */
                        }
                    }else  if(msg.getData().getString("event_like") != null){

                        String message = msg.getData().getString("event_like");
                        Log.d(TAG, "like Message: " + message);
                        JSONObject messageObject = new JSONObject(message);

                        if (messageObject != null) {
                            //increase like amount
                            final int numberOfLikes = Integer.parseInt(noOfLikesTv.getText().toString());
                            Log.d(TAG, "current likes: " + numberOfLikes);

                            if(messageObject.getJSONObject("event_like").getString("status").equalsIgnoreCase("like")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        int finalLikes =  numberOfLikes+1;
                                        Log.d(TAG, "finalLikes: " + finalLikes);
                                        noOfLikesTv.setText(String.valueOf(finalLikes));
                                    }
                                });
                            }else{
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(numberOfLikes > 0){
                                            int finalLikes =  numberOfLikes-1;
                                            Log.d(TAG, "finalLikes: " + finalLikes);
                                            noOfLikesTv.setText(String.valueOf(finalLikes));
                                        }
                                    }
                                });
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        subscribeToComments(incomingMessageHandler);
/*
        commentBtnLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = typeCommentEt.getText().toString();

                if (!comment.trim().isEmpty()) {
                    updateUI(comment);
                    postComment(comment);

                } else{
                    showSnackbar(commentBtnLl, getString(R.string.please_enter_your_comment), Constant.ERROR);
                    Log.d(TAG, "EMPTY COMMENT");
                }
            }
        });
*/

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeEvent();
                if(event.isLike()){
                    postLikeToQueue("unlike");
                    event.setLike(false);
                    likeImageView.setImageDrawable(ContextCompat.getDrawable(EventSocialActivity.this, R.drawable.ico_event_like_normal));

                }else{
                    postLikeToQueue("like");
                    event.setLike(true);
                    likeImageView.setImageDrawable(ContextCompat.getDrawable(EventSocialActivity.this, R.drawable.ico_event_like_active));

                }
            }
        });

        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventSocialActivity.this.onBackPressed();
                if(hasDataSetChanged){
                    Log.d(TAG, "hasDataSetChanged");
                    Log.d(TAG, "event likes: " + event.getCountLikes());
                    EventBus.getDefault().postSticky(new ReloadEventData(Constant.SUCCESS, event));
                    }
            }
        });
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNavigationtoPlace();
            }
        });

        getEventDetails(event.getEventid());
        getEventPermissions();

    }

    private void updateUI(String comment) {
//        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);
//        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
//        ;
//        Log.d(TAG, "Current Date :=> " + currentDate.toString());
//        CommentData commentData = new CommentData();
//        commentData.setUserId(event.getUser_id());
//        commentData.setContent(comment);
//        commentData.setCreatedAt(currentDate.toString());
//        commentData.setFirstName(profileResponse.getData().getFirst_name());
//        commentData.setProfilephoto(profileResponse.getData().getProfilephoto());
//        commentData.setZoneId(event.getEventid());
//        commentAdapter.addItem(commentData);
        typeCommentEt.setText("");
    }

    private void getEventDetails(int eventid) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventInfoWebJob(accessToken, eventid));
        } else {
            stopProgressBar();
        }
    }


    private void getComments(boolean isWithProgress) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetCommentsWebJob(accessToken, String.valueOf(event.getEventid())));
            if (isWithProgress) {
                startProgressBar();
            }
        } else {
            showSnackbar(commentBtnLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void getEventPermissions() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventPermissionsWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(commentBtnLl, getString(R.string.no_internet), Constant.ERROR);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventPermissionsWebJob(EventPermissionResponse eventPermissionResponse) {
        if (eventPermissionResponse != null) {
            if (eventPermissionResponse.getStatus() != null && eventPermissionResponse.getStatus().equals(Constant.SUCCESS)) {
                if (eventPermissionResponse.getEvent_permission() != null) {
                    Event_permission eventPermission = eventPermissionResponse.getEvent_permission();
                    Log.d(TAG, "Permisssions for " + event.getEventid() + " is " + eventPermission.toString());

                    if (!eventPermission.getInteract()) {
                        commentTvLl.setVisibility(View.GONE);
                        commentBtnLl.setVisibility(View.GONE);
                    } else {
                        commentTvLl.setVisibility(View.VISIBLE);
                        commentBtnLl.setVisibility(View.VISIBLE);
                    }

                } else {

                }
            } else {

            }
        } else {

        }
    }

    private void initViews() {
        backIv = (ImageView) findViewById(R.id.backIv);
        searchIv = (ImageView) findViewById(R.id.searchIv);
        moreIv = (ImageView) findViewById(R.id.moreIv);
        calenderIv = (ImageView) findViewById(R.id.calenderIv);
        likeImageView = (ImageView) findViewById(R.id.likeImageView);
        eventNameTv = (TextView) findViewById(R.id.eventNameTv);
        eventNameHeaderTv = (TextView) findViewById(R.id.eventNameHeaderTv);
        eventStatusTv = (TextView) findViewById(R.id.eventStatusTv);
        profileViewTv = (TextView) findViewById(R.id.profileViewTv);
        eventTypeTv = (TextView) findViewById(R.id.eventTypeTv);
        // eventSocialLv = (ListView) findViewById(R.id.eventSocialLv);
        typeCommentEt = (EditText) findViewById(R.id.typeCommentEt);
        commentBtnLl = (LinearLayout) findViewById(R.id.commentBtnLl);
        commentCountTv = (TextView) findViewById(R.id.commentCountTv);
        noOfLikesTv = (TextView) findViewById(R.id.noOfLikesTv);
        //commentsRv = (RecyclerView) findViewById(R.id.commentsRv);
        moreCommentsLlBtn = (LinearLayout) findViewById(R.id.moreCommentsLlBtn);
        moreLikeLlBtn = (LinearLayout) findViewById(R.id.moreLikeLlBtn);
        photosRv = (RecyclerView) findViewById(R.id.photosRv);
        currentLocationTv = (TextView) findViewById(R.id.currentLocationTv);
        commentTvLl = (LinearLayout) findViewById(R.id.commentTvLl);
        likeBtn = (LinearLayout) findViewById(R.id.likeBtn);

        comment1Tv=(TextView)findViewById(R.id.comment1Txt);
        comment2Tv=(TextView)findViewById(R.id.comment2Txt);
        commentEditTextLl=(LinearLayout)findViewById(R.id.commentEditTextLl);
        comment1AvatarIv=(ImageView)findViewById(R.id.comment1AvatarIv);
        comment2AvatarIv=(ImageView)findViewById(R.id.comment2AvatarIv);
        comment1Ll=(LinearLayout)findViewById(R.id.comment1Ll);
        comment2Ll=(LinearLayout)findViewById(R.id.comment2Ll);
        CommentEventTv=(TextView)findViewById(R.id.CommentEventTv);
        moreLlBtn=(LinearLayout)findViewById(R.id.moreLlBtn);
        seeMoreCommentsTvLl=(LinearLayout)findViewById(R.id.seeMoreCommentsTvLl);


        moreCommentsLlBtn.setOnClickListener(this);
        moreLikeLlBtn.setOnClickListener(this);
        commentEditTextLl.setOnClickListener(this);
        CommentEventTv.setOnClickListener(this);
        moreLlBtn.setOnClickListener(this);
        seeMoreCommentsTvLl.setOnClickListener(this);


    }

    private void setupViews() {
        String nameAndLocation = event.getTitle() + " @ " + event.getPlaceName();
        Log.d(TAG,"Event tier 1: "+event.getTier1());
        eventNameTv.setText(nameAndLocation);
        eventNameHeaderTv.setText(nameAndLocation);
        switch (event.getTier1()) {
            case (1):
                profileViewTv.setText("Sports & Activities");
                break;
            case (2):
                profileViewTv.setText("Board Games");
                break;
            case (3):
                profileViewTv.setText("Creative Corner");
                break;
            case (4):
                profileViewTv.setText("Gaming (digital)");
                break;
            case (5):
                profileViewTv.setText("Going Out");
                break;
            case (6):
                profileViewTv.setText("Parks / Amusements");
                break;
        }
        Log.d(TAG,"Event getLive: "+event.getTier1());
        if (event.getLive() == 0) {
            eventStatusTv.setText("(public)");
        } else if (event.getLive() == 1) {
            eventStatusTv.setText("(private)");
        }
        commentCountTv.setText(event.getCountComments() + "");
        noOfLikesTv.setText(event.getCountLikes() + "");

        if(event.isLike()){
            likeImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_event_like_active));
        }else{
            likeImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ico_event_like_normal));
        }

        eventTypeTv.setText(event.getInterestName());
        eventStatusTv.setText(getEventAudience(event.getType()));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.moreCommentsLlBtn:
                sendToMoreCommentsActivity();
                Helper.clickGaurd(moreCommentsLlBtn);
                break;
            case R.id.moreLikeLlBtn:
                sendToMoreLikesActivity();
                Helper.clickGaurd(moreLikeLlBtn);
                break;
            case R.id.commentEditTextLl:
                sendToMoreCommentsActivity();
                Helper.clickGaurd(commentEditTextLl);
                break;
            case R.id.CommentEventTv:
                sendToMoreCommentsActivity();
                Helper.clickGaurd(CommentEventTv);
                break;
            case R.id.moreLlBtn:
                showPopUpMenu(moreLlBtn,event);
                break;
            case R.id.seeMoreCommentsTvLl:
                sendToMoreCommentsActivity();
                Helper.clickGaurd(seeMoreCommentsTvLl);
                break;
        }
    }

    private void showPopUpMenu(View anchor, final Event event) {
        final PopupMenu popup = new PopupMenu(this, anchor);
        //Inflating the Popup using xml file
        if (event.isHost()) {
            popup.getMenuInflater().inflate(R.menu.popup_menu_host, popup.getMenu());
        } else {
            popup.getMenuInflater().inflate(R.menu.popup_menu_non_host, popup.getMenu());
        }
        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if(event.isHost()){
                    switch (item.getItemId()) {
                        case R.id.editItm:
                            sendToEditEventAvtivity(event.getEventid());
                            break;
                        case R.id.copyItm:
                            copyEvent(event);
                            break;
                    }
                }else{
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

    private void sendToEditEventAvtivity(int eventId) {
        Intent intent = new Intent(this, CreateEventActivity.class);
        intent.putExtra(Constant.IS_EDIT_EVENT, true);
        intent.putExtra(Constant.EDIT_EVENT_EVENT_ID, eventId);
        startActivity(intent);
    }

    private void copyEvent(Event event) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new CopyEventWebJob(accessToken, event.getEventid()));
        } else {
            showSnackbar(moreLlBtn, getString(R.string.no_internet), Constant.ERROR);
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

    @Subscribe
    public void onCopyEventWebJob(CopyEventWebJobResponse copyEventWebJobResponse) {
        stopProgressBar();
        if (copyEventWebJobResponse != null) {
            if (copyEventWebJobResponse.getStatus() != null && copyEventWebJobResponse.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(moreLlBtn, getString(R.string.event_copy_successfully), Constant.SUCCESS);
            } else {
                showSnackbar(moreLlBtn, getString(R.string.event_copy_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(moreLlBtn, getString(R.string.event_copy_failed), Constant.ERROR);
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        if (Utility.checkLocationPermission(this)) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {

                List<Address> addresses = getAddressFromLatLong(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                final String address = addresses.get(0).getAddressLine(0);
                currentLocationTv.setText(String.valueOf(address));
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult:requestCode " + requestCode);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.onConnected(null);
                    Log.d(TAG, "onRequestPermissionsResult:PERMISSION_GRANTED ");

                } else {

                    Log.d(TAG, "onRequestPermissionsResult:PERMISSIONdenied ");
                }
                return;
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventInfoWebJob(EventInfoResponse eventInfoResponse) {
        //stopProgressBar();
        Log.d(TAG, "onGetEventInfoWebJob() EventSocialActivity");
        if (eventInfoResponse != null) {
            Log.d(TAG, "onGetEventInfoWebJob() EventSocialActivity eventInfoResponse!=null");
            if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "onGetEventInfoWebJob() EventSocialActivity eventInfoResponse.getStatus()!=null");
                Log.d(TAG, "onGetEventInfoWebJob() EventSocialActivity respose: "+eventInfoResponse.getData().get(0).getPhotos().toString());
                setDataToPhotoList(eventInfoResponse.getData().get(0).getPhotos());
                event.setCountLikes(eventInfoResponse.getData().get(0).getCountLikes());
                event.setCountComments(eventInfoResponse.getData().get(0).getCountComments());
                hasDataSetChanged = true;

                jobManager.addJobInBackground(new GetEventsWebJob(accessToken));

//                Log.d(TAG, "onGetEventInfoWebJob() getCountLikes: "+ event.getCountLikes());
//                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
//                dbHelper.insertOrUpdateEvent(event);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupViews();
                    }
                });

            } else {
                stopProgressBar();
                Log.d(TAG, "onGetEventInfoWebJob() EventSocialActivity eventInfoResponse.getStatus()!=null ELSE");
                showSnackbar(moreLlBtn, getString(R.string.failed_to_load_images), Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onGetEventInfoWebJob() EventSocialActivity eventInfoResponse.getStatus()!=null ELSE ELSE");
            showSnackbar(moreLlBtn, getString(R.string.failed_to_load_images), Constant.ERROR);
        }
    }

    //Need to call!!
    private void setDataToPhotoList(List<Photos> photos) {
        photosEventAdapter = new PhotosEventRecyclerAdapter(this, photos);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photosRv.setLayoutManager(linearLayoutManager);
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


    private void sendToMoreLikesActivity() {
        Intent intent = new Intent(this, EventLikesActivity.class);
        intent.putExtra(Constant.EVENT_ID, event.getEventid());
        startActivity(intent);
    }

    private void sendToMoreCommentsActivity() {

        Log.d(TAG, "sendToMoreCommentsActivity user id: " + event.getUser_id());

        Intent intent = new Intent(this, EventCommentsActivity.class);
        intent.putExtra(Constant.EVENT_ID, String.valueOf(event.getEventid()));
        intent.putExtra(Constant.USER_ID, String.valueOf(event.getUser_id()));
        startActivity(intent);
    }

/*
    private void postComment(String comment) {

        Log.d(TAG, "postComment");
        postCommentToQueue(comment);
        jobManager.addJobInBackground(new PostCommentWebJob(accessToken, String.valueOf(event.getEventid()), comment));
    }

    private void postCommentToQueue(final String comment) {
        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        Log.d(TAG, "Current Date :=> " + currentDate.toString());

        CommentData commentData = new CommentData();
        commentData.setUserId(event.getUser_id());
        commentData.setContent(comment);
        commentData.setCreatedAt(currentDate.toString());
        commentData.setFirstName(profileResponse.getData().getFirst_name());
        commentData.setProfilephoto(profileResponse.getData().getProfilephoto());
        commentData.setZoneId(event.getEventid());

        //String comment1 = getObjectString(commentData);
        String commentJsonStr = commentData.toJson();
        publishMessage(commentJsonStr);
    }
    */

    private void postLikeToQueue(final String status) {
        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        Log.d(TAG, "Current Date :=> " + currentDate.toString());

        EventLikeData eventLikeData = new EventLikeData();
        eventLikeData.setStatus(status);
        eventLikeData.setProfilephoto(profileResponse.getData().getProfilephoto());
        eventLikeData.setUser_id(event.getUser_id());
        eventLikeData.setZone_id(event.getEventid());

        String likeJsonStr = eventLikeData.toJson();
        publishMessage(likeJsonStr);
    }

    private String getObjectString(Object object) {
        try {
            Gson gson = new Gson();
            String objectJson = gson.toJson(object);
            return objectJson;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private CommentData getStringObject(String objString) {
        CommentData commentData = null;
        Gson gson = new Gson();
        try {
            commentData = gson.fromJson(objString, CommentData.class);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return commentData;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostCommentJobCompleted(HttpResponseComments httpResponse) {
        if (httpResponse != null) {
            if (httpResponse.getStatus() != null && httpResponse.getStatus().toString().equals(Constant.SUCCESS)) {
                Log.d(TAG, "post comment success");
                //jobManager.addJobInBackground(new GetCommentsWebJob(accessToken, String.valueOf(event.getEventid())));
                //typeCommentEt.setText("");
            }
        } else {
            showSnackbar(commentCountTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCommentsWebJobCompleted(EventCommentGet eventCommentGet) {
        stopProgressBar();
        Log.d(TAG, "onGetCommentsWebJobCompleted");
        if (eventCommentGet != null) {
            if (eventCommentGet.getData() != null) {

                //change here
                /*
                commentAdapter = new CommentAdapter(this, eventCommentGet.getData());
                commentCountTv.setText(eventCommentGet.getData().size() + "");
                LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
//                mLayoutManager.setStackFromEnd(true);
                commentsRv.setLayoutManager(mLayoutManager);

                mLayoutManager.setStackFromEnd(true);
                commentsRv.setAdapter(commentAdapter);
                commentsRv.setItemAnimator(new DefaultItemAnimator());
                mLayoutManager.scrollToPosition(1);
                commentAdapter.notifyDataSetChanged();
                */

                commentCountTv.setText(eventCommentGet.getData().size() + "");
                if(eventCommentGet!=null){
                    if(eventCommentGet.getData().size()>1){
                        comment1Tv.setText(eventCommentGet.getData().get(0).getContent());
                        comment2Tv.setText(eventCommentGet.getData().get(1).getContent());

                        String imgeUrl1 = eventCommentGet.getData().get(0).getProfilephoto() == null || eventCommentGet.getData().get(0).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + eventCommentGet.getData().get(0).getProfilephoto();
                        Picasso.with(this.getApplicationContext())
                                .load(imgeUrl1)
                                .placeholder(R.drawable.siloet_img)
                                .error(ContextCompat.getDrawable(this, R.drawable.siloet_img))
                                .into(comment1AvatarIv);

                        String imgeUrl2 = eventCommentGet.getData().get(1).getProfilephoto() == null || eventCommentGet.getData().get(1).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + eventCommentGet.getData().get(1).getProfilephoto();
                        Picasso.with(this)
                                .load(imgeUrl2)
                                .placeholder(R.drawable.siloet_img)
                                .error(ContextCompat.getDrawable(this, R.drawable.siloet_img))
                                .into(comment2AvatarIv);

                        comment1Ll.setVisibility(View.VISIBLE);
                        comment2Ll.setVisibility(View.VISIBLE);


                        imageUrl=imgeUrl1;
                    }else if(eventCommentGet.getData().size()>0){
                        comment2Tv.setText(eventCommentGet.getData().get(0).getContent());

                        String imgeUrl1 = eventCommentGet.getData().get(0).getProfilephoto() == null || eventCommentGet.getData().get(0).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + eventCommentGet.getData().get(0).getProfilephoto();
                        Picasso.with(this)
                                .load(imgeUrl1)
                                .placeholder(R.drawable.siloet_img)
                                .error(ContextCompat.getDrawable(this, R.drawable.siloet_img))
                                .into(comment1AvatarIv);

                        comment1Ll.setVisibility(View.VISIBLE);
                        comment2Ll.setVisibility(View.INVISIBLE);

                        imageUrl=imgeUrl1;
                    }else{
                        //hide
                        comment1Ll.setVisibility(View.INVISIBLE);
                        comment2Ll.setVisibility(View.INVISIBLE);
                    }


                    if(eventCommentGet.getData().size()>2){
                        seeMoreCommentsTvLl.setVisibility(View.VISIBLE);
                    }else{
                        seeMoreCommentsTvLl.setVisibility(View.GONE);
                    }
                }


            } else {
                showSnackbar(commentCountTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(commentCountTv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

//Not used
    /*
    private CommentData getComment(String comment) {
        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        ;
        Log.d(TAG, "Current Date :=> " + currentDate.toString());

        CommentData commentData = new CommentData();
        commentData.setUserId(event.getUser_id());
        commentData.setContent(comment);
        commentData.setCreatedAt(currentDate.toString());
        commentData.setFirstName(profileResponse.getData().getFirst_name());
        commentData.setProfilephoto(profileResponse.getData().getProfilephoto());
        commentData.setZoneId(event.getEventid());

        commentAdapter.addItem(commentData);

        return commentData;
    }
    */

    private void startNavigationtoPlace() {
//        stopRabbitQueue();

        double lat = event.getLatitude();
        double lang = event.getLongitude();

        String packageName = "com.google.android.apps.maps";
        String query = "google.navigation:q=" + lat + "," + lang;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(query));
        intent.setPackage(packageName);
        startActivity(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopRabbitQueue();
        EventBus.getDefault().unregister(this);
    }

    private void likeEvent() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostLikeEventAsFriendWebJob(accessToken, event.getEventid()));
            startProgressBar();
        } else {
            showSnackbar(photosRv, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostLikeEventAsFriendWebJobCompleted(HttpResponseLikeUnlike httpResponseLike) {

        if (httpResponseLike != null) {
            if (httpResponseLike.getStatus() != null && httpResponseLike.getStatus().equals(Constant.SUCCESS)) {
                //setLikeButtonImage(httpResponseLike.is_like());
                noOfLikesTv.setText(String.valueOf(httpResponseLike.getLike_count()));
                //jobManager.addJobInBackground(new GetEventsWebJob(accessToken));
                getEventDetails(event.getEventid());

            } else {
                stopProgressBar();
            }
        } else {
            stopProgressBar();
        }
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
                //showSnackbar(going_people1_imageView, "check eror 1", Constant.ERROR);
            }
        } else {
            stopProgressBar();
            //showSnackbar(going_people1_imageView, "check error 2", Constant.ERROR);
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

}
