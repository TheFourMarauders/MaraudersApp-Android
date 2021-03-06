package com.maraudersapp.android.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsAccessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * This class retrieves the location via polling and chooses when to update the location or stop
 * location updates
 */
public  final class LocationUpdaterService extends Service
        implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public LocationUpdaterService() {
    }

    private enum State {
        IDLE, WORKING;
    }

    private static State state;

    private LocationManager locationManager;
    private PowerManager.WakeLock wakeLock;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private boolean gpsActive;
    private boolean networkActive;
    private Location gpsLocation;
    private Location networkLocation;

    private ServerComm remote;
    private SharedPrefsAccessor storage;

    private static final ConcurrentLinkedDeque<LocationInfo> locationBuffer = new ConcurrentLinkedDeque<>();

    static {
        state = State.IDLE;
    }


    //TODO begin alarm on startup
    /**
     * Scheduling polling because based on network availability to poll for locations. Checks
     * if user is incognito as well
     *
     * @param context
     */
    public static void scheduleLocationPolling(Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Same intents will overwrite any existing previous one :)
        final PendingIntent wakeupIntent = PendingIntent.getService(context, 0,
                new Intent(context, LocationUpdaterService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean hasNetwork = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.i(LocationConstants.LOG_TAG, "Attempting to schedule location update. Network on?: " + hasNetwork);

        //if (hasNetwork) {
            // start service now for doing once
        if (!new SharedPrefsAccessor(context).isIncognito()) {
            Log.i(LocationConstants.LOG_TAG, "Incognito off. Going for location");
            context.startService(new Intent(context, LocationUpdaterService.class));

            // TODO change back
            // schedule service for every minute
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    LocationConstants.GPS_INTERVAL, wakeupIntent);
        }
        //} else {
        //    alarmManager.cancel(wakeupIntent);
        //}
    }

    /**
     * Stops polling for location updates
     *
     * @param context
     */
    public static void stopLocationPolling(Context context) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Same intents will overwrite any existing previous one :)
        final PendingIntent wakeupIntent = PendingIntent.getService(context, 0,
                new Intent(context, LocationUpdaterService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Log.i(LocationConstants.LOG_TAG, "Stopping location updates");
        alarmManager.cancel(wakeupIntent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LocationConstants.LOG_TAG, "Location service started");
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LocationUpdaterService");

        storage = new SharedPrefsAccessor(getApplicationContext());

        remote = ServerCommManager.getCommForContext(getApplicationContext());
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        state = State.IDLE;
        if(mGoogleApiClient.isConnected()) {
            Log.i(LocationConstants.LOG_TAG, "Location API disconnected");
            mGoogleApiClient.disconnect();
        }
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
    }

    /**
     * Send location to server if there are any
     *
     * @param location
     */
    private void sendToServer(Location location) {

        final List<LocationInfo> locations = new ArrayList<>(locationBuffer);
        locations.add(new LocationInfo(location));
        // send to server in background thread. you might want to start AsyncTask here
        locationBuffer.clear();

        remote.putLocationsFor(
            storage.getUsername(), locations,
            new RemoteCallback<String>() {
                @Override
                public void onSuccess(String response) {
                    Log.i(LocationConstants.LOG_TAG, response);
                    onSendingFinished();
                }

                @Override
                public void onFailure(int errorCode, String message) {
                    Log.i("Location: send failure", message);
                    // TODO logout / credential issues
                    locationBuffer.addAll(locations);
                    Log.i("Location Buffer: ", locationBuffer.toString());
                    onSendingFinished();
                }
            }
        );
    }

    private void onSendingFinished() {
        // call this after sending finished to stop the service
        this.stopSelf(); //stopSelf will call onDestroy and the WakeLock releases.
        //Be sure to call this after everything is done (handle exceptions and other stuff) so you release a wakeLock
        //or you will end up draining battery like hell
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (state == State.IDLE) {
            state = State.WORKING;
            this.wakeLock.acquire();
            Log.i(LocationConstants.LOG_TAG, "Location onStartCommand. Starting location.");

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                    .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
        }

        return START_STICKY;
    }



    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Location is changed, so we will send our new location to the server
     *
     * @param location new location
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(LocationConstants.LOG_TAG, "onLocationChanged " + location.getProvider().toString());
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        sendToServer(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}

