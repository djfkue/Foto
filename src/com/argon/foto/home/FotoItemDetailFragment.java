package com.argon.foto.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

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
    private static final String SYSTEM_UI_LOW_PROFILE = "system_ui_low_profile";
    private String mImageUrl;
    private String mThumbUrl;
    private ImageView mImageView;
    private ImageFetcher mImageFetcher;

    private boolean mShouldRunAnim = false;
    private boolean mSystemUILowProfile = false;

    private View mFotoInfoContainer;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static FotoItemDetailFragment newInstance(String imageUrl, String thumbUrl, boolean shouldRunAnim, boolean systemUILowProfile) {
        final FotoItemDetailFragment f = new FotoItemDetailFragment();

        Log.e("SD_TRACE", "newInstance... ... ... imageUrl: " + imageUrl);
        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putString(IMAGE_THUMB_DATA_EXTRA, thumbUrl);
        args.putBoolean(RUN_ENTER_ANIM, shouldRunAnim);
        args.putBoolean(SYSTEM_UI_LOW_PROFILE, systemUILowProfile);
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
        mSystemUILowProfile = getArguments() != null ? getArguments().getBoolean(SYSTEM_UI_LOW_PROFILE) : false;
        Log.e("SD_TRACE", "should run anim: " + mShouldRunAnim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.foto_detail_fragment, container, false);

        mFotoInfoContainer = rootView.findViewById(R.id.foto_info_container);

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

            mImageView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int vis) {
                    if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                        hideFotoInfo();
                    } else {
                        showFotoInfo();
                    }
                }
            });

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
                        runEnterAnimation(new Runnable() {
                            @Override
                            public void run() {
                                mFotoInfoContainer.setVisibility(View.VISIBLE);
                                mShouldRunAnim = false;
                            }
                        });
                    } else {
                        Log.e("SD_TRACE", "set visible ................");
                        if (mSystemUILowProfile) {
                            rootView.findViewById(R.id.foto_info_container).setVisibility(View.INVISIBLE);
                        } else {
                            rootView.findViewById(R.id.foto_info_container).setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }
            });
        }
        return rootView;
    }

    private void showFotoInfo() {
        mFotoInfoContainer.setVisibility(View.VISIBLE);
        mFotoInfoContainer.animate().alpha(1).translationY(getView().getHeight() - mFotoInfoContainer.getHeight()).
                withLayer().setDuration(300).setInterpolator(new AccelerateDecelerateInterpolator());
    }

    private void hideFotoInfo() {
        mFotoInfoContainer.animate().alpha(0).
                translationY(getView().getHeight()).
                withLayer().
                setDuration(500).
                setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void runEnterAnimation(final  Runnable runnable) {

        mImageView.setTranslationX(getView().getWidth());

        mFotoInfoContainer.setTranslationY(getView().getHeight());
        mFotoInfoContainer.setVisibility(View.VISIBLE);

        TextView actionInfo = (TextView) mFotoInfoContainer.findViewById(R.id.action_info);
        TextView actionShare = (TextView) mFotoInfoContainer.findViewById(R.id.action_share);
        TextView actionFavor = (TextView) mFotoInfoContainer.findViewById(R.id.action_favor);

        actionInfo.setAlpha(0);
        actionShare.setAlpha(0);
        actionFavor.setAlpha(0);

        int duration = 300;
        ObjectAnimator infoFadeInAnim = ObjectAnimator.ofFloat(actionInfo, "alpha", 0f, 1f);
        infoFadeInAnim.setDuration(300);
        ObjectAnimator shareFadeInAnim = ObjectAnimator.ofFloat(actionShare, "alpha", 0f, 1f);
        shareFadeInAnim.setDuration(200);
        ObjectAnimator favorFadeInAnim = ObjectAnimator.ofFloat(actionFavor, "alpha", 0f, 1f);
        favorFadeInAnim.setDuration(100);

        final AnimatorSet actionAnimSet = new AnimatorSet();
        actionAnimSet.play(favorFadeInAnim).after(shareFadeInAnim).after(infoFadeInAnim);
        actionAnimSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mImageView.animate().translationX(0).alpha(1).setDuration(500);
        mFotoInfoContainer.animate().translationY(getView().getHeight() - mFotoInfoContainer.getHeight()).alpha(1).
                setDuration(500).withEndAction(new Runnable() {
            @Override
            public void run() {
                actionAnimSet.start();
            }
        });

    }

    public void runExitAnimation(Runnable runnable) {

        final View fotoInfoContainer = getView().findViewById(R.id.foto_info_container);
        fotoInfoContainer.setVisibility(View.VISIBLE);

        mImageView.animate().translationX(getView().getWidth()).alpha(0).setDuration(500);
        fotoInfoContainer.animate().translationY(getView().getHeight()).alpha(0).
                setDuration(500).withEndAction(runnable);

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
