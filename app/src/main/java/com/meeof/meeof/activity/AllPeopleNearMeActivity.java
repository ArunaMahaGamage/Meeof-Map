package com.meeof.meeof.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.PeopleNearMeAdapter;
import com.meeof.meeof.model.PeopleNearMeInsideModel;
import com.meeof.meeof.model.PeopleNearMeMainModel;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.PeopleNearMeWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class AllPeopleNearMeActivity extends BaseActivity {

    RecyclerView RecyclerAllPeoples;
    ProgressBar progressBar;
    PeopleNearMeAdapter peopleNearMeAdapter;
    ArrayList<PeopleNearMeInsideModel> peopleNearMeInsideModels=new ArrayList<>();
    double latitude;
    double longitude;
    String accessToken;
    ImageView backAcIvBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people_near_me);
        latitude=getIntent().getDoubleExtra("Latitude",0);
        longitude=getIntent().getDoubleExtra("Longitude",0);
        accessToken=getIntent().getStringExtra("accesstoken");
        RecyclerAllPeoples=(RecyclerView)findViewById(R.id.RecyclerAllPeoples);
        progressBar=(ProgressBar)findViewById(R.id.ProgressPeoples);
        RecyclerAllPeoples.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        peopleNearMeAdapter = new PeopleNearMeAdapter(AllPeopleNearMeActivity.this, peopleNearMeInsideModels);
        RecyclerAllPeoples.setAdapter(peopleNearMeAdapter);
        progressBar.setVisibility(View.VISIBLE);
        backAcIvBtn= (ImageView) findViewById(R.id.backAcIvBtn);
        backAcIvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        jobManager.addJobInBackground(new PeopleNearMeWebJob(latitude, longitude, accessToken));
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPeopleNearMeCompleted(PeopleNearMeMainModel peopleNearMeMainModel) {
        progressBar.setVisibility(View.GONE);
        if (peopleNearMeMainModel != null) {
            if (peopleNearMeMainModel.getStatus() != null && peopleNearMeMainModel.getStatus().equals(Constant.SUCCESS)) {
                peopleNearMeInsideModels.addAll(peopleNearMeMainModel.getData());
                peopleNearMeAdapter.notifyDataSetChanged();
                RecyclerAllPeoples.invalidate();
            } else {
                Toast.makeText(AllPeopleNearMeActivity.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(AllPeopleNearMeActivity.this, getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
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
