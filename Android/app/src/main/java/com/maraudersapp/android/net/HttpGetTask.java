package com.maraudersapp.android.net;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.maraudersapp.android.net.methods.get.HttpGetMethod;

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
 * For example, if you want a HttpGetTask that will process a Location response from the server,
 * then it will require a HttpGetMethod\<Location\> as well as a HttpCallback\<Location\>.
 * This might look like the following:
 * new HttpGetTask\<Location\>(new HttpCallback\<Location\>(){}).execute(some HttpGetMethod\<Location\> class
 *      from .net.methods.get)
 *
 * When the method is done executing, it will call one of the callback functions.
 *
 * @param <T> The type of HttpGetMethod and HttpCallback desired.
 */
public class HttpGetTask<T> extends AsyncTask<HttpGetMethod<T>, Void, HttpResponse<T>> {

    private final HttpCallback<T> callback;

    public HttpGetTask(HttpCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    protected HttpResponse<T> doInBackground(HttpGetMethod<T>... params) {
        HttpGetMethod<T> method = params[0];
        try {
            Pair<Integer, String> result = downloadUrl(method);
            if (result.first == HttpConstants.GOOD_RESPONSE) {
                Log.w(this.getClass().getName(), "Bad response. Code: " + result.first + ". Message: "
                        + result.second + ". URL attempt: " + method.getPath());
                return new HttpResponse<T>(result.first, result.second);
            } else {
                return new HttpResponse<T>(method.parseJsonResult(result.second));
            }
        } catch (IOException e){
            Log.w(this.getClass().getName(), e);
            return new HttpResponse<T>(867, e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(HttpResponse<T> result) {
        if (result.isError()) {
            callback.handleFailure(result.getErrorCode(), result.getErrorMessage());
        } else {
            callback.handleSuccess(result.getResponse());
        }
    }

    /**
     * Reads raw JSON response from server.
     *
     * Pair\<responseCode, response\>
     */
    private Pair<Integer, String> downloadUrl(HttpGetMethod<T> method) throws IOException {
        URL url = new URL(method.getPath());
        // TODO mock out httpURLConnection and a provider to make testing easier
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(HttpConstants.READ_TIMEOUT);
        conn.setConnectTimeout(HttpConstants.CONNECT_TIMEOUT);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        int response = conn.getResponseCode();

        StringBuilder sb = new StringBuilder();
        try (InputStream is = response == HttpConstants.GOOD_RESPONSE
                ? conn.getInputStream() : conn.getErrorStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line+"\n");
            }
            br.close();
        } finally {
            conn.disconnect();
        }

        return Pair.create(response, sb.toString());
    }
}
