package com.maraudersapp.android.net;

/**
 * See HttpGetTask for proper use.
 */
public interface HttpCallback<T> {

    /**
     * Called when the HttpGetTask returns with the response
     */
    void handleSuccess(T t);

    /**
     * Called when a call to the server fails
     *
     * 867: This means that the page could not be parsed on Android's side
     */
    void handleFailure(int errorCode, String message);
}
