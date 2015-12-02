package com.maraudersapp.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.maraudersapp.android.util.TimeUtil;

import java.util.Date;

/**
 * Simplified method of setting and clearing fields from shared preferences.
 */
public class SharedPrefsAccessor {

    public static final String PREFS_NAME = "PrefsFile";

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    /**
     * @param context current context used to access shared preferences.
     */
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

    /**
     * Stores state of incognito into shared preferences.
     */
    public void setIncognito(boolean isIncognito) {
        prefsEditor.putBoolean("incognito", isIncognito);
        prefsEditor.commit();
    }

    /**
     * Stores user name and password into shared preferences.
     */
    public void putCredentials(String username, String password) {
        prefsEditor.putString("username", username);
        prefsEditor.putString("password", password);
        prefsEditor.commit();
    }

    /**
     * Removes username and password from shared preferences.
     */
    public void clearCredentials() {
        prefsEditor.putString("username", "");
        prefsEditor.putString("password", "");
        prefsEditor.commit();
    }

    /**
     * Checks if user has credentials stored in shared preferences.
     */
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

    /**
     * Put the start of user poll range into shared preferences.
     */
    public void putStartTime(Date startTime) {
        putTime("start", startTime);
    }

    /**
     * Put the end of user poll range into shared preferences.
     */
    public void putEndTime(Date endTime) {
        putTime("end", endTime);
    }

    /**
     * Put time into shared preferences.
     */
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

    /**
     * Checks if user has start time stored in shared preferences.
     */
    public boolean isStartTimeNull() {
        return prefs.getString("start", null) == null;
    }

    /**
     * Checks if user has end time stored in shared preferences.
     */
    public boolean isEndTimeNull() {
        return prefs.getString("end", null) == null;
    }
}
