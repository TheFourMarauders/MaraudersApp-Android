package com.maraudersapp.android.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;

import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsAccessor;

import java.util.ArrayList;
import java.util.List;


public  final class LocationUpdaterService extends Service implements LocationListener {

    private enum State {
        IDLE, WORKING;
    }

    private static State state;

    private LocationManager locationManager;
    private PowerManager.WakeLock wakeLock;

    private boolean gpsActive;
    private boolean networkActive;
    private Location gpsLocation;
    private Location networkLocation;

    private ServerComm remote;
    private SharedPrefsAccessor storage;

    static {
        state = State.IDLE;
    }


    //TODO begin alarm on startup
    /**
     * We need data because aGPS needs data to retreive location.
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

        if (hasNetwork) {
            // start service now for doing once
            context.startService(new Intent(context, LocationUpdaterService.class));

            // TODO change back
            // schedule service for every minute
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    LocationConstants.GPS_INTERVAL, wakeupIntent);
        } else {
            alarmManager.cancel(wakeupIntent);
        }
    }

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


            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                Log.i(LocationConstants.LOG_TAG, "Network active");
                networkActive = true;
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
            }

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i(LocationConstants.LOG_TAG, "GPS active");
                gpsActive = true;
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
            }

            if (!networkActive && !gpsActive) {
                Log.i(LocationConstants.LOG_TAG, "Neither active");
                onSendingFinished();
            }
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

        List<LocationInfo> locations = new ArrayList<>();
        locations.add(new LocationInfo(location));
        // send to server in background thread. you might want to start AsyncTask here

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
                    Log.i(LocationConstants.LOG_TAG, message);
                    // TODO logout / credential issues
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
    public void onLocationChanged(Location location) {
        Log.i(LocationConstants.LOG_TAG, "onLocationChanged " + location.getProvider().toString());
        if (networkLocation != null && gpsLocation != null) {
            Log.i(LocationConstants.LOG_TAG, "Received all locations");
            locationManager.removeUpdates(this); // you will want to listen for updates only once
            networkLocation = null;
            gpsLocation = null;
            gpsActive = false;
            networkActive = false;
            sendToServer(getBestLocation(gpsLocation, networkLocation));
        } else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            networkLocation = location;
            if (!gpsActive) {
                locationManager.removeUpdates(this); // you will want to listen for updates only once
                networkActive = false;
                networkLocation = null;
                sendToServer(location);
            }
        } else if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            gpsLocation = location;
            if (!networkActive) {
                locationManager.removeUpdates(this); // you will want to listen for updates only once
                gpsActive = false;
                gpsLocation = null;
                sendToServer(location);
            }
        }
    }

    private Location getBestLocation(Location location1, Location location2) {
        long timeDelta = location1.getTime() - location2.getTime();
        boolean isSignificantlyNewer = timeDelta > 1000*60*2;
        boolean isSignificantlyOlder = timeDelta < -1000*60*2;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return location1;
        } else if (isSignificantlyOlder) {
            return location2;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location1.getAccuracy() - location2.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return location1;
        } else if (isNewer && !isLessAccurate) {
            return location1;
        } else if (isNewer && !isSignificantlyLessAccurate) {
            return location1;
        }
        return location2;
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
