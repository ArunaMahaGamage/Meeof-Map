package com.meeof.meeof.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;
import com.birbit.android.jobqueue.JobManager;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.meeof.meeof.MeeofApplication;
import com.meeof.meeof.R;
import com.meeof.meeof.util.Constant;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    protected static final int RC_GOOGLE_SIGN_IN = 9001;
    protected static final int RC_FACEBOOk_SIGN_IN = 64206;
    private static final String TAG = BaseActivity.class.getSimpleName();
    public JobManager jobManager;
    protected GoogleApiClient mGoogleApiClient;
    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor sharedEditor;
    private static ProgressDialog progressDialog;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
        FacebookSdk.sdkInitialize(getApplicationContext());

        jobManager = MeeofApplication.getInstance().getJobManager();

        sharedPreferences = getSharedPreferences(Constant.MEEOF_SHARED_PREF, MODE_PRIVATE);
        sharedEditor = sharedPreferences.edit();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        initProgressBar();
    }

    protected boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        }
        return false;
    }

    public void showOtherOptions(final Context context) {
        final CharSequence[] items = {"Sign-up with Email", "Login with Email",
                "I have a code", "Privacy Policy", "Terms of Use", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.sign_up_with_email);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Sign-up with Email")) {

                    Intent intent = new Intent(context, RegisterActivity.class);
                    startActivity(intent);

                } else if (items[item].equals("Login with Email")) {

                    Intent intent = new Intent(context, LoginWithEmailActivity.class);
                    startActivity(intent);

                } else if (items[item].equals("I have a code")) {

                    Intent intent = new Intent(context, VerificationActivity.class);
                    intent.putExtra(Constant.USER_EMAIL, "you");
                    intent.putExtra(Constant.IS_FROM_OPTIONS_MENU, true);
                    startActivity(intent);

                } else if (items[item].equals("Privacy Policy")) {

                    Intent intent = new Intent(context, PrivacyPolicyActivity.class);
                    startActivity(intent);

                } else if (items[item].equals("Terms of Use")) {

                    Intent intent = new Intent(context, TermsAndPrivacyActivity.class);
                    startActivity(intent);

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    @UiThread
    public void showSnackbar(View view, String message, String status) {
//        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show();
        showToast(view, message, status);
    }

    //////////////google//////////////////
    protected void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        mGoogleApiClient.connect();
    }

    protected void setUpGoogleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.clientID), false)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }


    @UiThread
    protected void initProgressBar() {
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }

    @UiThread
    protected void startProgressBar() {
        try {
            if (progressDialog != null) {
                progressDialog.show();
            }
        } catch (Exception ex) {
            Log.d(TAG, "startProgressBar : Exception = " + ex);
        }
    }

    @UiThread
    protected void stopProgressBar() {
        if (progressDialog != null) {
            if (progressDialog != null) {
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

    protected List<Address> getAddressFromLatLong(double latitude, double longitude){
        try{
            geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            return  addresses;

        }catch (IOException e){
            e.printStackTrace();
            Log.d(TAG, "getAddressFromLatLong error");
            return null;
        }
    }

    void setFragment(Fragment fragment, String key, String tag) {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.mainContainer, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    protected void disconnectFromFacebook() {
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    protected void printFbHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
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
