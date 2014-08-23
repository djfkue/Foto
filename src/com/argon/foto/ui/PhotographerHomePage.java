package com.argon.foto.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;

import com.argon.foto.R;

public class PhotographerHomePage extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photographer_home);
        setupActionbar();

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("WORKS").setIndicator("WORKS"), WorksFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("PRICING").setIndicator("PRICING"), PricingFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("ABOUT").setIndicator("ABOUT"), AboutFragment.class, null);
    }

    private void setupActionbar() {
        // setup action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View v = getLayoutInflater().inflate(R.layout.custom_action_bar, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, layout);

        v.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        v.findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: to be implemented
            }
        });
    }
}