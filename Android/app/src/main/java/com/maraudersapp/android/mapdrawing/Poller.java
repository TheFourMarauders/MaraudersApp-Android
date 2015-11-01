package com.maraudersapp.android.mapdrawing;

import android.content.Context;
import android.os.Handler;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;

/**
 * Created by Michael on 10/28/2015.
 */
public abstract class Poller implements Runnable {

    protected static final float DEFAULT_HUE = BitmapDescriptorFactory.HUE_BLUE;

    protected Handler handler;
    protected GoogleMap gMap;

    protected final SharedPrefsUserAccessor storage;
    protected final ServerComm remote;

    Poller(Handler handler, GoogleMap googleMap, Context ctx) {
        this.handler = handler;
        gMap = googleMap;
        storage = new SharedPrefsUserAccessor(ctx);
        remote = ServerCommManager.getCommForContext(ctx);
    }

    abstract void removeAllMarkings();
}
