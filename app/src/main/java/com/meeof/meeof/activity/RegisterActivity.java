package com.meeof.meeof.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.model.LoginResponse;
import com.meeof.meeof.model.profile.ProfileResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.GetProfileWebJob;
import com.meeof.meeof.webjob.SocialRegistrationWebJob;
import com.meeof.meeof.webjob.UserRegistrationWebJob;
import com.onesignal.OSPermissionSubscriptionState;
import com.onesignal.OneSignal;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends BaseActivity implements View.OnFocusChangeListener, View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private EditText nameET;
    private EditText emailET;
    private EditText passwordET;

    private LinearLayout nameEditTextBg;
    private LinearLayout emailAddressEditTextBg;
    private LinearLayout passwordEditTextBg;


    private LinearLayout googleBtn;
    private LinearLayout facebookBtn;
    private LinearLayout otherOptionsBtn;
    private LoginButton facebookLgnBtn;
    private LinearLayout registerBtn;
    private String googleToken;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ImageView backIvBtn;
    private RelativeLayout nameETClearBtn;
    private RelativeLayout emailAddressETClearBtn;
    private RelativeLayout showPasswordRlBtn;
    private RelativeLayout hidePasswordRlBtn;
    private LinearLayout termsConditionsLlBtn;
    private String googleAuthToken;
    private String googleId;
    private String name;
    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));
//        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register);

        intViews();

        printHashKey(this);

        setUpGoogleSignIn();
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public String fbToken;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("ACCESS TOKEN FB", loginResult.getAccessToken().getToken().toString());
                fbToken = loginResult.getAccessToken().getToken().toString();
