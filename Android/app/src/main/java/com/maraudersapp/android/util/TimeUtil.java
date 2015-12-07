package com.maraudersapp.android.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides utilities for working with ISO 8601 format.
 */
public final class TimeUtil {

    public synchronized static long getCurrentTimeInMillis() {
        return new Date().getTime();
    }

    /**
     * Get current time in ISO 8601.
     */
    public synchronized static String getCurrentTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    /**
     * Parses ISO 8601 string into Date object.
     */
    public synchronized static Date parseDate(String date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        Date d;
        try {
            d = df.parse(date);
        }catch (java.text.ParseException ex){
            d = null;
        }
        return d;
    }

    /**
     * Converts date into ISO 8601 string.
     */
    public synchronized static String dateToString(Date d) {
        if (d == null) {
            return null;
        }
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(d);
    }

    /**
     * Converts date to human readable string.
     */
    public synchronized static String dateToNiceString(Date d) {
        if (d == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        return df.format(d);
    }

}
