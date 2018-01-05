package com.meeof.meeof.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.SearchInterestsAdapter;
import com.meeof.meeof.helper.RecyclerItemClickListener;
import com.meeof.meeof.model.InterestsSearchResponse;
import com.meeof.meeof.model.interests.InterestsBaseItem;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetInterestsSearchResultsWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.meeof.meeof.activity.InterestsActivity.selectedCategories;

public class SearchInterests extends BaseActivity implements TextWatcher {

    private EditText searchET;
    private TextView doneBtn;
    private String accessToken;
    private static final String TAG = "SearchInterests";
    private SearchInterestsAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private Map<String, HashMap<String, ArrayList<Boolean>>> totalCheckMap;
    private LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interestsCategories;
    private List<JSONObject> selectedActivitiesArr;
    private List<JSONObject> deselectedActivitiesArr;
    private static final int REQUEST_CODE = 101;
    private JSONArray interestsCategoriesArr;
    private boolean isFromInterests;
    private boolean isFromCreateEvent;
    private JSONObject selectedItemsJson;
    private int timeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.wtf(TAG, "Inside isFromCreateEvent :" + getIntent().hasExtra(Constant.IS_FROM_CREATE_EVENT));
        Log.wtf(TAG, "Inside isFromCreateEvent :" + getIntent().getBooleanExtra(Constant.IS_FROM_CREATE_EVENT, false));

        if (getIntent().hasExtra(Constant.IS_FROM_INTERESTS_ACTIVITY) && getIntent().getBooleanExtra(Constant.IS_FROM_INTERESTS_ACTIVITY, false)) {
            try {
                Log.d(TAG, "Inside isFromInterests");
                isFromInterests = true;
                totalCheckMap = (HashMap) getIntent().getSerializableExtra("totalCheckMap");
                interestsCategories = (LinkedHashMap) getIntent().getSerializableExtra("interestsCategories");
                String arrayDataStr = getIntent().getStringExtra("interestsCategoriesArr");
                interestsCategoriesArr = new JSONArray(arrayDataStr);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (getIntent().hasExtra(Constant.IS_FROM_CREATE_EVENT) && getIntent().getBooleanExtra(Constant.IS_FROM_CREATE_EVENT, false)) {
            isFromCreateEvent = true;
            try {
                String jsonString = getIntent().getStringExtra(Constant.SELECTED_ACTIVITY_EVENT);
                if (jsonString != null) {
                    Log.d(TAG, "Inside isFromCreateEvent");
                    selectedItemsJson = new JSONObject(jsonString);
                }
                String arrayDataStr = getIntent().getStringExtra("interestsCategoriesArr");
                Log.d(TAG, "Inside isFromCreateEvent arrayDataStr: " + arrayDataStr);
                interestsCategoriesArr = new JSONArray(arrayDataStr);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Inside isFromCreateEvent exception: " + e.getMessage());
            }
        }


        try {
            selectedActivitiesArr = new ArrayList<>();
            deselectedActivitiesArr = new ArrayList<>();
            Log.d(TAG, "interestsCategoriesArr size: " + interestsCategoriesArr.length());
            setAlreadySelectedItems();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        searchET = (EditText) findViewById(R.id.search_et);
        doneBtn = (TextView) findViewById(R.id.doneBtn);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedActivitiesArr.size() > 0) {

                    try {
                        saveSelectedAcitvities();
                        removedDeselectedInterets();
                        Log.d(TAG, "selectedCategories: " + selectedCategories);
                        Intent mIntent = new Intent();
                        mIntent.putExtra("selectedCategories", selectedCategories);
                        setResult(RESULT_OK, mIntent);
                        SearchInterests.this.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    SearchInterests.this.finish();
                }
            }
        });

