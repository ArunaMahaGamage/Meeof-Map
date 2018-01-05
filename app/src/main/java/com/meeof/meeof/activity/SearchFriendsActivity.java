package com.meeof.meeof.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.cunoraz.tagview.Tag;
import com.cunoraz.tagview.TagView;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.SearchFriendsAdapter;
import com.meeof.meeof.adapter.SearchTagsAdapter;
import com.meeof.meeof.model.friends_all_dto.Friends;
import com.meeof.meeof.model.friends_all_dto.FriendsAllResponse;
import com.meeof.meeof.model.search_my_friend_dto.SearchMyFriendsQueryResponse;

import com.meeof.meeof.model.search_my_friend_dto.SendFriendBack;
import com.meeof.meeof.model.search_query_dto.Data;
import com.meeof.meeof.model.search_tag_dto.SearchHashTagResponse;
import com.meeof.meeof.model.search_tag_dto.SendTagBack;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetMyAllFriendsWebJob;
import com.meeof.meeof.webjob.GetTagsWebJob;
import com.meeof.meeof.webjob.SearchMyFriendsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SearchFriendsActivity extends BaseActivity implements View.OnClickListener, TagView.OnTagDeleteListener, TextWatcher

{

    private static final int RC_INVITE_FRIENDS = 7654;

    //    private TagGroup toBeInvitedTg;
    private TagView hashTagTg;
    private LinearLayout closeLlBtn;
    private LinearLayout addTagsLl;
    private String accessToken;
    private EditText searchTv;
    private RecyclerView searchTagRv;
    private SearchFriendsAdapter searchTagsAdapter;
    private LinearLayout tagLayout;
    private List<Data> searchTagList = new ArrayList<>();
    private List<com.meeof.meeof.model.search_my_friend_dto.Data> selectedList = new ArrayList<>();


    private String TAG = SearchFriendsActivity.class.getSimpleName();


    private boolean isFromUpdateFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);
        initViews();

        Intent intent = getIntent();

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        if (this.getIntent().hasExtra("FROM_FILTER")) {
            Bundle args = this.getIntent().getBundleExtra("FROM_FILTER");
            selectedList = (List<com.meeof.meeof.model.search_my_friend_dto.Data>) args.getSerializable("BUNDLE_TAGS");
            Log.d(TAG, "Has Extra:Name  " + selectedList.toString());
            addHashTagToTagView();

        }
        getHashTags("");


    }


    private void getHashTags(String searchtags) {
        if (isNetworkAvailable()) {
            startProgressBar();
            jobManager.addJobInBackground(new SearchMyFriendsWebJob(accessToken, searchtags));
            Log.d(TAG, "Inside getInviteInfo");
        } else {
            showSnackbar(hashTagTg, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    private void initViews() {

        hashTagTg = (TagView) findViewById(R.id.toBeInvitedTg);
        searchTv = (EditText) findViewById(R.id.searchTv);
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        searchTagRv = (RecyclerView) findViewById(R.id.searchTagRv);
        addTagsLl = (LinearLayout) findViewById(R.id.addTagsLl);
        tagLayout=(LinearLayout)findViewById(R.id.tagLayout);

        searchTv.addTextChangedListener(this);
        closeLlBtn.setOnClickListener(this);
        addTagsLl.setOnClickListener(this);

        hashTagTg.setOnTagDeleteListener(this);
        initProgressBar();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.closeLlBtn:
                onBackPressed();
                break;
            case R.id.addTagsLl:
                postTagsToPreviousActivity();
                break;
        }
    }

    public void postTagsToPreviousActivity() {

        Log.d(TAG, "postTagsToPreviousActivity");
        SendFriendBack sendTagBack = new SendFriendBack();
        sendTagBack.setStatus(Constant.SUCCESS);
        sendTagBack.setData(selectedList);
        EventBus.getDefault().postSticky(sendTagBack);
        this.finish();

    }

    @Override
    public void onTagDeleted(TagView tagView, Tag tag, int i) {
        Log.d(TAG, "On Tag Delete: " + tag.id);
        hashTagTg.remove(i);
        for (com.meeof.meeof.model.search_my_friend_dto.Data data : selectedList) {
            if (data.getUser_id() == tag.id) {
                selectedList.remove(data);
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetSearchTagResponse(SearchMyFriendsQueryResponse friendsQueryResponse) {
        Log.d(TAG, "onGetSearchTagResponse ");
        stopProgressBar();
        if (friendsQueryResponse != null) {
            if (friendsQueryResponse.getStatus() != null && friendsQueryResponse.getStatus().equals(Constant.SUCCESS)) {

                if (searchTagsAdapter != null) {
                    searchTagsAdapter.updateList(friendsQueryResponse.getData(), null);
                } else {
                    searchTagsAdapter = new SearchFriendsAdapter(this, friendsQueryResponse.getData(), null);
                    searchTagRv.setLayoutManager(new LinearLayoutManager(this));
                    searchTagRv.setAdapter(searchTagsAdapter);
                    searchTagRv.setItemAnimator(new DefaultItemAnimator());
                    searchTagsAdapter.notifyDataSetChanged();

                    searchTagsAdapter.setOnClick(new SearchFriendsAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position, String item) {
                            Log.d(TAG, "Onclick postition: " + position + " item id/hashid: " + item);
                            Log.d(TAG, "Onclick :" + searchTagsAdapter.getData().toString());

                            if (!selectedList.contains(searchTagsAdapter.getData().get(position))) {
                                selectedList.add(searchTagsAdapter.getData().get(position));
                                addHashTagToTagView(searchTagsAdapter.getData().get(position));
                            }
                        }
                    });
                }


            } else {
                showSnackbar(searchTagRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(searchTagRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }


    private void addHashTagToTagView(com.meeof.meeof.model.search_my_friend_dto.Data data) {
        List<Tag> tagList = new ArrayList<>();
        Tag tag;
        Log.d(TAG, "TAGGG selectedList: " + selectedList.toString());


        tag = new Tag(data.getFirst_name());
        tag.radius = 50f;
        tag.isDeletable = true;
        tag.id = data.getUser_id();
        tag.layoutColor = ContextCompat.getColor(this, R.color.light_grey);
        tagList.add(tag);

        //currentEmailTagList.add(new CustomTagEmail(tag.text, tag.id));

        final Tag finalTag = tag;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "TAGGG :" + finalTag.toString());
                hashTagTg.addTag(finalTag);
            }
        });
        if(selectedList.size()>0){
            tagLayout.setVisibility(View.VISIBLE);
        }else{
            tagLayout.setVisibility(View.GONE);
        }

    }

    private void addHashTagToTagView() {
        //Call on create
        List<Tag> tagList = new ArrayList<>();
        Tag tag;
        Log.d(TAG, "TAGGG selectedList: " + selectedList.toString());

        for (com.meeof.meeof.model.search_my_friend_dto.Data data : selectedList) {
            tag = new Tag(data.getFirst_name());
            tag.radius = 50f;
            tag.isDeletable = true;
            tag.id = data.getUser_id();
            tag.layoutColor = ContextCompat.getColor(this, R.color.light_grey);
            tagList.add(tag);

            //currentEmailTagList.add(new CustomTagEmail(tag.text, tag.id));

            final Tag finalTag = tag;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "TAGGG :" + finalTag.toString());
                    hashTagTg.addTag(finalTag);
                }
            });
        }
        if (selectedList.size() > 0) {
            tagLayout.setVisibility(View.VISIBLE);
        } else {
            tagLayout.setVisibility(View.GONE);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        getHashTags(s.toString());
    }

}
