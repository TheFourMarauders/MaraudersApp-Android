package com.maraudersapp.android.datamodel;

import android.location.Location;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maraudersapp.android.util.TimeUtil;

import java.sql.Time;
import java.util.Date;

/**
 * Created by joe on 10/16/15.
 *
 * Model for location: latitude, longtitude, time
 */
public class LocationInfo {
    private double latitude, longitude;
    private String time;

    @JsonCreator
    public LocationInfo(
            @JsonProperty("latitude") double latitude,
            @JsonProperty("longitude") double longitude,
            @JsonProperty("time") String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public LocationInfo(Location l) {
        this(l.getLatitude(), l.getLongitude(), TimeUtil.getCurrentTime());
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Date timeStringToDate() {
        return TimeUtil.parseDate(time);
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationInfo)) return false;

        LocationInfo that = (LocationInfo) o;

        return getTime().equals(that.getTime());

    }

    @Override
    public int hashCode() {
        return getTime().hashCode();
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", time='" + time + '\'' +
                '}';
    }
}
