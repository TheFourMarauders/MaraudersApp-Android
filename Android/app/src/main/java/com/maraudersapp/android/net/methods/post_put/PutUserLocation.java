package com.maraudersapp.android.net.methods.post_put;

import android.location.Location;

import com.maraudersapp.android.net.HttpConstants;
import com.maraudersapp.android.util.TimeUtil;

import org.json.JSONObject;

/**
 * Created by Michael on 10/6/2015.
 */
public class PutUserLocation implements HttpPostPutMethod {

    private final String url;
    private final String date;
    private final Location loc;

    public PutUserLocation(String username, Location location) {
        url = HttpConstants.BASE_SERVICE_URL + "user/" + username + "/location";
        date = TimeUtil.getCurrentTime();
        loc = location;
    }

    @Override
    public String getPath() {
        return url;
    }

    @Override
    public String toJson() {
        JSONObject res = new JSONObject();
        try {
            res.put("latitude", loc.getLatitude());
            res.put("longitude", loc.getLongitude());
            res.put("time", date);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "[" + res.toString() + "]";
    }

    @Override
    public String getType() {
        return "PUT";
    }
}
