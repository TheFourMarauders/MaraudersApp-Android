package com.maraudersapp.android.net;

public final class HttpConstants {

    public static final String BASE_SERVICE_URL = "http://128.61.114.141:8080/api/services/";

    // Amount of time before the HTTP will timeout from reading
    public static final int READ_TIMEOUT = 10000;

    // Amount of time before the connection will timeout from opening
    public static final int CONNECT_TIMEOUT = 15000;

    // OK server resopnse code
    public static final int GOOD_RESPONSE = 200;

    public static final String LOG_TAG = "android.Network";

}
