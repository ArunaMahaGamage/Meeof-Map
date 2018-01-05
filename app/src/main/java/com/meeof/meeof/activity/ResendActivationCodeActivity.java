package com.meeof.meeof.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.ResendVerificationCodeWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ResendActivationCodeActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = ResendActivationCodeActivity.class.getSimpleName();
    private RelativeLayout backIvBtn;
    private LinearLayout sendCodeLlBtn;
    private EditText emailET;
    private LinearLayout emailAddressEditTextBg;
    private RelativeLayout emailAddressETClearBtn;
    String enteredEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resend_activation_code);

        initViews();
    }

    private void initViews() {
        backIvBtn = (RelativeLayout) findViewById(R.id.backIvBtn);
        sendCodeLlBtn = (LinearLayout) findViewById(R.id.sendCodeLlBtn);
        emailET = (EditText) findViewById(R.id.emailET);

        emailAddressEditTextBg = (LinearLayout) findViewById(R.id.emailAddressEditTextBg);
        emailAddressETClearBtn = (RelativeLayout) findViewById(R.id.emailAddressETClearBtn);

        backIvBtn.setOnClickListener(this);
        sendCodeLlBtn.setOnClickListener(this);

        emailAddressEditTextBg.setOnFocusChangeListener(this);
        emailET.setOnFocusChangeListener(this);
        emailAddressETClearBtn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backIvBtn:
                this.onBackPressed();
                break;
            case R.id.sendCodeLlBtn:
                enteredEmail = emailET.getText().toString();
                if (isEmailValid()) {
                    resendCodeForEmail(enteredEmail);
                } else {
                    showSnackbar(emailET, getString(R.string.non_valid_email), Constant.ERROR);
                }
                break;
            case R.id.emailAddressETClearBtn:
                emailET.setText("");
                break;


        }

    }

    private void resendCodeForEmail(String email) {
        if (isNetworkAvailable()) {
            jobManager.addJobInBackground(new ResendVerificationCodeWebJob(email));
            startProgressBar();
        } else {
            showSnackbar(backIvBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onVerificationJobCompleted(HttpResponse httpResponse) {
        stopProgressBar();
        Log.d(TAG, "Insode Subscribe");
        if ((httpResponse.getStatus() != null && httpResponse.getStatus() != null)
                && (!httpResponse.getStatus().isEmpty()) && !httpResponse.getMessage().isEmpty()) {
            Log.d(TAG, "Inside if");
            if (httpResponse.getStatus().equals("success")) {
                Log.d(TAG, "Inside if if");
                showSnackbar(backIvBtn, getString(R.string.email_sent_success_verificaion)+ enteredEmail, Constant.ERROR);
                sendToVerificationActivity();
            } else {
                showSnackbar(backIvBtn, getString(R.string.email_sent_failed_to_email), Constant.ERROR);

            }
        } else {
            showSnackbar(backIvBtn, getString(R.string.email_sent_failed_try_again), Constant.ERROR);
        }
    }

    private void sendToVerificationActivity() {
        this.finish();
        Intent intent = new Intent(this, VerificationActivity.class);
        String email = emailET.getText().toString();
        intent.putExtra(Constant.USER_EMAIL, email);
        intent.putExtra(Constant.IS_FROM_OPTIONS_MENU, false);
        this.startActivity(intent);
    }

    private Boolean isEmailValid() {
        return emailET.getText().toString().matches(Constant.EMAIL_REGEX) ? true : false;
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
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(TAG, "onFocusChange");
        switch (v.getId()) {
            case R.id.emailET:
                if (hasFocus) {
                    enableEmailET();
                } else {
                    disableEmailET();
                    hideKeyboard(v);
                }
                break;
        }
    }

    private void enableEmailET() {
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
    }

    private void disableEmailET() {
        Log.d(TAG, "disableEmailET");
        emailAddressEditTextBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}


