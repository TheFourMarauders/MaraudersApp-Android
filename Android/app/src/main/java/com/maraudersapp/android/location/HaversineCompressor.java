package com.maraudersapp.android.location;

import com.maraudersapp.android.datamodel.LocationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 11/16/15.
 */
public class HaversineCompressor implements LocationCompressor {
    private static final double EARTH_RAD = 6.371e6;
    private static final double MIN_DIFF = 50;

    @Override
    public List<LocationInfo> filter(List<LocationInfo> allLocations) {
        List<LocationInfo> filtered = new ArrayList<>();
        LocationInfo prevAdded = allLocations.get(allLocations.size() - 1);
        filtered.add(prevAdded);
        for (int i = allLocations.size() - 2; i >= 0; i--) {
            LocationInfo maybeAdd = allLocations.get(i);
            if (haversineDistance(prevAdded, maybeAdd) >= MIN_DIFF) {
                filtered.add(maybeAdd);
                prevAdded = maybeAdd;
            }
        }
        return filtered;
    }

    private double haversineDistance(LocationInfo l1, LocationInfo l2) {
        double lat1 = l1.getLatitude();
        double lat2 = l2.getLatitude();
        double long1 = l1.getLongitude();
        double long2 = l2.getLongitude();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLong = Math.toRadians(long2 - long1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                + Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = EARTH_RAD * c; // Distance in m
        return Math.abs(d);
    }
}
