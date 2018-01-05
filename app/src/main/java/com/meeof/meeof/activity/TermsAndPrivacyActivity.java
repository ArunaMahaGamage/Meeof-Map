package com.meeof.meeof.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.meeof.meeof.R;

public class TermsAndPrivacyActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout closeRlBtn;
    private WebView termsOfServiceWv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        initViews();
    }

    private void initViews() {
        closeRlBtn = (RelativeLayout)findViewById(R.id.closeRlBtn);

        termsOfServiceWv = (WebView)findViewById(R.id.termsOfServiceWv);

        termsOfServiceWv.loadUrl(getString(R.string.terms_of_use_link));

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
