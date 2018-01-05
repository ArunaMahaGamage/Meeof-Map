package com.meeof.meeof.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.AddUpdateBiginingActivity;
import com.meeof.meeof.activity.UpdateFilterActivity;
import com.meeof.meeof.activity.SettingsActivity;
import com.meeof.meeof.adapter.UpdatesRecyclerAdapter;
import com.meeof.meeof.custom.CustomFontTextView;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.helper.VpLifeCycleManager;
import com.meeof.meeof.model.DeleteUpdateWebJobResponse;
import com.meeof.meeof.model.HttpResponseLikeUnlike;
import com.meeof.meeof.model.updates_all_dto.Array_updates;
import com.meeof.meeof.model.updates_all_dto.UpdatesAllResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.Utility;
import com.meeof.meeof.webjob.GetEventsWebJob;
import com.meeof.meeof.webjob.GetUpdatesWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class UpdatesFragment extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = UpdatesFragment.class.getSimpleName();

    private TextView viewNameTv, viewActionTvBtn;
    private LinearLayout manageFriendsLlBtn;
    public UpdatesRecyclerAdapter updatesRecyclerAdapter;
    private RecyclerView updateListRv;


    private TextView newEventTv;
    private ImageView filterIv;

    private int previousPosition = 0;
    private int currentPosition = 0;






    private TextView currentLocationTv;



    private SharedPreferences sharedPreferences;
    private RelativeLayout filterOnRlTv;
    private Location mLastLocation;
    private LinearLayout newEventLlBtn;
    private LinearLayout searchViewLl;
    private EditText searchEt;
    private ImageView clear;
    private LinearLayout searchLlIv;
    private TextView currentLocationLabelTV;
    private SwipeRefreshLayout updatesSRL;


    private String accessToken;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_updates, container, false);


        sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);

        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateUpdatesList,
                new IntentFilter(Constant.UPDATE_UPDATE_TAB));

        initViews(view);
        return view;
    }

    private BroadcastReceiver updateUpdatesList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));
        }
    };

    private void initViews(View view) {


//        float lastKnownLat = sharedPreferences.getFloat(Constant.latitudeService, 0);
//        float lastKnownLongi = sharedPreferences.getFloat(Constant.longitudeService, 0);




        searchViewLl = (LinearLayout) view.findViewById(R.id.searchViewLl);
        searchEt = (EditText) view.findViewById(R.id.searchEt);
        clear = (ImageView) view.findViewById(R.id.clear);
        searchLlIv = (LinearLayout) view.findViewById(R.id.searchLlIv);

        currentLocationTv = (CustomFontTextView) view.findViewById(R.id.currentLocationTv);
        filterOnRlTv = (RelativeLayout) view.findViewById(R.id.filterOnRlTv);

        //badgeTabLayout = (BadgeTabLayout) view.findViewById(R.id.tabs);

        newEventLlBtn = (LinearLayout) view.findViewById(R.id.newEventLlBtn);

        //badgeTabLayout.setupWithViewPager(viewPager);




        filterIv = (ImageView) view.findViewById(R.id.filterIv);

        currentLocationLabelTV = (TextView) view.findViewById(R.id.currentLocationLabelTV);

        currentLocationTv.setText("Loading ...");

        updatesSRL=(SwipeRefreshLayout)view.findViewById(R.id.updatesSRL);
        updatesSRL.setOnRefreshListener(this);

        newEventLlBtn.setOnClickListener(this);

        //searchEt.addTextChangedListener(this);
        clear.setOnClickListener(this);
        searchLlIv.setOnClickListener(this);

        filterIv.setOnClickListener(this);



        if (!sharedPreferences.getString(Constant.EVENT_FILTER_OBJ, "").equals("")) {
            filterOnRlTv.setVisibility(View.VISIBLE);
        } else {
            filterOnRlTv.setVisibility(View.GONE);
        }




//        viewNameTv = (TextView) view.findViewById(R.id.viewNameTv);
//        viewActionTvBtn = (TextView) view.findViewById(R.id.viewActionTvBtn);
//        manageFriendsLlBtn = (LinearLayout) view.findViewById(R.id.manageFriendsLlBtn);
//
//        viewActionTvBtn.setText("Settings");
//        viewActionTvBtn.setOnClickListener(this);

        updateListRv=(RecyclerView)view.findViewById(R.id.updatesRv);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            EventBus.getDefault().register(this);
        } catch (Throwable t) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateUpdatesList);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.viewActionTvBtn:
