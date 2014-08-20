package com.argon.foto.home;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewManager;
import android.view.WindowManager;

import com.argon.foto.R;

/**
 * An activity representing a list of FotoItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FotoItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link FotoItemListFragment} and the item details
 * (if present) is a {@link FotoItemDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link FotoItemListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class FotoItemListActivity extends Activity
        implements FotoItemListFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        setContentView(R.layout.activity_fotoitem_list);

        if (findViewById(R.id.fotoitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((FotoItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.fotoitem_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link FotoItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

    }
}
