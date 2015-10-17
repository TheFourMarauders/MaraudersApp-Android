package com.maraudersapp.android.datamodel;

/**
 * Created by joe on 10/16/15.
 */
public class LocationInfo {
    private double latitude, longitude;
    private String time;

    public LocationInfo(double latitude, double longitude, String time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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
}
