package com.maraudersapp.android;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.maraudersapp.android.location.LocationUpdaterService;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;

/**
 * Main activity that displays the map that the users can interact with.
 *
 * Extends AppCompatActivity for Toolbar use.
 */
public class MainActivity extends AppCompatActivity {

    // Logging within this class.
    private static final String MAIN_ACTIVITY_TAG = "MAIN";

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private AccountHeader mHeader;
    private GoogleApiClient mGApi;
    private SharedPrefsUserAccessor storage;
    private Fragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = new SharedPrefsUserAccessor(getApplicationContext());
        mapFragment = new MapsFragment();

        initToolbarAndDrawer();
        initMap();

        LocationUpdaterService.scheduleLocationPolling(getApplicationContext());
    }

    private void initMap() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_parent_view,
                        mapFragment,
                        "map");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * Uses MaterialDrawer (https://github.com/mikepenz/MaterialDrawer)
     * to create a Navigation Drawer and Toolbar to host it.
     */
    private void initToolbarAndDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Your History");
        setSupportActionBar(mToolbar);

        mHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.navigation_header)
                .addProfiles(
                        new ProfileDrawerItem().withName(storage.getUsername())
                                .withIcon(getResources().getDrawable(R.drawable.michael)) // TODO user icon
                )
                .build();

        mDrawer = new DrawerBuilder()
            .withActivity(this)
            .withAccountHeader(mHeader)
            .withToolbar(mToolbar)
            .withTranslucentStatusBar(true)
            .withSelectedItem(-1) // So no default item is selected
            .withDrawerItems(DrawerItem.getAllItems())
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    // DrawerItem position is exactly the order it is added,
                    // so this is a clean way to handle clicks.

                    System.out.println(position);
                    if(position == 7) {
                        Intent i = new Intent(view.getContext(), LoginActivity.class);
                        i.putExtra("nullify", true);
                        view.getContext().startActivity(i);
                    }

                    if (drawerItem.isEnabled()) {
                        mDrawer.closeDrawer();
                    }


                    DrawerItem.values()[position - 1].handleClick(MainActivity.this, getSupportFragmentManager(), mapFragment);
                    return true;
                }
            })
            .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.maps_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.plus_button:
                // Display "Create" dialog at the bottom of the screen.
                // https://github.com/soarcn/BottomSheet
                new BottomSheet.Builder(this).title("Create new...").sheet(R.menu.create).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                        }
                    }
                }).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.i(MAIN_ACTIVITY_TAG, "stack count: " + count);
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else if (count == 0) {
            moveTaskToBack(true);
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    /**
     * Structure for holding each item in the Navigation Drawer (left panel).
     *
     * Adding a new item to the drawer is as easy as adding a new enum instance here.
     * Just make sure that the identifier is unique. Items are added in the order they are
     * specified. handleClick will be called when the item is clicked in the UI.
     */
    private enum DrawerItem {

        YOURSELF(new PrimaryDrawerItem().withName(R.string.yourself_item_name).withIdentifier(1).withSelectable(false)) {

            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) {
                Log.i(MAIN_ACTIVITY_TAG, "Yourself clicked");
            }
        },
        FRIENDS(new PrimaryDrawerItem().withName(R.string.friends_item_name).withIdentifier(2).withSelectable(false)) {

            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) {
                fragmentManager.beginTransaction()
                        .hide(mapsFrag)
                        .add(R.id.main_parent_view, new FriendsListFragment(mapsFrag))
                        .addToBackStack(null)
                        .commit();
                Log.i(MAIN_ACTIVITY_TAG, "Friends clicked");
            }
        },
        GROUPS(new PrimaryDrawerItem().withName(R.string.groups_item_name).withIdentifier(3).withSelectable(false)) {
            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) {
                Log.i(MAIN_ACTIVITY_TAG, "Groups clicked");
            }
        },
        SEPARATE(new DividerDrawerItem().withEnabled(false)) {
            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) {}
        },
        INCOGNITO(new ToggleDrawerItem().withName(R.string.incognito_item_name).withIdentifier(4).withSelectable(false)) {

            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) {
                Log.i(MAIN_ACTIVITY_TAG, "Incognito clicked");
            }
        },
        SETTINGS(new PrimaryDrawerItem().withName(R.string.settings_item_name).withIdentifier(5).withSelectable(false)) {

            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) {
                Log.i(MAIN_ACTIVITY_TAG, "Settings clicked");
            }
        },
        LOGOUT(new PrimaryDrawerItem().withName(R.string.logout_item_name).withIdentifier(6).withSelectable(false)) {

            @Override
            public void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag) { Log.i(MAIN_ACTIVITY_TAG, "Logout clicked"); }
        };

        // Actual DrawerItem to be drawn.
        private IDrawerItem drawerItem;

        /**
         * Action to complete when the DrawerItem is clicked in the UI.
         */
        public abstract void handleClick(Context context, FragmentManager fragmentManager, Fragment mapsFrag);

        DrawerItem(IDrawerItem drawerItem) {
            this.drawerItem = drawerItem;
        }

        /**
         * @return all DrawerItems' IDrawerItem (what is actually given to the API).
         */
        public static ArrayList<IDrawerItem> getAllItems() {
            ArrayList<IDrawerItem> toReturn = new ArrayList<>();
            for (DrawerItem item : values()) {
                toReturn.add(item.drawerItem);
            }
            return toReturn;
        }

        private static void loadFragment(Fragment fragment, FragmentManager fragmentManager) {

        }
    }
}
