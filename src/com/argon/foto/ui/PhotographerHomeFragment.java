package com.argon.foto.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.achep.header2actionbar.HeaderFragmentSupportV4;
import com.argon.foto.R;

public class PhotographerHomeFragment extends HeaderFragmentSupportV4 {
    private final static String TAG = "PhotographerHomeFragment";
    private TextView mNameOnActionBar;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mNameOnActionBar = (TextView) activity.getActionBar().getCustomView().findViewById(R.id.name);
        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_NORMAL);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {
                int actionBarHeight = getActivity().getActionBar().getHeight();
                height -= actionBarHeight;

                progress = (float) scroll / height;
                if (progress > 1f) progress = 1f;

                progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;
                int alphaOfActionbarBg = (int) (255 * progress);
                ((PhotographerHomePage) getActivity())
                        .getFadingActionBarHelper()
                        .setActionBarAlpha(alphaOfActionbarBg);

                int remainScrollSpace = height - scroll;
                Log.i(TAG, "scroll:" + scroll + "height:" + height + ", remain:" + remainScrollSpace);
                if((remainScrollSpace >=0) && (remainScrollSpace <= actionBarHeight)) {
                    float nameAlphaProgress = (float)(actionBarHeight - remainScrollSpace) / actionBarHeight;
                    Log.i(TAG, "nameAlphaProgress:" + nameAlphaProgress);
                    mNameOnActionBar.setAlpha(nameAlphaProgress);
                } else {
                    mNameOnActionBar.setAlpha(0);
                }
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
