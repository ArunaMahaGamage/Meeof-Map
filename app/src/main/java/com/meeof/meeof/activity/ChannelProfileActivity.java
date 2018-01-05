package com.meeof.meeof.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.meeof.meeof.R;

/**
 * Created by Dharmesh on 11/28/2017.
 */

public class ChannelProfileActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_profile);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
