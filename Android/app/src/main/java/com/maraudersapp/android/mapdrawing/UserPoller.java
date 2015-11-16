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
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Michael on 10/29/2015.
 */
public class UserPoller extends Poller {

    private List<Marker> currentMarkers;
    private final String username;

    UserPoller(Handler handler, GoogleMap googleMap, Context ctx) {
        super(handler, googleMap, ctx);
        currentMarkers = new ArrayList<>();
        username = storage.getUsername();
    }

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
                            float opacity = 1.0f / response.size();
                            float step = opacity;
                            for (LocationInfo locInfo : response) {
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

    @Override
    void removeAllMarkings() {
        for (Marker marker : currentMarkers) {
            marker.remove();
        }
        currentMarkers.clear();
    }
}
