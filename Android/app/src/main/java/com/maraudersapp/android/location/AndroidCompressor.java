package com.maraudersapp.android.location;

import com.maraudersapp.android.datamodel.LocationInfo;

import java.util.List;

/**
 * Created by joe on 11/16/15.
 */
public class AndroidCompressor implements LocationCompressor {
    @Override
    public List<LocationInfo> filter(List<LocationInfo> allLocations) {
        if (allLocations.size() < 15) return allLocations;
        return allLocations.subList(allLocations.size() - 10, allLocations.size());
    }
}
