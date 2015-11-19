package com.maraudersapp.android;

import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.maraudersapp.android.drawer.DrawerManager;
import com.maraudersapp.android.StateFragment.InstanceData;
import com.maraudersapp.android.location.LocationUpdaterService;
import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

/**
 * Main activity that displays the map that the users can interact with.
 *
 * Extends AppCompatActivity for Toolbar use.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Logging within this class.
    private static final String MAPS_ACTIVITY_TAG = "MAPS_TAG";
    private static final String STATE_TAG = "DATA";


    private GoogleMap mMap;
    private StateFragment stateFragment;

    private Toolbar mToolbar;
    private Drawer mDrawer;
    private ServerComm remote;
    private SharedPrefsAccessor storage;
    private DrawerManager drawerManager;
    private PollingManager pollingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        remote = ServerCommManager.getCommForContext(getApplicationContext());
        storage = new SharedPrefsAccessor(getApplicationContext());

        FragmentManager fm = getFragmentManager();
        stateFragment = (StateFragment) fm.findFragmentByTag(STATE_TAG);

        // create the fragment and data the first time
        if (stateFragment == null) {
            Log.i(MAPS_ACTIVITY_TAG, "Found no previous state");
            // add the fragment
            stateFragment = new StateFragment();
            fm.beginTransaction().add(stateFragment, STATE_TAG).commit();
            stateFragment.setData(initializeState());
            storage.setIncognito(false);
        }

        loadInstanceState(stateFragment);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!storage.isIncognito()) {
            LocationUpdaterService.scheduleLocationPolling(getApplicationContext());
        }
    }

    private InstanceData initializeState() {
        pollingManager = new PollingManager();
        return new InstanceData("Your History", pollingManager);
    }

    private void loadInstanceState(StateFragment state) {
        InstanceData stateData = state.getData();
        pollingManager = stateData.getPollingManager();
        initToolbarAndDrawer(stateData.getTitle());
        // TODO will poll continue to poll?
    }

    /**
     * Uses MaterialDrawer (https://github.com/mikepenz/MaterialDrawer)
     * to create a Navigation Drawer and Toolbar to host it.
     */
    private void initToolbarAndDrawer(String title) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);

        AccountHeader mHeader = new AccountHeaderBuilder()
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
            .build();

        drawerManager = new DrawerManager(mDrawer, mToolbar, this, pollingManager);
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
                pollingManager.onPlusPressed(MapsActivity.this);
                break;
            case R.id.minus_button:
                pollingManager.onMinusPressed(MapsActivity.this);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
        pollingManager.setGoogleMap(mMap, getApplicationContext());

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

    @Override
    public void onPause() {
        pollingManager.stopPolling();
        super.onPause();
    }

    @Override
    public void onResume() {
        pollingManager.continuePolling();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.i(MAPS_ACTIVITY_TAG, "Destroyed");
        super.onDestroy();
        stateFragment.setData(new InstanceData(mToolbar.getTitle().toString(), pollingManager));
    }
}
