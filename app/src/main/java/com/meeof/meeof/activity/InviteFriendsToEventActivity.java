package com.meeof.meeof.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.edit_event_dto.AttendeeList;
import com.meeof.meeof.model.edit_event_dto.EventInfoResponse;
import com.meeof.meeof.model.event_invites.EmailModel;
import com.meeof.meeof.model.event_invites.raw_emails_dto.Data;
import com.meeof.meeof.model.event_invites.raw_emails_dto.RawEmailsResponse;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_dto.FriendListItem;
import com.meeof.meeof.model.tag.CustomTagAlreadyInvitedFriend;
import com.meeof.meeof.model.tag.CustomTagEmail;
import com.meeof.meeof.model.tag.CustomTagFriend;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteEventInviteWebJob;
import com.meeof.meeof.webjob.GetEventInfoWebJob;
import com.meeof.meeof.webjob.PostEmailEventInviteWebJob;
import com.meeof.meeof.webjob.PostEventInviteFriendsWebJob;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by ransikadesilva on 10/19/17.
 */

public class InviteFriendsToEventActivity extends BaseActivity implements View.OnClickListener, TagView.OnTagDeleteListener {

    private static final int RC_INVITE_FRIENDS = 7654;

    //    private TagGroup toBeInvitedTg;
    private TagView toBeInvitedTg;
    private TextView eventTitleTv;
    private LinearLayout inviteFriendsLl;
    private LinearLayout friendsLlSb;
    private LinearLayout closeLlBtn;
    private LinearLayout addEmailLlBtn;
    private EditText addEmailsEt;


    private String TAG = InviteFriendsToEventActivity.class.getSimpleName();
    private LinearLayout peopleAlreadyInvitedLl;
    private LinearLayout peopleToBeInvitedLl;
    private TagView alreadyInvitedTg;

    private final int noUserId = Integer.MIN_VALUE;

    private Set<EmailModel> emailsSet = new HashSet<>();
    private Set<Friends> selectedFriendsSet = new HashSet<>();

    private Set<CustomTagFriend> currentFriendTagList = new HashSet<>();
    private Set<CustomTagEmail> currentEmailTagList = new HashSet<>();

    private Set<CustomTagAlreadyInvitedFriend> alreadyInvitedTagList = new HashSet<>();
    private Set<AttendeeList> alreadyInvitedFriendsList = new HashSet<>();

