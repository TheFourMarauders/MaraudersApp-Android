package com.maraudersapp.android.location;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;

public final class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final PendingIntent wakeupIntent = PendingIntent.getService(context, 0,
                new Intent(context, LocationUpdaterService.class), PendingIntent.FLAG_UPDATE_CURRENT);

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean hasNetwork = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.i(LocationConstants.LOG_TAG, "Received network broadcast. Network on: " + hasNetwork);
        if (hasNetwork) {
            // start service now for doing once
            context.startService(new Intent(context, LocationUpdaterService.class));

            // TODO change back
            // schedule service for every minute
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    15 * 1000, wakeupIntent);
        } else {
            alarmManager.cancel(wakeupIntent);
        }
    }

}
