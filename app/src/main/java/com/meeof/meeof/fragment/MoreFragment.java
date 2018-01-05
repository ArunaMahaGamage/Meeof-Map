package com.meeof.meeof.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.AllChannelNearMeActivity;
import com.meeof.meeof.activity.AllPeopleNearMeActivity;
import com.meeof.meeof.activity.AllPromotionsNearMeActivity;
import com.meeof.meeof.activity.ProfileActivity;
import com.meeof.meeof.activity.SettingsActivity;
import com.meeof.meeof.adapter.ChannelNearMeAdapter;
import com.meeof.meeof.adapter.CustomInfoViewAdapter;
import com.meeof.meeof.adapter.PeopleChannelYouMightKnowAdapter;
import com.meeof.meeof.adapter.PeopleNearMeAdapter;
import com.meeof.meeof.adapter.PromotionNearMeAdapter;
import com.meeof.meeof.adapter.UpdateEventsAdapter;
import com.meeof.meeof.model.ChannelInsideModel;
import com.meeof.meeof.model.ChannelMainModel;
import com.meeof.meeof.model.ClusterModel;
import com.meeof.meeof.model.GetNearMeEventInsideModel;
import com.meeof.meeof.model.GetNearMeEventMainModel;
import com.meeof.meeof.model.GetNearMePromotionsMainModel;
import com.meeof.meeof.model.NearMeUpdateInsideModel;
import com.meeof.meeof.model.NearMeUpdateMainModel;
import com.meeof.meeof.model.PeopleNearMeInsideModel;
import com.meeof.meeof.model.PeopleNearMeMainModel;
import com.meeof.meeof.model.PeopleYouMightKnowInsideModel;
import com.meeof.meeof.model.PeopleYouMightKnowMainModel;
import com.meeof.meeof.model.PromoNearMeInsideModel;
import com.meeof.meeof.model.PromoNearMeMainModel;
import com.meeof.meeof.model.UpcomingEventInsideModel;
import com.meeof.meeof.model.UpcomingEventModel;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.util.LockableScrollView;
import com.meeof.meeof.util.MultiTouchMapFragment;
import com.meeof.meeof.webjob.ChannelNearMeWebJob;
import com.meeof.meeof.webjob.GetPeopleYouMightKnowWebJob;
import com.meeof.meeof.webjob.GetUpcomingEventWebJob;
import com.meeof.meeof.webjob.NearMeEventsWebJob;
import com.meeof.meeof.webjob.NearMePromotionsWebJob;
import com.meeof.meeof.webjob.NearMeUpdateChannelWebJob;
import com.meeof.meeof.webjob.PeopleNearMeWebJob;
import com.meeof.meeof.webjob.PromotionNearMeWebJob;
import com.squareup.picasso.Picasso;




import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class MoreFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener{
    private static final String TAG = "MoreFragment";
    RecyclerView RecyclerUpcomingEvents;
    public JobManager jobManager;
    protected SharedPreferences sharedPreferences;
    UpdateEventsAdapter adapter;
    private String accessToken;
    ArrayList<UpcomingEventInsideModel> upcomingEventInsideModels;
    ProgressBar progressBar, ProgressMap;
    RecyclerView RecyclerChannelNearMe;
    ChannelNearMeAdapter channelNearMeAdapter;
    ArrayList<ChannelInsideModel> channelInsideModels;
    RecyclerView RecyclerPeopleNearMe;
    PeopleNearMeAdapter peopleNearMeAdapter;
    PeopleChannelYouMightKnowAdapter peopleChannelYouMightKnowAdapter;
    ArrayList<PeopleNearMeInsideModel> peopleNearMeInsideModels;
    ArrayList<PeopleYouMightKnowInsideModel> peopleYouMightKnowInsideModels;
    private GoogleMap mMap;
    private LockableScrollView mLockableScrollView;
    //private MultiTouchMapFragment mapFragment;
    double latitude, longitude;
    MultiTouchMapFragment mapView;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<GetNearMeEventInsideModel> nearMeEventInsideModels = new ArrayList<>();
    ArrayList<NearMeUpdateInsideModel> nearMeUpdateInsideModels = new ArrayList<>();
    ArrayList<PromoNearMeInsideModel> nearMePromotionsInsideModels = new ArrayList<>();
    LinearLayout RelativeUpdate, LinearBelowButtonsmap, RelativeEvents;
    TextView userNameTv, eventTimeTv, userLocationTv, TxtStatusEvent, ImageCount, TxtTags, commentNoTv, likesNoTv;
    ImageView ImgFirstImage, ImgSecondImage, ImgThirdImage;
    LinearLayout AllPhotoLinear;
    RelativeLayout mapOverView;
    Button BtnEvents, BtnUpdates, BtnOffersPromotion;
    Button BtnCloseEventMap, BtnBackToMap, BtnCloseUpdateDetail, BtnBackToMapUpdateDetail, BtnCloseEventDetail, BtnBackToMapEventDetail;
    TextView eventNameHeaderTv, eventDistanceTv, profileViewTv, eventDateTv, eventMonthTv, eventLocationTv, eventFullDateTv, eventCreaterTv, commentNoTvEvent, likesNoTvEvent, goingNumberTv;
    ImageView eventPictureIv, cameraIv, going_people1_imageView, going_people2_imageView, going_people3_imageView;
    LinearLayout LinearSetting, LinearSearch, LinearHostEvent;
    ImageView ImgUserProfile;
    Button BtnLoadMoreChannel, BtnLoadMorePeopleNearMe;
    RecyclerView RecyclerChannelsMightKnow;
    ViewGroup container;
    RelativeLayout RelativeUpcomingEvent, RelativeChannelNear, RelativePeopleNearMe, RelativePromotionsNearYou;
    TextView LocationTextView;
    RelativeLayout RelativePeopleChannelsMightKnow;
    RecyclerView RecyclerPromotionsNearMe;
    PromotionNearMeAdapter promotionNearMeAdapter;
    ArrayList<PromoNearMeInsideModel> promoNearMeInsideModels;
    Button BtnLoadMorePromotion;
    ProfileResponse profileResponse;
    int userId;
    boolean isBtnEventsSelected, isBtnUpdatesSelected, isBtnPromotionsSelected;

    private ClusterManager<ClusterModel> mClusterManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.more_fragment, container, false);

        this.container = container;
        jobManager = MeeofApplication.getInstance().getJobManager();
        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        peopleNearMeInsideModels = new ArrayList<>();
        peopleYouMightKnowInsideModels = new ArrayList<>();
        promoNearMeInsideModels = new ArrayList<>();
        InIt(view);
        RecyclerPeopleNearMe.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        peopleNearMeAdapter = new PeopleNearMeAdapter(getActivity(), peopleNearMeInsideModels);
        RecyclerPeopleNearMe.setAdapter(peopleNearMeAdapter);
        RelativePeopleChannelsMightKnow = view.findViewById(R.id.RelativePeopleChannelsMightKnow);
        RelativeUpcomingEvent = (RelativeLayout) view.findViewById(R.id.RelativeUpcomingEvent);
        LocationTextView = (TextView) view.findViewById(R.id.LocationTextView);
        RelativeChannelNear = (RelativeLayout) view.findViewById(R.id.RelativeChannelNear);
        RelativePeopleNearMe = (RelativeLayout) view.findViewById(R.id.RelativePeopleNearMe);
        RelativePromotionsNearYou = (RelativeLayout) view.findViewById(R.id.RelativePromotionsNearYou);
        RecyclerPromotionsNearMe = (RecyclerView) view.findViewById(R.id.RecyclerPromotionsNearMe);
        BtnLoadMorePromotion = (Button) view.findViewById(R.id.BtnLoadMorePromotion);
        BtnLoadMorePromotion.setOnClickListener(this);
        RecyclerPromotionsNearMe.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        promotionNearMeAdapter = new PromotionNearMeAdapter(getActivity(), promoNearMeInsideModels);
        RecyclerPromotionsNearMe.setAdapter(promotionNearMeAdapter);
        upcomingEventInsideModels = new ArrayList<>();
        channelInsideModels = new ArrayList<>();
        RecyclerUpcomingEvents.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        RecyclerChannelNearMe.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        channelNearMeAdapter = new ChannelNearMeAdapter(getActivity(), channelInsideModels);
        RecyclerChannelNearMe.setAdapter(channelNearMeAdapter);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        adapter = new UpdateEventsAdapter(getActivity(), upcomingEventInsideModels);
        progressBar = view.findViewById(R.id.ProgressAutoUpdate);
        RecyclerUpcomingEvents.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);

