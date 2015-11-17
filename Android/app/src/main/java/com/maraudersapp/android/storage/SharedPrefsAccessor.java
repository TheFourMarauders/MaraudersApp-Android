package com.maraudersapp.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.maraudersapp.android.util.TimeUtil;

import java.util.Date;

/**
 * Created by joe on 10/21/15.
 */
public class SharedPrefsAccessor {
    public static final String PREFS_NAME = "PrefsFile";

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    public SharedPrefsAccessor(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.prefsEditor = prefs.edit();
    }

    public boolean isIncognito() {
        return prefs.getBoolean("incognito", false);
    }

    public String getUsername() {
        return prefs.getString("username", null);
    }

    public String getPassword() {
        return prefs.getString("password", null);
    }

    public void setIncognito(boolean isIncognito) {
        prefsEditor.putBoolean("incognito", isIncognito);
        prefsEditor.commit();
    }

    public void putCredentials(String username, String password) {
        prefsEditor.putString("username", username);
        prefsEditor.putString("password", password);
        prefsEditor.commit();
    }

    public void clearCredentials() {
        prefsEditor.putString("username", "");
        prefsEditor.putString("password", "");
        prefsEditor.commit();
    }

    public boolean isCredentialsNull() {
        return prefs.getString("username", null) == null;
    }

    public Date getStartTime() {
        return getTime("start");
    }

    public Date getEndTime() {
        return getTime("end");
    }

    private Date getTime(String key) {
        String time = prefs.getString(key, null);
        if (time == null) {
            return null;
        }
        return TimeUtil.parseDate(time);
    }

    public void putStartTime(Date startTime) {
        putTime("start", startTime);
    }

    public void putEndTime(Date endTime) {
        putTime("end", endTime);
    }

    private void putTime(String key, Date endTime) {
        prefsEditor.putString(key, TimeUtil.dateToString(endTime));
        prefsEditor.commit();
    }

    public void clearStartTime() {
        prefsEditor.putString("start", null);
        prefsEditor.commit();
    }

    public void clearEndTime() {
        prefsEditor.putString("end", null);
        prefsEditor.commit();
    }

    public void clearStartAndEndTime() {
        clearStartTime();
        clearEndTime();
    }

    public boolean isStartTimeNull() {
        return prefs.getString("start", null) == null;
    }

    public boolean isEndTimeNull() {
        return prefs.getString("end", null) == null;
    }
}
