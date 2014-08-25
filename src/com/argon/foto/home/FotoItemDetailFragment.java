package com.argon.foto.home;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.argon.foto.R;

import com.argon.foto.home.dummy.DummyContent;
import com.argon.foto.ui.PhotographerHomePage;
import com.argon.foto.util.ImageFetcher;
import com.argon.foto.util.ImageWorker;
import com.argon.foto.util.Utils;

/**
 * A fragment representing a single FotoItem detail screen.
 * This fragment is either contained in a {@link FotoItemListActivity}
 * in two-pane mode (on tablets) or a {@link FotoItemDetailActivity}
 * on handsets.
 */
public class FotoItemDetailFragment extends Fragment {

    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String IMAGE_THUMB_DATA_EXTRA = "extra_thumb_image_data";
    private String mImageUrl;
    private String mThumbUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static FotoItemDetailFragment newInstance(String imageUrl, String thumbUrl) {
        final FotoItemDetailFragment f = new FotoItemDetailFragment();

        Log.e("SD_TRACE", "newInstance... ... ... imageUrl: " + imageUrl);
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putString(IMAGE_THUMB_DATA_EXTRA, thumbUrl);
        f.setArguments(args);

        return f;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FotoItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() == null) {
            Log.e("SD_TRACE", "should not get null arguments...");
        } else {
            Log.e("SD_TRACE", "getImageUrl: " + getArguments().getString(IMAGE_DATA_EXTRA));
        }
        mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;
        mThumbUrl = getArguments() != null ? getArguments().getString(IMAGE_THUMB_DATA_EXTRA) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.foto_detail_fragment, container, false);

        mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        mImageFetcher = ((FotoItemDetailActivity) getActivity()).getImageFetcher();

        rootView.findViewById(R.id.foto_grapher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PhotographerHomePage.class));
            }
        });

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (FotoItemDetailActivity.class.isInstance(getActivity())) {
            mImageFetcher = ((FotoItemDetailActivity) getActivity()).getImageFetcher();

            //mImageView.setImageBitmap(mImageFetcher.getImageCache().getBitmapFromDiskCache(mThumbUrl));
            Log.e("SD_TRACE", "loadImage... ... ... mImageUrl: " + mImageUrl);
            mImageFetcher.setImageFadeIn(true);
            mImageFetcher.loadImage(mImageUrl, mImageView);
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (View.OnClickListener.class.isInstance(getActivity()) && Utils.hasHoneycomb()) {
            mImageView.setOnClickListener((View.OnClickListener) getActivity());
        }
        if (savedInstanceState == null) {
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);

                    Log.e("SD_TRACE", "screenLocation: " + screenLocation[0] + ", " + screenLocation[1]);
                    Log.e("SD_TRACE", "screenHeight: " + mImageView.getHeight());

                    return true;
                }
            });
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }
}
