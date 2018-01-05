package com.meeof.meeof.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.ForgotPasswordWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();
    private RelativeLayout backIvBtn;
    private RelativeLayout emailAddressETClearBtn;
    private LinearLayout sendLlBtn;
    private EditText emailET;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        intitViews();

    }

    private void intitViews() {
        token=sharedPreferences.getString(Constant.ACCESS_TOKEN,"");
        backIvBtn = (RelativeLayout) findViewById(R.id.backIvBtn);
        emailAddressETClearBtn = (RelativeLayout) findViewById(R.id.emailAddressETClearBtn);
        sendLlBtn = (LinearLayout) findViewById(R.id.sendLlBtn);
        emailET = (EditText) findViewById(R.id.emailET);
        backIvBtn.setOnClickListener(this);
        sendLlBtn.setOnClickListener(this);
        emailAddressETClearBtn.setOnClickListener(this);

    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backIvBtn:
                this.onBackPressed();
                break;
            case R.id.emailAddressETClearBtn:
                emailET.setText("");
                break;

            case R.id.sendLlBtn:
                Log.d(TAG,"sendLlBtn clicked");
                String email=emailET.getText().toString();

                if (isEmailValid()) {
                    forgotPassword(token,email);
                    emailET.setText("");
                    emailET.clearFocus();
                }else{
                    showSnackbar(sendLlBtn, getString(R.string.non_valid_email), Constant.ERROR);
                }

                break;

        }
    }


    private void forgotPassword(String token, String email) {
        if (isNetworkAvailable()) {
            Log.d(TAG, "Inside forgotPasswordWebJob");
            jobManager.addJobInBackground(new ForgotPasswordWebJob(token, email));
            startProgressBar();
        } else {
            showSnackbar(sendLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onForgotPasswordJobCompleted(HttpResponse httpResponseChangePw) {
        Log.d(TAG, "onForgotPasswordJobCompleted inside");

        if (httpResponseChangePw != null) {
            if (httpResponseChangePw.getStatus() != null) {
                if (httpResponseChangePw.getStatus().equals(Constant.SUCCESS)) {
                    Log.d(TAG, "onForgotPasswordJobCompleted if");
                    stopProgressBar();
                    showSnackbar(sendLlBtn, getString(R.string.email_sent_success), Constant.SUCCESS);

                } else {
                    Log.d(TAG, "onForgotPasswordJobCompleted else");
                    stopProgressBar();

                    if(httpResponseChangePw.getMessage() != null){
                        showSnackbar(sendLlBtn, httpResponseChangePw.getMessage(), Constant.ERROR);
                    }else{
                        showSnackbar(sendLlBtn, getString(R.string.email_sent_failed_try_again), Constant.ERROR);
                    }
                }
            }else{
                stopProgressBar();
                showSnackbar(sendLlBtn, getString(R.string.email_sent_failed_try_again), Constant.ERROR);
            }
        } else {
            Log.d(TAG, "onForgotPasswordJobCompleted else else");
            stopProgressBar();
            showSnackbar(sendLlBtn, getString(R.string.email_sent_failed_try_again), Constant.ERROR);
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

    private Boolean isEmailValid() {
        return emailET.getText().toString().matches(Constant.EMAIL_REGEX) ? true : false;
    }
}
