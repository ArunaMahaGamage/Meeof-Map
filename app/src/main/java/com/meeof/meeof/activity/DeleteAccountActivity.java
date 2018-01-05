package com.meeof.meeof.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.helper.Helper;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.DeleteAccountWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by ransikadesilva on 10/13/17.
 */

public class DeleteAccountActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = DeleteAccountActivity.class.getSimpleName();
    private LinearLayout deleteAccountLlBtn;
    private String accessToken;
    private ImageView backAcIvBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_delete_account);

        initViews();
    }

    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");
        deleteAccountLlBtn = (LinearLayout) findViewById(R.id.deleteAccountLlBtn);
        backAcIvBtn = (ImageView) findViewById(R.id.backAcIvBtn);

        deleteAccountLlBtn.setOnClickListener(this);
        backAcIvBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteAccountLlBtn:
                Log.d(TAG, "delete account button clicked ");
                deleteAccount();
                break;

            case R.id.backAcIvBtn:
                Log.d(TAG,"on back press clicked");
                onBackPressed();
                break;
        }
    }

    private void deleteAccount() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "delete account web job ");
            jobManager.addJobInBackground(new DeleteAccountWebJob(accessToken));
            startProgressBar();
        } else {
            showSnackbar(deleteAccountLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onDeleteAccountWebJobCompleted(HttpResponse httpResponseDeleteAccount) {
        stopProgressBar();
        if (httpResponseDeleteAccount != null) {
            if (httpResponseDeleteAccount.getStatus()!=null&&httpResponseDeleteAccount.getStatus().equals(Constant.SUCCESS)) {
                Log.d(TAG, "onDeleteAccountWebJobCompleted success ");
                showSnackbar(deleteAccountLlBtn, "Your account is successfully deleted ", Constant.SUCCESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Helper.delay(1500, new Helper.DelayCallBack() {
                            @Override
                            public void postDelay() {
                                logoutUser();
                            }
                        });
                    }
                });

            } else {
                Log.d(TAG, "onDeleteAccountWebJobCompleted unsuccess " + httpResponseDeleteAccount.getStatus().toString());
                showSnackbar(deleteAccountLlBtn, httpResponseDeleteAccount.getMessage().toString(), Constant.ERROR);
            }
        } else {

            Log.d(TAG, "onDeleteAccountWebJobCompleted null ");
            showSnackbar(deleteAccountLlBtn, httpResponseDeleteAccount.getMessage().toString(), Constant.ERROR);
        }

    }

    private void logoutUser() {
        sharedEditor.putString(Constant.ACCESS_TOKEN, "");
        sharedEditor.putString(Constant.REFRESH_TOKEN, "");
        sharedEditor.apply();
        this.finish();

        Intent intent = new Intent(this, GetStartedActivity.class);
        startActivity(intent);
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
