package com.meeof.meeof.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.meeof.meeof.R;
import com.meeof.meeof.adapter.GoogleAutocompleteSearchAdapter;
import com.meeof.meeof.adapter.PlaceAutocompleteListAdapter;
import com.meeof.meeof.model.EditProfileGooglePlaceIdResponse;
import com.meeof.meeof.model.GoogleAutocompletePlace;
import com.meeof.meeof.model.GoogleAutocompleteResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GoogleAutocompleteRequestWebJob;
import com.meeof.meeof.webjob.PostEditProfileInfoWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlacesActivity extends BaseActivity implements GoogleAutocompleteSearchAdapter.OnItemClicked, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,View.OnClickListener{

    Context mContext;
    GoogleApiClient mGoogleApiClient;

    LinearLayout mParent;
    private RecyclerView mRecyclerView;
    LinearLayoutManager llm;
    PlaceAutocompleteListAdapter mAdapter;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(
            new LatLng(-0, 0), new LatLng(0, 0));

    EditText mSearchEdittext;
    ImageView mClear;
    private TextView doneTvBtn;
    private String userCountry;
    private static final String TAG = PlacesActivity.class.getSimpleName();
    private GoogleAutocompleteSearchAdapter googleAutocompleteSearchAdapter;
    private int timeOut;


    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        EventBus.getDefault().register(this);

        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = PlacesActivity.this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this,  this)
                .build();

        Intent intent = getIntent();
        userCountry = intent.getStringExtra("country");
        timeOut = 300;
        initViews();
    }

    private void initViews(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.list_search);
        doneTvBtn = (TextView)findViewById(R.id.doneTvBtn);

        mRecyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(llm);

        mSearchEdittext = (EditText)findViewById(R.id.search_et);
        mClear = (ImageView)findViewById(R.id.clear);
        mClear.setOnClickListener(this);

        doneTvBtn.setOnClickListener(this);

//        mAdapter = new PlaceAutocompleteListAdapter(this, R.layout.item_placesearch,
//                mGoogleApiClient, null, null);
//        mRecyclerView.setAdapter(mAdapter);

        googleAutocompleteSearchAdapter = new GoogleAutocompleteSearchAdapter(mContext, new ArrayList<GoogleAutocompletePlace>(), this);


        mSearchEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                timeOut = 300;

                if (s.toString().length() > 0) {
                    mClear.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            googleAutocomplete(s.toString(), userCountry, getString(R.string.google_api_map_key));
                        }
                    }, timeOut);
                }

//                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
//                    if(userCountry!=null) {
//                        //mAdapter.getFilter().filter(s.toString() + " " + userCountry);
//                        mAdapter.getFilter().filter(s.toString());
//                    }else{
//                        mAdapter.getFilter().filter(s.toString());
//                    }
//                } else if (!mGoogleApiClient.isConnected()) {
//                    Log.e("", "NOT CONNECTED");
//                }
//                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        if(getIntent().getStringExtra("SELECTED_LOCATION") != null){
            String selectedLocation = getIntent().getStringExtra("SELECTED_LOCATION");
            mSearchEdittext.setText(selectedLocation);
            mSearchEdittext.setSelection(selectedLocation.length());
        }
    }

    public void googleAutocomplete(String input, String country, String apiKey){
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GoogleAutocompleteRequestWebJob(input, country, apiKey));
        } else {
            showSnackbar(mRecyclerView, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void googleAutoCompleteResponse(final GoogleAutocompleteResponse googleAutocompleteResponse) {
        Log.wtf(TAG, "googleAutoCompleteResponse");
        stopProgressBar();

        if (googleAutocompleteResponse != null) {

            if (googleAutocompleteResponse.getStatus() != null && googleAutocompleteResponse.getStatus().contains(Constant.SUCCESS)) {

                try {
                    List<GoogleAutocompletePlace> data = googleAutocompleteResponse.getPredictions();

                    if(data != null && data.size() > 0){
                        googleAutocompleteSearchAdapter.setDataList(data);
                    }else{
                        googleAutocompleteSearchAdapter.clearList();
                    }
                    mRecyclerView.setAdapter(googleAutocompleteSearchAdapter);
                    googleAutocompleteSearchAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    //showSnackbar(mSearchEdittext, getString(R.string.google_place_failed), Constant.ERROR);
                }

            } else {
                if(googleAutocompleteResponse.getPredictions() != null){
                    googleAutocompleteSearchAdapter.clearList();
                    mRecyclerView.setAdapter(googleAutocompleteSearchAdapter);
                    googleAutocompleteSearchAdapter.notifyDataSetChanged();
                }else {
                    //showSnackbar(mSearchEdittext, getString(R.string.google_place_failed), Constant.ERROR);
                }
            }
        } else {
            showSnackbar(mSearchEdittext, getString(R.string.google_place_failed), Constant.ERROR);
        }
    }

    @Override
    public void onClick(View v) {
        if(v == mClear){
            mSearchEdittext.setText("");
            if(mAdapter!=null){
                mAdapter.clearList();
            }

            if(googleAutocompleteSearchAdapter != null){
                googleAutocompleteSearchAdapter.clearList();
                mRecyclerView.setAdapter(googleAutocompleteSearchAdapter);
                googleAutocompleteSearchAdapter.notifyDataSetChanged();
            }
        }
        switch (v.getId()) {
            case R.id.doneTvBtn:
                this.finish();
                break;
        }
    }



    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onItemClick(int position, GoogleAutocompletePlace item) {

        try {
            final String placeId = String.valueOf(item.getPlace_id());
            /*
               Issue a request to the Places Geo Data API to retrieve a Place object with additional details about the place.
            */

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(PlaceBuffer places) {
                    if(places.getCount()==1){
                        //Do the things here on Click.....
                        Intent data = new Intent();
                        data.putExtra("lat",String.valueOf(places.get(0).getLatLng().latitude));
                        data.putExtra("lng", String.valueOf(places.get(0).getLatLng().longitude));
                        data.putExtra("place_name",places.get(0).getName());
                        data.putExtra("place_id",places.get(0).getId());
                        data.putExtra("place_address",places.get(0).getAddress());
                        setResult(PlacesActivity.RESULT_OK, data);

                        Log.d(TAG, "selected place id: " + places.get(0).getId());
                        Log.d(TAG, "selected place_name: " + places.get(0).getName());

                        finish();
                    }else {
                    }
                }
            });
        }
        catch (Exception e){

        }
    }
}
