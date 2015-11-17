package com.maraudersapp.android.location;

import com.maraudersapp.android.datamodel.LocationInfo;

import java.util.List;

/**
 * Created by joe on 11/16/15.
 */
public class AndroidCompressor implements LocationCompressor {
    @Override
    public List<LocationInfo> filter(List<LocationInfo> allLocations) {
        return allLocations;
    }
}
