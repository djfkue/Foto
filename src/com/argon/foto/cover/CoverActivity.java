package com.argon.foto.cover;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.argon.foto.R;
import com.argon.foto.home.FotoItemListActivity;
import com.argon.foto.util.ImageBlurrer;
import com.argon.foto.util.ImageCropper;

public class CoverActivity extends Activity {
    private final static String TAG = "CoverActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cover);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cover, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private View mCover;
        private ImageBlurrer mBlurrer;
        private Bitmap[] mBlurFrames = new Bitmap[4];
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_cover, container, false);
            rootView.findViewById(R.id.sign_up_weibo_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), FotoItemListActivity.class);
                    startActivity(intent);
                    // Override transitions: we don't want the normal window animation in addition
                    // to our custom one
                    getActivity().overridePendingTransition(0, 0);
                }
            });
            mCover = rootView.findViewById(R.id.cover);
            AssetManager assetManager = getActivity().getAssets();
            try {
                InputStream istr = null;
                istr = assetManager.open("cover_story_default.jpg");
                mCover.setBackgroundDrawable(new BitmapDrawable (getActivity().getResources(), istr));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rootView;
        }

        @Override
        public void onResume() {
            super.onResume();

            mBlurrer = new ImageBlurrer(getActivity());
            new PrepareBlurAnimationTask().execute("");
        }

        private class PrepareBlurAnimationTask extends AsyncTask<String, Void, Void> {
            @Override
            protected void onPreExecute() {
            }
            @Override
            protected Void doInBackground(String... params) {
                Point displaySize = new Point();
                getActivity().getWindow().getWindowManager().getDefaultDisplay().getSize(displaySize);
                AssetManager assetManager = getActivity().getAssets();
                try {
                    InputStream istr = null;
                    istr = assetManager.open("cover_story_default.jpg");
                    Bitmap original = ImageCropper.cropImage(istr, displaySize.x, displaySize.y, 0);
                    Bitmap quarterBitmap = Bitmap.createScaledBitmap(original, original.getWidth() / 2, original.getHeight() / 2, true);
                    original.recycle();
                    mBlurFrames[3] = mBlurrer.blurBitmap(quarterBitmap, 2, 0);
                    mBlurFrames[2] = mBlurrer.blurBitmap(quarterBitmap, 4, 0);
                    mBlurFrames[1] = mBlurrer.blurBitmap(quarterBitmap, 8, 0);
                    mBlurFrames[0] = mBlurrer.blurBitmap(quarterBitmap, 16, 0);
                    quarterBitmap.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void v) {
                // TODO: here
            }
        }
    }
}
