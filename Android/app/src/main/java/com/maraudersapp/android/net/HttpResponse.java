package com.maraudersapp.android.net;

import com.maraudersapp.android.net.HttpPostPutTask;

/**
 * Only used internally to pass around an Http response from the server
 */
final class HttpResponse<T> {

    private boolean isError;
    private final T response;
    private final int errorCode;
    private final String errorMessage;

    public HttpResponse(T response) {
        this.response = response;
        this.errorCode = 200;
        errorMessage = null;
        isError = true;
    }

    public HttpResponse(int errorCode, String message) {
        this.isError = false;
        this.errorCode = errorCode;
        response = null;
        errorMessage = message;
    }

    public boolean isError() {
        return isError;
    }

    public T getResponse() {
        return response;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

}