//        mapView = view.findViewById(R.id.mapView);
//        mapView.onCreate(savedInstanceState);
//        mapView.getMapAsync(this);

        mapView = (MultiTouchMapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_fragment);
        mapView.getMapAsync(this);
        mLockableScrollView = (LockableScrollView) view.findViewById(R.id.scrollView);
        mLockableScrollView.setSmoothScrollingEnabled(true);


        profileResponse = retriveSavedProfileObject(sharedPreferences);
        userId = profileResponse.getData().getUser_id();

        mapOverView = view.findViewById(R.id.mapOverView);
        //mapOverView.setVisibility(View.GONE);

        isBtnEventsSelected = true;
        isBtnUpdatesSelected = true;
        isBtnPromotionsSelected = true;

        return view;
    }

    private ProfileResponse retriveSavedProfileObject(SharedPreferences sharedPreferences) {
        String profileObjectJsonStr = sharedPreferences.getString(Constant.USER_PROFILE_OBJECT, "");
        Log.d(TAG, "User Profile :=> " + profileObjectJsonStr);
        if (!profileObjectJsonStr.equals("")) {
            Gson gson = new Gson();
            ProfileResponse profileResponse = gson.fromJson(profileObjectJsonStr, ProfileResponse.class);
            return profileResponse;
        }
        return null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetUpcomingEventCompleted(UpcomingEventModel upcomingEventModel) {
        progressBar.setVisibility(View.GONE);
        if (upcomingEventModel != null) {
            if (upcomingEventModel.getStatus() != null && upcomingEventModel.getStatus().equals(Constant.SUCCESS)) {


                for (int i = 0; i < upcomingEventModel.getData().size(); i++) {
                    if (i < 2) {
                        upcomingEventInsideModels.add(upcomingEventModel.getData().get(i));
                    }
                }
                adapter.notifyDataSetChanged();
                RecyclerUpcomingEvents.invalidate();

                if (upcomingEventInsideModels.size() == 0) {
                    RelativeUpcomingEvent.setVisibility(View.GONE);
                } else {
                    RelativeUpcomingEvent.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new ChannelNearMeWebJob(latitude, longitude, accessToken));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetChannelNearMeCompleted(ChannelMainModel channelMainModel) {
        progressBar.setVisibility(View.GONE);
        if (channelInsideModels != null && channelInsideModels.size() > 0)
            channelInsideModels.clear();
        if (channelMainModel != null) {
            if (channelMainModel.getStatus() != null && channelMainModel.getStatus().equals(Constant.SUCCESS)) {
                if (channelMainModel.getData().size() > 2) {
                    BtnLoadMoreChannel.setVisibility(View.VISIBLE);
                } else {
                    BtnLoadMoreChannel.setVisibility(View.GONE);
                }
                for (int i = 0; i < channelMainModel.getData().size(); i++) {
                    if (i < 2) {
                        channelInsideModels.add(channelMainModel.getData().get(i));
                    }
                }
                channelNearMeAdapter.notifyDataSetChanged();
                RecyclerChannelNearMe.invalidate();

                if (channelInsideModels.size() == 0) {
                    RelativeChannelNear.setVisibility(View.GONE);
                } else {
                    RelativeChannelNear.setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
        }
        progressBar.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new PeopleNearMeWebJob(latitude, longitude, accessToken));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPeopleNearMeCompleted(PeopleNearMeMainModel peopleNearMeMainModel) {
        if (peopleNearMeInsideModels != null && peopleNearMeInsideModels.size() > 0)
            peopleNearMeInsideModels.clear();
        progressBar.setVisibility(View.GONE);
        if (peopleNearMeMainModel != null) {
            if (peopleNearMeMainModel.getStatus() != null && peopleNearMeMainModel.getStatus().equals(Constant.SUCCESS)) {
                if (peopleNearMeMainModel.getData().size() > 2) {
                    BtnLoadMorePeopleNearMe.setVisibility(View.VISIBLE);
                } else {
                    BtnLoadMorePeopleNearMe.setVisibility(View.GONE);
                }
                for (int i = 0; i < peopleNearMeMainModel.getData().size(); i++) {
                    if (i < 2) {
                        peopleNearMeInsideModels.add(peopleNearMeMainModel.getData().get(i));
                    }
                }
                peopleNearMeAdapter.notifyDataSetChanged();
                RecyclerPeopleNearMe.invalidate();
                if (peopleNearMeInsideModels.size() == 0) {
                    RelativePeopleNearMe.setVisibility(View.GONE);
                } else {
                    RelativePeopleNearMe.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
                if (peopleNearMeInsideModels.size() == 0) {
                    RelativePeopleNearMe.setVisibility(View.GONE);
                } else {
                    RelativePeopleNearMe.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            if (peopleNearMeInsideModels.size() == 0) {
                RelativePeopleNearMe.setVisibility(View.GONE);
            } else {
                RelativePeopleNearMe.setVisibility(View.VISIBLE);
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new GetPeopleYouMightKnowWebJob(accessToken));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPeopleYouMightKnowCompleted(PeopleYouMightKnowMainModel peopleYouMightKnowMainModel) {
        if (peopleYouMightKnowInsideModels != null && peopleYouMightKnowInsideModels.size() > 0)
            peopleYouMightKnowInsideModels.clear();
        progressBar.setVisibility(View.GONE);
        if (peopleYouMightKnowMainModel != null) {
            if (peopleYouMightKnowMainModel.getStatus() != null && peopleYouMightKnowMainModel.getStatus().equals(Constant.SUCCESS)) {
                peopleYouMightKnowInsideModels.addAll(peopleYouMightKnowMainModel.getData());
                peopleChannelYouMightKnowAdapter.notifyDataSetChanged();
                RecyclerChannelsMightKnow.invalidate();
                if (peopleYouMightKnowInsideModels.size() == 0) {
                    RelativePeopleChannelsMightKnow.setVisibility(View.GONE);
                } else {
                    RelativePeopleChannelsMightKnow.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
                if (peopleYouMightKnowInsideModels.size() == 0) {
                    RelativePeopleChannelsMightKnow.setVisibility(View.GONE);
                } else {
                    RelativePeopleChannelsMightKnow.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Toast.makeText(getActivity(), getString(R.string.unable_to_retrive_friends), Toast.LENGTH_SHORT).show();
            if (peopleYouMightKnowInsideModels.size() == 0) {
                RelativePeopleChannelsMightKnow.setVisibility(View.GONE);
            } else {
                RelativePeopleChannelsMightKnow.setVisibility(View.VISIBLE);
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        jobManager.addJobInBackground(new PromotionNearMeWebJob(latitude, longitude, accessToken));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetPromotionNearMeCompleted(PromoNearMeMainModel promoNearMeMainModel) {
        progressBar.setVisibility(View.GONE);
        if (promoNearMeMainModel != null) {
            if (promoNearMeMainModel.isSuccess()) {
                if (promoNearMeMainModel.getData().size() > 0) {
                    for (int i = 0; i < promoNearMeMainModel.getData().size(); i++) {
                        if (i < 2) {
                            promoNearMeInsideModels.add(promoNearMeMainModel.getData().get(i));
                        }
                    }
                    promotionNearMeAdapter.notifyDataSetChanged();
                    RecyclerPromotionsNearMe.invalidate();

                    if (promoNearMeInsideModels.size() == 0) {
                        RelativePromotionsNearYou.setVisibility(View.GONE);
                    } else {
                        RelativePromotionsNearYou.setVisibility(View.VISIBLE);
                    }

                    if (promoNearMeInsideModels.size() < 3) {
                        BtnLoadMorePromotion.setVisibility(View.GONE);
                    } else {
                        BtnLoadMorePromotion.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (promoNearMeInsideModels.size() == 0) {
                        RelativePromotionsNearYou.setVisibility(View.GONE);
                    } else {
                        RelativePromotionsNearYou.setVisibility(View.VISIBLE);
                    }

                    if (promoNearMeInsideModels.size() < 3) {
                        BtnLoadMorePromotion.setVisibility(View.GONE);
                    } else {
                        BtnLoadMorePromotion.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                if (promoNearMeInsideModels.size() == 0) {
                    RelativePromotionsNearYou.setVisibility(View.GONE);
                } else {
                    RelativePromotionsNearYou.setVisibility(View.VISIBLE);
                }

                if (promoNearMeInsideModels.size() < 3) {
                    BtnLoadMorePromotion.setVisibility(View.GONE);
                } else {
                    BtnLoadMorePromotion.setVisibility(View.VISIBLE);
                }
            }
        } else {
            if (promoNearMeInsideModels.size() == 0) {
                RelativePromotionsNearYou.setVisibility(View.GONE);
            } else {
                RelativePromotionsNearYou.setVisibility(View.VISIBLE);
            }

            if (promoNearMeInsideModels.size() < 3) {
                BtnLoadMorePromotion.setVisibility(View.GONE);
            } else {
                BtnLoadMorePromotion.setVisibility(View.VISIBLE);
            }
        }
        new GetAddress().execute();
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

    LocationManager locationManager;
    String mprovider;

    public Location GetCurrentLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);

        if (mprovider != null && !mprovider.equals("")) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location location = locationManager.getLastKnownLocation(mprovider);
            locationManager.requestLocationUpdates(mprovider, 15000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

            if (location != null)
                return location;
            else
                return null;
        }
        return null;
    }

//    void ZoomToMyLocation(double latitude, double longitude) {
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13));
//
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude, longitude))      // Sets the center of the map to location user
//                .zoom(15)                   // Sets the zoom
//                .bearing(90)                // Sets the orientation of the camera to east
//                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
//                .build();                   // Creates a CameraPosition from the builder
//        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    void AddMarkerEvent(LatLng latLng, String position) {
        Marker NewAddedMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapiconsevent_resized))
                .draggable(false)
                .title(position));
        markers.add(NewAddedMarker);
    }

    void AddMarkerUpdate(LatLng latLng, String position) {
        Marker NewAddedMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapiconsupdate_resized))
                .draggable(false)
                .title(position));
        markers.add(NewAddedMarker);
    }

    void AddMarkerPromotions(LatLng latLng, String position) {
        Marker NewAddedMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapiconspromo_resized))
                .draggable(false)
                .title(position));
        markers.add(NewAddedMarker);
    }


    private void moveToCurrentLocation(CameraUpdate cameraUpdate) {
        mMap.moveCamera(cameraUpdate);
        mMap.animateCamera(cameraUpdate);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap = googleMap;



        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(18.0f);

        mClusterManager = new ClusterManager<>(getContext(), mMap);
        final CustomClusterRenderer renderer = new CustomClusterRenderer(getContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(mClusterManager);


        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Location location = GetCurrentLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

        } else {
            latitude = 1.289299;
            longitude = 103.863137;
        }

        jobManager.addJobInBackground(new NearMeEventsWebJob(latitude, longitude, accessToken));
        jobManager.addJobInBackground(new NearMeUpdateChannelWebJob(latitude, longitude, accessToken));
        jobManager.addJobInBackground(new NearMePromotionsWebJob(latitude, longitude, accessToken));


        jobManager.addJobInBackground(new GetUpcomingEventWebJob(accessToken));







//        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            @Override
//            public View getInfoWindow(Marker marker) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(final Marker marker) {
//                View v = getActivity().getLayoutInflater().inflate(R.layout.map_bubble, container, false);
//                TextView TxtUserNamePopUp = v.findViewById(R.id.TxtUserNamePopUp);
//                TextView TxtAddressMapBubble = v.findViewById(R.id.TxtAddressMapBubble);
//                TextView TxtDescription = v.findViewById(R.id.TxtDescription);
//                ImageView imageUserIv = v.findViewById(R.id.ImgUserImageButton);
//
//
//                v.setBackgroundColor(Color.TRANSPARENT);
////                String MarkerTitle = marker.getTitle();
////                String[] titles = MarkerTitle.split("_");
////                String positionString = titles[1];
////                int position = Integer.parseInt(positionString);
////                if (titles[0].equalsIgnoreCase("updates")) {
//                    TxtUserNamePopUp.setText(nearMeUpdateInsideModels.get(0).getFirst_name());
//                    TxtAddressMapBubble.setText(nearMeUpdateInsideModels.get(0).getLocation());
//                    TxtDescription.setText(nearMeUpdateInsideModels.get(0).getTitle());
//                    String imgeUrl1 = nearMeUpdateInsideModels.get(0).getProfilephoto() == null || nearMeUpdateInsideModels.get(0).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                            "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeUpdateInsideModels.get(0).getProfilephoto();
//
//                    Log.d(TAG, "IMAGE URL update: " + imgeUrl1);
//
//                    Picasso.with(getActivity())
//                            .load(imgeUrl1)
//                            .placeholder(R.drawable.ico_profile_edit_avatar)
//                            .error(ContextCompat.getDrawable(getContext(), R.drawable.ico_profile_edit_avatar))
//                            .resize(100, 100)
//                            .centerCrop()
//                            .into(imageUserIv);
//
////                } else {
////                    TxtUserNamePopUp.setText(nearMeEventInsideModels.get(position).getFirst_name());
////                    TxtAddressMapBubble.setText(nearMeEventInsideModels.get(position).getLocation());
////                    TxtDescription.setText(nearMeEventInsideModels.get(position).getTitle());
////
////                    String imgeUrl1 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
////                            "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getProfilephoto();
////                    Log.d(TAG, "IMAGE URL event: " + imgeUrl1);
////
////                    Picasso.with(getActivity())
////                            .load(imgeUrl1)
////                            .placeholder(R.drawable.ico_profile_edit_avatar)
////                            .error(ContextCompat.getDrawable(getContext(), R.drawable.ico_profile_edit_avatar))
////                            .resize(100, 100)
////                            .centerCrop()
////                            .into(imageUserIv);
////                }
//
//                return v;
//            }
//        });

//        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
//            @Override
//            public void onInfoWindowClick(Marker marker) {
//                Log.d(TAG, "onInfoWindowClick 1");
//
//
//                //LinearBelowButtonsmap.requestFocus();
//                mLockableScrollView.smoothScrollTo(0, mapView.getView().getBottom());
//
//
//                marker.hideInfoWindow();
//                String MarkerTitle = marker.getTitle();
//                String[] titles = MarkerTitle.split("_");
//                int position = Integer.parseInt(titles[1]);
//                if (titles[0].equalsIgnoreCase("updates")) {
//
//                    RelativeUpdate.setVisibility(View.VISIBLE);
//                    RelativeEvents.setVisibility(View.GONE);
//                    LinearBelowButtonsmap.setVisibility(View.VISIBLE);
//                    userNameTv.setText(nearMeUpdateInsideModels.get(position).getFirst_name());
//                    eventTimeTv.setText(Math.round(nearMeUpdateInsideModels.get(position).getDistance()) + "m away");
//                    userLocationTv.setText(nearMeUpdateInsideModels.get(position).getLocation());
//                    TxtStatusEvent.setText(nearMeUpdateInsideModels.get(position).getTitle());
//                    commentNoTv.setText("" + nearMeUpdateInsideModels.get(position).getComments().size() + " Comments");
//                    likesNoTv.setText("" + nearMeUpdateInsideModels.get(position).getLikes().size() + " Likes");
//                    if (nearMeUpdateInsideModels.get(position).getPhotos() == null || nearMeUpdateInsideModels.get(position).getPhotos().size() == 0) {
//                        AllPhotoLinear.setVisibility(View.GONE);
//                    } else if (nearMeUpdateInsideModels.get(position).getPhotos().size() == 1) {
//                        AllPhotoLinear.setVisibility(View.VISIBLE);
//                        ImgFirstImage.setVisibility(View.VISIBLE);
//                        ImgSecondImage.setVisibility(View.GONE);
//                        ImgThirdImage.setVisibility(View.GONE);
//                        ImageCount.setVisibility(View.GONE);
//                    } else if (nearMeUpdateInsideModels.get(position).getPhotos().size() == 2) {
//                        AllPhotoLinear.setVisibility(View.VISIBLE);
//                        ImgFirstImage.setVisibility(View.VISIBLE);
//                        ImgSecondImage.setVisibility(View.VISIBLE);
//                        ImgThirdImage.setVisibility(View.GONE);
//                        ImageCount.setVisibility(View.GONE);
//                    } else if (nearMeUpdateInsideModels.get(position).getPhotos().size() == 3) {
//                        AllPhotoLinear.setVisibility(View.VISIBLE);
//                        ImgFirstImage.setVisibility(View.VISIBLE);
//                        ImgSecondImage.setVisibility(View.VISIBLE);
//                        ImgThirdImage.setVisibility(View.VISIBLE);
//                        ImageCount.setVisibility(View.GONE);
//                    } else if (nearMeUpdateInsideModels.get(position).getPhotos().size() > 3) {
//                        AllPhotoLinear.setVisibility(View.VISIBLE);
//                        ImgFirstImage.setVisibility(View.VISIBLE);
//                        ImgSecondImage.setVisibility(View.VISIBLE);
//                        ImgThirdImage.setVisibility(View.VISIBLE);
//                        ImageCount.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    RelativeUpdate.setVisibility(View.GONE);
//                    LinearBelowButtonsmap.setVisibility(View.VISIBLE);
//                    RelativeEvents.setVisibility(View.VISIBLE);
//                    eventNameHeaderTv.setText(nearMeEventInsideModels.get(position).getTitle() != null ? nearMeEventInsideModels.get(position).getTitle() : "");
//                    eventCreaterTv.setText(nearMeEventInsideModels.get(position).getFirst_name() == null || nearMeEventInsideModels.get(position).getFirst_name().trim().length() <= 0 ? " " : nearMeEventInsideModels.get(position).getFirst_name().toString());
//                    eventLocationTv.setText((nearMeEventInsideModels.get(position).getLocation() == null || nearMeEventInsideModels.get(position).getLocation().trim().length() <= 0 ? " " : nearMeEventInsideModels.get(position).getLocation().toString()));
//                    commentNoTv.setText("" + nearMeEventInsideModels.get(position).getComments().size() + " Comments");
//                    likesNoTv.setText("" + nearMeEventInsideModels.get(position).getLikes().size() + " Likes");
//                    configureEventDate(nearMeEventInsideModels.get(position).getStart_date());
//
//                    Double d = nearMeEventInsideModels.get(position).getDistance();
//                    int distance = d.intValue();
//                    Log.d("", "distace " + distance);
//                    eventDistanceTv.setText("( " + distance + " KM away" + " )");
//
//                    int profileCount = nearMeEventInsideModels.get(position).getAttendeeList().size();    //TODO JANITHA
//
//                    String imgeUrl1 = "";
//                    String imgeUrl2 = "";
//                    String imgeUrl3 = "";
//                    if (profileCount == 0) {
//                        going_people1_imageView.setVisibility(View.GONE);
//                        going_people2_imageView.setVisibility(View.GONE);
//                        going_people3_imageView.setVisibility(View.GONE);
//                    } else if (profileCount == 1) {
//                        going_people1_imageView.setVisibility(View.VISIBLE);
//                        going_people2_imageView.setVisibility(View.GONE);
//                        going_people3_imageView.setVisibility(View.GONE);
//                        imgeUrl1 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getAttendeeList().get(0).getProfilephoto();
//                        Picasso.with(getActivity())
//                                .load(imgeUrl1)
//                                .placeholder(R.drawable.ico_profile_edit_avatar)
//                                .error(ContextCompat.getDrawable(getActivity(), R.drawable.ico_profile_edit_avatar))
//                                .into(going_people1_imageView);
//                    } else if (profileCount == 2) {
//                        going_people1_imageView.setVisibility(View.VISIBLE);
//                        going_people2_imageView.setVisibility(View.VISIBLE);
//                        going_people3_imageView.setVisibility(View.GONE);
//                        imgeUrl1 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getAttendeeList().get(0).getProfilephoto();
//                        Picasso.with(getActivity())
//                                .load(imgeUrl1)
//                                .placeholder(R.drawable.ico_profile_edit_avatar)
//                                .error(ContextCompat.getDrawable(getActivity(), R.drawable.ico_profile_edit_avatar))
//                                .into(going_people1_imageView);
//                        imgeUrl2 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getAttendeeList().get(1).getProfilephoto();
//                        Picasso.with(getActivity())
//                                .load(imgeUrl2)
//                                .placeholder(R.drawable.ico_profile_edit_avatar)
//                                .error(ContextCompat.getDrawable(getActivity(), R.drawable.ico_profile_edit_avatar))
//                                .into(going_people2_imageView);
//
//                    } else if (profileCount >= 3) {
//                        going_people1_imageView.setVisibility(View.VISIBLE);
//                        going_people2_imageView.setVisibility(View.VISIBLE);
//                        going_people3_imageView.setVisibility(View.VISIBLE);
//                        imgeUrl1 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getAttendeeList().get(0).getProfilephoto();
//                        Picasso.with(getActivity().getApplicationContext())
//                                .load(imgeUrl1)
//                                .placeholder(R.drawable.ico_profile_edit_avatar)
//                                .error(ContextCompat.getDrawable(getActivity(), R.drawable.ico_profile_edit_avatar))
//                                .into(going_people1_imageView);
//                        imgeUrl2 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getAttendeeList().get(1).getProfilephoto();
//                        Picasso.with(getActivity())
//                                .load(imgeUrl2)
//                                .placeholder(R.drawable.ico_profile_edit_avatar)
//                                .error(ContextCompat.getDrawable(getActivity(), R.drawable.ico_profile_edit_avatar))
//                                .into(going_people2_imageView);
//                        imgeUrl3 = nearMeEventInsideModels.get(position).getProfilephoto() == null || nearMeEventInsideModels.get(position).getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
//                                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + nearMeEventInsideModels.get(position).getAttendeeList().get(2).getProfilephoto();
//                        Picasso.with(getActivity())
//                                .load(imgeUrl3)
//                                .placeholder(R.drawable.ico_profile_edit_avatar)
//                                .error(ContextCompat.getDrawable(getActivity(), R.drawable.ico_profile_edit_avatar))
//                                .into(going_people3_imageView);
//
//                    }
//
//                    if (nearMeEventInsideModels.get(position).getAttendeeList().size() > 3) {
//                        LinearMoreGoing.setVisibility(View.VISIBLE);
//                        int size = nearMeEventInsideModels.get(position).getAttendeeList().size() - 3;
//                        goingNumberTv.setText("" + size);
//                    } else {
//                        LinearMoreGoing.setVisibility(View.GONE);
//                    }
//
//                }
//            }
//        });


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "setOnMarkerClickListener");

                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());

                return false;
            }
        });

        googleMap.getUiSettings().setAllGesturesEnabled(false);
        mapView.mTouchView.setGoogleMapAndScroll(googleMap, mLockableScrollView);


//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//        LatLngBounds bounds = builder.build();



        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNearMeEventsCompleted(GetNearMeEventMainModel getEventsByUserModel) {
        Log.d(TAG,"onNearMeEventsCompleted 1");
        nearMeEventInsideModels.clear();

        //mClusterManager.clearItems();


        progressBar.setVisibility(View.GONE);
        if (getEventsByUserModel != null) {
            if (getEventsByUserModel.getStatus() != null && getEventsByUserModel.getStatus().equals(Constant.SUCCESS)) {
                for (int i = 0; i < getEventsByUserModel.getEvents().size(); i++) {
                    nearMeEventInsideModels.add(getEventsByUserModel.getEvents().get(i));
                    mClusterManager.addItem(new ClusterModel(1,getEventsByUserModel.getEvents().get(i)));

                    //AddMarkerEvent(new LatLng(getEventsByUserModel.getEvents().get(i).getLatitude(), getEventsByUserModel.getEvents().get(i).getLongitude()), "events_" + i);
                }

                //no markers since no AddMarkerEvent, so exception
                //fix this
                try{
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    moveToCurrentLocation(cu);
                }catch (Exception e){
                    Log.d(TAG,"onNearMeEventsCompleted Exception:"+e.getClass().getCanonicalName());
                }



            }

//            mClusterManager = new ClusterManager<>(getContext(), mMap);
//            mMap.setOnCameraIdleListener(mClusterManager);
//            List<GetNearMeEventInsideModel> items =nearMeEventInsideModels;
//            mClusterManager.addItems(items);

//            mClusterManager = new ClusterManager<GetNearMeEventInsideModel>(getContext(), mMap);
//            mClusterManager.setRenderer(new MyClusterRenderer());
//            mMap.setOnCameraIdleListener(mClusterManager);
//            mMap.setOnMarkerClickListener(mClusterManager);
//            mMap.setOnInfoWindowClickListener(mClusterManager);
//            mClusterManager.setOnClusterClickListener(this);
//            mClusterManager.setOnClusterInfoWindowClickListener(this);
//            mClusterManager.setOnClusterItemClickListener(this);
//            mClusterManager.setOnClusterItemInfoWindowClickListener(this);
//
//            mClusterManager.addItems(nearMeEventInsideModels);
//            mClusterManager.cluster();





            mClusterManager.cluster();


        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNearMePromotionsCompleted(GetNearMePromotionsMainModel getNearMePromotionsMainModel) {

        Log.d(TAG, "onNearMePromotionsCompleted 1");
        progressBar.setVisibility(View.GONE);
        if (getNearMePromotionsMainModel != null) {
            if (getNearMePromotionsMainModel.isSuccess()) {
                for (int i = 0; i < getNearMePromotionsMainModel.getData().size(); i++) {
                    Log.d(TAG, "onNearMePromotionsCompleted 2");
                    nearMePromotionsInsideModels.add(getNearMePromotionsMainModel.getData().get(i));

                    mClusterManager.addItem(new ClusterModel(2,getNearMePromotionsMainModel.getData().get(i)));

                    //AddMarkerPromotions(new LatLng(getNearMePromotionsMainModel.getData().get(i).getLatitude(), getNearMePromotionsMainModel.getData().get(i).getLongitude()), "promotions_" + i);

                }

                try {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }

                    LatLngBounds bounds = builder.build();
                    int padding = 10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    moveToCurrentLocation(cu);
                }catch (Exception e){
                    Log.d(TAG,"onNearMeEventsCompleted Exception:"+e.getClass().getCanonicalName());
                }

            }
        }
        mClusterManager.cluster();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNearMeUpdatesCompleted(NearMeUpdateMainModel nearMeUpdateMainModel) {
        Log.d(TAG,"onNearMeUpdatesCompleted 1");
        progressBar.setVisibility(View.GONE);
        if (nearMeUpdateMainModel != null) {
            Log.d(TAG,"onNearMeUpdatesCompleted 2");
            if (nearMeUpdateMainModel.getStatus() != null && nearMeUpdateMainModel.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG,"onNearMeUpdatesCompleted 3 size"+nearMeUpdateMainModel.getUpdates().size() );
                for (int i = 0; i < nearMeUpdateMainModel.getUpdates().size(); i++) {
                    Log.d(TAG,"onNearMeUpdatesCompleted 4 item: "+nearMeUpdateMainModel.getUpdates().get(i).toString());
                    nearMeUpdateInsideModels.add(nearMeUpdateMainModel.getUpdates().get(i));

                    mClusterManager.addItem(new ClusterModel(3,nearMeUpdateMainModel.getUpdates().get(i)));

                    //AddMarkerUpdate(new LatLng(nearMeUpdateMainModel.getUpdates().get(i).getLatitude(), nearMeUpdateMainModel.getUpdates().get(i).getLongitude()), "updates_" + i);
                }

                try {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        Log.d(TAG,"onNearMeUpdatesCompleted 5");
                        builder.include(marker.getPosition());
                    }
                    LatLngBounds bounds = builder.build();
                    int padding = 10; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    moveToCurrentLocation(cu);
                }catch (Exception e){
                    Log.d(TAG,"onNearMeEventsCompleted Exception:"+e.getClass().getCanonicalName());
                }
            }
        }

        mClusterManager.cluster();

    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "Btn Color: " + BtnEvents.getCurrentTextColor());
        switch (view.getId()) {
            case R.id.BtnEvents:
                if (mMap != null) {
                    mMap.clear();
                }
                if (markers != null && markers.size() > 0)
                    markers.clear();

                if (isBtnEventsSelected) {
                    isBtnEventsSelected = false;
                    BtnEvents.setTextColor(Color.BLACK);
                    BtnEvents.setBackgroundColor(Color.parseColor("#D2D1D2"));
                } else {
                    isBtnEventsSelected = true;
                    BtnEvents.setTextColor(Color.WHITE);
                    BtnEvents.setBackgroundColor(Color.parseColor("#133047"));
                }
                break;
            case R.id.BtnOffersPromotion:
                if (mMap != null) {
                    mMap.clear();
                }
                if (markers != null && markers.size() > 0)
                    markers.clear();

                if (isBtnPromotionsSelected) {
                    isBtnPromotionsSelected = false;
                    BtnOffersPromotion.setTextColor(Color.BLACK);
                    BtnOffersPromotion.setBackgroundColor(Color.parseColor("#D2D1D2"));
                } else {
                    isBtnPromotionsSelected = true;
                    BtnOffersPromotion.setTextColor(Color.WHITE);
                    BtnOffersPromotion.setBackgroundColor(Color.parseColor("#133047"));
                }

                break;
            case R.id.BtnUpdates:
                if (mMap != null) {
                    mMap.clear();
                }
                if (markers != null && markers.size() > 0)
                    markers.clear();

                if (isBtnUpdatesSelected) {
                    isBtnUpdatesSelected = false;
                    BtnUpdates.setTextColor(Color.BLACK);
                    BtnUpdates.setBackgroundColor(Color.parseColor("#D2D1D2"));
                } else {
                    isBtnUpdatesSelected = true;
                    BtnUpdates.setTextColor(Color.WHITE);
                    BtnUpdates.setBackgroundColor(Color.parseColor("#133047"));
                }
                break;
            case R.id.BtnCloseEventMap:
                RelativeUpdate.setVisibility(View.GONE);
                LinearBelowButtonsmap.setVisibility(View.GONE);
                RelativeEvents.setVisibility(View.GONE);
                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());
                break;
            case R.id.BtnBackToMap:
                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());
                break;
            case R.id.BtnCloseEventDetail:
                RelativeUpdate.setVisibility(View.GONE);
                LinearBelowButtonsmap.setVisibility(View.GONE);
                RelativeEvents.setVisibility(View.GONE);
                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());
                break;
            case R.id.BtnBackToMapEventDetail:
                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());
                break;
            case R.id.BtnCloseUpdateDetail:
                RelativeUpdate.setVisibility(View.GONE);
                LinearBelowButtonsmap.setVisibility(View.GONE);
                RelativeEvents.setVisibility(View.GONE);
                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());
                break;
            case R.id.BtnBackToMapUpdateDetail:
                mLockableScrollView.smoothScrollTo(0, mLockableScrollView.getTop());
                break;
            case R.id.BtnLoadMoreChannel:
                Intent intent = new Intent(getActivity(), AllChannelNearMeActivity.class);
                intent.putExtra("Latitude", latitude);
                intent.putExtra("Longitude", longitude);
                intent.putExtra("accesstoken", accessToken);
                startActivity(intent);
                break;
            case R.id.BtnLoadMorePeopleNearMe:
                Intent intent1 = new Intent(getActivity(), AllPeopleNearMeActivity.class);
                intent1.putExtra("Latitude", latitude);
                intent1.putExtra("Longitude", longitude);
                intent1.putExtra("accesstoken", accessToken);
                startActivity(intent1);
                break;
            case R.id.BtnLoadMorePromotion:
                Intent intent2 = new Intent(getActivity(), AllPromotionsNearMeActivity.class);
                intent2.putExtra("Latitude", latitude);
                intent2.putExtra("Longitude", longitude);
                intent2.putExtra("accesstoken", accessToken);
                startActivity(intent2);
                break;
        }

        if (isBtnEventsSelected) {
            jobManager.addJobInBackground(new NearMeEventsWebJob(latitude, longitude, accessToken));
        }
        if (isBtnPromotionsSelected) {
            jobManager.addJobInBackground(new NearMePromotionsWebJob(latitude, longitude, accessToken));
        }
        if (isBtnUpdatesSelected) {
            jobManager.addJobInBackground(new NearMeUpdateChannelWebJob(latitude, longitude, accessToken));
        }

        mClusterManager.clearItems();
        //mClusterManager.cluster();
    }

    LinearLayout LinearMoreGoing;

    void InIt(View view) {
        LinearSetting = view.findViewById(R.id.LinearSetting);
        LinearSearch = view.findViewById(R.id.LinearSearch);
        BtnLoadMorePeopleNearMe = view.findViewById(R.id.BtnLoadMorePeopleNearMe);
        LinearHostEvent = view.findViewById(R.id.LinearHostEvent);
        RecyclerChannelsMightKnow = view.findViewById(R.id.RecyclerChannelsMightKnow);
        RecyclerChannelsMightKnow.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        peopleChannelYouMightKnowAdapter = new PeopleChannelYouMightKnowAdapter(getActivity(), peopleYouMightKnowInsideModels);
        peopleChannelYouMightKnowAdapter.setOnClick(new PeopleChannelYouMightKnowAdapter.OnItemClicked() {
            @Override
            public void onItemClick(int position, PeopleYouMightKnowInsideModel item) {
//                if (item.getStatus().equals("active")) {
//                    if (item.getFriend_status().equals("friend")) {
//                        //NOTHING
//                    } else if (item.getFriend_status().equals("pending")) {
//                        //NOTHING
//                    } else if (item.getFriend_status().equals("none")) {
//                        Log.d(TAG,"::: INSIDE ADD AS FRIEND :::");
//                        addAsFriend(item);
//                    } else {
//                        if (currentClicked.equals(Constant.FACEBOOK)) {
//                            showFacebookInviteDialog();
//                        } else {
//                            inviteFriend(item);
//                        }
//                    }
//                } else if (item.getStatus().equals("dummy")) {
//                    Log.d(TAG,"invite user clicked 3");
//
//                    if (currentClicked.equals(Constant.FACEBOOK)) {
//                        showFacebookInviteDialog();
//                    } else {
//                        inviteFriend(item);
//                    }
//                } else {
//                    Log.d(TAG,"invite user clicked 4");
//                    if (currentClicked.equals(Constant.FACEBOOK)) {
//                        Log.d(TAG,"invite user clicked 5");
//                        showFacebookInviteDialog();
//                    } else {
//                        Log.d(TAG,"invite user clicked 6");
//                        inviteFriend(item);
//                    }
//                }
            }
        });
        RecyclerChannelsMightKnow.setAdapter(peopleChannelYouMightKnowAdapter);
        ImgUserProfile = view.findViewById(R.id.ImgUserProfile);
        LoadProfileImage();
        BtnLoadMoreChannel = view.findViewById(R.id.BtnLoadMoreChannel);
        BtnBackToMapUpdateDetail = view.findViewById(R.id.BtnBackToMapUpdateDetail);
        BtnCloseUpdateDetail = view.findViewById(R.id.BtnCloseUpdateDetail);
        eventNameHeaderTv = view.findViewById(R.id.eventNameHeaderTv);
        eventDistanceTv = view.findViewById(R.id.eventDistanceTv);
        profileViewTv = view.findViewById(R.id.profileViewTv);
        eventDateTv = view.findViewById(R.id.eventDateTv);
        eventMonthTv = view.findViewById(R.id.eventMonthTv);
        eventLocationTv = view.findViewById(R.id.eventLocationTv);
        eventFullDateTv = view.findViewById(R.id.eventFullDateTv);
        eventCreaterTv = view.findViewById(R.id.eventCreaterTv);
        commentNoTvEvent = view.findViewById(R.id.commentNoTvEvent);
        likesNoTvEvent = view.findViewById(R.id.likesNoTvEvent);
        goingNumberTv = view.findViewById(R.id.goingNumberTv);
        LinearMoreGoing = view.findViewById(R.id.LinearMoreGoing);
        eventPictureIv = view.findViewById(R.id.eventPictureIv);
        cameraIv = view.findViewById(R.id.cameraIv);
        going_people1_imageView = view.findViewById(R.id.going_people1_imageView);
        going_people2_imageView = view.findViewById(R.id.going_people2_imageView);
        going_people3_imageView = view.findViewById(R.id.going_people3_imageView);
        ProgressMap = view.findViewById(R.id.ProgressMap);
        RecyclerUpcomingEvents = view.findViewById(R.id.RecyclerUpcomingEvents);
        RecyclerChannelNearMe = view.findViewById(R.id.RecyclerChannelNearMe);
        RecyclerPeopleNearMe = view.findViewById(R.id.RecyclerPeopleNearMe);
        RelativeUpdate = view.findViewById(R.id.RelativeUpdate);
        RelativeEvents = view.findViewById(R.id.RelativeEvents);
        userNameTv = view.findViewById(R.id.userNameTv);
        eventTimeTv = view.findViewById(R.id.eventTimeTv);
        userLocationTv = view.findViewById(R.id.userLocationTv);
        TxtStatusEvent = view.findViewById(R.id.TxtStatusEvent);
        ImgFirstImage = view.findViewById(R.id.ImgFirstImage);
        ImgSecondImage = view.findViewById(R.id.ImgSecondImage);
        ImgThirdImage = view.findViewById(R.id.ImgThirdImage);
        commentNoTv = view.findViewById(R.id.commentNoTv);
        LinearBelowButtonsmap = view.findViewById(R.id.LinearBelowButtonsmap);
        likesNoTv = view.findViewById(R.id.likesNoTv);
        AllPhotoLinear = view.findViewById(R.id.AllPhotoLinear);
        TxtTags = view.findViewById(R.id.TxtTags);
        ImageCount = view.findViewById(R.id.ImageCount);
        BtnEvents = view.findViewById(R.id.BtnEvents);
        BtnUpdates = view.findViewById(R.id.BtnUpdates);
        BtnOffersPromotion = view.findViewById(R.id.BtnOffersPromotion);
        BtnCloseEventMap = view.findViewById(R.id.BtnCloseEventMap);
        BtnCloseEventDetail = view.findViewById(R.id.BtnCloseEventDetail);
        BtnBackToMapEventDetail = view.findViewById(R.id.BtnBackToMapEventDetail);
        BtnBackToMap = view.findViewById(R.id.BtnBackToMap);
        BtnCloseEventMap.setOnClickListener(this);
        BtnCloseEventDetail.setOnClickListener(this);
        BtnBackToMapEventDetail.setOnClickListener(this);
        BtnBackToMap.setOnClickListener(this);
        BtnEvents.setOnClickListener(this);
        BtnUpdates.setOnClickListener(this);
        BtnOffersPromotion.setOnClickListener(this);
        BtnCloseUpdateDetail.setOnClickListener(this);
        BtnBackToMapUpdateDetail.setOnClickListener(this);
        BtnLoadMoreChannel.setOnClickListener(this);
        BtnLoadMorePeopleNearMe.setOnClickListener(this);
        LinearSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToSettingsActivity();
            }
        });
        LinearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent=new Intent(getActivity(), ChannelProfileActivity.class);
