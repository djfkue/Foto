package com.argon.foto.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.View;

import com.argon.foto.R;

public class PhotographerHomePage extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photographer_home);
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotographerHomePage.this.finish();
            }
        });
        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: to be implemented
            }
        });

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("WORKS").setIndicator("WORKS"), WorksFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("PRICING").setIndicator("PRICING"), WorksFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("ABOUT").setIndicator("ABOUT"), WorksFragment.class, null);
    }
}