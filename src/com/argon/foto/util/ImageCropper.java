package com.argon.foto.util;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.util.Log;

public class ImageCropper {
    private final static String TAG = "ImageCropper";
    public static Bitmap cropImage(InputStream is, int frameWidth, int frameHeight, int flag) {
        if(is == null || frameWidth < 0 || frameHeight < 0 || (frameWidth >= frameHeight) ) {
            throw new IllegalStateException();
        }
        long startTimeMs = System.currentTimeMillis();
        Bitmap croppedImage = null;
        is.mark(0);

        BitmapFactory.Options boundOption = new BitmapFactory.Options();
        boundOption.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, boundOption);
        Log.i(TAG, "original width:" + boundOption.outWidth + ", height:" + boundOption.outHeight);

        float frameAspectRatio = (float)frameWidth / frameHeight;
        float imageAspectRatio = (float)boundOption.outWidth / boundOption.outHeight;
        float scaleRate = 0f;
        Rect cropRegion = null;
        int cropWidth = boundOption.outWidth, cropHeight = boundOption.outHeight;
        Log.i(TAG, "frameAspectRatio:" + frameAspectRatio + ", imageAspectRatio:" + imageAspectRatio);
        if(frameAspectRatio >= imageAspectRatio) {
            cropHeight = (int)(boundOption.outWidth / frameAspectRatio);
            cropRegion = new Rect(0, (boundOption.outHeight - cropHeight) / 2, boundOption.outWidth, (boundOption.outHeight + cropHeight) / 2);
        } else {
            cropWidth = (int)(boundOption.outHeight * frameAspectRatio);
            cropRegion = new Rect((boundOption.outWidth - cropWidth) / 2, 0, (boundOption.outWidth + cropWidth) / 2, boundOption.outHeight);
        }
        scaleRate = (float)cropWidth / frameWidth;
        Log.i(TAG, "cropWidth:" + cropWidth + ", cropHeight:" + cropHeight + ", cropRegion:" + cropRegion + ", scaleRate:" + scaleRate);

        BitmapRegionDecoder brd = null;
        try {
            is.reset();
            BitmapFactory.Options imageOption = new BitmapFactory.Options();
            imageOption.inJustDecodeBounds = false;
            imageOption.inPreferQualityOverSpeed = true;
            imageOption.inSampleSize = (int)(scaleRate + 0.5);
            Log.i(TAG, "imageOption.inSampleSize:" + imageOption.inSampleSize);
            brd = BitmapRegionDecoder.newInstance(is, false);
            croppedImage = brd.decodeRegion(cropRegion, imageOption);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            if(brd != null) {
                brd.recycle();
            }
        }
        if(croppedImage != null) {
            Log.i(TAG, "croppedImage width:" + croppedImage.getWidth() + ", height:" + croppedImage.getHeight());
        }
        long stopTimeMs = System.currentTimeMillis();
        Log.i(TAG, "cost {" + (stopTimeMs - startTimeMs) + "ms}");
        return croppedImage;
    }
}
