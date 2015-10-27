package com.maraudersapp.android;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Michael on 10/26/2015.
 */
public class MapsFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final String MAPS_ACTIVITY_TAG = "MAPS";

    private GoogleMap mMap;
    private View mView;

    public static MapsFragment newInstance() {
        return new MapsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(MAPS_ACTIVITY_TAG, "Creating map view");

        super.onCreateView(inflater, container, savedInstanceState);

        if (container == null) {
            return null;
        }

        if (mView == null) {
            Log.i(MAPS_ACTIVITY_TAG, "Maps view is null");
            mView = (RelativeLayout) inflater.inflate(R.layout.maps_fragment, container, false);
        }

        SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.location_map);

        if (mapFrag == null) {
            Log.i(MAPS_ACTIVITY_TAG, "Maps fragment is null");
            mapFrag = MapsFragment.newInstance();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.add(R.id.main_parent_view, mapFrag);
            ft.addToBackStack(null);
            ft.commit();
        }

        mapFrag.getMapAsync(this);

        return mView;
    }

    @Override
    public void onPause() {
        Log.i(MAPS_ACTIVITY_TAG, "Maps paused");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(MAPS_ACTIVITY_TAG, "Maps stopped");
        super.onPause();
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

        Log.i(MAPS_ACTIVITY_TAG, "Map ready");
        mMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
    public void onDestroyView() {

        Log.i(MAPS_ACTIVITY_TAG, "Map destroyed");

        super.onDestroyView();
    }
}
