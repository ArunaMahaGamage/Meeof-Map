package com.meeof.meeof.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;

public class PrivacyPolicyActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout closeRlBtn;
    private WebView privacyPolicyWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        initViews();
    }

    private void initViews() {
        closeRlBtn = (RelativeLayout)findViewById(R.id.closeRlBtn);
        privacyPolicyWv = (WebView)findViewById(R.id.privacyPolicyWv);

        privacyPolicyWv.loadUrl(getString(R.string.privacy_policy_link));
        closeRlBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.closeRlBtn:
                this.finish();
                break;
        }
    }
}