//                Toast.makeText(RegisterActivity.this, "FB TOKEN : " + fbToken, Toast.LENGTH_LONG).show();
                Log.d(TAG, "fbToken : " + fbToken);

                signInWithToken(fbToken, Constant.GRANT_TYPE_FB);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "loginButton onError: " + error.toString());
            }

        });

        //////////////////facebook////////////////////

    }

    private void signInWithToken(String fbToken, String grantType) {
        OSPermissionSubscriptionState state = OneSignal.getPermissionSubscriptionState();
        state.getPermissionStatus();

        Log.d(TAG, "OSPermissionSubscriptionState:state " + state);
        String userId = "";

        if(state.getPermissionStatus().getEnabled()){
            userId = state.getSubscriptionStatus().getUserId();
        }
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new SocialRegistrationWebJob(fbToken, grantType,userId));
            startProgressBar();

        } else {
            showSnackbar(registerBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }


    private void sendToDashboard() {
        Intent intent = new Intent(this, EditProfileActivity.class);
        this.startActivity(intent);
    }

    @Subscribe
    public void onSocialRegistrationJobCompleted(LoginResponse loginResponse) {
        stopProgressBar();
        if (loginResponse != null) {
            if (loginResponse.getAccessToken() != null && loginResponse.getRefreshToken() != null && loginResponse.getTokenType() != null) {
                String accessToken = loginResponse.getAccessToken();
                String refreshToken = loginResponse.getRefreshToken();
                String tokenType = loginResponse.getTokenType();

                Log.d("ACCESS TOKEN ", tokenType + " " + accessToken);
                Log.d("REFRESH TOKEN ", refreshToken);

                sharedEditor.putString(Constant.ACCESS_TOKEN, tokenType + " " + accessToken);
                sharedEditor.putString(Constant.REFRESH_TOKEN, refreshToken);

                sharedEditor.putString(Constant.LOGGED_IN_WITH, loginResponse.getSocialType());

                sharedEditor.apply();
//                sendToDashboard();
                getUserProfile(tokenType+" "+accessToken);

            } else {
                showSnackbar(registerBtn, getString(R.string.login_failed), Constant.ERROR);
            }
        } else {
            showSnackbar(registerBtn, getString(R.string.login_failed), Constant.ERROR);
        }
    }

    private void getUserProfile(String accessToken) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new GetProfileWebJob(accessToken));
        } else {
            showSnackbar(googleBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onGetUserProfileWebJobCompleted(final ProfileResponse profileResponse) {
        stopProgressBar();
        Log.d(TAG,"onGetUserProfileWebJobCompleted");
        if (profileResponse != null) {
            if (profileResponse.getStatus() != null && profileResponse.getStatus().equals(Constant.SUCCESS)) {
                if(profileResponse.getData().getIs_first_login()==1){
                    Log.d(TAG,"First Time User");
                    sendToEditProfileActivity(); //IF first time user send to EditProfile Activity//

                }else{
                    Log.d(TAG,"Not First Time User");
                    sendToDashboard(); //ELSE send to Dashboard Activity//
                }

            } else {
                goToGetStartedActivity();
            }
        } else {
            goToGetStartedActivity();
        }
    }

    private void sendToEditProfileActivity() {
        this.finish();
        Intent intent = new Intent(this, EditProfileActivity.class);
        this.startActivity(intent);
    }

    private void goToGetStartedActivity() {
        Intent intent = new Intent(this, GetStartedActivity.class);
        startActivity(intent);
        finish();
    }

    private void intViews() {
        nameET = (EditText) findViewById(R.id.nameET);
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        nameEditTextBg = (LinearLayout) findViewById(R.id.nameEditTextBg);
        emailAddressEditTextBg = (LinearLayout) findViewById(R.id.emailAddressEditTextBg);
        passwordEditTextBg = (LinearLayout) findViewById(R.id.passwordEditTextBg);

        loginButton = (LoginButton) findViewById(R.id.connectWithFbButton); //FACEBOOK BUTTON

        registerBtn = (LinearLayout) findViewById(R.id.registerBtn);
        googleBtn = (LinearLayout) findViewById(R.id.googleBtn);
        facebookBtn = (LinearLayout) findViewById(R.id.facebookBtn);
        otherOptionsBtn = (LinearLayout) findViewById(R.id.otherBtn);
        backIvBtn = (ImageView) findViewById(R.id.backIvBtn);

        nameETClearBtn = (RelativeLayout) findViewById(R.id.nameETClearBtn);
        emailAddressETClearBtn = (RelativeLayout) findViewById(R.id.emailAddressETClearBtn);
        showPasswordRlBtn = (RelativeLayout) findViewById(R.id.showPasswordRlBtn);
        hidePasswordRlBtn = (RelativeLayout) findViewById(R.id.hidePasswordRlBtn);

        termsConditionsLlBtn = (LinearLayout) findViewById(R.id.termsConditionsLlBtn);

        Typeface mFont = Typeface.createFromAsset(this.getAssets(), "Hind-Medium.ttf");
        passwordET.setTypeface(mFont);

        googleBtn.setOnClickListener(this);
        facebookBtn.setOnClickListener(this);
        otherOptionsBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);

        nameETClearBtn.setOnClickListener(this);
        emailAddressETClearBtn.setOnClickListener(this);
        showPasswordRlBtn.setOnClickListener(this);
        hidePasswordRlBtn.setOnClickListener(this);

        nameET.setOnFocusChangeListener(this);
        emailET.setOnFocusChangeListener(this);
        passwordET.setOnFocusChangeListener(this);
        backIvBtn.setOnClickListener(this);
        termsConditionsLlBtn.setOnClickListener(this);

    }

    @Override
    public void onFocusChange(View view, boolean b) {

        Log.d(TAG, "onFocusChange");

        switch (view.getId()) {
            case R.id.nameET:

                if (b)
                    enableNameET();
                break;

            case R.id.emailET:
                if (b)
                    enableEmailET();
                break;

            case R.id.passwordET:
                if (b)
                    enablePasswordET();
                break;
        }
    }

    private void setCursorAtEnd(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setSelection(editText.getText().length());
    }

    private void enableNameET() {
        Log.d(TAG, "enableNameET");
        nameEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
        passwordEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    private void enableEmailET() {
        Log.d(TAG, "enableEmailET");
        nameEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
        passwordEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    private void enablePasswordET() {
        Log.d(TAG, "enablePasswordET");
        nameEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        passwordEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.googleBtn:
                signIn();
                break;

            case R.id.facebookBtn:
                loginButton.performClick();
                break;

            case R.id.otherBtn:
                showOtherOptions(RegisterActivity.this);
                break;

            case R.id.registerBtn:
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                if (isValidName()) {
                    if (isEmailValid()) {
                        if (isPasswordValid()) {
                            Log.d(TAG, "Inside registerBtn");

                            name = nameET.getText().toString();
                            email = emailET.getText().toString();
                            password = passwordET.getText().toString();

                            registerUser(name, email, password);
                        } else {
                            showSnackbar(nameET, getString(R.string.min_charcter_password), Constant.ERROR);
                        }
                    } else {
                        showSnackbar(nameET, getString(R.string.non_valid_email), Constant.ERROR);
                    }
                } else {
                    showSnackbar(nameET, getString(R.string.invalid_name), Constant.ERROR);
                }
                break;

            case R.id.backIvBtn:
                this.onBackPressed();
                break;

            case R.id.nameETClearBtn:
                nameET.setText("");
                break;

            case R.id.emailAddressETClearBtn:
                emailET.setText("");
                break;

            case R.id.showPasswordRlBtn:
                Log.d(TAG, "showPasswordRlBtn");
                passwordET.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordET.setSelection(passwordET.getText().length());
                showPasswordRlBtn.setVisibility(View.GONE);
                hidePasswordRlBtn.setVisibility(View.VISIBLE);
                break;

            case R.id.hidePasswordRlBtn:
                Log.d(TAG, "hidePasswordRlBtn");
                passwordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordET.setSelection(passwordET.getText().length());
                showPasswordRlBtn.setVisibility(View.VISIBLE);
                hidePasswordRlBtn.setVisibility(View.GONE);
                break;

            case R.id.termsConditionsLlBtn:
                sendToTermsConditions();
                Helper.clickGaurd(termsConditionsLlBtn);
                break;
        }
    }

    private void sendToTermsConditions() {
        Intent intent = new Intent(this, TermsAndPrivacyActivity.class);
        startActivity(intent);
    }

    private boolean isValidName() {
        return nameET.getText().toString().trim().length() > 0 ? true : false;
    }

    private void registerUser(String name, String email, String password) {
        Log.d(TAG, "Inside registerUser");
        if (isNetworkAvailable()) {
            Log.d(TAG, "Inside network");

            jobManager.addJobInBackground(new UserRegistrationWebJob(name, email, password));
            startProgressBar();
        } else {
            Log.d(TAG, "Inside registerUser nonetwork");
            showSnackbar(registerBtn, getString(R.string.no_internet), Constant.ERROR);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserRegistrationJobCompleted(HttpResponse reponse) {
        stopProgressBar();
        Log.d(TAG, "Inside Subscribe");
        if ((reponse.getStatus() != null && reponse.getStatus() != null)
                && (!reponse.getStatus().isEmpty()) && !reponse.getMessage().isEmpty()) {
            Log.d(TAG, "Insode if");
            if (reponse.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "Insode if if");
                if (email != null && password != null) {
                    sharedEditor.putString(Constant.USER_EMAIL, email);
                    sharedEditor.putString(Constant.PASSWORD, password);
                    sharedEditor.apply();
                }
                sendToVerification();
            } else {
                if (!reponse.getMessage().equals(Constant.ERROR)) {
                    showSnackbar(registerBtn, getString(R.string.email_already_registered), Constant.ERROR);
                } else {
                    showSnackbar(registerBtn, getString(R.string.registration_failed), Constant.ERROR);
                }
                //TODO MESSAGE
            }
        } else {
            showSnackbar(registerBtn, getString(R.string.registration_failed), Constant.ERROR);
            //TODO MESSAGE
        }
    }

    private void sendToVerification() {
        //TODO SIGN IN INTENT
        Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
        intent.putExtra(Constant.USER_EMAIL, emailET.getText().toString());
        intent.putExtra(Constant.IS_FROM_OPTIONS_MENU, false);
        this.startActivity(intent);
    }

    @Override
    public void showOtherOptions(Context context) {
        super.showOtherOptions(context);
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


    //////////////google/////////////////

    /////////////////////google//////////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Log.d(TAG, "Inside onActivityResult RC_GOOGLE_SIGN_IN");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "Google result: " + result.getStatus().toString());
            if (result.isSuccess()) {
                Log.d(TAG, "Inside onActivityResult isSuccess");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                googleToken = account.getIdToken(); //job to send

                googleAuthToken = account.getServerAuthCode();
                Log.d(TAG, "googleAuthToken : " + googleAuthToken);

                googleId = account.getId();
                Log.d(TAG, "googleId : " + googleId);

                signInWithToken(googleToken, Constant.GRANT_TYPE_GOOGLE);

                Log.d(TAG, "googleToken :" + googleToken);

                Log.d(" GOOG ID", googleToken);
                Log.d("JOHN CENA", "You cant see me");
                Log.d(" DISP NAME", "Hello World " + account.getAccount().name);
                Log.d(" DISP NAME", "Hello World " + account.getDisplayName());

//                Toast.makeText(RegisterActivity.this, "googleToken : " + googleToken, Toast.LENGTH_LONG).show();
                Log.d(TAG, "google token : " + googleToken);
                //TODO JOB to send google Auth token to Server

//                Toast.makeText(getApplicationContext(), account.getDisplayName(), Toast.LENGTH_LONG).show();
            } else {

                Log.d(" GOOG ID", " failed");
                // Google Sign In failed, update UI appropriately
                // ...
            }
        } else { //facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void printHashKey(Context pContext) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }


    private Boolean isEmailValid() {
        return emailET.getText().toString().matches(Constant.EMAIL_REGEX) ? true : false;
    }

    private Boolean isPasswordValid() {
        return passwordET.getText().toString().trim().length() > 6 ? true : false;
    }

}
