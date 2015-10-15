package com.maraudersapp.android.net.methods.post_put;

import com.maraudersapp.android.net.HttpConstants;

import org.json.JSONObject;

/**
 * Created by brent on 10/14/15.
 */
public class PutUserNamePass implements HttpPostPutMethod{

    private final String url;
    private final String username;
    private final String password;

    public PutUserNamePass(String username, String password) {
        url = HttpConstants.BASE_SERVICE_URL;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getPath() {
        return url;
    }

    @Override
    public String toJson() {
        JSONObject res = new JSONObject();
        try {

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
