package com.meeof.meeof.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.birbit.android.jobqueue.JobManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.activity.BaseActivity;
import com.meeof.meeof.util.Constant;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ransikadesilva on 10/18/17.
 */

public class BaseFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = BaseActivity.class.getSimpleName();
    public JobManager jobManager;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor sharedEditor;
    private static ProgressDialog progressDialog;
    private Geocoder geocoder;
    protected GoogleApiClient mGoogleApiClient;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());

        jobManager = MeeofApplication.getInstance().getJobManager();

        sharedPreferences = getActivity().getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();

        initProgressBar();


        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    @UiThread
    protected void showSnackbar(View view, String message, String status) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        showToast(view, message, status);
    }
    @UiThread
    protected void initProgressBar() {
        progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }

    @UiThread
    protected void startProgressBar() {
        try {
            if (progressDialog != null) {
                Log.d(TAG,"startProgressBar");
                progressDialog.show();
                dismissProgressBarAnyway(6000);
            }
        } catch (Exception ex) {
            Log.d(TAG, "startProgressBar : Exception = " + ex);
        }
    }

    @UiThread
    protected void startProgressBarSepcial(String tag) {
        try {
            if (progressDialog != null) {
                Log.d(TAG, "startProgressBar : "+tag);
                progressDialog.show();
                dismissProgressBarAnyway(6000);
            }
        } catch (Exception ex) {
            Log.d(TAG, "startProgressBar : Exception = " + ex);
        }
    }

    @UiThread
    protected void stopProgressBarSpecial(String tag) {
        if (progressDialog != null) {
            if (progressDialog != null) {
                Log.d(TAG, "stopProgressBar : "+tag);
                progressDialog.dismiss();
            }
        }
    }

    private void dismissProgressBarAnyway(long timeInMillis){
        final Handler h = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Log.d(TAG,"Dismissing PB");
                progressDialog.dismiss();
            }
        };
        h.sendMessageDelayed(new Message(), timeInMillis);
    }

    @UiThread
    protected void stopProgressBar() {
        if (progressDialog != null) {
            if (progressDialog != null) {
                Log.d(TAG,"stopProgressBar");
                progressDialog.dismiss();
            }
        }
    }
    public void showToast(View view, String message, String status) {

        TSnackbar snackbar = TSnackbar
                .make(view, " " + message, TSnackbar.LENGTH_LONG);
        snackbar.setActionTextColor(Color.WHITE);
        snackbar.setIconLeft(getIcon(status), 15);
        snackbar.setIconPadding(5);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getBackgroundColor(status));
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
    private int getBackgroundColor(String status) {

        int color = Color.parseColor("#dd3c40");

        switch (status) {
            case "success":
                color = Color.parseColor("#76CF67");
                break;

            case "failed":
                color = Color.parseColor("#dd3c40");
                break;

            case "error":
                color = Color.parseColor("#dd3c40");
                break;
        }

        return color;
    }

    private int getIcon(String status) {

        int icon = R.drawable.ic_close_white_error;

        switch (status) {
            case "success":
                icon = R.drawable.ic_success;
                break;

            case "failed":
                icon = R.drawable.ic_close_white_error;
                break;

            case "error":
                icon = R.drawable.ic_close_white_error;
                break;
        }
        return icon;
    }

    public List<Address> getAddressFromLatLong(double latitude, double longitude){
        try{
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return  addresses;

        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG, "getAddressFromLatLong error");
            return null;
        }
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
