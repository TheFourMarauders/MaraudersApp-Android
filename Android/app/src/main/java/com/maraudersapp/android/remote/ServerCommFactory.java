package com.maraudersapp.android.remote;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Matthew on 10/16/2015.
 */
public class ServerCommFactory {

    private static Map<Context, ServerComm> comms = new HashMap<>();

    public ServerComm build(Context context) {
        if (comms.get(context) == null) {
            ServerComm comm = new HttpVolleyDispatcher(
                    context, new ServerConfig("http://maraudersapp.cloudapp.net", 27380));
            comms.put(context, comm);
        }
        return comms.get(context);
    }
}
