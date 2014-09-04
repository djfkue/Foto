package com.argon.foto.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.argon.foto.R;

/**
 * Created by argon on 14-8-15.
 */

public class FotoTextView extends TextView {
    private static final String TAG = "TextView";

    public FotoTextView(Context context) {
        super(context);
    }

    public FotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public FotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.FotoTextView);
        String customFont = a.getString(R.styleable.FotoTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
//        try {
//            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
//        } catch (Exception e) {
//            Log.e(TAG, "Could not get typeface: "+e.getMessage());
//            return false;
//        }

        tf = TypefaceLoader.getInstance().getTypeface(ctx, asset);
        setTypeface(tf);
        return true;
    }

}