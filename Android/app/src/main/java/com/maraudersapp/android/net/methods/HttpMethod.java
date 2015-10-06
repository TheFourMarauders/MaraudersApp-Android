package com.maraudersapp.android.net.methods;

/**
 * See HttpTask for proper use.
 *
 * Represents a HttpMethod that is available from the Maurader's App server.
 *
 * The type parameter is a POJO (ideally) representation of the server response.
 */
public interface HttpMethod<T> {

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

    /**
     * HTTP method type (GET, PUT, POST, ...)
     */
    String getType();
}
