package com.meeof.meeof.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.AllInterstAdapter;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetInterestsListWebJob;
import com.meeof.meeof.webjob.GetMyInterestWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dharmesh on 11/30/2017.
 */

public class MyAllInterestActivity extends BaseActivity {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    private String accessToken;
    JSONObject AllListOfInterest;
    ImageView backAcIvBtn;
    ArrayList<String> MyAllInterest=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_all_interest);
        recyclerView = (RecyclerView) findViewById(R.id.RecyclerAllInterest);
        recyclerView.setLayoutManager(new LinearLayoutManager(MyAllInterestActivity.this,LinearLayoutManager.VERTICAL,false));
        backAcIvBtn=(ImageView)findViewById(R.id.backAcIvBtn);
        backAcIvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        progressBar = (ProgressBar) findViewById(R.id.ProgressRecyclerAllInterest);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        progressBar.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new GetInterestsListWebJob(accessToken));

    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onGetInterestsWebJobCompleted(JSONObject interestsJObj) {

        if (interestsJObj != null) {
            try {
                if (interestsJObj.get("status") != null && interestsJObj.get("status").equals(Constant.SUCCESS)) {
                    if (interestsJObj.get("data") != null) {
                        try {
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMyInterestWebJobCompleted(JSONArray jsonArray) {
        progressBar.setVisibility(View.GONE);
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
                                                            String Name = jsonObject3.getString("name");
                                                            MyAllInterest.add(Name);

                                                        }
                                                    }
                                                }
                                            }
                                        } else {
                                            int key = jsonObject2.getInt("catid");
                                            if (key == id) {
                                                String Name = jsonObject2.getString("name");
                                                MyAllInterest.add(Name);
                                            }
                                        }
                                    }
                                }
                            } else {
                                int key = jsonObject.getInt("catid");
                                if (key == id) {
                                    String Name = jsonObject.getString("name");
                                    MyAllInterest.add(Name);

                                }
                            }
                        }
                    }
                }
                AllInterstAdapter allInterstAdapter=new AllInterstAdapter(MyAllInterestActivity.this,MyAllInterest);
                recyclerView.setAdapter(allInterstAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
