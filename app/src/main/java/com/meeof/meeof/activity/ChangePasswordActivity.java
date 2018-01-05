package com.meeof.meeof.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;
import com.meeof.meeof.model.HttpResponse;
import com.meeof.meeof.util.Constant;
import com.meeof.meeof.webjob.ChangePasswordWebJob;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by ransikadesilva on 10/13/17.
 */

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText currentPasswordET, newPasswordET, confirmPasswordET;
    private RelativeLayout showCurrentPwBtn;
    private RelativeLayout hideCurrentPwBtn;
    private RelativeLayout showNewPwBtn;
    private RelativeLayout hideNewPwBtn;
    private RelativeLayout showConfirmPwBtn;
    private RelativeLayout hideConfirmPwBtn;
    private LinearLayout changePasswordLlBtn;
    private String accessToken;
    private AppCompatImageView backAcIvBtn;
    private LinearLayout currentPasswordLlBg;
    private LinearLayout confirmPasswordLlBg;
    private LinearLayout newPasswordLlBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initViews();
    }

    private void initViews() {
        accessToken = sharedPreferences.getString(Constant.ACCESS_TOKEN, "");

        currentPasswordET = (EditText) findViewById(R.id.currentPasswordET);
        newPasswordET = (EditText) findViewById(R.id.newPasswordET);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);

        Typeface mFont = Typeface.createFromAsset(this.getAssets(), "Hind-Medium.ttf");
        currentPasswordET.setTypeface(mFont);
        newPasswordET.setTypeface(mFont);
        confirmPasswordET.setTypeface(mFont);

        currentPasswordET.setOnFocusChangeListener(this);
        newPasswordET.setOnFocusChangeListener(this);
        confirmPasswordET.setOnFocusChangeListener(this);

        backAcIvBtn = (AppCompatImageView) findViewById(R.id.backAcIvBtn);

        changePasswordLlBtn = (LinearLayout) findViewById(R.id.changePasswordLlBtn);

        showCurrentPwBtn = (RelativeLayout) findViewById(R.id.showCurrentPwBtn);
        hideCurrentPwBtn = (RelativeLayout) findViewById(R.id.hideCurrentPwBtn);

        showNewPwBtn = (RelativeLayout) findViewById(R.id.showNewPwBtn);
        hideNewPwBtn = (RelativeLayout) findViewById(R.id.hideNewPwBtn);

        showConfirmPwBtn = (RelativeLayout) findViewById(R.id.showConfirmPwBtn);
        hideConfirmPwBtn = (RelativeLayout) findViewById(R.id.hideConfirmPwBtn);


        currentPasswordLlBg = (LinearLayout) findViewById(R.id.currentPasswordLlBg);
        confirmPasswordLlBg = (LinearLayout) findViewById(R.id.confirmPasswordLlBg);
        newPasswordLlBg = (LinearLayout) findViewById(R.id.newPasswordLlBg);

        showCurrentPwBtn.setOnClickListener(this);
        hideCurrentPwBtn.setOnClickListener(this);

        showNewPwBtn.setOnClickListener(this);
        hideNewPwBtn.setOnClickListener(this);

        showConfirmPwBtn.setOnClickListener(this);
        hideConfirmPwBtn.setOnClickListener(this);

        changePasswordLlBtn.setOnClickListener(this);

        backAcIvBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.showCurrentPwBtn:
                showPassword(showCurrentPwBtn, hideCurrentPwBtn, currentPasswordET);
                break;

            case R.id.hideCurrentPwBtn:
                hidePassword(showCurrentPwBtn, hideCurrentPwBtn, currentPasswordET);
                break;

            case R.id.showConfirmPwBtn:
                showPassword(showConfirmPwBtn, hideConfirmPwBtn, confirmPasswordET);
                break;

            case R.id.hideConfirmPwBtn:
                hidePassword(showConfirmPwBtn, hideConfirmPwBtn, confirmPasswordET);
                break;

            case R.id.showNewPwBtn:
                showPassword(showNewPwBtn, hideNewPwBtn, newPasswordET);
                break;

            case R.id.hideNewPwBtn:
                hidePassword(showNewPwBtn, hideNewPwBtn, newPasswordET);
                break;

            case R.id.backAcIvBtn:
                this.finish();
                break;

            case R.id.changePasswordLlBtn:
                if (isPasswordValid(currentPasswordET)) {
                    if (isPasswordValid(newPasswordET)) {
                        if (isPasswordValid(confirmPasswordET)) {
                            if (isPasswordSame()) {
                                String oldPassword = currentPasswordET.getText().toString();
                                String newPassword = newPasswordET.getText().toString();
                                changeUserPassword(oldPassword, newPassword);
                            } else {
                                showSnackbar(changePasswordLlBtn, getString(R.string.password_mismatch), Constant.ERROR);
                            }
                        } else {
                            showSnackbar(changePasswordLlBtn, getString(R.string.password_mismatch), Constant.ERROR);
                        }
                    } else {
                        showSnackbar(changePasswordLlBtn, getString(R.string.please_enter_your_new_password), Constant.ERROR);
                    }
                } else {
                    showSnackbar(changePasswordLlBtn, getString(R.string.please_enter_your_existing_password), Constant.ERROR);
                }
                break;

        }
    }

    private void changeUserPassword(String oldPassword, String newPassword) {
        if (isNetworkAvailable()) {
            Log.d(TAG, "Inside changeUserPassword");
            jobManager.addJobInBackground(new ChangePasswordWebJob(accessToken, oldPassword, newPassword));
        } else {
            showSnackbar(changePasswordLlBtn, getString(R.string.no_internet), Constant.ERROR);
        }
    }

    @Subscribe
    public void onChangePasswordJobCompleted(HttpResponse httpResponseChangePw) {
        Log.d(TAG, "onChangePasswordJobCompleted inside");

        if (httpResponseChangePw != null) {
            if (httpResponseChangePw.getStatus() != null)
                if (httpResponseChangePw.getStatus().equals(Constant.SUCCESS)) {
                    Log.d(TAG, "onChangePasswordJobCompleted if");
                    showSnackbar(changePasswordLlBtn, getString(R.string.password_changed_success), Constant.SUCCESS);
                } else {
                    Log.d(TAG, "onChangePasswordJobCompleted else");
                    showSnackbar(changePasswordLlBtn, getString(R.string.password_cannot_be_changed_for_social_users), Constant.ERROR);
                }
        } else {
            Log.d(TAG, "onChangePasswordJobCompleted else else");
            showSnackbar(changePasswordLlBtn, getString(R.string.password_change_failed), Constant.ERROR);
        }
    }

    private boolean isPasswordSame() {
        return newPasswordET.getText().toString().equals(confirmPasswordET.getText().toString()) ? true : false;
    }

    private void showPassword(RelativeLayout rlEyeShowBtn, RelativeLayout rlEyeHideBtn, EditText passwordFeild) {
        Log.d(TAG, "showPasswordRlBtn");
        passwordFeild.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordFeild.setSelection(passwordFeild.getText().length());
        rlEyeHideBtn.setVisibility(View.VISIBLE);
        rlEyeShowBtn.setVisibility(View.GONE);
    }

    private void hidePassword(RelativeLayout rlEyeShowBtn, RelativeLayout rlEyeHideBtn, EditText passwordFeild) {
        Log.d(TAG, "hidePasswordRlBtn");
        passwordFeild.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordFeild.setSelection(passwordFeild.getText().length());
        rlEyeShowBtn.setVisibility(View.VISIBLE);
        rlEyeHideBtn.setVisibility(View.GONE);
    }

    @Override
    public void onFocusChange(View view, boolean b) {

        Log.d(TAG, "onFocusChange");

        switch (view.getId()) {
            case R.id.currentPasswordET:
                if (b)
                    enableCurrentPwET();
                break;
            case R.id.newPasswordET:
                if (b)
                    enableNewPwET();
                break;
            case R.id.confirmPasswordET:
                if (b)
                    enableConfirmPwET();
                break;
        }
    }

    private void enableCurrentPwET() {
        Log.d(TAG, "enableNameET");
        currentPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
        newPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        confirmPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    private void enableNewPwET() {
        Log.d(TAG, "enableEmailET");
        newPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
        currentPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        confirmPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    private void enableConfirmPwET() {
        Log.d(TAG, "enablePasswordET");
        confirmPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_bright_blue_border);
        currentPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
        newPasswordLlBg.setBackgroundResource(R.drawable.rounded_corners_lightgrey_border);
    }

    private Boolean isPasswordValid(EditText passwordEt) {
        return passwordEt.getText().toString().trim().length() > 0 ? true : false;
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
