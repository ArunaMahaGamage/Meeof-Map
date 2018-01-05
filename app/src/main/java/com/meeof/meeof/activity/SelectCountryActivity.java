package com.meeof.meeof.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.meeof.meeof.R;
import com.meeof.meeof.adapter.SearchCountryAdapter;
import com.meeof.meeof.model.countries_dto.CountriesResponse;
import com.meeof.meeof.model.countries_dto.Data;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetCountriesListWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ransikadesilva on 10/16/17.
 */

public class SelectCountryActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private static final String TAG = SelectCountryActivity.class.getSimpleName();
    private Context mContext;


    private RecyclerView mRecyclerView;
    private SearchCountryAdapter searchCountryAdapter;


    private EditText mSearchEdittext;
    private ImageView mClear;
    private TextView doneTvBtn;
    private String accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_country);
        mContext = SelectCountryActivity.this;

        initViews();

        if(getIntent().getStringExtra("SELECTED_COUNTRY") != null){
            String selectedCountry = getIntent().getStringExtra("SELECTED_COUNTRY");
            Log.d(TAG, "selectedCountry: " + selectedCountry);
            mSearchEdittext.setText(selectedCountry);
            mSearchEdittext.setSelection(selectedCountry.length());
        }
    }

    /*
   Initialize Views
    */
    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.list_search);
        doneTvBtn = (TextView) findViewById(R.id.doneTvBtn);


        mSearchEdittext = (EditText) findViewById(R.id.search_et);
        mClear = (ImageView) findViewById(R.id.clear);
        mClear.setOnClickListener(this);

        doneTvBtn.setOnClickListener(this);


        mSearchEdittext.addTextChangedListener(this);



        getCountryList();

    }


    @Override
    public void onClick(View v) {
        if (v == mClear) {
            mSearchEdittext.setText("");
            if (searchCountryAdapter != null) {
                searchCountryAdapter.clearList();
            }

        }
        switch (v.getId()) {
            case R.id.doneTvBtn:
                this.finish();
                break;
        }
    }

    private void getCountryList() {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetCountriesListWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(mRecyclerView, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCountriesJobCompleted(CountriesResponse countriesResponse) {
        stopProgressBar();
        if (countriesResponse != null) {
            if (countriesResponse.getStatus() != null && countriesResponse.getStatus().equals(Constant.SUCCESS)) {
                final List<Data> countryList = countriesResponse.getData();
                final List<String> countriesWithAlphaCode = new ArrayList<>();
                for (Data data : countryList) {
                    countriesWithAlphaCode.add(data.getenglish_name() + " " + data.getalpha2_code());
                    Log.d(TAG, "Country: "+data.getenglish_name() + " " + data.getalpha2_code());
                }


                setDataToListCountries(countriesWithAlphaCode);


            } else {
               // showSnackbar(mRecyclerView, getString(R.string.country_list_load_failed), Constant.ERROR);
            }
        } else {
            //(mRecyclerView, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
    }

    private void setDataToListCountries(List<String> countryList) {

        searchCountryAdapter = new SearchCountryAdapter(SelectCountryActivity.this, countryList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(SelectCountryActivity.this));
        mRecyclerView.setAdapter(searchCountryAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        searchCountryAdapter.notifyDataSetChanged();

        searchCountryAdapter.setOnClick(new SearchCountryAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, String item) {
                if (item != null) {
                    Intent data = new Intent();
                    data.putExtra("country", item);
                    setResult(PlacesActivity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (searchCountryAdapter != null) {
            searchCountryAdapter.filter(s.toString());
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
}

