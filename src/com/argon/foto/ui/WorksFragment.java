package com.argon.foto.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.argon.foto.R;
import com.argon.foto.util.ImageCache;
import com.argon.foto.util.ImageFetcher;
import com.argon.foto.util.Utils;

public class WorksFragment extends Fragment {
    private ArrayList<Integer> mWorksDataSet = new ArrayList<Integer>();
    private GridView mWorks;
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
                convertView = getActivity().getLayoutInflater().inflate(R.layout.works_album_cover, parent, false);
            }
            ImageView cover = (ImageView) convertView.findViewById(R.id.cover);
            cover.setImageResource(mWorksDataSet.get(pos));
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
        mImageFetcher.setLoadingImage(R.drawable.empty_photo);
        mImageFetcher.addImageCache(getActivity().getFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_works, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWorks = (GridView)getView().findViewById(R.id.works);
        mWorks.setAdapter(mWorksAdapter);
        mock_loadWorks();
        mWorks.setDrawingCacheEnabled(false);
        mWorks.setOnScrollListener(new AbsListView.OnScrollListener() {
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
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
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
