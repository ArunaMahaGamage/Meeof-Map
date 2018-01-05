package com.meeof.meeof.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.PromotionNearMeAdapter;
import com.meeof.meeof.model.PromoNearMeInsideModel;
import com.meeof.meeof.model.PromoNearMeMainModel;
import com.meeof.meeof.webjob.PromotionNearMeWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class AllPromotionsNearMeActivity extends BaseActivity {

    RecyclerView RecyclerPromotionsAll;
    ImageView backAcIvBtn;
    PromotionNearMeAdapter adapter;
    ArrayList<PromoNearMeInsideModel> arrayList=new ArrayList<>();
    private String accessToken;
    double latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_promotions_near_me);
        latitude=getIntent().getDoubleExtra("Latitude",0);
        longitude=getIntent().getDoubleExtra("Longitude",0);
        accessToken=getIntent().getStringExtra("accesstoken");
        RecyclerPromotionsAll=(RecyclerView)findViewById(R.id.RecyclerPromotionsAll);
        RecyclerPromotionsAll.setLayoutManager(new LinearLayoutManager(AllPromotionsNearMeActivity.this,LinearLayoutManager.VERTICAL,false));
        adapter=new PromotionNearMeAdapter(AllPromotionsNearMeActivity.this,arrayList);
        RecyclerPromotionsAll.setAdapter(adapter);
        backAcIvBtn=(ImageView)findViewById(R.id.backAcIvBtn);
        backAcIvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        jobManager.addJobInBackground(new PromotionNearMeWebJob(latitude,longitude,accessToken));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPromotionNearMeCompleted(PromoNearMeMainModel promoNearMeMainModel)
    {
        if (promoNearMeMainModel!=null)
        {
            if (promoNearMeMainModel.isSuccess())
            {
                if (promoNearMeMainModel.getData().size()>0)
                {
                    arrayList.addAll(promoNearMeMainModel.getData());
                    adapter.notifyDataSetChanged();
                    RecyclerPromotionsAll.invalidate();

                }
            }
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
