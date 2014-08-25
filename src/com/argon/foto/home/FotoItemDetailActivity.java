package com.argon.foto.home;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.argon.foto.BuildConfig;
import com.argon.foto.R;
import com.argon.foto.provider.Images;
import com.argon.foto.util.ImageCache;
import com.argon.foto.util.ImageFetcher;
import com.argon.foto.util.Utils;

/**
 * An activity representing a single FotoItem detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FotoItemListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link FotoItemDetailFragment}.
 */
public class FotoItemDetailActivity extends Activity implements View.OnClickListener {

    private static final String IMAGE_CACHE_DIR = "images";
    public static final String EXTRA_IMAGE = "extra_image";

    private static final String PACKAGE_NAME = "com.argon.foto.home";

    private ImageFetcher mImageFetcher;
    private ViewGroup mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            Utils.enableStrictMode();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotoitem_detail);

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = height > width ? height : width;

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(this, longest);
        mImageFetcher.addImageCache(getFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(true);

        mFragmentContainer = (ViewGroup) findViewById(R.id.fotoitem_detail_container);

        // Set up activity to go full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
        if (Utils.hasHoneycomb()) {
            final ActionBar actionBar = getActionBar();

            // Hide title text and set home as up
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayShowHomeEnabled(false);

            // Hide and show the ActionBar as the visibility changes
            mFragmentContainer.setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int vis) {
                            if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                                actionBar.hide();
                            } else {
                                actionBar.show();
                            }
                        }
                    });

            // Start low profile mode and hide ActionBar
            //mFragmentContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            //actionBar.hide();
        }

        // Retrieve the data we need for the picture/description to display and
        // the thumbnail to animate it from
        Bundle bundle = getIntent().getExtras();
        String description = bundle.getString(PACKAGE_NAME + ".description");
        final int thumbnailTop = bundle.getInt(PACKAGE_NAME + ".top");
        final int thumbnailLeft = bundle.getInt(PACKAGE_NAME + ".left");
        final int thumbnailWidth = bundle.getInt(PACKAGE_NAME + ".width");
        final int thumbnailHeight = bundle.getInt(PACKAGE_NAME + ".height");

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            final int extraCurrentItem = getIntent().getIntExtra(EXTRA_IMAGE, -1);
            Bundle arguments = new Bundle();

            FotoItemDetailFragment fragment = FotoItemDetailFragment.newInstance(Images.imageUrls[extraCurrentItem], Images.imageThumbUrls[extraCurrentItem]);
            getFragmentManager().beginTransaction()
                    .add(R.id.fotoitem_detail_container, fragment)
                    .commit();
        }
    }

    /**
     * Called by the fragments to load images via the one ImageFetcher
     */
    public ImageFetcher getImageFetcher() {
        return mImageFetcher;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            // navigateUpTo(new Intent(this, FotoItemListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public void onClick(View view) {
        final int vis = mFragmentContainer.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mFragmentContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mFragmentContainer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
