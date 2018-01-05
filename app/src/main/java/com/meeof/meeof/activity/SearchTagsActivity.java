package com.meeof.meeof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.meeof.meeof.adapter.SearchTagsAdapter;

import com.meeof.meeof.model.events_dto.Event;
import com.meeof.meeof.model.search_tag_dto.Data;
import com.meeof.meeof.model.search_tag_dto.SearchHashTagResponse;
import com.meeof.meeof.model.search_tag_dto.SendTagBack;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetTagsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by ransikadesilva on 10/19/17.
 */

public class SearchTagsActivity extends BaseActivity implements View.OnClickListener, TagView.OnTagDeleteListener, TextWatcher {

    private static final int RC_INVITE_FRIENDS = 7654;

    //    private TagGroup toBeInvitedTg;
    private TagView hashTagTg;
    private LinearLayout closeLlBtn;
    private LinearLayout addTagsLl;
    private String accessToken;
    private EditText searchTv;
    private RecyclerView searchTagRv;
    private SearchTagsAdapter searchTagsAdapter;
    private LinearLayout tagLayout;

    private List<com.meeof.meeof.model.search_tag_dto.Data> searchTagList=new ArrayList<>();
    private List<com.meeof.meeof.model.search_tag_dto.Data> selectedList=new ArrayList<>();


    private String TAG = SearchTagsActivity.class.getSimpleName();





    private boolean isFromUpdateFilter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tags);
        initViews();

        Intent intent = getIntent();

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        if (this.getIntent().hasExtra("FROM_FILTER")) {
            Bundle args = this.getIntent().getBundleExtra("FROM_FILTER");
            selectedList = (List<Data>) args.getSerializable("BUNDLE_TAGS");
            Log.d(TAG, "Has Extra:Name  " + selectedList.toString());
            addHashTagToTagView();

        }
        getHashTags("");


    }


    private void getHashTags(String searchtags) {
        if (isNetworkAvailable()) {
            startProgressBar();
            jobManager.addJobInBackground(new GetTagsWebJob(accessToken, searchtags));
            Log.d(TAG, "Inside getInviteInfo");
        } else {
            showSnackbar(hashTagTg, getString(R.string.no_internet), Constant.ERROR);
        }
    }



    private void initViews() {

        hashTagTg = (TagView) findViewById(R.id.toBeInvitedTg);
        searchTv=(EditText)findViewById(R.id.searchTv);
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        searchTagRv=(RecyclerView)findViewById(R.id.searchTagRv);
        addTagsLl=(LinearLayout)findViewById(R.id.addTagsLl);
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

    public void postTagsToPreviousActivity(){

        Log.d(TAG,"postTagsToPreviousActivity");
        SendTagBack sendTagBack=new SendTagBack();
        sendTagBack.setStatus(Constant.SUCCESS);
        sendTagBack.setData(selectedList);
        EventBus.getDefault().postSticky(sendTagBack);
        this.finish();

    }

    @Override
    public void onTagDeleted(TagView tagView, Tag tag, int i) {
        Log.d(TAG,"On Tag Delete: "+tag.id);
        hashTagTg.remove(i);
        for(com.meeof.meeof.model.search_tag_dto.Data data:selectedList){
            if(data.getHashid()==tag.id){
                selectedList.remove(data);
                break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onGetSearchTagResponse(SearchHashTagResponse searchHashTagResponse) {
        Log.d(TAG,"onGetSearchTagResponse ");
        stopProgressBar();
        if (searchHashTagResponse != null) {
            if (searchHashTagResponse.getStatus() != null && searchHashTagResponse.getStatus().equals(Constant.SUCCESS)) {

                if(searchTagsAdapter!=null){
                    searchTagsAdapter.updateList(searchHashTagResponse.getData(),null);
                }else {
                    searchTagsAdapter = new SearchTagsAdapter(this, searchHashTagResponse.getData(),null);
                    searchTagRv.setLayoutManager(new LinearLayoutManager(this));
                    searchTagRv.setAdapter(searchTagsAdapter);
                    searchTagRv.setItemAnimator(new DefaultItemAnimator());
                    searchTagsAdapter.notifyDataSetChanged();

                    searchTagsAdapter.setOnClick(new SearchTagsAdapter.OnItemClicked() {
                        @Override
                        public void onItemClick(int position, String item) {
                            Log.d(TAG,"Onclick postition: "+position+" item id/hashid: "+item);
                            Log.d(TAG,"Onclick :"+searchTagsAdapter.getData().toString());

                            if(!selectedList.contains(searchTagsAdapter.getData().get(position))){
                                selectedList.add(searchTagsAdapter.getData().get(position));
                                addHashTagToTagView(searchTagsAdapter.getData().get(position));

                            }
                        }
                    });
                }


            }else {
                showSnackbar(searchTagRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(searchTagRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }


    private void addHashTagToTagView(com.meeof.meeof.model.search_tag_dto.Data data) {
        List<Tag> tagList = new ArrayList<>();
        Tag tag;
        Log.d(TAG,"TAGGG selectedList: "+selectedList.toString());


            tag=new Tag(data.getHashtag());
            tag.radius = 50f;
            tag.isDeletable = true;
            tag.id = data.getHashid();
            tag.layoutColor = ContextCompat.getColor(this, R.color.light_grey);
            tagList.add(tag);

            //currentEmailTagList.add(new CustomTagEmail(tag.text, tag.id));

            final Tag finalTag = tag;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"TAGGG :"+finalTag.toString());
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
        Log.d(TAG,"TAGGG selectedList: "+selectedList.toString());

        for(com.meeof.meeof.model.search_tag_dto.Data data:selectedList){
            tag=new Tag(data.getHashtag());
            tag.radius = 50f;
            tag.isDeletable = true;
            tag.id = data.getHashid();
            tag.layoutColor = ContextCompat.getColor(this, R.color.light_grey);
            tagList.add(tag);

            //currentEmailTagList.add(new CustomTagEmail(tag.text, tag.id));

            final Tag finalTag = tag;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG,"TAGGG :"+finalTag.toString());
                    hashTagTg.addTag(finalTag);
                }
            });
        }

        if(selectedList.size()>0){
            tagLayout.setVisibility(View.VISIBLE);
        }else{
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
