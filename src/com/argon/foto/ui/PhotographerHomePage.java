package com.argon.foto.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;

import com.achep.header2actionbar.FadingActionBarHelper;
import com.argon.foto.R;

public class PhotographerHomePage extends FragmentActivity {
    private FadingActionBarHelper mFadingActionBarHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SEAN", "PhotographerHomePage onCreate ....");
        setContentView(R.layout.photographer_home_activity);
        setupActionbar();

        mFadingActionBarHelper = new FadingActionBarHelper(getActionBar(),
                getResources().getDrawable(R.drawable.actionbar_bg));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PhotographerHomeFragment())
                    .commit();
        }
    }

    @SuppressLint("InflateParams")
    private void setupActionbar() {
        // setup action bar
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View v = getLayoutInflater().inflate(R.layout.photo_grapher_home_custom_action_bar, null);
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
                startActivity(new Intent(PhotographerHomePage.this, PhotographerHomePage2.class));
            }
        });
    }

    public FadingActionBarHelper getFadingActionBarHelper() {
        return mFadingActionBarHelper;
    }
}