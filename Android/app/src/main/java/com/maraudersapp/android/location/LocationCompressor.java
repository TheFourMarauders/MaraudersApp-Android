package com.maraudersapp.android.location;

import com.maraudersapp.android.datamodel.LocationInfo;

import java.util.List;

/**
 * Created by joe on 11/16/15.
 */
public interface LocationCompressor {
    /**
     * Takes a list of locations and filters them based on some compression algorithm
     * @param allLocations
     * @return the filtered list
     */
    List<LocationInfo> filter(List<LocationInfo> allLocations);
}