        searchET.addTextChangedListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {


                        try {
                            JSONObject item = (JSONObject) adapter.dataList().get(position);
                            String catId = item.getString("id");

                            Log.wtf(TAG, "RecyclerItemClickListener: " + catId);
                            Log.wtf(TAG, "RecyclerItemClickListener:item " + item.toString());

                            Log.wtf(TAG, "selectedActivitiesArr size1: " + selectedActivitiesArr.size());


                            if (isFromInterests) {
                                if (selectedActivitiesArr.contains(item)) {
                                    selectedActivitiesArr.remove(item);
                                    deselectedActivitiesArr.add(item);
                                }else if(containsItemWithId(selectedActivitiesArr, item)){
                                    int index = getIndexOfItem(selectedActivitiesArr,item);
                                    Log.wtf(TAG, "selectedActivitiesArr indexOf: " + index);
                                    if(index >= 0) {
                                        selectedActivitiesArr.remove(index);
                                        deselectedActivitiesArr.add(item);
                                    }
                                } else {
                                    selectedActivitiesArr.add(item);
                                    deselectedActivitiesArr.remove(item);
                                }
                            } else if (isFromCreateEvent) {
                                finishSearchInterests(item);

                            }

                            adapter.setSelectedActivitiesArr(selectedActivitiesArr);
                            adapter.notifyDataSetChanged();

                            Log.wtf(TAG, "selectedActivitiesArr size3: " + selectedActivitiesArr.size());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
        timeOut = 500;
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
    }



    private boolean containsItemWithId(List<JSONObject> selectedActivitiesArr, JSONObject item) throws JSONException {
        boolean value = false;

        Log.wtf(TAG, "item obj: " + item.toString());

        for(int i = 0 ; i < selectedActivitiesArr.size() ; i++){

            JSONObject obj = (JSONObject) selectedActivitiesArr.get(i);

            Log.wtf(TAG, "containsItemWithId obj: " + obj);

            if(obj.getString("id").equalsIgnoreCase(item.getString("id"))){
                value = true;
                Log.wtf(TAG, "containsItemWithId index: " + i);
                break;
            }
        }
        Log.wtf(TAG, "containsItemWithId: " + value);
        return  value;
    }

    private void finishSearchInterests(JSONObject item) {
        Intent data = new Intent();
        data.putExtra(Constant.SELECTED_ACTIVITY_EVENT, item.toString());
        setResult(PlacesActivity.RESULT_OK, data);
        finish();
    }

    //check here if the search doesn't work
    private void saveSelectedAcitvities() throws JSONException {

        try{

            LinkedHashMap<String, List<String>> dataMap = new LinkedHashMap<>();

            for (JSONObject item : selectedActivitiesArr) {

                Log.wtf(TAG, "selectedActivitiesArr item: " + item.toString());
                String tier1Id = item.getString("tier1");
                String tier2Id = item.getString("tier2");
                String catId = item.getString("id");

                List<String> selectedCategoryIdsArr = new ArrayList<>();

                if (selectedCategories.get(tier1Id) != null) {
                    dataMap = selectedCategories.get(tier1Id);
                    Log.wtf(TAG, "selectedCategories have for tier 1: " + dataMap.toString());

                    if (dataMap.get(tier2Id) != null) {
                        selectedCategoryIdsArr = dataMap.get(tier2Id);
                    }
                }

                if(selectedCategoryIdsArr.size() > 0){

                    for(String tempId : selectedCategoryIdsArr){

                        if(tempId.equalsIgnoreCase(catId)){

                        }else {
                            selectedCategoryIdsArr.add(catId);
                        }
                    }
                }else{
                    selectedCategoryIdsArr.add(catId);
                }

                //this category has no sub categories
                if (tier1Id == tier2Id) {
                    dataMap.put(catId, selectedCategoryIdsArr);
                } else {
                    //this is sub category of a main category
                    dataMap.put(tier2Id, selectedCategoryIdsArr);
                }
                selectedCategories.put(tier1Id, dataMap);
            }

        }catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "saveSelectedAcitvities exeption: " + e.toString());
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        timeOut = 300;
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        if (editable.toString().length() > 0) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    retreiveInterestSearchResults(editable.toString().trim());
                }
            }, timeOut);
        }
    }

    private void retreiveInterestSearchResults(String searchStr) {

        Log.d(TAG, "retreiveInterestSearchResults: " + searchStr);

        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetInterestsSearchResultsWebJob(accessToken, searchStr));
        } else {
            showSnackbar(searchET, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    @Subscribe
    public void InterestsSearchResponse(InterestsSearchResponse interestSearchResponse) {
        Log.d(TAG, "onGetCountriesJobCompleted: ");
        stopProgressBar();
        if (interestSearchResponse != null) {

            if (interestSearchResponse.getStatus() != null && interestSearchResponse.getStatus().equals(Constant.SUCCESS)) {

                final JSONArray dataArr = interestSearchResponse.getData();
                Log.d(TAG, "onGetCountriesJobCompleted: " + dataArr.length());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isFromCreateEvent) {
                            adapter = new SearchInterestsAdapter(SearchInterests.this, dataArr, interestsCategoriesArr, selectedActivitiesArr,true,selectedItemsJson);
                        } else {
                            adapter = new SearchInterestsAdapter(SearchInterests.this, dataArr, interestsCategoriesArr, selectedActivitiesArr,false,null);
                        }
                        recyclerView.setAdapter(adapter);

                    }
                });


            } else {
                showSnackbar(searchET, getString(R.string.oop_something_went_wrong), Constant.ERROR);
            }
        } else {
            showSnackbar(searchET, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private String getCategoryName(String parentId) {
        try {

            String name = "";

            for (int i = 0; i < interestsCategoriesArr.length(); i++) {
                JSONObject tempCategoryItem = (JSONObject) interestsCategoriesArr.get(i);
                Log.d(TAG, "categoryItem: " + tempCategoryItem.toString());
                Iterator<String> keysIterator = tempCategoryItem.keys();
                String gridKey = (String) keysIterator.next();
                String tempId = gridKey.split(",")[0];

                if (tempId == parentId) {
                    name = gridKey.split(",")[1];
                    break;
                }
            }
            return name;

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
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

    private void setAlreadySelectedItems() throws JSONException {

        LinkedHashMap<String, List<String>> dataMap = new LinkedHashMap<>();
        Set<String> keySet = selectedCategories.keySet();
        Log.wtf(TAG, "setAlreadySelectedItems:keysSet " + keySet.toString());

        for (String key : keySet) {
            LinkedHashMap<String, List<String>> LinkedHashMap = selectedCategories.get(key);
            Set<String> idSet = LinkedHashMap.keySet();
            Log.wtf(TAG, "setAlreadySelectedItems 2:idSet " + idSet.toString());
            Log.wtf(TAG, "setAlreadySelectedItems 2:LinkedHashMap " + LinkedHashMap.toString());


            for (String idKey : idSet) {
                List<String> idStringList = LinkedHashMap.get(idKey);
                Log.wtf(TAG, "idStringList " + idStringList.toString());

                for(String catId : idStringList){
                    JSONObject ojb = new JSONObject();
                    ojb.put("id", catId);
                    ojb.put("tier1", key);
                    ojb.put("tier2", idKey);
                    selectedActivitiesArr.add(ojb);
                    Log.wtf(TAG, "catId ojb " + ojb.toString());
                    break;
                }
            }
        }
        Log.wtf(TAG, "setAlreadySelectedItems selectedActivitiesArr " + selectedActivitiesArr.size());

        for(JSONObject obj : selectedActivitiesArr){
            Log.wtf(TAG, "selectedActivitiesArr obj " + obj.toString());
        }
    }

    private int getIndexOfItem(List<JSONObject> selectedActivitiesArr, JSONObject item) throws JSONException {
         int value = -1;

        Log.wtf(TAG, "item obj: " + item.toString());

        for(int i = 0 ; i < selectedActivitiesArr.size() ; i++){

            JSONObject obj = (JSONObject) selectedActivitiesArr.get(i);

            Log.wtf(TAG, "getIndexOfItem obj: " + obj);

            if(obj.getString("id").equalsIgnoreCase(item.getString("id"))){
                value = i;
            }
        }

        Log.wtf(TAG, "getIndexOfItem: " + value);
        return  value;
    }

    private void removedDeselectedInterets() throws JSONException {

        LinkedHashMap<String, List<String>> dataMap = new LinkedHashMap<>();

        for (JSONObject item : deselectedActivitiesArr) {

            Log.wtf(TAG, "deselectedActivitiesArr item: " + item.toString());
            String tier1Id = item.getString("tier1");
            String tier2Id = item.getString("tier2");
            String catId = item.getString("id");

            List<String> selectedCategoryIdsArr = new ArrayList<>();

            if (selectedCategories.get(tier1Id) != null) {
                dataMap = selectedCategories.get(tier1Id);
                Log.wtf(TAG, "deselectedActivitiesArr have for tier 1: " + dataMap.toString());

                if (dataMap.get(tier2Id) != null) {
                    selectedCategoryIdsArr = dataMap.get(tier2Id);
                }
            }

            if(selectedCategoryIdsArr.size() > 0){
                for(String tempId : selectedCategoryIdsArr){
                    if(tempId.equalsIgnoreCase(catId)){
                        selectedCategoryIdsArr.remove(catId);
                    }
                }
            }

            //this category has no sub categories
            if (tier1Id == tier2Id) {
                dataMap.put(catId, selectedCategoryIdsArr);
            } else {
                //this is sub category of a main category
                dataMap.put(tier2Id, selectedCategoryIdsArr);
            }
            selectedCategories.put(tier1Id, dataMap);
        }
    }

    private void removeFromSelectedCategories(JSONObject item) throws JSONException {

        String tier1 = item.getString("tier1");
        String tier2 = item.getString("tier2");

        Log.wtf(TAG, "removeFromSelectedCategories tier1 " + tier1);
        Log.wtf(TAG, "removeFromSelectedCategories tier2 " + tier2);
        Log.wtf(TAG, "removeFromSelectedCategories id " + item.getString("id"));

        LinkedHashMap<String, List<String>> LinkedHashMap  = selectedCategories.get(tier1);
        List<String> idStringList = LinkedHashMap.get(tier2);

        Log.wtf(TAG, "LinkedHashMap " + LinkedHashMap.toString());
        Log.wtf(TAG, "idStringList1 " + idStringList.toString());

        for(int i = 0 ; i < idStringList.size() ; i ++){
            String catId = idStringList.get(i);
            Log.wtf(TAG, "catId " + catId);

            if(catId.equalsIgnoreCase(item.getString("id"))){
                idStringList.remove(catId);
            }
        }


        LinkedHashMap.put(tier2, idStringList);
        selectedCategories.put(tier1, LinkedHashMap);

        Log.wtf(TAG, "idStringList2 " + idStringList.toString());
        Log.wtf(TAG, "LinkedHashMap2 " + LinkedHashMap.toString());
        Log.wtf(TAG, "selectedCategories " + selectedCategories.toString());
    }

    private List<JSONObject> removeDuplicates(List<JSONObject> selectedActivitiesArr){
//        List<String> al = new ArrayList<>();
        // add elements to al, including duplicates
        Set<JSONObject> hs = new HashSet<>();
        hs.addAll(selectedActivitiesArr);
        selectedActivitiesArr.clear();
        selectedActivitiesArr.addAll(hs);

        return selectedActivitiesArr;
    }

}
