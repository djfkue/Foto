package com.argon.foto.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.argon.foto.R;
import com.argon.foto.util.RecyclingBitmapDrawable;

/**
 * Created by argon on 14-8-14.
 */
public class FotoImageView extends ImageView {

    private float mDefaultRatio;
    private float mMaxRatio;

    public FotoImageView(Context context) {
        super(context);
    }

    public FotoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FotoImageView);
        mDefaultRatio = a.getFloat(R.styleable.FotoImageView_ratio, -1.0f);
        mMaxRatio = a.getFloat(R.styleable.FotoImageView_maxRatio, -1.0f);
    }

    public FotoImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FotoImageView);
        mDefaultRatio = a.getFloat(R.styleable.FotoImageView_ratio, -1.0f);
        mMaxRatio = a.getFloat(R.styleable.FotoImageView_maxRatio, -1.0f);
    }


    /**
     * @see android.widget.ImageView#onDetachedFromWindow()
     */
    @Override
    protected void onDetachedFromWindow() {
        // This has been detached from Window, so clear the drawable
        setImageDrawable(null);

        super.onDetachedFromWindow();
    }

    /**
     * @see android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable)
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        // Keep hold of previous Drawable
        final Drawable previousDrawable = getDrawable();

        // Call super to set new Drawable
        super.setImageDrawable(drawable);

        // Notify new Drawable that it is being displayed
        notifyDrawable(drawable, true);

        // Notify old Drawable so it is no longer being displayed
        notifyDrawable(previousDrawable, false);
    }

    /**
     * Notifies the drawable that it's displayed state has changed.
     *
     * @param drawable
     * @param isDisplayed
     */
    private static void notifyDrawable(Drawable drawable, final boolean isDisplayed) {
        if (drawable instanceof RecyclingBitmapDrawable) {
            // The drawable is a CountingBitmapDrawable, so notify it
            ((RecyclingBitmapDrawable) drawable).setIsDisplayed(isDisplayed);
        } else if (drawable instanceof LayerDrawable) {
            // The drawable is a LayerDrawable, so recurse on each layer
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            for (int i = 0, z = layerDrawable.getNumberOfLayers(); i < z; i++) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mDefaultRatio <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        if (getDrawable() != null) {
            mDefaultRatio = ((float) getDrawable().getIntrinsicHeight()) / getDrawable().getIntrinsicWidth();
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mMaxRatio > 0 && mDefaultRatio >= mMaxRatio) {
            mDefaultRatio = mMaxRatio;
        }
        int height = (int) (width * mDefaultRatio);
        setMeasuredDimension(width, height);
    }
}
