package com.maraudersapp.android.remote;

import android.content.Context;

/**
 * Created by Matthew on 10/16/2015.
 */
public class ServerCommFactory {

    public ServerComm build(Context context) {
        return new HttpVolleyDispatcher(context, new ServerConfig("http://maraudersapp.cloudapp.net", 27380));
    }
}
