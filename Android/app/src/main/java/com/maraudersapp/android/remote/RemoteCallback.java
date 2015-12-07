package com.maraudersapp.android.remote;

/**
 * Interface for success or failure of callbacks
 *
 * Created by Matthew on 10/16/2015.
 */
public interface RemoteCallback<T>{
    void onSuccess(T response);
    void onFailure(int errorCode, String message);
}