    private String accessToken;
    private boolean isEditEvent;
    private int eventId;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends_to_event);

        Intent intent = getIntent();

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        initViews();

        Log.d(TAG, "Has extra IS_EDIT_EVENT: " + intent.hasExtra(Constant.IS_EDIT_EVENT));
        Log.d(TAG, "Has extra IS_EDIT_EVENT: bool:  " + intent.getBooleanExtra(Constant.IS_EDIT_EVENT, false));
        if (intent.hasExtra(Constant.IS_EDIT_EVENT) && intent.getBooleanExtra(Constant.IS_EDIT_EVENT, false)) {
            isEditEvent = intent.getBooleanExtra(Constant.IS_EDIT_EVENT, false);
            int eventId = intent.getIntExtra(Constant.EDIT_EVENT_EVENT_ID, 0);
            getInviteFriendsInfo(eventId);
            peopleAlreadyInvitedLl.setVisibility(View.VISIBLE);
            Log.d(TAG, "Inside HasExtra");
        } else {
            isEditEvent = false;
            this.eventId = intent.getIntExtra(Constant.EDIT_EVENT_EVENT_ID, 0);
            String eventTitle = intent.getStringExtra(Constant.EDIT_EVENT_TITLE);
            eventTitleTv.setText(eventTitle.toString());

            if (this.getIntent().hasExtra("BUNDLE_EVENT")) {
                Bundle args = this.getIntent().getBundleExtra("BUNDLE_EVENT");
                event = (Event) args.getSerializable(Constant.EVENT);
                Log.d(TAG, "Has Extra:Name  " + event.getFirst_name());
                Log.d(TAG, "Event:  " + event.toString());
            }
        }
    }

    private void getInviteFriendsInfo(int eventId) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetEventInfoWebJob(accessToken, eventId));
            Log.d(TAG, "Inside getInviteInfo");
        } else {
            showSnackbar(toBeInvitedTg, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetEventInfoWebJobCompleted(EventInfoResponse eventInfoResponse) {
        if (eventInfoResponse != null) {
            if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.SUCCESS)) {
                setEventInviteDetailsToUI(eventInfoResponse.getData().get(0).getAttendeeList());
                this.eventId = eventInfoResponse.getData().get(0).getEventid();
                eventTitleTv.setText(eventInfoResponse.getData().get(0).getTitle());
                Log.d(TAG, "Inside Subscribe onGetEventInfoWebJobCompleted");
            } else if (eventInfoResponse.getStatus() != null && eventInfoResponse.getStatus().equals(Constant.ERROR)) {
                showSnackbar(toBeInvitedTg, getString(R.string.unable_to_retrieve_event_details), Constant.ERROR);
            }
        } else {
            showSnackbar(toBeInvitedTg, getString(R.string.unable_to_retrieve_event_details), Constant.ERROR);
        }
    }

    private void setEventInviteDetailsToUI(List<AttendeeList> attendeeList) {
        if (attendeeList.size() > 0) {
            alreadyInvitedFriendsList.addAll(attendeeList);
            addAlreadyAddedFriendTags();
        } else {

        }
    }


    private void initViews() {

        toBeInvitedTg = (TagView) findViewById(R.id.toBeInvitedTg);
        alreadyInvitedTg = (TagView) findViewById(R.id.alreadyInvitedTg);

        eventTitleTv = (TextView) findViewById(R.id.eventTitleTv);
        inviteFriendsLl = (LinearLayout) findViewById(R.id.inviteFriendsLl);
        friendsLlSb = (LinearLayout) findViewById(R.id.friendsLlSb);
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        addEmailLlBtn = (LinearLayout) findViewById(R.id.addEmailLlBtn);
        addEmailsEt = (EditText) findViewById(R.id.addEmailsEt);

        peopleAlreadyInvitedLl = (LinearLayout) findViewById(R.id.peopleAlreadyInvitedLl);
        peopleToBeInvitedLl = (LinearLayout) findViewById(R.id.peopleToBeInvitedLl);

        inviteFriendsLl.setOnClickListener(this);
        closeLlBtn.setOnClickListener(this);
        addEmailLlBtn.setOnClickListener(this);
        friendsLlSb.setOnClickListener(this);

        toBeInvitedTg.setOnTagDeleteListener(this);
        alreadyInvitedTg.setOnTagDeleteListener(this);
        initProgressBar();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inviteFriendsLl:
                inviteFriendsPost();
                break;
            case R.id.closeLlBtn:
                if(isToBeInvitedAvaiable()){
                    showConfirmationAlert(this, null, null);
                }else {
                    if(isEditEvent){
                        this.finish();
                    }else {
                        goToEventDetailsScreen(event);
                    }
                }
                break;
            case R.id.addEmailLlBtn:
                getEmailsFromEt();
                break;
            case R.id.friendsLlSb:
                sendToInviteFriendListActivity();
                break;
        }
    }

    public void showConfirmationAlert(Context c, String title, String imageUrl) {
        try {
            final Dialog dialog = new Dialog(c);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_event_invite_close_confirmation);
            dialog.setCancelable(false);

            Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            Button confirmBtn = (Button) dialog.findViewById(R.id.confirmBtn);
            ImageView eventIv = (ImageView) dialog.findViewById(R.id.eventIv);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    InviteFriendsToEventActivity.this.finish();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    inviteFriendsPost();
                }
            });
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isToBeInvitedAvaiable(){
        boolean value = false;

        if(toBeInvitedTg.getTags().size() > 0){
            value = true;
        }
        return  value;
    }

    private void sendToInviteFriendListActivity() {
        Intent intent = new Intent(this, InviteFriendsToEventListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constant.SELECTED_FRIENDS_FOR_EVENT, (Serializable) selectedFriendsSet);
        intent.putExtra("BUNDLE_B", args);
        startActivityForResult(intent, RC_INVITE_FRIENDS);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "Inside on activity result ");
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Inside on activity result RESULT_OK");
            switch (requestCode) {
                case RC_INVITE_FRIENDS:
                    Log.d(TAG, "Inside on activity result RC_INVITE_FRIENDS");
                    if (data != null) {
                        Log.d(TAG, "Inside on activity result RC_INVITE_FRIENDS not null");
                        Bundle args = data.getBundleExtra("BUNDLE");
                        List<Friends> friendsList = (ArrayList<Friends>) args.getSerializable(Constant.SELECTED_FRIENDS_FOR_EVENT);
                        for (Friends friend : friendsList) {
                            Log.d(TAG, "friend : " + friend.getFirst_name());
                        }

                        selectedFriendsSet.clear();
                        selectedFriendsSet.addAll(friendsList);
//                        addTagsToTagGroupFriends(selectedFriendsSet);
                        addFriendTags();
                        //refreshTags();
                    }
                    break;
            }
        }
    }

    private void getEmailsFromEt() {

        Log.d(TAG,"getEmailsFromEt 1");
        String emailsString = addEmailsEt.getText().toString();
        String[] emails = emailsString.split(",");
        for (String email : emails) {
            Log.d(TAG,"getEmailsFromEt 2");
            String emailTemp = email.trim();
            if (!emailTemp.equals("")) {
                if (isValidEmail(emailTemp)) {
                    Log.d(TAG,"getEmailsFromEt 3");
                    emailsSet.add(new EmailModel(emailTemp, noUserId, "n"));
                } else {
                    Log.d(TAG,"getEmailsFromEt 4");
                    showSnackbar(addEmailsEt, getString(R.string.please_enter_valid_emails), Constant.ERROR);
                }
            }
        }
        Log.d(TAG,"getEmailsFromEt 5");
        addEmailsEt.setText("");
        refreshTags();
    }


    private void removeTagToBeInvited(int tagId) {
        CustomTagFriend tempCustomTgFrnd = null;
        Friends removeFriend = null;
        for (CustomTagFriend customFriendTag : currentFriendTagList) {
            if (tagId == customFriendTag.getTagId()) {
                for (Friends friend : selectedFriendsSet) {
                    if (friend.getId() == customFriendTag.getFriend().getId()) {
                        tempCustomTgFrnd = customFriendTag;
                        removeFriend = friend;
                    }
                }
            }
        }
        if (tempCustomTgFrnd != null && removeFriend != null) {
            currentFriendTagList.remove(tempCustomTgFrnd);
            selectedFriendsSet.remove(removeFriend);
        }

        CustomTagEmail tempCustomTgEmail = null;
        EmailModel emailModel = null;
        for (CustomTagEmail customTagEmail : currentEmailTagList) {
            Log.d(TAG, "removeTagToBeInvited customTagEmail" + customTagEmail.getEmail() + customTagEmail.getTagId());
            if (tagId == customTagEmail.getTagId()) {
                Log.d(TAG, "removeTagToBeInvited customTagEmail" + customTagEmail.getEmail() + customTagEmail.getTagId());
                for (EmailModel email : emailsSet) {
                    Log.d(TAG, "removeTagToBeInvited customTagEmail" + customTagEmail.getEmail() + customTagEmail.getTagId());
                    if (customTagEmail.getEmail().equals(email.getEmail())) {
                        emailModel = email;
                        tempCustomTgEmail = customTagEmail;
                        Log.d(TAG, "removeTagToBeInvited customTagEmail.getEmail().equals(email): " + emailModel.getEmail() + "===" + tempCustomTgEmail.getEmail());
                    }
                }
            } else {
                Log.d(TAG, "tagId == customTagEmail.getTagId()");
            }
        }
        if (tempCustomTgEmail != null && emailModel != null) {
            Log.d(TAG, "inside remove");
            currentEmailTagList.remove(tempCustomTgEmail);
            emailsSet.remove(emailModel);
        }

        printTags("Remove Tag");
        refreshTags();
    }

    private void refreshTags() {
        currentEmailTagList.clear();
        currentFriendTagList.clear();
        toBeInvitedTg.removeAll();

        //addEmailTags();
        //addFriendTags();
        inviteFriendsPost(emailsSet);
    }

    //check added emails
    public void inviteFriendsPost(Set<EmailModel> emailsSet) {
        Log.d(TAG,"inviteFriendsPost 1");
        if (emailsSet.size() > 0) {
            List<String> emailList = new ArrayList<>();

            for (EmailModel emailModel : emailsSet) {
                emailList.add(emailModel.getEmail());
            }

            String[] emailArray = emailList.toArray(new String[emailList.size()]);
            if (isNetworkAvailable()) {
                Log.d(TAG,"inviteFriendsPost 2");
                jobManager.addJobInBackground(new PostEmailEventInviteWebJob(accessToken, emailArray, eventId));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"inviteFriendsPost 3");
                        startProgressBar();
                    }
                });
            } else {
                showSnackbar(addEmailsEt, getString(R.string.no_internet), Constant.ERROR);
            }
        } else  if(selectedFriendsSet.size() > 0){
            Log.d(TAG,"inviteFriendsPost 4");

            List<String> emailList = new ArrayList<>();

            for (Friends friendModel : selectedFriendsSet) {
                emailList.add(friendModel.getEmail());
            }

            String[] emailArray = emailList.toArray(new String[emailList.size()]);

            if (isNetworkAvailable()) {
                Log.d(TAG,"inviteFriendsPost 5");
                jobManager.addJobInBackground(new PostEmailEventInviteWebJob(accessToken, emailArray, eventId));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG,"inviteFriendsPost 6");
                        //startProgressBar();
                    }
                });
            } else {
                showSnackbar(addEmailsEt, getString(R.string.no_internet), Constant.ERROR);
            }

        }
        else {
            showSnackbar(addEmailsEt, getString(R.string.you_should_invite_at_least_one), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostEmailEventInviteWebJobCompleted(RawEmailsResponse rawEmailsResponse) {
        if (rawEmailsResponse != null) {
            if (rawEmailsResponse.getStatus() != null && rawEmailsResponse.getStatus().equals(Constant.SUCCESS)) {
                for (Data emailData : rawEmailsResponse.getData()) {
                    for (EmailModel emailModel : emailsSet) {
                        if (emailData.getEmail().equals(emailModel.getEmail())) {
                            Log.d(TAG, "Emails are equal: " + emailData.getEmail() + " : " + emailModel.getEmail());
                            emailModel.setUserId(emailData.getId());
                            emailModel.setDummy(emailData.getDummy());
                        }
                    }
                }
//                inviteFriendsPost();
                addEmailTags();
                addFriendTags();
                stopProgressBar();
            } else {
                stopProgressBar();
                showSnackbar(addEmailsEt, getString(R.string.unable_to_invite_friends_to_event), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(addEmailsEt, getString(R.string.unable_to_invite_friends_to_event), Constant.ERROR);
        }

    }


    private void addEmailTags() {
        List<Tag> tagList = new ArrayList<>();
        Tag tag;

        for (EmailModel emailModel : emailsSet) {

            tag = new Tag(emailModel.getEmail());
            tag.radius = 50f;
            tag.isDeletable = true;
            tag.id = emailModel.getEmail().hashCode();

            if(emailModel.getDummy().equalsIgnoreCase("y")){
                tag.layoutColor = ContextCompat.getColor(this, R.color.yellow);
            }else{
                tag.layoutColor = ContextCompat.getColor(this, R.color.blackblue);
            }
            tagList.add(tag);
            Log.d(TAG, "adding tag inside for loop");
            currentEmailTagList.add(new CustomTagEmail(tag.text, tag.id));

            final Tag finalTag = tag;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toBeInvitedTg.addTag(finalTag);
                }
            });
        }

        for (Tag tag2 : tagList) {
            Log.d(TAG, tag2.text);
        }

        printTags("add Email");
    }

    private void addAlreadyAddedFriendTags() {
        List<Tag> tagList = new ArrayList<>();
        Tag tag;

        for (AttendeeList tempFriend : alreadyInvitedFriendsList) {
            tag = new Tag(tempFriend.getFirst_name() != null ? " " + tempFriend.getFirst_name() : tempFriend.getEmail());
            tag.radius = 50f;

            if(tempFriend.getStatus().equalsIgnoreCase("dummy")){
                tag.layoutColor = ContextCompat.getColor(this, R.color.yellow);
            }else{
                tag.layoutColor = ContextCompat.getColor(this, R.color.blackblue);
            }

            tag.isDeletable = true;
            tag.id = (tempFriend.getUser_id() + "" + tempFriend.getEmail()).hashCode();
            tagList.add(tag);
            alreadyInvitedTagList.add(new CustomTagAlreadyInvitedFriend(tempFriend, tag.id));
            alreadyInvitedTg.addTag(tag);
        }
        printTags("Add friends");
    }


    private void addFriendTags() {
        List<Tag> tagList = new ArrayList<>();
        Tag tag;

        for (Friends tempFriend : selectedFriendsSet) {
            tag = new Tag(tempFriend.getFirst_name() + (tempFriend.getLast_name() != null ? " " + tempFriend.getLast_name() : ""));
            tag.radius = 50f;
            tag.layoutColor = ContextCompat.getColor(this, R.color.blackblue);
            tag.isDeletable = true;
            tag.id = (tempFriend.getId() + "" + tempFriend.getFirst_name()).hashCode();
            tagList.add(tag);
            currentFriendTagList.add(new CustomTagFriend(tempFriend, tag.id));

            final Tag finalTag = tag;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toBeInvitedTg.addTag(finalTag);
                }
            });

        }
        printTags("Add friends");
    }


    private void inviteFriendsPost() {

        if (emailsSet.size() > 0 || selectedFriendsSet.size() > 0) {
            List<Integer> inviteUsList = new ArrayList<>();
            for (EmailModel emails : emailsSet) {
                inviteUsList.add(emails.getUserId());
            }

            for (Friends frnd : selectedFriendsSet) {
                inviteUsList.add(frnd.getId());
            }

            String inviteUsUids = TextUtils.join(",", inviteUsList);
            Log.d(TAG, "inviteUsUids: " + inviteUsUids);
            Log.d(TAG, "invite eventId: " + eventId);

            if (isNetworkAvailable()) {
                jobManager.addJobInBackground(new PostEventInviteFriendsWebJob(accessToken, inviteUsUids, eventId));
                startProgressBar();
            } else {
                showSnackbar(addEmailsEt, getString(R.string.no_internet), Constant.ERROR);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostEventInviteFriendsWebJob(HttpResponse httpResponse) {

        stopProgressBar();

        if (httpResponse != null) {
            if (httpResponse.getStatus() != null) {
                if (httpResponse.getStatus().equals(Constant.SUCCESS)) {

                    if(isEditEvent){
                        showSnackbar(addEmailsEt, getString(R.string.event_invitations_sent), Constant.SUCCESS);
                        Log.d(TAG, "onPostEventInviteFriendsWebJob 1");

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "onPostEventInviteFriendsWebJob 2");
                                InviteFriendsToEventActivity.this.finish();
                            }
                        }, 3000);

                    }else {
                        showSnackbar(addEmailsEt, getString(R.string.all_invitations_sent), Constant.SUCCESS);
                    }

                } else {
                    showSnackbar(addEmailsEt, httpResponse.getMessage(), Constant.SUCCESS);
                }
            } else {
                showSnackbar(addEmailsEt, getString(R.string.could_not_send_event_invitations), Constant.SUCCESS);

            }
        } else {
            showSnackbar(addEmailsEt, getString(R.string.could_not_send_event_invitations), Constant.SUCCESS);
        }
    }

    private void sendToDashBoardActivity() {
        Intent intent = new Intent(this,DashboardActivity.class);
        startActivity(intent);
    }


    @Override
    public void onTagDeleted(TagView tagView, Tag tag, int i) {
        tagView.remove(i);
        if (tagView.getId() == R.id.toBeInvitedTg) {
            Log.d(TAG, "Remove Tag: > tagId = " + tag.id);
            removeTagToBeInvited(tag.id);
        } else if (tagView.getId() == R.id.alreadyInvitedTg) {
            Log.d(TAG, "Remove Tag: > tagId = " + tag.id);
            removeTagAlreadyInvited(tag.id);
        }
    }

    private void removeTagAlreadyInvited(int id) {
        Log.d(TAG, "(removeTagAlreadyInvited) inside removeTagAlreadyInvited tagId: " + id);
        CustomTagAlreadyInvitedFriend tempCustomTgFrnd = null;
        AttendeeList removeFriend = null;
        for (CustomTagAlreadyInvitedFriend customFriendTag : alreadyInvitedTagList) {
            if (id == customFriendTag.getTagId()) {
                Log.d(TAG, "(removeTagAlreadyInvited) id == customFriendTag.getTagId() " + id + " : " + customFriendTag.getTagId());
                for (AttendeeList friend : alreadyInvitedFriendsList) {
                    if (friend.getUser_id() == customFriendTag.getFriend().getUser_id()) {
                        Log.d(TAG, "(removeTagAlreadyInvited) friend.getUser_id() == customFriendTag.getFriend().getUser_id() " + friend.getUser_id() + " : " + customFriendTag.getFriend().getUser_id());
                        tempCustomTgFrnd = customFriendTag;
                        removeFriend = friend;
                    } else {
                        Log.d(TAG, "(removeTagAlreadyInvited) friend.getUser_id() == customFriendTag.getFriend().getUser_id() ELSE");
                    }
                }
            } else {
                Log.d(TAG, "(removeTagAlreadyInvited) id == customFriendTag.getTagId() ELSE");
            }
        }
        Log.d(TAG, "(removeTagAlreadyInvited) tempCustomTgFrnd : " + tempCustomTgFrnd.toString() + " removeFriend : " + removeFriend.toString());
        if (tempCustomTgFrnd != null && removeFriend != null) {
            alreadyInvitedTagList.remove(tempCustomTgFrnd);
            alreadyInvitedFriendsList.remove(removeFriend);

            deleteEventInvite(this.eventId, removeFriend.getUser_id());
        }
    }

    private void deleteEventInvite(int eventId, int user_id) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new DeleteEventInviteWebJob(accessToken, eventId, user_id));
        } else {
            showSnackbar(toBeInvitedTg, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    private void printTags(String tagPlce) {
        Log.d(TAG, ":::::::::" + tagPlce + ":::::::::::" + toBeInvitedTg.getTags().size() + "\n" + "currentFriendsTags size: " + currentFriendTagList.size()
                + "\n" + "currentEmailTagList size: " + currentEmailTagList.size());

        Log.d(TAG, ":::::::::" + tagPlce + ":::::::::::" + toBeInvitedTg.getTags().size() + "\n" + "currentFriends size: " + selectedFriendsSet.size()
                + "\n" + "currentEmails size: " + emailsSet.size());

        for (CustomTagFriend friend :
                currentFriendTagList) {
            Log.d(TAG, "CustomTagFriend: " + friend.getTagId() + " | " + friend.getFriend().getFirst_name());
        }

        for (CustomTagEmail email :
                currentEmailTagList) {
            Log.d(TAG, "CustomTagEmail: " + email.getTagId() + " | " + email.getEmail());
        }

        for (Friends email :
                selectedFriendsSet) {
            Log.d(TAG, "selectedFriendsSet: " + email.getId() + " | " + email.getFirst_name());
        }

        for (EmailModel email : emailsSet) {
            Log.d(TAG, "emailsSet: " + email.getEmail());
        }
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private void goToEventDetailsScreen(Event event){
        Intent intent = new Intent(this, EventDetailsHostActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constant.SELECTED_EVENT_ITEM, (Serializable) event);
        intent.putExtra("BUNDLE_EVENT", args);
        startActivity(intent);
        finish();
    }
}
