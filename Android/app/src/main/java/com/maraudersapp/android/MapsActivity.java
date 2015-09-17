package com.maraudersapp.android;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Logging within this class.
    private static final String MAPS_ACTIVITY_TAG = "MAPS_TAG";

    private GoogleMap mMap;

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private AccountHeader mHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initToolbarAndDrawer();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
                        new ProfileDrawerItem().withName("Michael Maurer")
                                .withEmail("mjmaurer777@gmail.com")
                                .withIcon(getResources().getDrawable(R.drawable.michael))
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
                    DrawerItem.values()[position - 1].handleClick();
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
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
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
            public void handleClick() {
                Log.i(MAPS_ACTIVITY_TAG, "Yourself clicked");
            }
        },
        FRIENDS(new PrimaryDrawerItem().withName(R.string.friends_item_name).withIdentifier(2).withSelectable(false)) {

            @Override
            public void handleClick() {
                Log.i(MAPS_ACTIVITY_TAG, "Friends clicked");
            }
        },
        GROUPS(new PrimaryDrawerItem().withName(R.string.groups_item_name).withIdentifier(3).withSelectable(false)) {
            @Override
            public void handleClick() {
                Log.i(MAPS_ACTIVITY_TAG, "Groups clicked");
            }
        },
        SEPARATE(new DividerDrawerItem()) {
            @Override
            public void handleClick() {}
        },
        INCOGNITO(new ToggleDrawerItem().withName(R.string.incognito_item_name).withIdentifier(4).withSelectable(false)) {

            @Override
            public void handleClick() {
                Log.i(MAPS_ACTIVITY_TAG, "Incognito clicked");
            }
        },
        SETTINGS(new PrimaryDrawerItem().withName(R.string.settings_item_name).withIdentifier(5).withSelectable(false)) {

            @Override
            public void handleClick() {
                Log.i(MAPS_ACTIVITY_TAG, "Settings clicked");
            }
        },
        LOGOUT(new PrimaryDrawerItem().withName(R.string.logout_item_name).withIdentifier(6).withSelectable(false)) {

            @Override
            public void handleClick() {
                Log.i(MAPS_ACTIVITY_TAG, "Logout clicked");
            }
        };

        // Actual DrawerItem to be drawn.
        private IDrawerItem drawerItem;

        /**
         * Action to complete when the DrawerItem is clicked in the UI.
         */
        public abstract void handleClick();

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
    }
}
