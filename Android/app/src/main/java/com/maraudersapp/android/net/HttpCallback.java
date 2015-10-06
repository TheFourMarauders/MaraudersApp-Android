package com.maraudersapp.android.net;

/**
 * See HttpTask for proper use.
 */
public interface HttpCallback<T> {

    /**
     * Called when the HttpTask returns with the response
     */
    void handleSuccess(T t);

    /**
     * Called when a call to the server fails
     */
    void handleFailure();
}
