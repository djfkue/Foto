package com.argon.foto.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.argon.foto.R;

import com.argon.foto.home.dummy.DummyContent;
import com.argon.foto.provider.Images;
import com.argon.foto.util.ImageCache;
import com.argon.foto.util.ImageFetcher;
import com.argon.foto.util.ImageUtil;
import com.argon.foto.util.Utils;
import com.argon.foto.widget.FotoImageView;
import com.argon.foto.widget.MaskView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * A list fragment representing a list of FotoItems. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link FotoItemDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class FotoItemListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    private static final String PACKAGE = "com.argon.foto.home";

    private static final String IMAGE_CACHE_DIR = "thumbs";
    public static final int FOTO_ITEM_POSITION = 0x01;

    private ImageFetcher mImageFetcher;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private int mLastFirstVisibleItem;
    private int mLastFistVisibleItemTop;
    private boolean mIsScrollingUp;
    private MaskView mMaskView;
    private Intent mCurrentFotoIntent;
    private float mCurrentPressX;
    private float mCurrentPressY;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(String id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(String id) {
        }
    };

    private ImageAdapter mImageAdatper;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FotoItemListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("SD_TRACE", "onCreate..................................................");
        // TODO: replace with a real list adapter.
//        setListAdapter(new ArrayAdapter<DummyContent.DummyItem>(
//                getActivity(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                DummyContent.ITEMS));

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;

        final int longest = height > width ? height : width;

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), longest);
        //mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getFragmentManager(), cacheParams);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        int mActionBarHeight = 0;
        if (getActivity().getTheme().resolveAttribute(
                android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getResources().getDisplayMetrics());
        }
        View headerHolder = new View(getActivity());
        headerHolder.setLayoutParams(new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));

        getListView().addHeaderView(headerHolder);

        mImageAdatper = new ImageAdapter(getActivity());
        setListAdapter(mImageAdatper);

        getListView().setDrawingCacheEnabled(false);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    if (!Utils.hasHoneycomb()) {
                        mImageFetcher.setPauseWork(true);
                    }
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                final ListView lw = getListView();

                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        mIsScrollingUp = false;
                        getActivity().getActionBar().hide();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        mIsScrollingUp = true;
                        getActivity().getActionBar().show();
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });
        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        mMaskView = (MaskView) view.findViewById(R.id.mask_view);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMaskView.setVisibility(View.INVISIBLE);
        mMaskView.setRadius(0);
        mImageFetcher.setExitTasksEarly(false);
        mImageAdatper.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.foto_list_layout, container, false);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mCurrentFotoIntent = new Intent(getActivity(), FotoItemDetailActivity.class);
        mCurrentFotoIntent.putExtra(FotoItemDetailActivity.EXTRA_IMAGE, (int) id);

        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        int orientation = getResources().getConfiguration().orientation;

        mCurrentFotoIntent.putExtra(PACKAGE + ".orientation", orientation).
                putExtra(PACKAGE + ".left", screenLocation[0]).
                putExtra(PACKAGE + ".top", screenLocation[1]).
                putExtra(PACKAGE + ".width", view.getWidth()).
                putExtra(PACKAGE + ".height", view.getHeight());

        runMaskAnimation();

    }

    private void runMaskAnimation() {
        mMaskView.setAlpha(0);
        mMaskView.setVisibility(View.VISIBLE);
        mMaskView.setCenter(mCurrentPressX, mCurrentPressY);

        getActivity().getActionBar().hide();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mMaskView, "alpha", 0, 1);
        alphaAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimator.setDuration(500);
        ObjectAnimator maskAnimator = ObjectAnimator.ofFloat(mMaskView, "radius", 32.0f, mMaskView.getHeight());
        maskAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        maskAnimator.setDuration(500);

        AnimatorSet maskAnim = new AnimatorSet();
        maskAnim.play(alphaAnimator).with(maskAnimator);

        maskAnim.start();
        maskAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                getActivity().startActivityForResult(mCurrentFotoIntent, FotoItemListActivity.CURRENT_FOTO);

                // Override transitions: we don't want the normal window animation in addition
                // to our custom one
                getActivity().overridePendingTransition(0, 0);
//                        mMaskView.setVisibility(View.GONE);
//                        mMaskView.setRadius(0);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }

    private class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        private int mLastPosition = -1;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
        }

        @Override
        public int getCount() {
            return Images.imageThumbUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return Images.imageThumbUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView;
            ViewGroup itemView = (ViewGroup)convertView;
            if (convertView == null) { // if it's not recycled, initialize some attributes
                ViewHolder holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = (ViewGroup)inflater.inflate(R.layout.foto_list_item, container, false);
                imageView = (ImageView)itemView.findViewById(R.id.foto_image);
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            mCurrentPressX = motionEvent.getRawX();
                            mCurrentPressY = motionEvent.getRawY();
                        }
                        return false;
                    }
                });
                holder.photo = imageView;
                itemView.setTag(holder);
            } else {
                //imageView = (ImageView)itemView.findViewById(R.id.foto_image);
                imageView = ((ViewHolder)convertView.getTag()).photo;
                convertView.clearAnimation();
            }

            mImageFetcher.loadImage(Images.imageThumbUrls[position], imageView);
            //imageView.setImageResource(R.drawable.mock_andywilliams);

            // animate the item
            TranslateAnimation animation = null;
            if (position > mLastPosition) {
                animation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF,
                        0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.3f,
                        Animation.RELATIVE_TO_SELF, 0.0f);

                animation.setDuration(600);
                itemView.startAnimation(animation);
            }

            mLastPosition = position;
            return itemView;
        }

        class ViewHolder {
            ImageView photo;
            int position;
        }
    }
}
