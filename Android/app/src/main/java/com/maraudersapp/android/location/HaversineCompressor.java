package com.maraudersapp.android.location;

import com.maraudersapp.android.datamodel.LocationInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by joe on 11/16/15.
 */
public class HaversineCompressor implements LocationCompressor {
    //private static final double EARTH_RAD = 6.371e6;
    private static final double MIN_DIFF = 20;

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
        Collections.reverse(filtered);
        return filtered;
    }

    private double haversineDistance(LocationInfo l1, LocationInfo l2) {
        double lat1 = l1.getLatitude();
        double lat2 = l2.getLatitude();
        double long1 = l1.getLongitude();
        double long2 = l2.getLongitude();
        float[] result = new float[1];
        android.location.Location.distanceBetween
                (lat1,long1,lat2, long2, result);
        return result[0];
    }
}
