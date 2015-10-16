package com.maraudersapp.android.remote;

/**
 * Created by Matthew on 10/16/2015.
 */
public interface RemoteCallback<T>{
    void onSuccess(T response);
    void onFailure(int errorCode, String message);
}
