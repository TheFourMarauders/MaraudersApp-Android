package com.maraudersapp.android.remote;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Connects to our server locatoin
 *
 * Created by Matthew on 10/16/2015.
 */
public class ServerCommManager {

    private static Map<Context, ServerComm> comms = new HashMap<>();

    /**
     * Method to retrieve our server
     *
     * @param context
     * @return the server at the specified location
     */
    public static ServerComm getCommForContext(Context context) {
        if (comms.get(context) == null) {
            ServerComm comm = new HttpVolleyDispatcher(
                    context, new ServerConfig("http://maraudersapp.cloudapp.net", 27380));
            comms.put(context, comm);
        }
        return comms.get(context);
    }
}
