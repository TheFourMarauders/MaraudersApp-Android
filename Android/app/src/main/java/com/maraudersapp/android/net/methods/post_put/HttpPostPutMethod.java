package com.maraudersapp.android.net.methods.post_put;

/**
 * See HttpPostPutTask for proper use.
 *
 * Represents a HttpPostPutMethod that is available from the Maurader's App server.
 *
 * Information to be sent should be entered through constructor (most likely as a POJO or list or POJOs)
 */
public interface HttpPostPutMethod {

    /**
     * The path that the service is located at.
     *
     * Ex: www.mapp.com:8000/api/services/user/mjmaurer/location
     */
    String getPath();

    /**
     * Takes information from constructor and turns into JSON string to be sent
     */
    String toJson();

    /**
     * POST or PUT
     */
    String getType();
}
