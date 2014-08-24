package com.argon.foto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.achep.header2actionbar.HeaderFragmentSupportV4;
import com.argon.foto.R;

public class PhotographerHomeFragment extends HeaderFragmentSupportV4 {
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_NORMAL);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {
                height -= getActivity().getActionBar().getHeight();

                progress = (float) scroll / height;
                if (progress > 1f) progress = 1f;

                progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;
                ((PhotographerHomePage) getActivity())
                        .getFadingActionBarHelper()
                        .setActionBarAlpha((int) (255 * progress));
            }
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentTabHost tabHost = (FragmentTabHost)view.findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity(), getActivity().getSupportFragmentManager(), android.R.id.tabcontent);
        tabHost.addTab(tabHost.newTabSpec("WORKS").setIndicator("WORKS"), WorksFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("PRICING").setIndicator("PRICING"), PricingFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("ABOUT").setIndicator("ABOUT"), AboutFragment.class, null);
    }

    @Override
    public View onCreateContentOverlayView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.photographer_home_content, container, false);
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.photographer_home_header, container, false);
    }

}
