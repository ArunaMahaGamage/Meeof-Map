package com.meeof.meeof.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.InterestHttpResponse;
import com.meeof.meeof.model.interests.InterestsBaseItem;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetInterestsListWebJob;
import com.meeof.meeof.webjob.PostNewInterestWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ransikadesilva on 10/16/17.
 */

public class RequestNewInterestActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String TAG = RequestNewInterestActivity.class.getSimpleName();
    private Spinner interestSp;
    private String accessToken;
    private List<String> mainCategoriesTitles;
    private List<String> mainCategories;
    private TextView nameOfCategoryInfoTv;
    private LinearLayout nameOfCategoryLl;
    private LinearLayout sendRequestLlBtn;
    private EditText categoryEt;
    private EditText activityEt;
    private LinearLayout nameCatLl;
    private Button sendRequestBtn;
    private LinearLayout closeLlBtn;
    private boolean isNewCategorySelected;
    private boolean isCategoryEmpty;
    private boolean isActivityEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_new_interest);

        initViews();
    }

    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        interestSp = (Spinner) findViewById(R.id.interestSp);
        nameOfCategoryInfoTv = (TextView) findViewById(R.id.nameOfCategoryInfoTv);
        nameOfCategoryLl = (LinearLayout) findViewById(R.id.nameOfCategoryLl);
        categoryEt = (EditText) findViewById(R.id.categoryEt);
        activityEt = (EditText) findViewById(R.id.activityEt);
        nameCatLl = (LinearLayout) findViewById(R.id.nameCatLl);
        sendRequestBtn = (Button) findViewById(R.id.sendRequestBtn);
        closeLlBtn = (LinearLayout) findViewById(R.id.closeLlBtn);
        interestSp.setOnItemSelectedListener(this);
        closeLlBtn.setOnClickListener(this);
        sendRequestBtn.setOnClickListener(this);
        getInterestsList();
    }


    private void getInterestsList() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "ACCESS TOKEN: " + accessToken);
            jobManager.addJobInBackground(new GetInterestsListWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(sendRequestBtn, getString(R.string.no_internet), Constant.ERROR);
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
                        }
                    } else {
                        showSnackbar(sendRequestBtn, getString(R.string.category_list_load_failed), Constant.ERROR);
                    }
                } else {
                    showSnackbar(sendRequestBtn, getString(R.string.category_list_load_failed), Constant.ERROR);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showSnackbar(sendRequestBtn, getString(R.string.category_list_load_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(sendRequestBtn, getString(R.string.category_list_load_failed), Constant.ERROR);
        }
    }

    private void processInterestsJSON(JSONObject interestsData) throws JSONException, IOException {

        mainCategories = new ArrayList<>();
        mainCategoriesTitles = new ArrayList<>();

        LinkedHashMap<String, LinkedHashMap<String, List<InterestsBaseItem>>> interests = new LinkedHashMap<>();
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

        }

        mainCategoriesTitles.add("NEW CATEGORY");
        mainCategories.add("NEW CATEGORY");
        Log.d(TAG, "Main Category List Size: " + mainCategoriesTitles.size());
        initActivitiesSpinner(interestSp, mainCategoriesTitles);

    }

    private boolean initActivitiesSpinner(final Spinner spinner, final List<String> listSpinner) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RequestNewInterestActivity.this, android.R.layout.simple_spinner_item, listSpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        });
        return true;
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (mainCategoriesTitles.get(position).equals("NEW CATEGORY")) {
            isNewCategorySelected = true;
            nameCatLl.setVisibility(View.VISIBLE);
        } else {
            nameCatLl.setVisibility(View.GONE);
        }
    }

    //set the hint the default selection so it appears on launch.

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.sendRequestBtn:
                Log.d(TAG, "Inside sendRequestBtn");
                retrieveNewInterests();
                break;

            case R.id.closeLlBtn:
                this.finish();
                activityEt.setText(null);

        }
    }

    private void retrieveNewInterests() {
        Log.d(TAG, "Inside sendRequestLlBtn");
        Log.d(TAG, "MainCategoryTitle : " + mainCategoriesTitles.size());
        Log.d(TAG, "MainCategories : " + mainCategories.size());

        String selectedItem = interestSp.getSelectedItem().toString();
        String selectedCatId = "";
        for (int i = 0; i < mainCategoriesTitles.size(); i++) {
            if (mainCategoriesTitles.get(i).contains(selectedItem)) {
                if (mainCategoriesTitles.get(i).equals("NEW CATEGORY")) {
                    selectedCatId = "new";
                } else {
                    if (!mainCategories.get(i).equals("NEW CATEGORY")) {
                        selectedCatId = mainCategories.get(i).split(",")[0];
                    }
                }
            }
        }

        String category = "";
        if (selectedItem.equals("NEW CATEGORY")) {
            isNewCategorySelected = true;
            if (categoryEt.getText().toString().equals("") || categoryEt.getText().toString().equals(null)) {
                Log.d(TAG, "inside category ");
                Log.d(TAG, "MainCategories : " + mainCategories.size());
                showSnackbar(sendRequestBtn, getString(R.string.category_requst_editext_empty), Constant.ERROR);
                isCategoryEmpty = true;
            } else {
                Log.d(TAG, "else category ");
                Log.d(TAG, "MainCategories : " + mainCategories.size());
                isCategoryEmpty = false;
                category = categoryEt.getText().toString();
            }
        }

        String activity = "";
        if (activityEt.getText().toString().equals("") || activityEt.getText().toString().equals(null)) {
            Log.d(TAG, "inside activity if");
            showSnackbar(sendRequestBtn, getString(R.string.activity_requst_editext_empty), Constant.ERROR);
            isActivityEmpty = true;
        } else {
            Log.d(TAG, "else activity- " + activityEt.getText().toString());
            isActivityEmpty = false;
            activity = activityEt.getText().toString();
        }

        if (isNewCategorySelected) {
            if (isActivityEmpty && isCategoryEmpty) {
                Log.d(TAG, "isNewCategorySelected if");
                showSnackbar(sendRequestBtn, "Please enter the new category and activity you wish to create", Constant.ERROR);
            } else {
                if (isActivityEmpty || isCategoryEmpty) {
                    if (isActivityEmpty) {
                        Log.d(TAG, "isActivityEmpty || ");
                        showSnackbar(sendRequestBtn, getString(R.string.activity_requst_editext_empty), Constant.ERROR);
                    } else {
                        Log.d(TAG, "isCategoryEmpty ||");
                        showSnackbar(sendRequestBtn, getString(R.string.category_requst_editext_empty), Constant.ERROR);
                    }
                } else {
                    Log.d(TAG, "isActivityEmpty || isCategoryEmpty else");
                    categoryEt.setText("");
                    activityEt.setText("");
                    postUserInterestRequest(selectedCatId, category, activity);
                }

            }
        }else {

            if(isActivityEmpty){
                Log.d(TAG, "isActivityEmpty main else ");
                showSnackbar(sendRequestBtn, getString(R.string.activity_requst_editext_empty), Constant.ERROR);
            }else {
                activityEt.setText("");
                postUserInterestRequest(selectedCatId, selectedItem, activity);
            }

        }
    }


    private void postUserInterestRequest(String selectedCatId, String category, String activity) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new PostNewInterestWebJob(accessToken, selectedCatId, category, activity));
            startProgressBar();
        } else {

            Log.d(TAG, "Empty Fields");
            showSnackbar(sendRequestLlBtn, getString(R.string.activity_requst_failed), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPostNewInterestWebJobCompleted(final InterestHttpResponse interestHttpResponse) {
        stopProgressBar();

        Log.d(TAG, "onPostNewInterestWebJobCompleted");
        // sendRequestBtn.setClickable(true);
        if (interestHttpResponse != null) {
            if (interestHttpResponse.getStatus() != null && interestHttpResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "onPostNewInterestWebJobCompleted if");
                showSnackbar(sendRequestBtn, getString(R.string.new_category_sucess), Constant.SUCCESS);

                Helper.delay(3000, new Helper.DelayCallBack() {
                    @Override
                    public void postDelay() {
                        showSnackbar(sendRequestBtn, getString(R.string.new_category_sucess), Constant.SUCCESS);
                        RequestNewInterestActivity.this.finish();
                    }
                });


            } else {
                Log.d(TAG, "onPostNewInterestWebJobCompleted else");
                showSnackbar(sendRequestBtn, getString(R.string.activity_requst_failed), Constant.ERROR);

            }
        } else {
            Log.d(TAG, "onPostNewInterestWebJobCompleted else else");
            showSnackbar(sendRequestBtn, getString(R.string.activity_requst_failed), Constant.ERROR);

        }

    }
}
