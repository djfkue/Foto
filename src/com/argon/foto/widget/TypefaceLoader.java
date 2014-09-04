package com.argon.foto.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TypefaceLoader {
    private static final String TAG = "TypefaceLoader";
    private static TypefaceLoader mInstance;

    private Map<String, Typeface> mTypefaceMap = new HashMap<String, Typeface>();

    public static TypefaceLoader getInstance() {
        if (mInstance == null) {
            mInstance = new TypefaceLoader();
        }
        return mInstance;
    }

    public Typeface getTypeface(Context context, String font) {
        if (font == null) {
            return null;
        }
        Typeface typeface = mTypefaceMap.get(font);
        if (typeface == null) {
            try {
                typeface = Typeface.createFromAsset(context.getAssets(), font);
            } catch (Exception e) {
                Log.e(TAG, "Could not get typeface: " + e.getMessage());
                return null;
            }
            mTypefaceMap.put(font, typeface);
        }
        return typeface;
    }
}
