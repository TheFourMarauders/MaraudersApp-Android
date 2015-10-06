package com.maraudersapp.android.net;

import android.os.AsyncTask;
import android.util.Log;

import com.maraudersapp.android.net.methods.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Cornerstone for making calls to the server.
 *
 * The class is parameterized on what should be returned in the callback.
 * For example, if you want a HttpTask that will process a Location response from the server,
 * then it will require a HttpMethod\<Location\> as well as a HttpCallback\<Location\>.
 * This might look like the following:
 * new HttpTask\<Location\>(new HttpCallback\<Location\>(){}).execute(some HttpMethod\<Location\> class
 *      from .net.methods)
 *
 * When the method is done executing, it will call one of the callback functions.
 *
 * @param <T> The type of HttpMethod and HttpCallback desired.
 */
public class HttpTask<T> extends AsyncTask<HttpMethod<T>, Void, T> {

    private final HttpCallback<T> callback;

    public HttpTask(HttpCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    protected T doInBackground(HttpMethod<T>... params) {
        HttpMethod<T> method = params[0];
        try {
            String result = downloadUrl(method);
            if (result == null) {
                return null;
            }
            return method.parseJsonResult(result);
        } catch (IOException e){
            Log.w(this.getClass().getName(), e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(T result) {
        if (result == null) {
            callback.handleFailure();
        } else {
            callback.handleSuccess(result);
        }
    }

    /**
     * Reads raw JSON response from server.
     */
    private String downloadUrl(HttpMethod<T> method) throws IOException {
        URL url = new URL(method.getPath());
        // TODO mock out httpURLConnection and a provider to make testing easier
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(HttpConstants.READ_TIMEOUT);
        conn.setConnectTimeout(HttpConstants.CONNECT_TIMEOUT);
        conn.setRequestMethod(method.getType());
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        int response = conn.getResponseCode();

        StringBuilder sb = new StringBuilder();
        try (InputStream is = conn.getInputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
        }

        if (response != HttpConstants.GOOD_RESPONSE) {
            Log.w(this.getClass().getName(), "Bad response. Code: " + response + ". Message: "
                + sb.toString() + ". URL attempt: " + method.getPath());
            return null;
        } else {
            return sb.toString();
        }
    }
}