//                sendToSettingsActivity();
//                Helper.clickGaurd(viewActionTvBtn);
//                break;
            case R.id.newEventLlBtn:
                sendToCreateEventActivity();
                //Helper.clickGaurd(newEventTv);
                break;
            case R.id.filterIv:
                sendToFilterActivity();
                Helper.clickGaurd(filterIv);
                break;
            case R.id.clear:
                searchViewLl.setVisibility(View.GONE);
//                meeofSearchLl.setVisibility(View.GONE);
//                eventFragmentLl.setVisibility(View.VISIBLE);
                break;
            case R.id.searchLlIv:
//                if(searchEt.getText().toString().length()>0){
//                    meeofSearchLl.setVisibility(View.VISIBLE);
//                    eventFragmentLl.setVisibility(View.GONE);
//                }
                searchViewLl.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void sendToCreateEventActivity() {
        Intent intent = new Intent(getActivity(), AddUpdateBiginingActivity.class);
        startActivity(intent);
    }
    private void sendToFilterActivity() {
        Intent intent = new Intent(getActivity(), UpdateFilterActivity.class);
        startActivity(intent);
    }

    private void sendToSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void OnEventLikeUnlikeComplete(HttpResponseLikeUnlike httpResponseLikeUnlike) {
        Log.i(TAG, "OnEventLikeUnlikeCompleted ");

        Log.d(TAG, "onPostLikeEventAsFriendWebJobCompleted ");
        if (httpResponseLikeUnlike != null) {
            {
                if (httpResponseLikeUnlike.getStatus().equals(Constant.SUCCESS)) {
                    for (int i = 0; i < updatesRecyclerAdapter.updatesList.size(); i++) {
                        Array_updates updateItem = updatesRecyclerAdapter.updatesList.get(i);
                        if (updateItem.getUpdateid() == Integer.parseInt(httpResponseLikeUnlike.getZoneid())) {

                            Log.d(TAG, "EVENT ID MATCH " + updateItem.getUpdateid() + " " + httpResponseLikeUnlike.getZoneid());
                            //updatesRecyclerAdapter.updatesList.get(i).setLike(httpResponseLikeUnlike.is_like());

                            //updatesRecyclerAdapter.updatesList.get(i).setCountLikes(httpResponseLikeUnlike.getLike_count());

                            UpdatesRecyclerAdapter.MyViewHolderUpdates myViewHolderUpdates= (UpdatesRecyclerAdapter.MyViewHolderUpdates) updateListRv.findViewHolderForAdapterPosition(i);
                            myViewHolderUpdates.likeTv.setText(httpResponseLikeUnlike.getLike_count()+"");
                            if(httpResponseLikeUnlike.is_like()){
                                myViewHolderUpdates.likedBtn.setVisibility(View.VISIBLE);
                                myViewHolderUpdates.unLiked.setVisibility(View.GONE);
                                if(!updatesRecyclerAdapter.updateData.getWhatDoILike().contains(Integer.parseInt(httpResponseLikeUnlike.getZoneid()))){
                                    updatesRecyclerAdapter.updateData.getWhatDoILike().add(Integer.parseInt(httpResponseLikeUnlike.getZoneid()));
                                }
                            }else{
                                myViewHolderUpdates.likedBtn.setVisibility(View.GONE);
                                myViewHolderUpdates.unLiked.setVisibility(View.VISIBLE);
                                for(int a=0;a<updatesRecyclerAdapter.updateData.getWhatDoILike().size();a++){
                                    if(updatesRecyclerAdapter.updateData.getWhatDoILike().get(a)==Integer.parseInt(httpResponseLikeUnlike.getZoneid())){
                                        updatesRecyclerAdapter.updateData.getWhatDoILike().remove(a);
                                        break;
                                    }
                                }

                            }

                            //updatesRecyclerAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                } else {
                    showSnackbar(updateListRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
                }
            }
        } else {
            showSnackbar(updateListRv, getString(R.string.oop_something_went_wrong), Constant.ERROR);
        }
        stopProgressBar();


        //updatesRecyclerAdapter.updateLikeCount(getContext(),Integer.parseInt(httpResponseLikeUnlike.getZoneid()),httpResponseLikeUnlike.getLike_count(),httpResponseLikeUnlike.is_like());

        //jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));
        //jobManager.addJobInBackground(new GetUpdatesLikeWebJob(accessToken,httpLikeUnlikeResponse.getId()));
    }



    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void OnEventUpdatesComplete(UpdatesAllResponse updatesAllResponse) {

        Log.i(TAG, "OnEventUpdatesComplete response: "+updatesAllResponse.getData().getArray_updates().toString());



        if(updatesRecyclerAdapter!=null){
            updatesRecyclerAdapter.setData(getActivity(),updatesAllResponse.getData());
            updatesRecyclerAdapter.notifyDataSetChanged();
        }else{
            updatesRecyclerAdapter = new UpdatesRecyclerAdapter(getActivity(), updatesAllResponse.getData());
            updateListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
            updateListRv.setAdapter(updatesRecyclerAdapter);
            updateListRv.setItemAnimator(new DefaultItemAnimator());
            updatesRecyclerAdapter.notifyDataSetChanged();
        }

        if(updatesSRL != null){
            updatesSRL.setRefreshing(false);
        }


    }
    @Subscribe
    public void deleteUpdateWebJobResponse(DeleteUpdateWebJobResponse deleteUpdateWebJobResponse) {
        //stopProgressBar();
        Log.i(TAG, "deleteUpdateWebJobResponse" + deleteUpdateWebJobResponse);
        if (deleteUpdateWebJobResponse != null) {
            if (deleteUpdateWebJobResponse.getStatus() != null && deleteUpdateWebJobResponse.getStatus().equals(Constant.SUCCESS)) {
                Log.i(TAG, "deleteUpdateWebJobResponse" + deleteUpdateWebJobResponse.getStatus());
                showSnackbar(searchViewLl, getString(R.string.update_delete_successfully), Constant.SUCCESS);
                jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));
            } else {
                showSnackbar(searchViewLl, getString(R.string.update_delete_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(searchViewLl, getString(R.string.update_delete_failed), Constant.ERROR);
        }
    }

    @Override
    public void onRefresh() {
        jobManager.addJobInBackground(new GetUpdatesWebJob(accessToken));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        Boolean trackingUserLocation = sharedPreferences.getBoolean(Constant.CURRENT_LOCATION, false);
        Log.d(TAG,"sharedPreferencesGetBooleanConstant.CURRENT_LOCATION: "+trackingUserLocation);
        if (trackingUserLocation){
            if (Utility.checkLocationPermission(this.getContext())) {

//                currentLocationLabelTV.setText("");
//                currentLocationTv.setText("Meeof will update the content when you are moving around");

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {

                    sharedEditor.putFloat(Constant.CURRENT_LATITUDE,(float) mLastLocation.getLatitude());
                    sharedEditor.putFloat(Constant.CURRENT_LONGITUDE,(float) mLastLocation.getLongitude());
                    sharedEditor.apply();

                    List<Address> addresses = getAddressFromLatLong(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    final String address = addresses.get(0).getAddressLine(0);
                    currentLocationLabelTV.setText("Current Location : ");
                    currentLocationTv.setText(String.valueOf(address));
                }

                try {
                    saveObjectToSharedPref(sharedEditor, Helper.getEventFilterDefaultModel(mLastLocation.getLatitude(), mLastLocation.getLongitude()), Constant.FILTER_MODEL_DEFAULT_OBJ);
                }catch (Exception ex){

                }
            }

        }
        else{
            currentLocationLabelTV.setText("");
//            currentLocationTv.setText(sharedPreferences.getString(Constant.USER_ADDRESS,"Home Address Unavailable"));
            currentLocationTv.setText("Content near Home");
        }
    }

    private void saveObjectToSharedPref(SharedPreferences.Editor sharedEditor, Object object, String objectSaveName) {
        try {
            Gson gson = new Gson();
            String objectJson = gson.toJson(object);
            sharedEditor.putString(objectSaveName, objectJson);
            sharedEditor.apply();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private BroadcastReceiver locationChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "locationChanged BroadcastReceiver");

            double lat = intent.getDoubleExtra(Constant.LATITUDE, 0);
            double longi = intent.getDoubleExtra(Constant.LONGITUDE, 0);

            if(lat !=0 && longi!=0){
                sharedEditor.putFloat(Constant.CURRENT_LATITUDE,(float) lat);
                sharedEditor.putFloat(Constant.CURRENT_LONGITUDE,(float) longi);
                sharedEditor.apply();
            }

            List<Address> addresses = getAddressFromLatLong(lat, longi);

            if(addresses != null && addresses.size() > 0){
                final String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                if(sharedPreferences.getBoolean(Constant.CURRENT_LOCATION,false)){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentLocationLabelTV.setText("Current Location : ");
                            currentLocationTv.setText(address);
                        }
                    });
                }
            }
        }
    };

}
