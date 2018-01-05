package com.meeof.meeof.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.LocaleDisplayNames;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.ExpandListAdapter;
import com.meeof.meeof.adapter.InterestMainCatGridAdapter;
import com.meeof.meeof.adapter.TotalListener;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.UserInterestResponse;
import com.meeof.meeof.model.interests.InterestsBaseItem;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetInterestsListWebJob;
import com.meeof.meeof.webjob.GetUserInterestsWebJob;
import com.meeof.meeof.webjob.PostUserInterestsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class InterestsActivity extends BaseActivity implements TotalListener, View.OnClickListener {


    private static final String TAG = InterestsActivity.class.getSimpleName();
    TextView mTextView;
    private ExpandableListView expandableListView;
    private GridView categoriesGridView;
    private String accessToken;
    private ExpandListAdapter adapterExpandListAdapter;
    private InterestMainCatGridAdapter interestGridAdapter;
    private static final int REQUEST_CODE = 101;


    public static Map<String, Boolean> mainCatCheckMap = new HashMap<>();
    public static Map<String, ArrayList<ArrayList<Boolean>>> subCatCheckMap = new HashMap<>();
    public static Map<String, HashMap<String, ArrayList<Boolean>>> totalCheckMap = new HashMap<>();
    public static Map<String, HashMap<String, ArrayList<String>>> totalCheckMap2 = new HashMap<>();
    public static HashMap<String, ArrayList<Boolean>> tempBoolMap2 = new HashMap<>();


    public static LinkedHashMap<String, LinkedHashMap<String, List<String>>> selectedCategories;
    public static LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interestsCategories;

    private ArrayList<Integer> userInterestList;

    private JSONArray interestsCategoriesArr;
    private LinearLayout searchBtnRl;
    private AppCompatImageView back_button;
    private TextView titleTv;
    private TextView requestNewActivityTvBtn;
    private LinearLayout nextLlBtn;
    private boolean isFromSettings;
    private TextView nextTvBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests);

        if (getIntent().hasExtra(Constant.IS_FROM_SETTINGS)) {
            isFromSettings = this.getIntent().getBooleanExtra(Constant.IS_FROM_SETTINGS, false);
        }
        initViews();
    }

    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        expandableListView = (ExpandableListView) findViewById(R.id.expandable_list);
        categoriesGridView = (GridView) findViewById(R.id.gridView1);

        searchBtnRl = (LinearLayout) findViewById(R.id.search_btn_ll);

        nextLlBtn = (LinearLayout) findViewById(R.id.nextLlBtn);

        back_button = (AppCompatImageView) findViewById(R.id.backAcIvBtn);

        nextTvBtn = (TextView) findViewById(R.id.nextTvBtn);

        titleTv = (TextView) findViewById(R.id.titleTv);
        requestNewActivityTvBtn = (TextView) findViewById(R.id.requestNewActivityTvBtn);


        if (isFromSettings) {
            nextTvBtn.setText("Save");
            userInterestList=new ArrayList<>();
            getUserSelectedInterests();
        }

        searchBtnRl.setOnClickListener(this);
        back_button.setOnClickListener(this);
        titleTv.setOnClickListener(this);
        nextLlBtn.setOnClickListener(this);
        requestNewActivityTvBtn.setOnClickListener(this);
        Log.d(TAG, "token::::::> " + accessToken);
        selectedCategories = new LinkedHashMap<>();
        getInterestsList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_btn_ll:
                Log.d(TAG, "totalCheckMap:123 " + totalCheckMap.toString());
                Intent intent2 = new Intent(this, SearchInterests.class);
                intent2.putExtra(Constant.IS_FROM_INTERESTS_ACTIVITY,true);
                intent2.putExtra("totalCheckMap", (Serializable) totalCheckMap);
                intent2.putExtra("interestsCategoriesArr", interestsCategoriesArr.toString());
                this.startActivityForResult(intent2, REQUEST_CODE);


                break;

            case R.id.backAcIvBtn:
                Log.wtf(TAG, "selectedCategories size: " + selectedCategories.size());

                boolean didDataSetChange = sharedPreferences.getBoolean(Constant.CHANGES_AVIALABLE, false);

                if(didDataSetChange){
                    showConfirmationAlert(this, null, null);
                }else{
                    this.onBackPressed();
                    finish();
                }
                break;

            case R.id.requestNewActivityTvBtn:
                sendToRequestNewInterestActivity();
                Helper.clickGaurd(requestNewActivityTvBtn);
                break;

            case R.id.nextLlBtn:
                postSelectedCategories();
                break;
        }
    }


    public void showConfirmationAlert(Context c, String title, String imageUrl) {

        try {
            final Dialog dialog = new Dialog(c);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_search_interests_close_confirmation);
            dialog.setCancelable(false);

            Button cancelBtn = (Button) dialog.findViewById(R.id.cancelBtn);
            Button confirmBtn = (Button) dialog.findViewById(R.id.confirmBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    sharedEditor = sharedPreferences.edit();
                    sharedEditor.putBoolean(Constant.CHANGES_AVIALABLE, false);
                    sharedEditor.commit();

                    InterestsActivity.super.onBackPressed();
                    finish();
                }
            });
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    postSelectedCategories();
                }
            });

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToRequestNewInterestActivity() {
        Intent intent = new Intent(this, RequestNewInterestActivity.class);
        startActivity(intent);
    }

    private void getInterestsList() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "ACCESS TOKEN: " + accessToken);
            jobManager.addJobInBackground(new GetInterestsListWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(expandableListView, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    public void getUserSelectedInterests() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "getUserSelectedInterests ACCESS TOKEN: " + accessToken);
            jobManager.addJobInBackground(new GetUserInterestsWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(searchBtnRl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetInterestsWebJObCompleted(JSONObject interestsJObj) {
        stopProgressBar();
        if (interestsJObj != null) {
            try {
                if (interestsJObj.get("status").equals(Constant.SUCCESS)) {
                    if (interestsJObj.get("data") != null) {
                        try {
                            processInterestsJSON(interestsJObj.getJSONObject("data"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            showSnackbar(expandableListView, getString(R.string.interest_category_load_failed), Constant.ERROR);
                        }
                    } else {
                        showSnackbar(expandableListView, getString(R.string.interest_category_load_failed), Constant.ERROR);
                    }
                } else {
                    showSnackbar(expandableListView, getString(R.string.interest_category_load_failed), Constant.ERROR);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showSnackbar(expandableListView, getString(R.string.interest_category_load_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(expandableListView, getString(R.string.interest_category_load_failed), Constant.ERROR);
        }
    }

    private void processInterestsJSON(JSONObject interestsData) throws JSONException, IOException {
        Log.d(TAG, "Interests::: " + interestsData.toString());
        ArrayList<String> mainCategories = new ArrayList<>();
        ArrayList<String> mainCategoriesTitles = new ArrayList<>();

        LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interests = new LinkedHashMap<>();
        interestsCategories = new LinkedHashMap<>();
        interestsCategoriesArr = new JSONArray();

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

//        LinkedHashMap<String, List<String>> map = new LinkedHashMap();
//        if(selectedCategories != null && selectedCategories.size() > 0 ){
//
//            Set<String> set = selectedCategories.keySet();
//            for (String key : set) {
//
//                map = selectedCategories.get(key);
//                Log.d(TAG, "selectedActivitiesMap: " + map);
//                initialSelectedCount +=  map.size();
//            }
//        }
//
//        Log.d(TAG, "selectedActivitiesMap size: " + map.size());
//        Log.d(TAG, "initialSelectedCount: " + initialSelectedCount);

        //setUpGridView(mainCategories, mainCategoriesTitles, interests);
        setUpGridView(mainCategories, mainCategoriesTitles, interestsCategoriesArr);



        for (String cat : mainCategories) {
            mainCatCheckMap.put(cat, false);
        }
    }


    private void setUpGridView(final ArrayList<String> mainCategories, final ArrayList<String> mainCategoryTitles, final JSONArray interestsCategoriesArr) {

        Log.wtf(TAG, "mainCategoryArray: " + Arrays.asList(mainCategories).toString());

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int[] mThumbIds = {
                        R.drawable.ico_activity_activity_sport, R.drawable.ico_activity_unboard,
                        R.drawable.ico_activity_uncreative, R.drawable.ico_activity_ungaming,
                        R.drawable.ico_activity_ungoing, R.drawable.ico_activity_unparks,
                };

                if(isFromSettings){
                    interestGridAdapter = new InterestMainCatGridAdapter(InterestsActivity.this, mThumbIds, interestsCategoriesArr,userInterestList);
                }else{
                    interestGridAdapter = new InterestMainCatGridAdapter(InterestsActivity.this, mThumbIds, interestsCategoriesArr,null);
                }

                interestGridAdapter.setIntialLoading(true);
                categoriesGridView.setAdapter(interestGridAdapter);



                //grid on click
                interestGridAdapter.setOnClick(new InterestMainCatGridAdapter.OnItemClicked() {
                    @Override
                    public void onItemClick(int position) throws JSONException {

                        try {

                            JSONObject tempCategoryItem = (JSONObject) interestsCategoriesArr.get(position);
                            Log.d(TAG, "categoryItem: " + tempCategoryItem.toString());
                            Iterator<String> keysIterator = tempCategoryItem.keys();
                            String gridKey = (String) keysIterator.next();

                            JSONObject categoryItem = tempCategoryItem.getJSONObject(gridKey);
                            String categoryName = categoryItem.getString("name");
                            Log.d(TAG, "categoryItem interestGridAdapter setOnClick  " + categoryItem.toString());

                            //these are the activities in the the main data set, this will always have items
                            JSONArray activitiesArr = getActivtieArray(categoryItem);
                            Log.d(TAG, "activitiesArr count for  " + categoryName + " " + activitiesArr.length());

                            Log.d(TAG, "categoryItem: selected " + categoryItem.toString());
                            Log.d(TAG, "categoryItem: selected " + categoryItem.length());


                            setUpExpandableList(activitiesArr);

                        } catch (Exception e) {
                            Log.d(TAG,"Exception Expandable List:"+e.getClass().getCanonicalName());
                            e.printStackTrace();
                        }

                    }

                });

                interestGridAdapter.setOnCheckBoxClicked(new InterestMainCatGridAdapter.OnCheckBoxClicked() {
                    @Override
                    public void onCheckBoxClick(int position, boolean b) {
                        interestGridAdapter.notifyDataSetChanged();
                        adapterExpandListAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    public void test(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject tempCategoryItem = (JSONObject) interestsCategoriesArr.get(0);
                    Log.d(TAG, "categoryItem: " + tempCategoryItem.toString());
                    Iterator<String> keysIterator = tempCategoryItem.keys();
                    String gridKey = (String) keysIterator.next();

                    JSONObject categoryItem = tempCategoryItem.getJSONObject(gridKey);
                    String categoryName = categoryItem.getString("name");
                    Log.d(TAG, "categoryItem interestGridAdapter setOnClick  " + categoryItem.toString());

                    //these are the activities in the the main data set, this will always have items
                    JSONArray activitiesArr = getActivtieArray(categoryItem);
                    Log.d(TAG, "activitiesArr count for  " + categoryName + " " + activitiesArr.length());

                    Log.d(TAG, "categoryItem: selected " + categoryItem.toString());
                    Log.d(TAG, "categoryItem: selected " + categoryItem.length());


                    setUpExpandableList(activitiesArr);

                } catch (Exception e) {
                    Log.d(TAG,"Exception Expandable List:"+e.getClass().getCanonicalName());
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onTotalChanged() {
        interestGridAdapter.notifyDataSetChanged();
    }

    private void setUpExpandableList(final JSONArray activitiesArr) {
        //Log.d(TAG,"User interest list: "+userInterestList.toString());

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isFromSettings){
                        adapterExpandListAdapter = new ExpandListAdapter(InterestsActivity.this, activitiesArr, interestGridAdapter,userInterestList);
                    }else{
                        adapterExpandListAdapter = new ExpandListAdapter(InterestsActivity.this, activitiesArr, interestGridAdapter,null);
                    }
                    adapterExpandListAdapter.setmListener(InterestsActivity.this);
                    expandableListView.setAdapter(adapterExpandListAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void expandGroupEvent(int groupPosition, boolean isExpanded) {
        if (isExpanded)
            expandableListView.collapseGroup(groupPosition);
        else
            expandableListView.expandGroup(groupPosition);
    }

    public void postSelectedCategories() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostUserInterestsWebJob(accessToken, getUserCheckedInterestsList()));
            startProgressBar();
        } else {
            showSnackbar(searchBtnRl, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostUserInterestsWebJob(HttpResponse httpResponseInterests) {
        stopProgressBar();
        if (httpResponseInterests != null) {
            if (httpResponseInterests.getStatus() != null && httpResponseInterests.getStatus().equals(Constant.SUCCESS)) {
                if(isFromSettings){

                    sharedEditor = sharedPreferences.edit();
                    sharedEditor.putBoolean(Constant.CHANGES_AVIALABLE, false);
                    sharedEditor.commit();


                    this.finish();
                }else{
                    sendToFindFriendsActivity();
                }

            } else {
                showSnackbar(searchBtnRl, getString(R.string.interest_save_info_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(searchBtnRl, getString(R.string.interest_save_info_failed), Constant.ERROR);
        }
    }

    @Subscribe
    public void onPostUserInterestsWebJob(UserInterestResponse userInterestResponse) {
        stopProgressBar();
        if (userInterestResponse != null) {
            if (userInterestResponse.getStatus() != null && userInterestResponse.getStatus().equals(Constant.SUCCESS)) {

                userInterestList=userInterestResponse.getData();

            } else {
                showSnackbar(searchBtnRl, "Could not retrieve user Interest", Constant.ERROR);
            }
        } else {
            showSnackbar(searchBtnRl, "Could not retrieve user Interest", Constant.ERROR);
        }
    }


    private void sendToFindFriendsActivity() {

        Intent intent = new Intent(this, InviteFriendsActivity.class);
        intent.putExtra(Constant.IS_FROM_INTERESTS, true);
        this.startActivity(intent);
    }

    private String[] getUserCheckedInterestsList() {
        Set<String> chekInterests = new HashSet<>();

        Set<String> keySet = selectedCategories.keySet();

        Log.wtf(TAG, "getCategoryName:keysSet " + keySet.toString());

        for (String key : keySet) {
            Set<String> keySetInner = selectedCategories.get(key).keySet();
            for (String keyInner : keySetInner) {
                List<String> chekListVals = selectedCategories.get(key).get(keyInner);
                chekInterests.addAll(chekListVals);
                Log.wtf(TAG, "Inner key: " + keyInner);
            }
        }
        Log.wtf(TAG, "chekInterests str: " + chekInterests.toString());

        return chekInterests.toArray(new String[chekInterests.size()]);
    }

    private JSONArray getActivtieArray(JSONObject tempCategoryItem) throws JSONException {

        JSONArray activitiesArr = new JSONArray();

        Object subCat = tempCategoryItem.get("sub_cat");

        if (subCat instanceof JSONObject) {

            JSONObject subCategoryJSONObj = tempCategoryItem.getJSONObject("sub_cat");
            Iterator<String> keysIterator = subCategoryJSONObj.keys();

            while (keysIterator.hasNext()) {
                String gridKey = (String) keysIterator.next(); //NEED
                activitiesArr.put(subCategoryJSONObj.getJSONObject(gridKey));
            }

        } else if (subCat instanceof JSONArray) {
            activitiesArr = (JSONArray) subCat;
        }
        return activitiesArr;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE && data != null) {
                interestGridAdapter.notifyDataSetChanged();
                adapterExpandListAdapter.notifyDataSetChanged();
            }
        }
    }


}
