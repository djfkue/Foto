package com.argon.foto.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.argon.foto.R;

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
    private static final String RUN_ENTER_ANIM = "run_enter_animation";
    private String mImageUrl;
    private String mThumbUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    private boolean mShouldRunAnim = false;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static FotoItemDetailFragment newInstance(String imageUrl, String thumbUrl, boolean shouldRunAnim) {
        final FotoItemDetailFragment f = new FotoItemDetailFragment();

        Log.e("SD_TRACE", "newInstance... ... ... imageUrl: " + imageUrl);
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putString(IMAGE_THUMB_DATA_EXTRA, thumbUrl);
        args.putBoolean(RUN_ENTER_ANIM, shouldRunAnim);
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
        mShouldRunAnim = getArguments() != null ? getArguments().getBoolean(RUN_ENTER_ANIM) : false;
        Log.e("SD_TRACE", "should run anim: " + mShouldRunAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.foto_detail_fragment, container, false);

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
                    if (mShouldRunAnim) {
                        runEnterAnimation();
                    } else {
                        Log.e("SD_TRACE", "set visible ................");
                        rootView.findViewById(R.id.foto_info_container).setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });
        }
        return rootView;
    }

    public void runEnterAnimation() {
        Animation imageInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.foto_in);

        imageInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mImageView.startAnimation(imageInAnim);

        Animation fotoInfoAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.fotographer_in);
        fotoInfoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getView().findViewById(R.id.foto_info_container).setVisibility(View.VISIBLE);
                mShouldRunAnim = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        getView().findViewById(R.id.foto_info_container).startAnimation(fotoInfoAnim);
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

    @Override
    public void onResume() {
        super.onResume();
        if (!mShouldRunAnim) {
            getView().findViewById(R.id.foto_info_container).setVisibility(View.VISIBLE);
        }
    }
}
