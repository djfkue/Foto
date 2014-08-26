package com.argon.foto.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.achep.header2actionbar.HeaderFragmentV2;
import com.argon.foto.R;
import com.argon.foto.util.ImageCache;
import com.argon.foto.util.ImageFetcher;

public class PhotographerHomeFragment2 extends HeaderFragmentV2 {
    private final static String TAG = "PhotographerHomeFragment";
    private TextView mNameOnActionBar;
    private View mNameGroupOnHeader;

    private ListView mListView;
    private ArrayList<Integer> mWorksDataSet = new ArrayList<Integer>();
    private ImageFetcher mImageFetcher;
    private static final String IMAGE_CACHE_DIR = "covers";
    private BaseAdapter mWorksAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mWorksDataSet.size();
        }

        @Override
        public Object getItem(int pos) {
            return mWorksDataSet.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.works_item, parent, false);
            }
            ImageView cover = (ImageView) convertView.findViewById(R.id.cover);
            mImageFetcher.loadImage(mWorksDataSet.get(pos), cover);

            TextView picCount = (TextView) convertView.findViewById(R.id.pic_count);
            picCount.setText("12");
            return convertView;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory
        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), 720);
        mImageFetcher.setLoadingImage(R.drawable.photographer_empty);
        mImageFetcher.addImageCache(getActivity().getFragmentManager(), cacheParams);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mNameOnActionBar = (TextView) activity.getActionBar().getCustomView().findViewById(R.id.name);
        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {
                int actionBarHeight = getActivity().getActionBar().getHeight();
                height -= actionBarHeight;

                progress = (float) scroll / height;
                if (progress > 1f) progress = 1f;

                progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;
                int alphaOfActionbarBg = (int) (255 * progress);
                ((PhotographerHomePage2) getActivity())
                        .getFadingActionBarHelper()
                        .setActionBarAlpha(alphaOfActionbarBg);

                int nameGroupHeight = mNameGroupOnHeader.getHeight();
                int remainScrollSpace = height - scroll;
                Log.i(TAG, "scroll:" + scroll + "height:" + height + ", remain:" + remainScrollSpace);
                if((remainScrollSpace >=0) && (remainScrollSpace <= nameGroupHeight)) {
                    float nameAlphaProgress = (float)(nameGroupHeight - remainScrollSpace) / nameGroupHeight;
                    Log.i(TAG, "nameAlphaProgress:" + nameAlphaProgress);
                    nameAlphaProgress = (1 - (float) Math.cos(nameAlphaProgress * Math.PI)) * 0.5f;
                    mNameOnActionBar.setAlpha(nameAlphaProgress);
                    mNameGroupOnHeader.setAlpha(1 - nameAlphaProgress);
                } else if(remainScrollSpace < 0){
                    mNameOnActionBar.setAlpha(1);
                    mNameGroupOnHeader.setAlpha(0);
                } else {
                    mNameOnActionBar.setAlpha(0);
                    mNameGroupOnHeader.setAlpha(1);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
        mWorksAdapter.notifyDataSetChanged();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = (ListView)getView().findViewById(R.id.works_and_pricing);
        mListView.setDrawingCacheEnabled(false);
        setListViewAdapter(mListView, mWorksAdapter);
        mock_loadWorks();
    }

    @Override
    public View onCreateContentOverlayView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.photographer_home_content2, container, false);
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
        View headerView = inflater.inflate(R.layout.photographer_home_header, container, false);
        mNameGroupOnHeader = headerView.findViewById(R.id.name_and_follow);
        return headerView;
    }

    private void mock_loadWorks() {
        mWorksDataSet.clear();
        mWorksDataSet.add(R.drawable.cover_01);
        mWorksDataSet.add(R.drawable.cover_02);
        mWorksDataSet.add(R.drawable.cover_03);
        mWorksDataSet.add(R.drawable.cover_04);
        mWorksDataSet.add(R.drawable.cover_05);
        mWorksDataSet.add(R.drawable.cover_06);
        mWorksDataSet.add(R.drawable.cover_07);
        mWorksAdapter.notifyDataSetChanged();
    }

}
