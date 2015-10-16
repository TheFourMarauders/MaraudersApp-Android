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

    public static String getCurrentTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:ss:mm'Z'");
        df.setTimeZone(tz);
        return df.format(new Date());
    }

    public static Date parseDate(String date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:ss:mm'Z'");
        df.setTimeZone(tz);
        Date d;
        try {
            d = df.parse(date);
        }catch (java.text.ParseException ex){
            d = null;
        }
        return d;
    }

    public static String dateToString(Date d) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:ss:mm'Z'");
        df.setTimeZone(tz);
        return df.format(d);
    }



}
