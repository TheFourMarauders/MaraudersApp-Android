package com.maraudersapp.android.mapdrawing;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;

import java.util.logging.LogRecord;

/**
 * Created by Michael on 10/28/2015.
 */
public class PollingManager {

    static final String POLL_TAG = "POLL";

    static final int POLL_INTERVAL = 10000;
    static final int LOCATION_SPAN = 1000 * 60 * 30; // half hour

    private Poller currentPoller;
    private final Handler pollHandler;
    private final GoogleMap googleMap;

    public PollingManager(Context ctx, GoogleMap googleMap) {
        currentPoller = newUserPoller(ctx);
        pollHandler = new Handler();
        this.googleMap = googleMap;
    }

    public void changePoller(Poller poller) {
        currentPoller.removeAllMarkings();
        pollHandler.removeCallbacks(currentPoller);
        pollHandler.postDelayed(poller, 0);
        currentPoller = poller;
    }

    public void stopPolling() {
        pollHandler.removeCallbacks(currentPoller);
    }

    public void continuePolling() {
        pollHandler.postDelayed(currentPoller, 0);
    }

    public Poller newFriendPoller(String username, Context ctx) {
        return new FriendPoller(pollHandler, googleMap, ctx, username);
    }

    public Poller newUserPoller(Context ctx) {
        return new UserPoller(pollHandler, googleMap, ctx);
    }
}
