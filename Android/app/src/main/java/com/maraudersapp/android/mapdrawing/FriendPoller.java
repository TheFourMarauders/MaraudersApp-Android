package com.maraudersapp.android.mapdrawing;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.location.HaversineCompressor;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class displays friends locations on the map
 *
 * Created by Michael on 10/28/2015.
 */
public class FriendPoller extends Poller {

    private final String username;
    private List<Marker> currentMarkers;

    /**
     * Constructs a new friend poller to display location in other classes
     *
     * @param handler
     * @param googleMap the map that is on the screen
     * @param ctx
     * @param username username of the friend to retrieve
     */
    FriendPoller(Handler handler, GoogleMap googleMap, Context ctx, String username) {
        super(handler, googleMap, ctx);
        this.username = username;
        currentMarkers = new ArrayList<>();
    }

    /**
     * Gets the locations of the friend and tries to display them on the map if they are
     * far enough away from each other
     */
    @Override
    public void run() {
        remote.getLocationsFor(username,
                storage.getStartTime(),
                storage.getEndTime(),
                new RemoteCallback<List<LocationInfo>>() {
                    @Override
                    public void onSuccess(List<LocationInfo> response) {
                        Log.i(PollingManager.POLL_TAG, "Locations received for " + username
                                + ". Size: " + response.size());
                        if (!response.isEmpty()) {
                            removeAllMarkings();
                            response = new HaversineCompressor().filter(response);
                            float opacity = 1.0f / response.size();
                            float step = opacity;
                            for (LocationInfo locInfo : response) {
                                // do something smarter because this is killing the ui thread :(
                                currentMarkers.add(gMap.addMarker(new MarkerOptions().position(
                                        new LatLng(locInfo.getLatitude(), locInfo.getLongitude()))
                                        .alpha(opacity).icon(BitmapDescriptorFactory.defaultMarker(DEFAULT_HUE))));
                                opacity += step;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {

                    }
                });
        handler.postDelayed(this, PollingManager.POLL_INTERVAL);
    }

    /**
     * Removes the markers on the map
     */
    void removeAllMarkings() {
        for (Marker marker : currentMarkers) {
            marker.remove();
        }
        currentMarkers.clear();
    }
}
