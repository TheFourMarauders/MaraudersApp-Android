package com.maraudersapp.android.mapdrawing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.GoogleMap;
import com.maraudersapp.android.InputDialog;
import com.maraudersapp.android.MapsActivity;
import com.maraudersapp.android.R;
import com.maraudersapp.android.remote.RemoteCallback;

import java.util.Collection;
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
    private GoogleMap googleMap;

    public PollingManager() {
        pollHandler = new Handler();
    }

    public void setGoogleMap(GoogleMap map, Context ctx) {
        if (googleMap == null) {
            googleMap = map;
            currentPoller = newUserPoller(ctx);
            pollHandler.postDelayed(currentPoller, 0);
        } else {
            googleMap = map;
            stopPolling();
            currentPoller.changeMap(googleMap);
            continuePolling();
        }
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
        if (currentPoller != null) {
            pollHandler.postDelayed(currentPoller, 0);
        }
    }

    public void onPlusPressed(Activity ctx) {
        currentPoller.onPlusPressed(ctx);
    }

    public Poller newFriendPoller(String username, Context ctx) {
        return new FriendPoller(pollHandler, googleMap, ctx, username);
    }

    public Poller newGroupPoller(String id, String groupName, Collection<String> members, Context ctx) {
        return new GroupPoller(pollHandler, googleMap, ctx, id, groupName, members);
    }

    public Poller newUserPoller(Context ctx) {
        return new UserPoller(pollHandler, googleMap, ctx);
    }
}
