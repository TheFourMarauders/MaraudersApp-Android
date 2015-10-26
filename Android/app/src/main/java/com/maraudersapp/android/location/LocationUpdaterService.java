package com.maraudersapp.android.location;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;

import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public  final class LocationUpdaterService extends Service implements LocationListener {

    private enum State {
        IDLE, WORKING;
    }

    private static State state;

    private LocationManager locationManager;
    private PowerManager.WakeLock wakeLock;

    private ServerComm remote;
    private SharedPrefsUserAccessor storage;

    static {
        state = State.IDLE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LocationConstants.LOG_TAG, "Location service started");
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationUpdaterService");

        storage = new SharedPrefsUserAccessor(getApplicationContext());

        if(!storage.isCredentialsNull()) {
            // TODO is this possible / what does this mean?
        }

        remote = ServerCommManager.getCommForContext(getApplicationContext());
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (state == State.IDLE) {
            state = State.WORKING;
            this.wakeLock.acquire();
            Log.i(LocationConstants.LOG_TAG, "Location onStartCommand. Starting location.");

            // Acquire a reference to the system Location Manager
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // TODO try with GPS
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        state = State.IDLE;
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
    }

    private void sendToServer(Location location) {

        // send to server in background thread. you might want to start AsyncTask here
        remote.putLocationsFor(
                storage.getUsername(),
                Arrays.asList(new LocationInfo(location)),
                new RemoteCallback<String>() {
                    @Override
                    public void onSuccess(String response) {
                        Log.i(LocationConstants.LOG_TAG, response);
                        onSendingFinished();
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        Log.i(LocationConstants.LOG_TAG, "Location send not successful. Code: " + errorCode
                                + ". Message: " + message);
                        onSendingFinished();
                    }
                });
    }

    private void onSendingFinished() {
        // call this after sending finished to stop the service
        this.stopSelf(); //stopSelf will call onDestroy and the WakeLock releases.
        //Be sure to call this after everything is done (handle exceptions and other stuff) so you release a wakeLock
        //or you will end up draining battery like hell
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LocationConstants.LOG_TAG, "Received location");
        locationManager.removeUpdates(this); // you will want to listen for updates only once
        sendToServer(location);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