//                intent.putExtra("ChannelId",10);
//                startActivity(intent);
            }
        });
        LinearHostEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        ImgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra(Constant.IS_MY_PROFILE, true);
                intent.putExtra(Constant.USER_ID, userId);
                startActivity(intent);
            }
        });

    }


    void configureEventDate(String dateString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(dateString);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            int day = c.get(Calendar.DATE);
            SimpleDateFormat monthDate = new SimpleDateFormat("MMMM");
            String monthName = monthDate.format(c.getTime());
            SimpleDateFormat monthDateShort = new SimpleDateFormat("MMM");
            String monthNameShort = monthDateShort.format(c.getTime());
            // monthNameShort = monthNameShort.toUpperCase();
            int year = c.get(Calendar.YEAR);
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            int am_pm = c.get(Calendar.AM_PM);  //0 am 1 pm
            // String monthName = c.get(Calendar.getD)
            Log.d("", "DATE " + hour + " " + minute + " " + am_pm + " " + day + " " + monthName + " " + monthNameShort + " " + year + " ");
            if (am_pm == 0) {
                eventFullDateTv.setText(day + " " + monthName + " " + "at " + hour + "." + minute + " AM");
            } else if (am_pm == 1) {
                eventFullDateTv.setText(day + " " + monthName + " " + "at " + hour + "." + minute + " PM");
            }
            eventDateTv.setText(day + "");
            eventMonthTv.setText(monthNameShort);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("", "error in date format " + e.getMessage().toString());
        }

    }

    private void sendToSettingsActivity() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void LoadProfileImage() {

        ProfileResponse profileResponse = retriveSavedProfileObject(sharedPreferences);

        String photoUrl = profileResponse.getData().getProfilephoto() == null || profileResponse.getData().getProfilephoto().trim().length() <= 0 ? "https://meeofbucket.s3.amazonaws.com/img/siloet_large.png" :
                "https://meeofbucket.s3.amazonaws.com/dev/public/avatar/" + profileResponse.getData().getProfilephoto();

        Log.d(TAG, "IMAGE URL profile: " + photoUrl);

        Picasso.with(getActivity())
                .load(photoUrl)
                .placeholder(R.drawable.ico_profile_edit_avatar)
                .error(ContextCompat.getDrawable(getActivity(), R.drawable.img_avatar_00))
                .resize(120, 120)
                .into(ImgUserProfile);
    }

    private class GetAddress extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (aVoid == null) {
                LocationTextView.setText("Current location : " + "Not found");
            } else {
                LocationTextView.setText("Current location : " + aVoid);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                if (city == null) {
                    return country;
                } else {
                    return city;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return "";
        }
    }


    private class CustomClusterRenderer extends DefaultClusterRenderer<ClusterModel> {

        private final Context mContext;
        private final IconGenerator mClusterIconGenerator= new IconGenerator(getContext());

        public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterModel> clusterManager) {
            super(context, map, clusterManager);
            mContext = context;
        }

        @Override protected void onBeforeClusterItemRendered(ClusterModel item, MarkerOptions markerOptions) {
            if(item.type==1){
                final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mapiconsevent_resized);
                markerOptions.icon(markerDescriptor).snippet(item.getTitle());
            }else if(item.type==2){
                final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mapiconspromo_resized);
                markerOptions.icon(markerDescriptor).snippet(item.getTitle());
            }else if(item.type==3){
                final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.mapiconsupdate_resized);
                markerOptions.icon(markerDescriptor).snippet(item.getTitle());
            }
        }


        @Override
        protected void onBeforeClusterRendered(Cluster<ClusterModel> cluster, MarkerOptions markerOptions) {
            Resources resources = getContext().getResources();
            float scale = resources.getDisplayMetrics().density;
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.cluster_marker_blue);

            android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            // new antialised Paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // text color - #3D3D3D
            paint.setColor(Color.rgb(0, 0, 0));
            // text size in pixels
            paint.setTextSize((int) (12 * scale));
            // text shadow
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

            paint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText(String.valueOf(cluster.getSize()), bitmap.getWidth()* 0.5f, bitmap.getHeight()*0.60f, paint);

            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
            markerOptions.icon(descriptor);

        }


        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
