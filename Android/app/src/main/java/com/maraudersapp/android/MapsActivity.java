package com.maraudersapp.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.maraudersapp.android.drawer.DrawerItem;
import com.maraudersapp.android.drawer.DrawerManager;
import com.maraudersapp.android.drawer.DrawerView;
import com.maraudersapp.android.drawer.MainDrawerView;
import com.maraudersapp.android.location.LocationUpdaterService;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
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
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Logging within this class.
    private static final String MAPS_ACTIVITY_TAG = "MAPS_TAG";


    private GoogleMap mMap;

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private AccountHeader mHeader;
    private GoogleApiClient mGApi;
    private ServerComm remote;
    private SharedPrefsUserAccessor storage;
    private DrawerManager drawerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        remote = ServerCommManager.getCommForContext(getApplicationContext());
        storage = new SharedPrefsUserAccessor(getApplicationContext());

        initToolbarAndDrawer();

        LocationUpdaterService.scheduleLocationPolling(getApplicationContext());
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

        DrawerView main = new MainDrawerView(remote, storage, null);
        mDrawer = new DrawerBuilder()
            .withActivity(this)
            .withAccountHeader(mHeader)
            .withToolbar(mToolbar)
            .withTranslucentStatusBar(true)
            .withSelectedItem(-1) // So no default item is selected
            .build();

        drawerManager = new DrawerManager(mDrawer, mToolbar, getApplicationContext());
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
                        Log.i(MAPS_ACTIVITY_TAG, "Sheet clicked: " + which);
                        switch (which) {
                            case R.id.add_friend:
                                new InputDialog(MapsActivity.this, "Send friend request by username", new InputDialog.OnTextEntered() {
                                    @Override
                                    public void onTextEntered(String text) {
                                        if (text != null && !text.isEmpty()) {
                                            sendFriendRequest(text);
                                        }
                                    }
                                }).show();
                                break;
                            case R.id.create_group:
                                new InputDialog(MapsActivity.this, "Create new group by name", new InputDialog.OnTextEntered() {
                                    @Override
                                    public void onTextEntered(String text) {
                                        if (text != null && !text.isEmpty()) {
                                            addGroup(text);
                                        }
                                    }
                                }).show();
                                break;
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
        if (!drawerManager.onBackPressed()) {
            moveTaskToBack(true);
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
     *
     * Sets the user location to be reflected on the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (loc != null) {
            Log.i(MAPS_ACTIVITY_TAG, "Last location: " + loc.toString());
            CameraUpdate center =
                    CameraUpdateFactory.newLatLngZoom(
                            new LatLng(loc.getLatitude(), loc.getLongitude()),
                            15); // Zoom level

            mMap.animateCamera(center);
        } else {
            Log.i(MAPS_ACTIVITY_TAG, "Last location null");
        }
    }

    private void sendFriendRequest(String username) {
        Log.i(MAPS_ACTIVITY_TAG, "Sending request to: " + username);
        remote.sendFriendRequest(storage.getUsername(), username,
                new RemoteCallback<String>() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Friend request sent!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                // TODO look at possible failures
                Toast.makeText(getApplicationContext(), "Friend request could not be sent", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addGroup(String groupName) {
        Log.i(MAPS_ACTIVITY_TAG, "Creating group: " + groupName);
        remote.createGroup(groupName, new RemoteCallback<String>() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(getApplicationContext(), "Group created!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                // TODO look at possible failures
                Toast.makeText(getApplicationContext(), "Group could not be created", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
