package com.argon.foto.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by yilily on 14-8-23.
 */
public class FotoListView extends ListView {

    private int mInitialScroll;
    private OnScrollListener mOnScrollCallback;

    public FotoListView(Context context) {
        super(context);
    }

    public FotoListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FotoListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (mOnScrollCallback != null) {
                    mOnScrollCallback.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrolledOffset = computeVerticalScrollOffset();

                boolean scrollUp = scrolledOffset > mInitialScroll;
                Log.e("SD_TRACE", "scrollUp: " + scrollUp);
                mInitialScroll = scrolledOffset;

                if (mOnScrollCallback != null) {
                    mOnScrollCallback.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });
    }

    @Override
    public void setOnScrollListener(OnScrollListener onScrollListener) {
        mOnScrollCallback = onScrollListener;
    }
}
