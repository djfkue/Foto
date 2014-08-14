package com.argon.foto.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by argon on 14-8-14.
 */
public class FotoImageView extends ImageView {
    public FotoImageView(Context context) {
        super(context);
    }

    public FotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FotoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
}
