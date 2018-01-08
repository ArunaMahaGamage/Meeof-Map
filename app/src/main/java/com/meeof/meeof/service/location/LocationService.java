package com.meeof.meeof.service.location;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.birbit.android.jobqueue.JobManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.model.AddressPlaceModel;
import com.meeof.meeof.model.GooglePlaceIdResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetPlaceIdWebJob;
import com.meeof.meeof.webjob.PostGeoInfoWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ransika on 7/27/2017.
 */

public class LocationService extends Service implements LocationListener {
    private FusedLocationProviderClient mFusedLocationClient;

    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;

    boolean canGetLocation = false;
    private Location location;
    private String TAG = "LocationService";
    private SharedPreferences sharedPreferences;
    public JobManager jobManager;
    private float mCurrentLatitude;
    private float mCurrentLongitude;
    private Geocoder geocoder;
    private String accessToken;

    private Location first;
    private Location second;
    double distance;


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        jobManager = MeeofApplication.getInstance().getJobManager();
        sharedPreferences = MeeofApplication.getInstance().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.i(TAG, "RUN onStartCommand");

        //TODO CHECK DISTANCE GAP TO UPDATE EVENTS
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putFloat(Constant.distanceLat, sharedPreferences.getFloat(Constant.latitudeService, (float) location.getLatitude()));
//        editor.putFloat(Constant.distanceLng, sharedPreferences.getFloat(Constant.longitudeService, (float) location.getLongitude()));
//        editor.apply();

        Location location = getLocation();
        if (location != null) {
            sendLocationMessageToActivity(location.getLatitude(), location.getLongitude());
        }
        return Service.START_STICKY;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ");
        Log.d(TAG, "onLocationChanged lat" + location.getLatitude());
        Log.d(TAG, "onLocationChanged long" + location.getLongitude());

    if (second == null && first != null) {
            second = location;

                distance = first.distanceTo(second);

                double distancePerKM = distance/1000;

                double firstTime = first.getTime();
                double secondTime = second.getTime();

                Log.e("first and second time",firstTime + "    " + secondTime);


        double time = secondTime - firstTime;

        Log.e("difference", String.valueOf(time));

        time = time/1000;

        double speed  = distance / (time);
        double kmph = speed * 3.6;
        Log.e("distance ", String.valueOf(distance));
        Log.e("time", String.valueOf(time));
        Log.e("kmph", String.valueOf(kmph));

        if (speed < 6) {
            //some thing here
            broadcastEventsUpdate();
            Log.e("if"," speed < 6");
        }

        Toast.makeText(getApplicationContext(),"kmph " + new String(String.valueOf(kmph)),
                Toast.LENGTH_LONG).show();
        first = second;
        second = null;

        }else {
            first = location;
        }



//        float speed = 0;
//        speed = location.getTime();
//
//        Toast.makeText(getApplicationContext(),new String(String.valueOf(speed)),
//                Toast.LENGTH_SHORT).show();


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(Constant.latitudeService, (float) location.getLatitude());
        editor.putFloat(Constant.longitudeService, (float) location.getLongitude());
        editor.apply();

        float currentLatitude = (float) location.getLatitude();
        mCurrentLatitude = (float) location.getLatitude();
        float currentLongitude = (float) location.getLongitude();
        mCurrentLongitude = (float) location.getLongitude();

        setDistance(mCurrentLatitude,mCurrentLongitude);

        retrievePlaceIdForLatLong(new LatLng(currentLatitude,currentLongitude));
        sendLocationMessageToActivity(currentLatitude, currentLongitude);
    }

    private void setDistance(double lat, double lng) {

    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }


    public void retrievePlaceIdForLatLong(LatLng latLng) {
        Log.d(TAG,"retrievePlaceIdForLatLong()");
        if(isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetPlaceIdWebJob(latLng.latitude, latLng.longitude));
        }
    }


    @Subscribe
    public void googlePlaceIdResponse(final GooglePlaceIdResponse googlePlaceIdResponse) {
        Log.d(TAG, "googlePlaceIdResponse");
        if (googlePlaceIdResponse != null) {
            if (googlePlaceIdResponse.getStatus() != null && googlePlaceIdResponse.getStatus().contains(Constant.SUCCESS)) {

                try {
                    JSONArray data = googlePlaceIdResponse.getData();
                    JSONObject placeObj = (JSONObject) data.get(0);

                    String placeId = placeObj.getString("place_id");

                    if (placeId != null && placeId.length() > 0) {
                        AddressPlaceModel currentAddressPlace = new AddressPlaceModel();

                        currentAddressPlace.setPlaceID(placeId);
                        currentAddressPlace.setLat(String.valueOf(mCurrentLatitude));
                        currentAddressPlace.setLng(String.valueOf(mCurrentLongitude));
                        currentAddressPlace.setAddniceaddress(getAddress(mCurrentLatitude, mCurrentLongitude).getAddressLine(0));
                        currentAddressPlace.setPlaceName(getAddress(mCurrentLatitude, mCurrentLongitude).getFeatureName());
                        currentAddressPlace.setCountry(getAddress(mCurrentLatitude, mCurrentLongitude).getCountryName());

                        postGeoLocationInfo(currentAddressPlace);
                    }
                } catch (Exception ex) {

                }
            }
        }
    }

    private void postGeoLocationInfo(AddressPlaceModel currentAddressPlace) {
        if(isNetworkAvailable()){
            //jobManager.addJobInBackground(new PostGeoInfoWebJob(accessToken, currentAddressPlace));
        }
    }




    @Override
    public void onDestroy() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            locationManager.removeUpdates(this);

            return;
        }
        stopSelf();
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    public Location getLocation() {
        //  Log.i(TAG, "RUN getLocation");
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;

                // First get location from Network Provider
                if (isNetworkEnabled) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        // Log.i(TAG, "RUN isGPSEnabled permission granted");
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                Constant.MIN_TIME_BW_UPDATES,
                                Constant.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        //  Log.d("Network", "Network");
                        if (locationManager != null) {
                            //Log.i(TAG, "RUN locationManager");
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }
                    }


                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    //  Log.i(TAG, "RUN isGPSEnabled");
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                Constant.MIN_TIME_BW_UPDATES,
                                Constant.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        // Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        }
                    }
                }

            } else {
                //Toast.makeText(getApplicationContext(), "please enable location", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {

        }

        if (location != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat(Constant.lastKnownLatitudeService, (float) location.getLatitude());
            editor.putFloat(Constant.lastKnownLongitudeService, (float) location.getLongitude());
            editor.apply();
        }

        return location;
    }

    private void sendLocationMessageToActivity(double latitude, double longitude) {
        Intent intent = new Intent(Constant.LOCATION_UPDATED);
        intent.putExtra(Constant.LATITUDE, latitude);
        intent.putExtra(Constant.LONGITUDE, longitude);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private Address getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
        Address address = null;
        if (addressList != null && !addressList.isEmpty()) {
            address = addressList.get(0);
        }
        return address;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private void broadcastEventsUpdate() {
        Log.e("broadcastEventsUpdate","broadcastEventsUpdate");
        Intent intent = new Intent(Constant.UPDATE_EVENTS);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}