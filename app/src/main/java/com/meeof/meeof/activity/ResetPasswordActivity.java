package com.meeof.meeof.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener{

    private RelativeLayout backIvBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initViews();
    }

    private void initViews() {
        backIvBtn = (RelativeLayout)findViewById(R.id.backIvBtn);

        backIvBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backIvBtn:
                this.onBackPressed();
                break;
        }
    }
}
