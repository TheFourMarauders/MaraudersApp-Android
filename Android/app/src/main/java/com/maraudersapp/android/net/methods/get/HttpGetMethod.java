package com.maraudersapp.android.net.methods.get;

/**
 * See HttpGetTask for proper use.
 *
 * Represents a HttpGetMethod that is available from the Maurader's App server.
 *
 * The type parameter is a POJO (ideally) representation of the server response.
 */
public interface HttpGetMethod<T> {

    // TODO update
    public static final String BASE_SERVICE_URL = "SOME_URL/api/services/";

    /**
     * The path that the service is located at.
     *
     * Ex: www.mapp.com:8000/api/services/user/mjmaurer/location
     */
    String getPath();

    /**
     * Takes the raw json from the server and should parse it into some POJO (type paramter T).
     *
     * Guaranteed to be valid JSON.
     */
    T parseJsonResult(String json);
}
