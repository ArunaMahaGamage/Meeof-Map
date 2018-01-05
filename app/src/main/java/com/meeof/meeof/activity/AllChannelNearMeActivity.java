package com.meeof.meeof.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.ChannelNearMeAdapter;
import com.meeof.meeof.model.ChannelInsideModel;
import com.meeof.meeof.model.ChannelMainModel;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.ChannelNearMeWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class AllChannelNearMeActivity extends BaseActivity {

    ProgressBar progressBar;
    RecyclerView RecyclerAllChannels;
    ChannelNearMeAdapter channelNearMeAdapter;
    ArrayList<ChannelInsideModel> channelInsideModels=new ArrayList<>();
    double latitude,longitude;
    String accessToken;
    ImageView backAcIvBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_channel_near_me);
        latitude=getIntent().getDoubleExtra("Latitude",0);
        longitude=getIntent().getDoubleExtra("Longitude",0);
        accessToken=getIntent().getStringExtra("accesstoken");
        progressBar= (ProgressBar) findViewById(R.id.ProgressChannel);
        RecyclerAllChannels=(RecyclerView)findViewById(R.id.RecyclerAllChannels);
        RecyclerAllChannels.setLayoutManager(new LinearLayoutManager(AllChannelNearMeActivity.this,LinearLayoutManager.VERTICAL,false));
        channelNearMeAdapter = new ChannelNearMeAdapter(AllChannelNearMeActivity.this, channelInsideModels);
        RecyclerAllChannels.setAdapter(channelNearMeAdapter);
        progressBar.setVisibility(View.VISIBLE);
        backAcIvBtn= (ImageView) findViewById(R.id.backAcIvBtn);
        backAcIvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        jobManager.addJobInBackground(new ChannelNearMeWebJob(latitude, longitude, accessToken));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetChannelNearMeCompleted(ChannelMainModel channelMainModel) {
        progressBar.setVisibility(View.GONE);
        if (channelMainModel != null) {
            if (channelMainModel.getStatus() != null && channelMainModel.getStatus().equals(Constant.SUCCESS)) {
                channelInsideModels.addAll(channelMainModel.getData());
                channelNearMeAdapter.notifyDataSetChanged();
                RecyclerAllChannels.invalidate();
            } else {
                Toast.makeText(AllChannelNearMeActivity.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AllChannelNearMeActivity.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
