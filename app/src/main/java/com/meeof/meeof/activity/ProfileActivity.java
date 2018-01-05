package com.meeof.meeof.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.google.gson.Gson;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.UpdatesAdapter;
import com.meeof.meeof.adapter.UpdatesProfileRecyclerAdapter;
import com.meeof.meeof.adapter.UpdatesRecyclerAdapter;
import com.meeof.meeof.adapter.UserEventsAdapter;
import com.meeof.meeof.adapter.UserImageAdapter;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.DeleteUpdateWebJobResponse;
import com.meeof.meeof.model.GetEventsByUserInsideModel;
import com.meeof.meeof.model.GetEventsByUserModel;
import com.meeof.meeof.model.GetEventsInsideModel;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.UpdatesInsideModel;
import com.meeof.meeof.model.UpdatesMainModel;
import com.meeof.meeof.model.array_updatesModel;
import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.friends_all_dto.Pending_requests;
import com.meeof.meeof.model.interests.InterestsBaseItem;
import com.meeof.meeof.model.profile.Data;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.model.updates_all_dto.Array_updates;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.CustomLinearLayoutManager;
import com.meeof.meeof.webjob.GetInterestsListWebJob;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;
import com.meeof.meeof.webjob.GetMyInterestWebJob;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.GetUpdatesWebJob;
import com.meeof.meeof.webjob.GetUserWiseEventsWebJob;
import com.meeof.meeof.webjob.UserUpdateWebJob;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Anuja Ranwalage on 10/11/17.
 */

public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";
    private RelativeLayout imageTintLl;
    private ImageView userImageBackgroundIv;
    private ImageView profileImageIv;
    private ImageView seeMoreFriendsIv;
    private boolean isFromUserProfile;
    private String accessToken;
    private TextView userNameTv;
    private TextView friendsCountTv;
    private RelativeLayout seeMoreFriendsRlBtn;
    private ImageView backAcIvBtn;
    private ImageView settingsIvBtn;
    private ImageView friendOneIv;
    private ImageView friendTwoIv;
    private ImageView friendThreeIv;
    private ImageView friendFourIv;
    private ImageView friendFiveIv;
    private TagView interestsTg;
    JSONObject AllListOfInterest;
    LinearLayout SeeAllInterestLayout;
    UserEventsAdapter userEventsAdapter;
    RecyclerView RecyclerHorizontalImages;
    ArrayList<GetEventsByUserInsideModel> arrayList = new ArrayList<>();
    UserImageAdapter userImageAdapter;
    ArrayList<String> userImages = new ArrayList<>();
    RecyclerView RecyclerEventsByUser, RecyclerUpdatesByUser;
    ArrayList<array_updatesModel> updatesInsideModels = new ArrayList<>();
    UpdatesProfileRecyclerAdapter updatesAdapter;
    int UserId = 0;
    LinearLayout LinearUpdatesByUser;
    int MyId;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        intent = getIntent();
        isFromUserProfile = intent.getBooleanExtra(Constant.IS_MY_PROFILE, false);
        Log.d(TAG,"isFromUserProfile "+isFromUserProfile);

        UserId = getIntent().getIntExtra(Constant.USER_ID, 22);
        initViews();
    }


    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        imageTintLl = (RelativeLayout) findViewById(R.id.imageTintLl);
        userImageBackgroundIv = (ImageView) findViewById(R.id.userImageBackgroundIv);
        profileImageIv = (ImageView) findViewById(R.id.profileImageIv);
        LinearUpdatesByUser = (LinearLayout) findViewById(R.id.LinearUpdatesByUser);
        RecyclerHorizontalImages = (RecyclerView) findViewById(R.id.RecyclerHorizontalImages);
        RecyclerEventsByUser = (RecyclerView) findViewById(R.id.RecyclerEventsByUser);
        RecyclerEventsByUser.setLayoutManager(new CustomLinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false));
        userEventsAdapter = new UserEventsAdapter(ProfileActivity.this, arrayList, new UserEventsAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, GetEventsByUserInsideModel item, View button) {
            }
        }, new UserEventsAdapter.OnChannelViewClick() {
            @Override
            public void onItemClick(int position, GetEventsByUserInsideModel item) {
                if (item.getAttendeeList().get(position).getChannel_id() == 0) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                    intent.putExtra("UserId", item.getAttendeeList().get(position).getUser_id());
                    intent.putExtra(Constant.IS_MY_PROFILE, false);
                    startActivity(intent);
                    finish();
                    return;
                }
                Intent intent = new Intent(ProfileActivity.this, ChannelProfileActivity.class);
                intent.putExtra("channelId", item.getAttendeeList().get(position).getChannel_id());
                startActivity(intent);
            }

            @Override
            public void onItemClick(int position, Event item) {

            }
        });
        RecyclerEventsByUser.setAdapter(userEventsAdapter);
        RecyclerHorizontalImages.setLayoutManager(new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        userImageAdapter = new UserImageAdapter(ProfileActivity.this, userImages);
        RecyclerHorizontalImages.setAdapter(userImageAdapter);
        seeMoreFriendsIv = (ImageView) findViewById(R.id.seeMoreFriendsIv);
        SeeAllInterestLayout = (LinearLayout) findViewById(R.id.SeeAllInterestLayout);
        RecyclerUpdatesByUser = (RecyclerView) findViewById(R.id.RecyclerUpdatesByUser);
        RecyclerUpdatesByUser.setLayoutManager(new CustomLinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false));

        SeeAllInterestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, MyAllInterestActivity.class);
                startActivity(intent);
            }
        });
        MyId = sharedPreferences.getInt(Constant.MYUSERID, 0);
        interestsTg = (TagView) findViewById(R.id.interestsTg);
        userNameTv = (TextView) findViewById(R.id.userNameTv);
        friendsCountTv = (TextView) findViewById(R.id.friendsCountTv);
        seeMoreFriendsRlBtn = (RelativeLayout) findViewById(R.id.seeMoreFriendsRlBtn);
        backAcIvBtn = (ImageView) findViewById(R.id.backAcIvBtn);
        settingsIvBtn = (ImageView) findViewById(R.id.settingsIvBtn);
        friendOneIv = (ImageView) findViewById(R.id.friendOneIv);
        friendTwoIv = (ImageView) findViewById(R.id.friendTwoIv);
        friendThreeIv = (ImageView) findViewById(R.id.friendThreeIv);
        friendFourIv = (ImageView) findViewById(R.id.friendFourIv);
        friendFiveIv = (ImageView) findViewById(R.id.friendFiveIv);
