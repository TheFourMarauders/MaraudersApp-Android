package com.maraudersapp.android.net.methods;

import com.maraudersapp.android.storage.UserInfo;

import java.util.List;

/**
 * Created by Michael on 10/5/2015.
 */
public class GetUserFriends implements HttpMethod<List<UserInfo>> {

    private final String servicePath;

    public GetUserFriends(String username) {
        servicePath = HttpMethod.BASE_SERVICE_URL + "user/" + username + "/friends";
    }

    @Override
    public String getPath() {
        return servicePath;
    }

    @Override
    public List<UserInfo> parseJsonResult(String json) {
        // TODO go from JSON to list of friends
        return null;
    }

    @Override
    public String getType() {
        return "GET";
    }
}
