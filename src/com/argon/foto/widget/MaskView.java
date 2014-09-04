package com.argon.foto.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.argon.foto.R;

/**
 * TODO: document your custom view class.
 */
public class MaskView extends View {

    private int mMaskColor = Color.WHITE;

    private Paint mPaint;

    private float mRadius;
    private float mCenterX;
    private float mCenterY;

    public MaskView(Context context) {
        super(context);
        init(null, 0);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MaskView, defStyle, 0);

        mMaskColor = a.getColor(
                R.styleable.MaskView_maskColor,
                mMaskColor);

        if (mPaint == null) {
            mPaint = new Paint();
            mPaint.setColor(mMaskColor);
            mPaint.setStrokeWidth(1);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mPaint.setAntiAlias(true);
        }

        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
    }

    public int getMaskColor() {
        return mMaskColor;
    }

    public void setMaskColor(int maskColor) {
        mMaskColor = maskColor;
        invalidate();
    }

    public float getRadius() {
        return mRadius;
    }

    public void setRadius(float radius) {
        mRadius = radius;
        invalidate();
    }

    public void setCenter(float cx, float cy) {
        mCenterX = cx;
        mCenterY = cy;
        invalidate();
    }

    public void setCenterX(float centerX) {
        mCenterX = centerX;
        invalidate();
    }

    public void setCenterY(float centerY) {
        mCenterY = centerY;
        invalidate();
    }
}