//        profile_messgae_linearLayout.setVisibility(View.GONE);
        seeMoreFriendsIv.setOnClickListener(this);
        seeMoreFriendsRlBtn.setOnClickListener(this);
        backAcIvBtn.setOnClickListener(this);
        settingsIvBtn.setOnClickListener(this);
        friendOneIv.setOnClickListener(this);
        friendTwoIv.setOnClickListener(this);
        friendThreeIv.setOnClickListener(this);
        friendFourIv.setOnClickListener(this);
        friendFiveIv.setOnClickListener(this);
        updateUI();
        getAllFriends();
        if (isFromUserProfile) {
            getMeData();
        } else {
            getOpponentDataData();
        }
        jobManager.addJobInBackground(new GetInterestsListWebJob(accessToken));
        startProgressBar();
    }

    @Subscribe
    public void onGetInterestsWebJobCompleted(JSONObject interestsJObj) {
        stopProgressBar();
        if (interestsJObj != null) {
            try {
                if (interestsJObj.get("status") != null && interestsJObj.get("status").equals(Constant.SUCCESS)) {
                    if (interestsJObj.get("data") != null) {
                        try {
                            startProgressBar();
                            jobManager.addJobInBackground(new GetMyInterestWebJob(accessToken));
                            AllListOfInterest = interestsJObj.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
//            showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyInterestWebJobCompleted(JSONArray jsonArray) {
        int count = 0;
        stopProgressBar();
        if (jsonArray != null) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    int id = jsonArray.getInt(i);
                    if (AllListOfInterest != null) {
                        Iterator<String> stringIterator = AllListOfInterest.keys();
                        while (stringIterator.hasNext()) {
                            String Key = stringIterator.next();
                            JSONObject jsonObject = AllListOfInterest.getJSONObject(Key);
                            boolean HasSub1 = jsonObject.getBoolean("has_sub");
                            if (HasSub1) {
                                Object object = jsonObject.get("sub_cat");
                                if (object instanceof JSONObject) {
                                    JSONObject jsonObject1 = jsonObject.getJSONObject("sub_cat");
                                    Iterator<String> keys = jsonObject1.keys();
                                    while (keys.hasNext()) {
                                        String MyKey = keys.next();
                                        JSONObject jsonObject2 = jsonObject1.getJSONObject(MyKey);
                                        boolean hasSub = jsonObject2.getBoolean("has_sub");
                                        if (hasSub) {
                                            Object objectNew = jsonObject2.get("sub_cat");
                                            if (objectNew instanceof JSONArray) {
                                                JSONArray jsonArray1 = jsonObject2.getJSONArray("sub_cat");
                                                for (int k = 0; k < jsonArray1.length(); k++) {
                                                    JSONObject jsonObject3 = jsonArray1.getJSONObject(k);
                                                    boolean hasSubItem = jsonObject3.getBoolean("has_sub");
                                                    if (!hasSubItem) {
                                                        int key = jsonObject3.getInt("catid");
                                                        if (key == id) {
                                                            if (count < 6) {
                                                                count++;
                                                                String Name = jsonObject3.getString("name");
                                                                Tag tag = new Tag(Name);
                                                                tag.layoutColor = Color.parseColor("#2A465E");
                                                                tag.tagTextSize = 12;

                                                                interestsTg.addTag(tag);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            int key = jsonObject2.getInt("catid");
                                            if (key == id) {
                                                if (count < 6) {
                                                    count++;
                                                    String Name = jsonObject2.getString("name");
                                                    Tag tag = new Tag(Name);
                                                    tag.layoutColor = Color.parseColor("#2A465E");
                                                    tag.tagTextSize = 12;
                                                    interestsTg.addTag(tag);
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                int key = jsonObject.getInt("catid");
                                if (key == id) {
                                    if (count < 6) {
                                        count++;
                                        String Name = jsonObject.getString("name");
                                        Tag tag = new Tag(Name);
                                        tag.layoutColor = Color.parseColor("#2A465E");
                                        tag.tagTextSize = 12;
                                        interestsTg.addTag(tag);
                                    }
                                }
                            }
                        }
                    }
                }

                startProgressBar();
                if (isFromUserProfile) {

                    Log.d(TAG,"isFromUserProfile events UserId: "+UserId);
                    jobManager.addJobInBackground(new GetUserWiseEventsWebJob(UserId, accessToken));
                    jobManager.addJobInBackground(new UserUpdateWebJob(UserId, accessToken));
                } else {

                    Log.d(TAG,"isFromUserProfile NOT events MyId: "+MyId);
                    jobManager.addJobInBackground(new GetUserWiseEventsWebJob(MyId, accessToken));
                    jobManager.addJobInBackground(new UserUpdateWebJob(MyId, accessToken));
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
//            showSnackbar(nextLlBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserWiseEventsWebJobCompleted(GetEventsByUserModel getEventsByUserModel) {
        stopProgressBar();
        if (getEventsByUserModel.getStatus() != null) {
            if (!getEventsByUserModel.getStatus().isEmpty()) {
                if (getEventsByUserModel.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {
                    arrayList.addAll(getEventsByUserModel.getData());

                    for (int i = 0; i < getEventsByUserModel.getData().size(); i++) {
                        for (int j = 0; j < getEventsByUserModel.getData().get(i).getPhotos().size(); j++) {
                            String Photo = getEventsByUserModel.getData().get(i).getPhotos().get(j).getFile_name();
                            userImages.add(Photo);
                        }
                    }
                    userImageAdapter.notifyDataSetChanged();
                    RecyclerHorizontalImages.invalidate();
                    userEventsAdapter.notifyDataSetChanged();
                    RecyclerEventsByUser.invalidate();
                }

            }


        }
        //startProgressBar();



    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUserUpdateWebJobCompleted(UpdatesMainModel updatesMainModel) {
        stopProgressBar();
        if (updatesMainModel.getStatus() != null) {
            if (!updatesMainModel.getStatus().isEmpty()) {
                if (updatesMainModel.getStatus().equalsIgnoreCase(Constant.SUCCESS)) {


                    updatesAdapter = new UpdatesProfileRecyclerAdapter(this, updatesMainModel);
                    RecyclerUpdatesByUser.setAdapter(updatesAdapter);
                    updatesAdapter.notifyDataSetChanged();


                    updatesInsideModels.addAll(updatesMainModel.getData().getArray_updates());
                    RecyclerUpdatesByUser.invalidate();
                    for (int i = 0; i < updatesMainModel.getData().getArray_updates().size(); i++) {
                        userImages.addAll(updatesMainModel.getData().getArray_updates().get(i).getPhoto_arrays());
                    }
                    if (updatesInsideModels == null || updatesInsideModels.size() == 0) {
                        LinearUpdatesByUser.setVisibility(View.GONE);
                    } else {
                        LinearUpdatesByUser.setVisibility(View.VISIBLE);
                    }
                    userImageAdapter.notifyDataSetChanged();
                    RecyclerHorizontalImages.invalidate();
                }

            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.seeMoreFriendsIv:
                sendToUserFriendsActivity();
                Helper.clickGaurd(seeMoreFriendsIv);
                break;
            case R.id.backAcIvBtn:
                this.onBackPressed();
                break;
            case R.id.settingsIvBtn:
                sendToSettingsActivity();
                Helper.clickGaurd(settingsIvBtn);
                break;
            case R.id.seeMoreFriendsRlBtn:
                sendToAllFriendsActivity();
                Helper.clickGaurd(seeMoreFriendsRlBtn);
                break;

        }
    }

    private void sendToAllFriendsActivity() {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    private void getMeData() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(MyId, accessToken));
            startProgressBar();
        } else {
            showSnackbar(imageTintLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void getOpponentDataData() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(UserId, accessToken));
            startProgressBar();
        } else {
            showSnackbar(imageTintLl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    private void updateUI() {
        ProfileResponse profileResponse = retriveSavedProfileObject();
        if (profileResponse != null) {
            Data profData = profileResponse.getData();
            userNameTv.setText(""+profData.getFirst_name());
            String photoUrl = profData.getProfilephoto() == null || profData.getProfilephoto().trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                    Constant.PROFILE_PIC_BASE_URL + profData.getProfilephoto();

            Log.d(TAG, "Photo Url: " + photoUrl);

            Picasso.with(getApplicationContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ico_profile_edit_avatar))
                    .resize(120, 120)
                    .into(profileImageIv);

            Picasso.with(getApplicationContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.ico_profile_edit_avatar)
                    .error(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ico_profile_edit_avatar))
                    .resize(0, 150)
                    .into(userImageBackgroundIv);
        }
    }

    private ProfileResponse retriveSavedProfileObject() {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            return profileResponse;
        }
        return null;
    }

    @Subscribe
    public void onGetProfileWebJob(ProfileResponse profileResponse) {
        stopProgressBar();
        if (profileResponse != null) {
            if (profileResponse.getStatus() != null && profileResponse.getStatus().equals(Constant.SUCCESS)) {
                setDataToProfile(profileResponse.getData());
            } else {
                showSnackbar(imageTintLl, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(imageTintLl, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }


    private void setDataToProfile(final com.meeof.meeof.model.profile.Data data) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String name = data.getFirst_name();
                userNameTv.setText(name);
            }
        });
    }


    private void sendToSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void sendToUserFriendsActivity() {
        Intent intent = new Intent(this, FriendsActivity.class);
        startActivity(intent);
    }

    private void getAllFriends() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetMyAllFriendsWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(backAcIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyAllFriendsCompleted(FriendsAllResponse friendsAllResponse) {
        stopProgressBar();
        if (friendsAllResponse != null) {
            if (friendsAllResponse.getStatus() != null && friendsAllResponse.getStatus().equals(Constant.SUCCESS)) {
                List<Friends> friends = friendsAllResponse.getFriends();
                List<Pending_requests> pendingRequests = friendsAllResponse.getPending_requests();
                String count = "";
                if (friends.size() > 110) {
                    count = "110+";
                } else {
                    count = friends.size() + "";
                }

                setFriendImages(friends);
                friendsCountTv.setText(count);
            } else {
                showSnackbar(backAcIvBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(backAcIvBtn, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void setFriendImages(List<Friends> friends) {
        ImageView[] imageViews = {friendOneIv, friendTwoIv, friendThreeIv, friendFourIv, friendFiveIv};
        int i = 0;
        for (Friends frnd : friends) {
            if (i < imageViews.length) {
                String photoUrl = frnd.getProfilephoto() == null || frnd.getProfilephoto().trim().length() <= 0 ? Constant.DEFAULT_AVATAR_URL :
                        Constant.PROFILE_PIC_BASE_URL + frnd.getProfilephoto();

                Picasso.with(getApplicationContext())
                        .load(photoUrl)
                        .placeholder(R.drawable.ico_profile_edit_avatar)
                        .error(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ico_profile_edit_avatar))
                        .resize(50, 50)
                        .into(imageViews[i]);
            } else {
                break;
            }
            i++;
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

    public static LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interestsCategories;

    private void processInterestsJSON(JSONObject interestsData) throws JSONException, IOException {
        Log.d(TAG, "Interests::: " + interestsData.toString());
        ArrayList<String> mainCategories = new ArrayList<>();
        ArrayList<String> mainCategoriesTitles = new ArrayList<>();

        LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interests = new LinkedHashMap<>();
        interestsCategories = new LinkedHashMap<>();
        JSONArray interestsCategoriesArr = new JSONArray();

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

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void OnEventLikeUnlikeComplete(HttpResponseLikeUnlike httpResponseLikeUnlike) {
        Log.i(TAG, "OnEventLikeUnlikeCompleted ");

        Log.d(TAG, "onPostLikeEventAsFriendWebJobCompleted ");
        if (httpResponseLikeUnlike != null) {
            {
                if (httpResponseLikeUnlike.getStatus().equals(Constant.SUCCESS)) {
                    for (int i = 0; i < updatesAdapter.updatesList.size(); i++) {
                        array_updatesModel updateItem = updatesAdapter.updatesList.get(i);
                        if (updateItem.getUpdateid() == Integer.parseInt(httpResponseLikeUnlike.getZoneid())) {

                            Log.d(TAG, "EVENT ID MATCH " + updateItem.getUpdateid() + " " + httpResponseLikeUnlike.getZoneid());
                            //updatesRecyclerAdapter.updatesList.get(i).setLike(httpResponseLikeUnlike.is_like());

                            //updatesRecyclerAdapter.updatesList.get(i).setCountLikes(httpResponseLikeUnlike.getLike_count());

                            UpdatesRecyclerAdapter.MyViewHolderUpdates myViewHolderUpdates= (UpdatesRecyclerAdapter.MyViewHolderUpdates) RecyclerUpdatesByUser.findViewHolderForAdapterPosition(i);
                            myViewHolderUpdates.likeTv.setText(httpResponseLikeUnlike.getLike_count()+"");
                            if(httpResponseLikeUnlike.is_like()){
                                myViewHolderUpdates.likedBtn.setVisibility(View.VISIBLE);
                                myViewHolderUpdates.unLiked.setVisibility(View.GONE);
                                if(!updatesAdapter.updateData.getData().getWhatDoILike().contains(Integer.parseInt(httpResponseLikeUnlike.getZoneid()))){
                                    updatesAdapter.updateData.getData().getWhatDoILike().add(Integer.parseInt(httpResponseLikeUnlike.getZoneid()));
                                }
                            }else{
                                myViewHolderUpdates.likedBtn.setVisibility(View.GONE);
                                myViewHolderUpdates.unLiked.setVisibility(View.VISIBLE);
                                for(int a=0;a<updatesAdapter.updateData.getData().getWhatDoILike().size();a++){
                                    if(updatesAdapter.updateData.getData().getWhatDoILike().get(a)==Integer.parseInt(httpResponseLikeUnlike.getZoneid())){
                                        updatesAdapter.updateData.getData().getWhatDoILike().remove(a);
                                        break;
                                    }
                                }

                            }

                            //updatesRecyclerAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } else {
                    showSnackbar(RecyclerUpdatesByUser, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                }
            }
        } else {
            showSnackbar(RecyclerUpdatesByUser, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
        stopProgressBar();


        //updatesRecyclerAdapter.updateLikeCount(getContext(),Integer.parseInt(httpResponseLikeUnlike.getZoneid()),httpResponseLikeUnlike.getLike_count(),httpResponseLikeUnlike.is_like());

        //jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));
        //jobManager.addJobInBackground(new GetUpdatesLikeWebJob(accessToken,httpLikeUnlikeResponse.getId()));
    }

    @Subscribe
    public void deleteUpdateWebJobResponse(DeleteUpdateWebJobResponse deleteUpdateWebJobResponse) {
        //stopProgressBar();
        Log.i(TAG, "deleteUpdateWebJobResponse" + deleteUpdateWebJobResponse);
        if (deleteUpdateWebJobResponse != null) {
            if (deleteUpdateWebJobResponse.getStatus() != null && deleteUpdateWebJobResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.i(TAG, "deleteUpdateWebJobResponse" + deleteUpdateWebJobResponse.getStatus());
                showSnackbar(RecyclerUpdatesByUser, getString(R.string.update_delete_successfully), Constant.SUCCESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Helper.delay(1500, new Helper.DelayCallBack() {
                            @Override
                            public void postDelay() {
                                if (isFromUserProfile) {

                                    Log.d(TAG,"isFromUserProfile events UserId: "+UserId);
                                    jobManager.addJobInBackground(new UserUpdateWebJob(UserId, accessToken));
                                } else {

                                    Log.d(TAG,"isFromUserProfile NOT events MyId: "+MyId);
                                    jobManager.addJobInBackground(new UserUpdateWebJob(MyId, accessToken));
                                }
                            }
                        });
                    }
                });

            } else {
                showSnackbar(RecyclerUpdatesByUser, getString(R.string.update_delete_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(RecyclerUpdatesByUser, getString(R.string.update_delete_failed), Constant.ERROR);
        }
    }
}
