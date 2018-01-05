package com.meeof.meeof.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.CommentDetailsAdapter;
import com.meeof.meeof.adapter.UpdateCommentDetailsAdapter;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.UpdateCommentReponse;
import com.meeof.meeof.model.comment_dto.HttpResponseComments;
import com.meeof.meeof.model.event_comment_dto.CommentData;
import com.meeof.meeof.model.event_comment_dto.EventCommentGet;
import com.meeof.meeof.model.event_permission_dto.EventPermissionResponse;
import com.meeof.meeof.model.event_permission_dto.Event_permission;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.update_comments_dto.Array_comments;
import com.meeof.meeof.model.update_comments_dto.UpdateCommentsResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetCommentsWebJob;
import com.meeof.meeof.webjob.GetEventPermissionsWebJob;
import com.meeof.meeof.webjob.GetUpdateCommentsWebJob;
import com.meeof.meeof.webjob.GetUpdatesWebJob;
import com.meeof.meeof.webjob.PostCommentWebJob;
import com.meeof.meeof.webjob.PostUpdateCommentWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ransikadesilva on 10/26/17.
 */

public class UpdatesCommentsActivity extends CommentsBaseActivity implements View.OnClickListener {

    private static final String TAG = EventCommentsActivity.class.getSimpleName();
    private String accessToken;
    private LinearLayout closeLlBtn;
    private RecyclerView commentsRv;
    private LinearLayout commentBtnLl;
    private EditText typeCommentEt;
    private String updateId;
    private LinearLayout commentTvLl;
    private UpdateCommentDetailsAdapter commentDetailsAdapter;
    private int userId;
    private ProfileResponse profileResponse;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_comments);
        initViews();

        profileResponse = retriveSavedProfileObject(sharedPreferences);
        mLayoutManager = new LinearLayoutManager(this);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        updateId = getIntent().getStringExtra(Constant.UPDATE_ID);
        String userIdStr = getIntent().getStringExtra(Constant.USER_ID);
        userId = Integer.parseInt(userIdStr);

        Log.d(TAG, "accessToken " + accessToken);
        Log.d(TAG, "updateId " + updateId);
        Log.d(TAG, "userId " + userId);
        Log.d(TAG, "userIdStr " + userIdStr);

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(TAG, "Inside HandleMsg");

                try {

                    if(msg.getData().getString("update_comment") != null){

                        String message = msg.getData().getString("update_comment");
                        Log.d(TAG, "comment Message: " + message);
                        JSONObject messageObject = new JSONObject(message);

                        if (messageObject != null) {
                            Array_comments commentData = new Array_comments();
                            commentData.setProfilephoto((messageObject.getJSONObject("update_comment").getString("profilephoto")));
                            commentData.setContent((messageObject.getJSONObject("update_comment").getString("content")));
                            commentData.setChannel_id((messageObject.getJSONObject("update_comment").getString("channel_id")));
                            commentData.setCreated_at((messageObject.getJSONObject("update_comment").getString("created_at")));
                            commentData.setFirst_name((messageObject.getJSONObject("update_comment").getString("first_name")));
                            commentData.setUser_id((messageObject.getJSONObject("update_comment").getInt("user_id")));
                            commentData.setZone_id((messageObject.getJSONObject("update_comment").getInt("zone_id")));

                            int zoneId =messageObject.getJSONObject("update_comment").getInt("zone_id");
                            Log.d(TAG, "Inside HandleMsg commentData: " + commentData.toString());
                            Log.d(TAG, "Inside HandleMsg updateId: " + updateId);
                            Log.d(TAG, "Inside HandleMsg zoneId: " + zoneId);

                            if (zoneId == Integer.parseInt(updateId)) {
                                Log.d(TAG, "zone id and event id equal 1");
                                commentDetailsAdapter.addItem(commentData);
                                Log.d(TAG, "zone id and event id equal 2");
                                commentDetailsAdapter.notifyDataSetChanged();
                                commentsRv.smoothScrollToPosition(1);
                                Log.d(TAG, "zone id and event id equal 3");
                            }
                        }
                    }

                }catch (JSONException e){
                    Log.d(TAG, "JSONException: " + e.toString());
                }
            }
        };

        subscribeToComments(incomingMessageHandler);
        getComments(updateId);


        //getEventPermissions();
    }

    /*
    private void getEventPermissions() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventPermissionsWebJob(accessToken, Integer.valueOf(eventId)));
        } else {
            showSnackbar(commentBtnLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventPermissionsWebJob(EventPermissionResponse eventPermissionResponse) {
        if (eventPermissionResponse != null) {
            if (eventPermissionResponse.getStatus() != null && eventPermissionResponse.getStatus().equals(Constant.SUCCESS)) {
                if (eventPermissionResponse.getEvent_permission() != null) {
                    Event_permission eventPermission = eventPermissionResponse.getEvent_permission();

                    Log.d(TAG, "Permisssions for " + eventId + " is " + eventPermission.toString());

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
    */


    private void initViews() {
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        commentBtnLl = (LinearLayout) findViewById(R.id.commentBtnLl);
        typeCommentEt = (EditText) findViewById(R.id.typeCommentEt);
        commentsRv = (RecyclerView) findViewById(R.id.commentsRv);
        commentTvLl = (LinearLayout) findViewById(R.id.commentTvLl);

        closeLlBtn.setOnClickListener(this);
        commentBtnLl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeLlBtn:
                this.finish();
                break;

            case R.id.commentBtnLl:
                Log.d(TAG, "commentBtnLl pressed ");
                String comment = typeCommentEt.getText().toString().trim();
                Log.d(TAG, "comment " + comment);
                Log.d(TAG, "eventId inside commentBTN " + comment);

                if(comment != null && comment.length() >0){
                    updateUI(comment);
                    postCommentToQueue(comment);
                    postComment(updateId, comment);
                }else{
                    showSnackbar(commentBtnLl, getString(R.string.please_enter_your_comment), Constant.ERROR);
                }
                break;
        }
    }


    private void updateUI(String comment) {
//        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());;
//        Log.d(TAG, "Current Date :=> " + currentDate.toString());
//        Log.d(TAG, "updateUI getProfilephoto :=> " + profileResponse.getData().getProfilephoto());
//        CommentData commentData = new CommentData();
//        commentData.setUserId(userId);
//        commentData.setContent(comment);
//        commentData.setCreatedAt(currentDate.toString());
//        commentData.setFirstName(profileResponse.getData().getFirst_name());
//        commentData.setProfilephoto(profileResponse.getData().getProfilephoto());
//        commentData.setZoneId(Integer.parseInt(eventId));
//
//        commentDetailsAdapter.addItem(commentData);
//        commentDetailsAdapter.notifyDataSetChanged();
//        commentsRv.smoothScrollToPosition(1);

        typeCommentEt.setText("");

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

    private void getComments(String eventId) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetUpdateCommentsWebJob(accessToken, eventId));
        } else {
            showSnackbar(commentBtnLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCommentsWebJobCompleted(UpdateCommentsResponse updateCommentsResponse) {
        Log.d(TAG, "onGetCommentsWebJobCompleted");
        if (updateCommentsResponse != null) {
            if (updateCommentsResponse.getArray_comments() != null) {
                commentDetailsAdapter = new UpdateCommentDetailsAdapter(this, updateCommentsResponse.getArray_comments());
//                mLayoutManager.setReverseLayout(false);
//                mLayoutManager.setStackFromEnd(false);

                commentsRv.setLayoutManager(mLayoutManager);
                commentsRv.setAdapter(commentDetailsAdapter);
                commentsRv.setItemAnimator(new DefaultItemAnimator());
                commentDetailsAdapter.notifyDataSetChanged();
                commentsRv.smoothScrollToPosition(1);

                //server sends the latest comment from top to bottom
                //changing layout order since new comments should be added to the top of the list
//                mLayoutManager.setReverseLayout(true);
//                mLayoutManager.setStackFromEnd(true);
//                commentsRv.setLayoutManager(mLayoutManager);

            } else {
                showSnackbar(commentsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(commentsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }


    private void postCommentToQueue(final String comment) {

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        Log.d(TAG, "Current Date :=> " + currentDate.toString());
        Log.d(TAG, "postCommentToQueue getProfilephoto :=> " + profileResponse.getData().getProfilephoto());

        CommentData commentData = new CommentData();
        commentData.setUserId(userId);
        commentData.setContent(comment);
        commentData.setCreatedAt(currentDate.toString());
        commentData.setFirstName(profileResponse.getData().getFirst_name());
        commentData.setProfilephoto(profileResponse.getData().getProfilephoto());
        commentData.setZoneId(Integer.parseInt(updateId));

        //String comment1 = getObjectString(commentData);
        String commentJsonStr = commentData.toJson();

        //Log.d(TAG, "postCommentToQueue: " + comment1);
        Log.d(TAG, "postCommentToQueue:commentJsonStr " + commentJsonStr);
        publishMessage(commentJsonStr);
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


    private void postComment(String updateId, String comment) {

        if (!comment.isEmpty()) {
            Log.d(TAG, "postComment");
            jobManager.addJobInBackground(new PostUpdateCommentWebJob(accessToken, updateId, comment));
        } else {
            showSnackbar(commentBtnLl, getString(R.string.please_enter_your_comment), Constant.ERROR);
            Log.d(TAG, "EMPTY COMMENT");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostCommentJobCompleted(UpdateCommentReponse httpResponse) {
        if (httpResponse != null) {
            if (httpResponse.getStatus() != null && httpResponse.getStatus().toString().equals(Constant.SUCCESS)) {
                Log.d(TAG, "post comment success");
                Log.d(TAG, "eventId inside onPostCommentJobCompleted " + updateId);

                jobManager.addJobInBackground(new GetUpdateCommentsWebJob(accessToken,updateId));
                typeCommentEt.setText("");
            }
        } else {
            showSnackbar(commentsRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
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
        stopRabbitQueue();
        EventBus.getDefault().unregister(this);
    }


}
