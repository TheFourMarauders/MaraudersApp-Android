package com.maraudersapp.android.net.methods.get;

import com.maraudersapp.android.net.HttpConstants;
import com.maraudersapp.android.storage.UserInfo;

import java.util.List;

/**
 * Created by Michael on 10/5/2015.
 */
public class GetUserFriends implements HttpGetMethod<List<UserInfo>> {

    private final String servicePath;

    public GetUserFriends(String username) {
        servicePath = HttpConstants.BASE_SERVICE_URL + "user/" + username + "/friends";
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
}
