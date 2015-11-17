package com.maraudersapp.android.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Michael on 10/6/2015.
 */
public final class TimeUtil {

    public synchronized static long getCurrentTimeInMillis() {
        return new Date().getTime();
    }

    public synchronized static String getCurrentTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

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

    public synchronized static String dateToString(Date d) {
        if (d == null) {
            return null;
        }
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        return df.format(d);
    }

    public synchronized static String dateToNiceString(Date d) {
        if (d == null) {
            return "";
        }
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");
        return df.format(d);
    }

}
