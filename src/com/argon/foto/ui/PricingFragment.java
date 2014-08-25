package com.argon.foto.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.argon.foto.R;
import com.argon.foto.util.ImageCache;
import com.argon.foto.util.ImageFetcher;

public class PricingFragment extends Fragment {
    private static final String IMAGE_CACHE_DIR = "covers";
    private ListView mPricingList;
    private class MockPricingData {
        public MockPricingData(int res, String title, String subtitle, float pricing) {
            this.coverRes = res;
            this.title = title;
            this.subTitle = subtitle;
            this.pricing = pricing;
        }
        public int coverRes;
        public String title;
        public String subTitle;
        public float pricing;
    };
    private ArrayList<MockPricingData> mMockPricingData = new ArrayList<MockPricingData>();
    private ImageFetcher mImageFetcher;

    private BaseAdapter mWorksAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mMockPricingData.size();
        }

        @Override
        public Object getItem(int pos) {
            return mMockPricingData.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return 0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.pricing_item, parent, false);
            }
            ImageView cover = (ImageView) convertView.findViewById(R.id.cover);
            mImageFetcher.loadImage(mMockPricingData.get(pos).coverRes, cover);

            TextView title = (TextView) convertView.findViewById(R.id.title);
            title.setText(mMockPricingData.get(pos).title);

            TextView subtitle = (TextView) convertView.findViewById(R.id.sub_title);
            subtitle.setText(mMockPricingData.get(pos).subTitle);

            TextView pricing = (TextView) convertView.findViewById(R.id.pricing);
            pricing.setText("￥" + String.valueOf(mMockPricingData.get(pos).pricing));
            return convertView;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMockPricingData.add(new MockPricingData(R.drawable.cover_01, "户外写真", "30张送8寸影集一本", 1200));
        mMockPricingData.add(new MockPricingData(R.drawable.cover_02, "旅游跟拍", "30张送8寸影集一本", 2000));
        mMockPricingData.add(new MockPricingData(R.drawable.cover_03, "旅游跟拍", "30张送8寸影集一本", 2000));
        mMockPricingData.add(new MockPricingData(R.drawable.cover_04, "旅游跟拍", "30张送8寸影集一本", 2000));
        mMockPricingData.add(new MockPricingData(R.drawable.cover_05, "旅游跟拍", "30张送8寸影集一本", 2000));
        mMockPricingData.add(new MockPricingData(R.drawable.cover_06, "旅游跟拍", "30张送8寸影集一本", 2000));
        mMockPricingData.add(new MockPricingData(R.drawable.cover_07, "旅游跟拍", "30张送8寸影集一本", 2000));
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
        View rootView = inflater.inflate(R.layout.fragment_pricing, container, false);
        mPricingList = (ListView) rootView.findViewById(R.id.pricing_list);
        mPricingList.setAdapter(mWorksAdapter);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
}
