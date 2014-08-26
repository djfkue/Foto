package com.argon.foto.home;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
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
        implements FotoItemListFragment.Callbacks, NavigationDrawerFragment.NavigationDrawerCallbacks{

    public static final int CURRENT_FOTO = 0x01;
    public static final String EXTRA_CURRENT_FOTO = "EXTRA_CURRENT_FOTO";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private NavigationDrawerFragment mNavigationDrawerFragment;

    private int mActionBarHeight;
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

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(
                android.R.attr.actionBarSize, tv, true)) {
            mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                    tv.data, getResources().getDisplayMetrics());
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("SD_TRACE", "-------------------------------");
        if (requestCode == CURRENT_FOTO && data != null) {
            final int currentFoto = data.getIntExtra(EXTRA_CURRENT_FOTO, 0);
            Log.e("SD_TRACE", "current photo: " + currentFoto);
            ((FotoItemListFragment) getFragmentManager()
                    .findFragmentById(R.id.fotoitem_list)).getListView().post(new Runnable() {
                @Override
                public void run() {
                    ((FotoItemListFragment) getFragmentManager()
                            .findFragmentById(R.id.fotoitem_list)).getListView().setSelection(currentFoto + 1);
                    getActionBar().hide();
                }
            });
        }
    }
    /**
     * Callback method from {@link FotoItemListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }
}
