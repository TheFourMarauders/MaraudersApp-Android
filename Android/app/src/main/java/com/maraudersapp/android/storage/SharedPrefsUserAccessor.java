package com.maraudersapp.android.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by joe on 10/21/15.
 */
public class SharedPrefsUserAccessor {
    public static final String LOGIN_PREFS_NAME = "LoginPrefsFile";

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    public SharedPrefsUserAccessor(Context context) {
        this.prefs = context.getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        this.prefsEditor = prefs.edit();
    }

    public String getUsername() {
        return prefs.getString("username", null);
    }

    public String getPassword() {
        return prefs.getString("password", null);
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
}
