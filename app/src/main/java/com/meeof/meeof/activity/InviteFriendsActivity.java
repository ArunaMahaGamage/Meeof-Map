package com.meeof.meeof.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.AppInviteContent;
import com.facebook.share.widget.AppInviteDialog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.PeopleScopes;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Photo;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.FriendsRecyclerAdapter;
import com.meeof.meeof.adapter.PotentialFriendsRecyclerAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.helper.PeopleHelper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.HttpResponseForAddFriend;
import com.meeof.meeof.model.RetrievePotentialFriendsWebJobResponse;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_dto.FacebookFriendModel;
import com.meeof.meeof.model.friends_dto.FriendListItem;
import com.meeof.meeof.model.friends_dto.GoogleFriendsModel;
import com.meeof.meeof.model.friends_dto.PhoneContactFriendModel;
import com.meeof.meeof.model.search_friends_dto.Data;
import com.meeof.meeof.model.search_friends_dto.SearchFriendsResponse;
import com.meeof.meeof.model.search_query_dto.SearchFriendsQueryResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.Utility;
import com.meeof.meeof.webjob.GetInviteFriendWebJob;
import com.meeof.meeof.webjob.GetPotentialFriends;
import com.meeof.meeof.webjob.GetSearchMeeofFriendsWebJob;
import com.meeof.meeof.webjob.PostAddAsFriendWebJob;
import com.meeof.meeof.webjob.RetrievePotentialFriendsWebJob;
import com.meeof.meeof.webjob.SearchFriendsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by ransikadesilva on 10/9/17.
 */

public class InviteFriendsActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, TextWatcher {

    private static final String TAG = InviteFriendsActivity.class.getSimpleName();
    private String accessToken;

    private GoogleApiClient mGoogleApiClient;

    final int RC_INTENT = 200;
    final int RC_API_CHECK = 100;
    private AppCompatImageView back_button;
    private ImageView facebookFriendsIvBtn, googleFriendsIvBtn, contactsFriendsIvBtn;


    private RecyclerView friends_recycleView, mightKnowFriendsRv;
    private EditText searchEditText;
    private TextView doneTvBtn;

    private String currentClicked;

