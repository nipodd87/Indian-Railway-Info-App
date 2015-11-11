package com.nitz.studio.indianrailwayinfo;


import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nitinpoddar on 11/6/15.
 */
public class BaseActivity extends ActionBarActivity {
    public TextView app_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actionbar_layout);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowHomeEnabled(false);

            View mActionBarView = getLayoutInflater().inflate(R.layout.actionbar_layout, null);
            actionBar.setCustomView(mActionBarView);
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }
}