    private List<GoogleFriendsModel> gPFriendList;
    public List<FacebookFriendModel> fBFriendList; //inner class
    private List<PhoneContactFriendModel> contactsFriendList;
    private FriendsRecyclerAdapter friendsRecyclerAdapter;
    private PotentialFriendsRecyclerAdapter mightKnowRecyclerAdapter;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView friendsFromTv;
    private GoogleSignInAccount acct;
    private TextView fbfriendsTopTv;
    private TextView fbfriendsBottomTv;
    private TextView gfriendsTopTv;
    private TextView gfriendsBottomTv;
    private TextView mobileContactsTopTv;
    private TextView mobileContactsBottomTv;
    private LinearLayout bottomLineTxtLl;
    private LinearLayout mightKnowLl;
    private boolean isFromInterests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friends);

        if (getIntent().hasExtra(Constant.IS_FROM_INTERESTS)) {
            isFromInterests = this.getIntent().getBooleanExtra(Constant.IS_FROM_INTERESTS, false);
        }

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        initViews();
        setUpGoogleSignInPeople();
        setUpFacebookSignIn();
        facebookFriendsIvBtn.callOnClick(); //INITIALLY FB

    }

    public void retrivePotentialFriends(){
        jobManager.addJobInBackground(new RetrievePotentialFriendsWebJob(accessToken));
        startProgressBar();
    }

    private void setUpFacebookSignIn() {

        callbackManager = CallbackManager.Factory.create();


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public String fbToken;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("ACCESS TOKEN FB", loginResult.getAccessToken().getToken().toString());
                fbToken = loginResult.getAccessToken().getToken().toString();
//                Toast.makeText(GetStartedActivity.this, "FB TOKEN : " + fbToken, Toast.LENGTH_LONG).show();
                Log.d(TAG, "fbToken : " + fbToken);
                aquireFacebookFriendList();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "loginButton onError: " + error.toString());
            }

        });


    }

    private void setUpGoogleSignInPeople() {
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.clientID), false)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN),
                        new Scope(PeopleScopes.CONTACTS_READONLY),
                        new Scope(PeopleScopes.USER_EMAILS_READ),
                        new Scope(PeopleScopes.USERINFO_EMAIL),
                        new Scope(PeopleScopes.USER_PHONENUMBERS_READ))
                .build();


        // To connect with Google Play Services and Sign In
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, signInOptions)
                .build();

    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);
        facebookFriendsIvBtn = (ImageView) findViewById(R.id.facebookFriendsIvBtn);
        googleFriendsIvBtn = (ImageView) findViewById(R.id.googleFriendsIvBtn);
        contactsFriendsIvBtn = (ImageView) findViewById(R.id.contactsFriendsIvBtn);
        friends_recycleView = (RecyclerView) findViewById(R.id.friends_recycleView);
        mightKnowFriendsRv = (RecyclerView) findViewById(R.id.mightKnowFriendsRv);
        friendsFromTv = (TextView) findViewById(R.id.friendsFromTv);

        fbfriendsTopTv = (TextView) findViewById(R.id.fbfriendsTopTv);
        fbfriendsBottomTv = (TextView) findViewById(R.id.fbfriendsBottomTv);
        gfriendsTopTv = (TextView) findViewById(R.id.gfriendsTopTv);
        gfriendsBottomTv = (TextView) findViewById(R.id.gfriendsBottomTv);
        mobileContactsTopTv = (TextView) findViewById(R.id.mobileContactsTopTv);
        mobileContactsBottomTv = (TextView) findViewById(R.id.mobileContactsBottomTv);

        bottomLineTxtLl = (LinearLayout)findViewById(R.id.bottomLineTxtLl);
        mightKnowLl = (LinearLayout)findViewById(R.id.mightKnowLl);

        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        doneTvBtn = (TextView) findViewById(R.id.doneTvBtn);

        searchEditText = (EditText) findViewById(R.id.searchEditText);

        if(isFromInterests){
            doneTvBtn.setText("Skip");
            doneTvBtn.setVisibility(View.VISIBLE);
        }

        facebookFriendsIvBtn.setOnClickListener(this);
        googleFriendsIvBtn.setOnClickListener(this);
        contactsFriendsIvBtn.setOnClickListener(this);
        searchEditText.addTextChangedListener(this);
        doneTvBtn.setOnClickListener(this);
        back_button.setOnClickListener(this);
        doneTvBtn.setOnClickListener(this);
        friendsFromTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backAcIvBtn:
                this.onBackPressed();
                break;
            case R.id.facebookFriendsIvBtn:
                facebookFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button));
                googleFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_deselected));
                contactsFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_deselected));

                friendsFromTv.setText("Facebook");
                searchEditText.setText("");
                Log.d(TAG, "facebookFriendsIvBtn");
                currentClicked = Constant.FACEBOOK;
                clearListData();
                aquireFacebookFriendList();
                break;
            case R.id.googleFriendsIvBtn:
                contactsFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_deselected));
                googleFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button));
                facebookFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_deselected));
                friendsFromTv.setText("Google");
                searchEditText.setText("");
                currentClicked = Constant.GOOGLE;
                clearListData();
                getIdTokenGoogle();
                break;
            case R.id.contactsFriendsIvBtn:
                contactsFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button));
                facebookFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_deselected));
                googleFriendsIvBtn.setBackground(ContextCompat.getDrawable(this, R.drawable.circle_button_deselected));
                friendsFromTv.setText("Contacts");
                searchEditText.setText("");
                currentClicked = Constant.CONTACTS;
                clearListData();
                aquireContactsFriendList();
                break;
            case R.id.doneTvBtn:
                sendToDashboardActivity();
                Helper.clickGaurd(doneTvBtn);
                break;
        }
    }

    private void getPotentialFriends() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetPotentialFriends(accessToken));
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetPotentialFriends(FriendsAllResponse friendsAllResponse) {
        stopProgressBar();
        if (friendsAllResponse != null) {
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                if (friendsAllResponse.getFriends() != null && friendsAllResponse.getFriends().size() > 0) {
                    setDataToListPotentialFriends(); //TODO POTENTIAL FRIENDS
                    mightKnowLl.setVisibility(View.VISIBLE);
                } else {
                    mightKnowLl.setVisibility(View.GONE);
                  //  showSnackbar(facebookFriendsIvBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                }
            } else {
              //  showSnackbar(facebookFriendsIvBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            //showSnackbar(facebookFriendsIvBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void setDataToListPotentialFriends() { //TODO POTENTIAL FRIENDS

    }

    private void sendToDashboardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

    private void getFriendListStatusContactsFriends(List<PhoneContactFriendModel> phoneContactFriendModelList) {
        List<String> emails = new ArrayList<>();
        for (PhoneContactFriendModel phoneFriend : phoneContactFriendModelList) {
            if (phoneFriend.getEmail() != null) {
                emails.add(phoneFriend.getEmail());
            }
        }
        String[] arrayEmails = emails.toArray(new String[0]);
        if (isNetworkAvailable()) {
            startProgressBar();
            if (emails.size() > 0) {
//                String[] temp = {"testchum3acc@gmail.com"}; //TODO REMOVE

                jobManager.addJobInBackground(new SearchFriendsWebJob(accessToken,arrayEmails));
            } else {
                String[] tempStr = new String[0];
                jobManager.addJobInBackground(new SearchFriendsWebJob(accessToken, tempStr));
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    private void aquireContactsFriendList() {
        if (Utility.checkReadContactPermission(this)) {
            new PhoneContactsAsyncTask().execute("param");
        } else {
            //showSnackbar(facebookFriendsIvBtn, getString(R.string.crop_image_activity_no_permissions), Constant.ERROR); //TODO
        }
    }


    private void aquireFacebookFriendList() {
        if (isNetworkAvailable()) {
            startProgressBar();
            final String graphPath = "me/friends";
            AccessToken token = AccessToken.getCurrentAccessToken();

            if (token != null) {

                Log.wtf(TAG, "Current FB Access Token:1 " + token.getToken());

                GraphRequest request = new GraphRequest(token, graphPath, null, HttpMethod.GET, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        Log.wtf(TAG, "onCompleted graphResponse1 " +graphResponse.toString());
                        JSONObject object = graphResponse.getJSONObject();
                        Log.wtf(TAG, "onCompleted graphResponse2 " +object.toString());
                        try {
                            if (object != null) {


                                if(object.getJSONArray("data") != null){

                                    JSONArray arrayOfUsersInFriendList = object.getJSONArray("data");
                                    Log.wtf(TAG, "JSON DATA: " + arrayOfUsersInFriendList);

                                    ObjectMapper objectMapper = new ObjectMapper();
                                    try {

                                        List<FacebookFriendModel> facebooFriendList =
                                                objectMapper.readValue(arrayOfUsersInFriendList.toString(),
                                                        new TypeReference<List<FacebookFriendModel>>() {
                                                        });
                                        fBFriendList = facebooFriendList;
                                        getFriendListStatusFacebookFriends(facebooFriendList);

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else{
                                    showSnackbar(facebookFriendsIvBtn, getString(R.string.get_facebook_friends_failed), Constant.ERROR);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.wtf(TAG, "graph error : " + e.toString());
                        }
                    }
                });
                Bundle param = new Bundle();
                param.putString("fields", "id, first_name, last_name, middle_name, name, email, picture");
                request.setParameters(param);
                request.executeAsync();
            } else {
                loginButton.performClick();
                Log.wtf(TAG, "please login to facebook");
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
        stopProgressBar();
        retrivePotentialFriends();
    }

    private void getFriendListStatusFacebookFriends(List<FacebookFriendModel> facebooFriendList) {
        List<String> emails = new ArrayList<>();
        for (FacebookFriendModel facebookFriend : facebooFriendList) {
            if (facebookFriend.getEmail() != null) {
                emails.add(facebookFriend.getEmail());
            }
        }

        String[] arrayEmails = emails.toArray(new String[0]);
        if (isNetworkAvailable()) {
            startProgressBar();
            if (emails.size() > 0) {

                 jobManager.addJobInBackground(new SearchFriendsWebJob(accessToken,arrayEmails));
            } else {
                String[] tempStr = new String[0];
                jobManager.addJobInBackground(new SearchFriendsWebJob(accessToken, tempStr));
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }

    }

    @Subscribe
    public void onSearchFriendsJobCompleted(SearchFriendsResponse searchFriends) {
        Log.wtf(TAG, "onSearchFriendsJobCompleted");
        stopProgressBar();
        if (searchFriends != null) {
            if (searchFriends.getStatus() != null && searchFriends.getStatus().equals(Constant.SUCCESS)) {
                setDataToListFBGoogleContacts(searchFriends.getData());

            } else {
                stopProgressBar();
                showSnackbar(facebookFriendsIvBtn, getString(R.string.friend_search_failed), Constant.ERROR);
            }
        } else {
            stopProgressBar();
            showSnackbar(facebookFriendsIvBtn, getString(R.string.friend_search_failed), Constant.ERROR);
        }
    }

    private void setDataToListFBGoogleContacts(List<Data> data) {
        List<FriendListItem> listItemFriend = new ArrayList<>();
        if (currentClicked.equals(Constant.FACEBOOK)) {
            if (fBFriendList != null && fBFriendList.size() > 0) {
                if (data.size() > 0) {
                    for (FacebookFriendModel fbFriend : fBFriendList) {
                        for (Data friend : data) {
                            try {
                                if (friend.getEmail().equals(fbFriend.getEmail())) {
                                    listItemFriend.add(new FriendListItem(friend.getUser_id() + "",
                                            friend.getFirst_name(),
                                            friend.getStatus(),
                                            fbFriend.getPicture().getData().getUrl(),
                                            friend.getEmail(),
                                            friend.getFriend_status()));
                                } else {
                                    listItemFriend.add(new FriendListItem(friend.getUser_id() + "",
                                            fbFriend.getFirst_name() + " " + fbFriend.getLast_name(),
                                            "none",
                                            fbFriend.getPicture().getData().getUrl(),
                                            fbFriend.getEmail(),
                                            "none"));
                                }
                            } catch (Exception ex) {
                                listItemFriend.add(new FriendListItem("-1",
                                        fbFriend.getFirst_name() + " " + fbFriend.getLast_name(),
                                        "none",
                                        fbFriend.getPicture().getData().getUrl(),
                                        fbFriend.getEmail(),
                                        "none"));
                            }
                        }
                    }
                } else {
                    for (FacebookFriendModel friend : fBFriendList) {
                        listItemFriend.add(new FriendListItem("-1",
                                friend.getFirst_name() + " " + friend.getLast_name(),
                                "none",
                                friend.getPicture().getData().getUrl(),
                                friend.getEmail(),
                                "none"));
                    }
                }

                setDataList(listItemFriend);
            }
        } else if (currentClicked.equals(Constant.GOOGLE)) {
            if (gPFriendList != null && gPFriendList.size() > 0) {
                if (data.size() > 0) {
                    for (GoogleFriendsModel fbFriend : gPFriendList) {
                        for (Data friend : data) {
                            if (acct != null && !acct.getEmail().equals(friend.getEmail())) {
                                try {
                                    if (friend.getEmail().equals(fbFriend.getEmail())) {
                                        listItemFriend.add(new FriendListItem(friend.getUser_id() + "",
                                                friend.getFirst_name(),
                                                friend.getStatus(),
                                                fbFriend.getPhotoUrl(),
                                                friend.getEmail(),
                                                friend.getFriend_status()));
                                    } else {
                                        listItemFriend.add(new FriendListItem(friend.getUser_id() + "",
                                                fbFriend.getName(),
                                                "none",
                                                fbFriend.getPhotoUrl(),
                                                fbFriend.getEmail(),
                                                "none"));
                                    }
                                } catch (Exception ex) {
                                    listItemFriend.add(new FriendListItem("-1",
                                            fbFriend.getName(),
                                            "none",
                                            fbFriend.getPhotoUrl(),
                                            fbFriend.getEmail(),
                                            "none"));
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                } else {
                    for (GoogleFriendsModel friend : gPFriendList) {
                        listItemFriend.add(new FriendListItem("-1",
                                friend.getName(),
                                "none",
                                friend.getPhotoUrl(),
                                friend.getEmail(),
                                "none"));
                    }
                }
                setDataList(listItemFriend);
            }
        } else if (currentClicked.equals(Constant.CONTACTS)) {
            if (contactsFriendList != null && contactsFriendList.size() > 0) {
                if (data.size() > 0) {
                    for (PhoneContactFriendModel contactFriend : contactsFriendList) {
                        for (Data friend : data) {
                            Log.d(TAG,"InviteFriendsMultipleContacts: 0, "+ friend);
                            try {
                                if (friend.getEmail().equals(contactFriend.getEmail())) {
                                    Log.d(TAG,"InviteFriendsMultipleContacts: 1, "+ friend.getEmail()+", contactFriend: "+contactFriend.getEmail());

//                                    for (FriendListItem friendListCheckItem : listItemFriend){
//                                        if (!friendListCheckItem.getEmail().equals(friend.getEmail())){
                                            listItemFriend.add(new FriendListItem(friend.getUser_id() + "",
                                                    friend.getFirst_name(),
                                                    friend.getStatus(),
                                                    contactFriend.getUri(),
                                                    friend.getEmail(),
                                                    friend.getFriend_status()));
//                                        }
//                                    }


                                } else {
                                    Log.d(TAG,"InviteFriendsMultipleContacts: 2, "+ friend.getEmail()+", contactFriend: "+contactFriend.getEmail());
//                                    for (FriendListItem friendListCheckItem : listItemFriend){
//                                        if (!friendListCheckItem.getEmail().equals(friend.getEmail())){
//                                            listItemFriend.add(new FriendListItem(friend.getUser_id() + "",
//                                                    contactFriend.getName(),
//                                                    "none",
//                                                    contactFriend.getUri(),
//                                                    contactFriend.getEmail(),
//                                                    "none"));
//                                        }
//                                    }

                                }
                            } catch (Exception ex) {
                                Log.d(TAG,"InviteFriendsMultipleContacts: 3, "+ friend.getEmail()+", contactFriend: "+contactFriend.getEmail());

//                                listItemFriend.add(new FriendListItem("-1",
//                                        contactFriend.getName(),
//                                        "none",
//                                        contactFriend.getUri(),
//                                        contactFriend.getEmail(),
//                                        "none"));
                            }
                        }
                    }
                } else {
                    for (PhoneContactFriendModel friend : contactsFriendList) {
                        listItemFriend.add(new FriendListItem("-1",
                                friend.getName(),
                                "none",
                                friend.getUri(),
                                friend.getEmail(),
                                "none"));
                    }
                }

                setDataList(listItemFriend);
            }
        }
        stopProgressBar();
    }

    private void clearListData(){
        List<FriendListItem> fbFriendList = new ArrayList<>();
        friendsRecyclerAdapter = new FriendsRecyclerAdapter(InviteFriendsActivity.this, fbFriendList);
        friends_recycleView.setLayoutManager(new LinearLayoutManager(InviteFriendsActivity.this));
        friends_recycleView.setAdapter(friendsRecyclerAdapter);
        friends_recycleView.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerAdapter.notifyDataSetChanged();
    }

    private void setDataList(final List<FriendListItem> fbFriendList) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                friendsRecyclerAdapter = new FriendsRecyclerAdapter(InviteFriendsActivity.this, fbFriendList);
                friends_recycleView.setLayoutManager(new LinearLayoutManager(InviteFriendsActivity.this));
                friends_recycleView.setAdapter(friendsRecyclerAdapter);
                friends_recycleView.setItemAnimator(new DefaultItemAnimator());
                friendsRecyclerAdapter.notifyDataSetChanged();

                friendsRecyclerAdapter.setOnClick(new FriendsRecyclerAdapter.OnItemClicked() {
                    @Override
                    public void onItemClick(int position, FriendListItem item, FriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {

                        Log.d(TAG,"invite user clicked 2");

                        if (item.getStatus().equals("active")) {
                            if (item.getFriend_status().equals("friend")) {
                                //NOTHING
                            } else if (item.getFriend_status().equals("pending")) {
                                //NOTHING
                            } else if (item.getFriend_status().equals("none")) {
                                Log.d(TAG,"::: INSIDE ADD AS FRIEND :::");
                                addAsFriend(item);
                            } else {
                                if (currentClicked.equals(Constant.FACEBOOK)) {
                                    showFacebookInviteDialog();
                                } else {
                                    inviteFriend(item);
                                }
                            }
                        } else if (item.getStatus().equals("dummy")) {
                            Log.d(TAG,"invite user clicked 3");

                            if (currentClicked.equals(Constant.FACEBOOK)) {
                                showFacebookInviteDialog();
                            } else {
                                inviteFriend(item);
                            }
                        } else {
                            Log.d(TAG,"invite user clicked 4");
                            if (currentClicked.equals(Constant.FACEBOOK)) {
                                Log.d(TAG,"invite user clicked 5");
                                showFacebookInviteDialog();
                            } else {
                                Log.d(TAG,"invite user clicked 6");
                                inviteFriend(item);
                            }
                        }
                    }


                });
            }
        });
    }

    private void setMightKnowDataList(final List<com.meeof.meeof.model.potential_friends.Data> fbFriendList) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(fbFriendList.size() > 0){
                    mightKnowLl.setVisibility(View.VISIBLE);
                    mightKnowRecyclerAdapter = new PotentialFriendsRecyclerAdapter(InviteFriendsActivity.this, fbFriendList);
                    mightKnowFriendsRv.setLayoutManager(new LinearLayoutManager(InviteFriendsActivity.this));
                    mightKnowFriendsRv.setAdapter(mightKnowRecyclerAdapter);
                    mightKnowFriendsRv.setItemAnimator(new DefaultItemAnimator());
                    mightKnowRecyclerAdapter.notifyDataSetChanged();

                    mightKnowRecyclerAdapter.setOnClick(new PotentialFriendsRecyclerAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position, com.meeof.meeof.model.potential_friends.Data item, PotentialFriendsRecyclerAdapter.MyViewHolderFriend myViewHolderParcel) {
                            if (item.getStatus().equals("friend")) {
                                //NOTHING
                            } else if (item.getStatus().equals("pending")) {
                                //NOTHING
                            } else if (item.getStatus().equals("none")) {
                                Log.d(TAG,"::: INSIDE ADD AS FRIEND :::");
                                addAsFriend(item.getUser_id());
                            }
                        }
                    });

                }else{
                    mightKnowLl.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showFacebookInviteDialog() {
        String appLinkUrl, previewImageUrl;

        appLinkUrl = Constant.FB_APP_LINK;
        previewImageUrl = Constant.FB_APP_IMAGE_LINK;

        if (AppInviteDialog.canShow()) {
            AppInviteContent content = new AppInviteContent.Builder()
                    .setApplinkUrl(appLinkUrl)
                    .setPreviewImageUrl(previewImageUrl)
                    .build();
            AppInviteDialog.show(this, content);
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void inviteFriend(FriendListItem item) {
        Log.d(TAG, "inviteFriend123");
        if (isNetworkAvailable()) {
            if (item.getEmail() != null && !item.getEmail().equals(""))
                jobManager.addJobInBackground(new GetInviteFriendWebJob(accessToken, item.getEmail()));
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetInviteFriendsWebJob(HttpResponse httpResponseInviteFriend) {
        stopProgressBar();
        if (httpResponseInviteFriend != null) {
            if (httpResponseInviteFriend.getStatus() != null && httpResponseInviteFriend.getStatus().equals(Constant.SUCCESS)) {
                //
                showSnackbar(facebookFriendsIvBtn, getString(R.string.successfully_invited_as_friend), Constant.SUCCESS);
            } else {
                showSnackbar(facebookFriendsIvBtn, getString(R.string.invitation_send_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.invitation_send_failed), Constant.ERROR);
        }

    }

    private void addAsFriend(FriendListItem item) {
        Log.wtf(TAG,"::: INSIDE ADD AS FRIEND :::");
        if (isNetworkAvailable()) {
            int userId = Integer.parseInt(item.getId());
            Log.wtf(TAG,"::: INSIDE ADD AS FRIEND userId:=> "+userId+"::");
            if (userId >= 0) {
                Log.wtf(TAG,"::: INSIDE ADD AS FRIEND userId:::");
                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
            } else {
                Log.d(TAG,"::: INSIDE ADD AS FRIEND else :::");
                showSnackbar(facebookFriendsIvBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            }
        } else {
            Log.d(TAG,"::: INSIDE ADD AS FRIEND else else :::");
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void addAsFriend(int userId) {
        Log.wtf(TAG,"::: INSIDE ADD AS FRIEND :::");
        if (isNetworkAvailable()) {
            Log.wtf(TAG,"::: INSIDE ADD AS FRIEND userId:=> "+userId+"::");
            if (userId >= 0) {
                Log.wtf(TAG,"::: INSIDE ADD AS FRIEND userId:::");
                jobManager.addJobInBackground(new PostAddAsFriendWebJob(accessToken, userId + ""));
            } else {
                Log.d(TAG,"::: INSIDE ADD AS FRIEND else :::");
                showSnackbar(facebookFriendsIvBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            }
        } else {
            Log.d(TAG,"::: INSIDE ADD AS FRIEND else else :::");
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostAddAsFriendJobCompleted(HttpResponseForAddFriend httpResponseForAddFriend) {
        if (httpResponseForAddFriend != null) {
            if (httpResponseForAddFriend.getStatus() != null && httpResponseForAddFriend.getStatus().equals(Constant.SUCCESS)) {
                showSnackbar(facebookFriendsIvBtn, getString(R.string.successfully_added_as_friend), Constant.SUCCESS);
            } else {
                showSnackbar(facebookFriendsIvBtn, getString(R.string.friend_request_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.friend_request_failed), Constant.ERROR);
        }

    }

    private void getIdTokenGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_INTENT) {
                Log.d(TAG, "sign in result google");
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {
                    acct = result.getSignInAccount();
                    Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());

                    Log.d(TAG, "auth Code:" + acct.getServerAuthCode());

                    new GoogleFriendListAsync().execute(acct.getServerAuthCode());

                } else {

                    Log.d(TAG, result.getStatus().toString() + "\nmsg: " + result.getStatus().getStatusMessage());
                }
            } else {
                Log.d(TAG, "sign in result facebook");
                callbackManager.onActivityResult(requestCode, resultCode, data); //facebook
            }

        }
    }

    private void searchFriends(String s) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetSearchMeeofFriendsWebJob(accessToken, s));
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetSearchMeeofFriendsWebJobCompleted(SearchFriendsQueryResponse searchFriendsQueryResponse) {
        stopProgressBar();
        if (searchFriendsQueryResponse != null) {

            if (searchFriendsQueryResponse.getStatus() != null && searchFriendsQueryResponse.getStatus().equals(Constant.SUCCESS)) {

                List<FriendListItem> tempFriendList = new ArrayList<>();

                List<com.meeof.meeof.model.search_query_dto.Data> listFriendData = searchFriendsQueryResponse.getData();

                for (com.meeof.meeof.model.search_query_dto.Data friend : listFriendData) {

                    String name = friend.getFirst_name();
                    String email = friend.getEmail();
                    String status = friend.getStatus();
                    int userId = friend.getId();
                    String friendStatus = friend.getFriend_status();
                    String photoUrl = friend.getProfilephoto() == null || friend.getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                            "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + friend.getProfilephoto();
                    FriendListItem tempFriend = new FriendListItem(userId + "", name, status, photoUrl, email, friendStatus);
                    Log.w(TAG, "Friend Search: " + tempFriend.toString());
                    tempFriendList.add(tempFriend);
                }

                setDataList(tempFriendList);
            }
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("connection", "msg: " + connectionResult.getErrorMessage());

        GoogleApiAvailability mGoogleApiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = mGoogleApiAvailability.getErrorDialog(this, connectionResult.getErrorCode(), RC_API_CHECK);
        dialog.show();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private void searchFriendList(String s) {
        if (friendsRecyclerAdapter != null) {
            friendsRecyclerAdapter.filter(s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        searchFriendList(s.toString());
    }

    class GoogleFriendListAsync extends AsyncTask<String, Void, List<GoogleFriendsModel>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<GoogleFriendsModel> doInBackground(String... params) {

            List<String> nameList = new ArrayList<>();
            List<GoogleFriendsModel> googleFriends = new ArrayList<>();
            try {
                People peopleService = PeopleHelper.setUp(InviteFriendsActivity.this, params[0]);

                ListConnectionsResponse response = peopleService.people().connections()
                        .list("people/me")
                        .setRequestMaskIncludeField("person.names,person.emailAddresses,person.phoneNumbers")
                        .execute();
                List<Person> connections = response.getConnections();
                if (connections != null) {
                    for (Person person : connections) {
                        List<Name> names = person.getNames();
                        List<EmailAddress> emailAddresses = person.getEmailAddresses();
                        List<Photo> personPhotos = person.getPhotos();
                        if (person != null && !person.isEmpty()) {
                            if (names != null) {
                                for (Name name1 : names) {
                                    if (name1.getDisplayName() != null && !name1.isEmpty()) {
                                        if (emailAddresses != null) {
                                            for (EmailAddress email1 : emailAddresses) {
                                                if (email1.getValue() != null && !email1.isEmpty()) {
                                                    String name = name1.getDisplayName();
                                                    String email = email1.getValue();
                                                    String photoUrl = "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png";
                                                    if (personPhotos != null) {
                                                        for (Photo picture : personPhotos) {
                                                            if (picture != null && picture.getUrl() != null) {
                                                                photoUrl = picture.getUrl();
                                                            }
                                                        }
                                                    }
                                                    GoogleFriendsModel tempGoogleFriend = new GoogleFriendsModel(name, email, photoUrl);
                                                    googleFriends.add(tempGoogleFriend);
                                                    Log.d(TAG, tempGoogleFriend.toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return googleFriends;
        }


        @Override
        protected void onPostExecute(List<GoogleFriendsModel> googleFriendsModelList) {
            super.onPostExecute(googleFriendsModelList);

            gPFriendList = googleFriendsModelList;
            getFriendListStatusGoogleFriends(googleFriendsModelList);

        }
    }

    private void getFriendListStatusGoogleFriends(List<GoogleFriendsModel> googleFriendsModelList) {
        List<String> emails = new ArrayList<>();
        for (GoogleFriendsModel googleFriend : googleFriendsModelList) {
            if (googleFriend.getEmail() != null) {
                emails.add(googleFriend.getEmail());
            }
        }

        String[] arrayEmails = emails.toArray(new String[0]);
        if (isNetworkAvailable()) {
            startProgressBar();
            if (emails.size() > 0) {
                jobManager.addJobInBackground(new SearchFriendsWebJob(accessToken, arrayEmails));
            } else {
                String[] tempStr = new String[0];
                jobManager.addJobInBackground(new SearchFriendsWebJob(accessToken, tempStr));
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }

    }

    private class PhoneContactsAsyncTask extends AsyncTask<String, Void, List<PhoneContactFriendModel>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startProgressBar();
        }

        @Override
        protected List<PhoneContactFriendModel> doInBackground(String... params) {
            ArrayList<PhoneContactFriendModel> phoneContacts = new ArrayList<PhoneContactFriendModel>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    Cursor cur1 = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (cur1.moveToNext()) {
                        //to get the contact names
                        String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        Log.wtf("InviteFriendsDeviceContactName :", name);
                        String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        Log.wtf("InviteFriendsDeviceContactEmail", email);
                        String imageUriString = cur1.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                        Log.wtf("InviteFriendsDeviceContactImageUri", "has Image: " + imageUriString);
                        if (email != null) {
                            phoneContacts.add(new PhoneContactFriendModel(name, email, imageUriString));
                        }
                    }
                    cur1.close();
                }
            }
            Log.wtf(TAG, "InviteFriendsDeviceContactPhoneContactListSize " + phoneContacts.size());

            return phoneContacts;
        }

        @Override
        protected void onPostExecute(List<PhoneContactFriendModel> phoneContactFriendModelsList) {
            super.onPostExecute(phoneContactFriendModelsList);
            contactsFriendList = phoneContactFriendModelsList;
            stopProgressBar();
            getFriendListStatusContactsFriends(contactsFriendList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Utility.MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                aquireContactsFriendList();
            } else {
                showSnackbar(facebookFriendsIvBtn, "Permission not granted", Constant.ERROR);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Subscribe
    public void onRetrievePotentialFriendsWebJob(RetrievePotentialFriendsWebJobResponse retrievePotentialFriendsWebJobResponse) {
        stopProgressBar();
        Log.wtf(TAG, "onRetrievePotentialFriendsWebJob");

        if (retrievePotentialFriendsWebJobResponse != null) {
            if (retrievePotentialFriendsWebJobResponse.getStatus() != null && retrievePotentialFriendsWebJobResponse.getStatus().equals(Constant.SUCCESS)) {

                List<com.meeof.meeof.model.potential_friends.Data> data = retrievePotentialFriendsWebJobResponse.getHttpPotentialFriendsResponse().getData();
                setMightKnowDataList(data);

            } else {
                showSnackbar(facebookFriendsIvBtn, "Could not retrieve potential friends", Constant.ERROR);
            }
        } else {
            showSnackbar(facebookFriendsIvBtn, "Could not retrieve potential friends", Constant.ERROR);
        }
    }

}
