package com.maraudersapp.android.net;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import com.maraudersapp.android.net.methods.get.HttpGetMethod;
import com.maraudersapp.android.net.methods.post_put.HttpPostPutMethod;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Cornerstone for making post / put calls to the server.
 *
 * Uploads JSON specified by HttpPostPutMethod to the backend and callsback with success string
 * or failure
 * new HttpPostPutTask(new HttpCallback\<String\>(){}).execute(some HttpPostPutMethod class
 *      from .net.methods.post_put)
 */
public class HttpPostPutTask extends AsyncTask<HttpPostPutMethod, Void, HttpResponse<String>> {

    private final HttpCallback<String> callback;

    public HttpPostPutTask(HttpCallback<String> callback) {
        this.callback = callback;
    }

    @Override
    protected HttpResponse<String> doInBackground(HttpPostPutMethod... params) {
        HttpPostPutMethod method = params[0];
        try {
            Pair<Integer, String> result = uploadToUrl(method);
            if (result.first == HttpConstants.GOOD_RESPONSE) {
                Log.w(this.getClass().getName(), "Bad response. Code: " + result.first + ". Message: "
                        + result.second + ". URL attempt: " + method.getPath());
                return new HttpResponse<String>(result.first, result.second);
            } else {
                return new HttpResponse<String>(result.second);
            }
        } catch (IOException e){
            Log.w(this.getClass().getName(), e);
            return new HttpResponse<String>(867, e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(HttpResponse<String> result) {
        if (result.isError()) {
            callback.handleFailure(result.getErrorCode(), result.getErrorMessage());
        } else {
            callback.handleSuccess(result.getResponse());
        }
    }

    /**
     * Uploads JSON from HttpPostPutMethod to server
     *
     * Returns Pair\<Reponse code, response\>
     */
    private Pair<Integer, String> uploadToUrl(HttpPostPutMethod method) throws IOException {
        URL url = new URL(method.getPath());
        // TODO mock out httpURLConnection and a provider to make testing easier
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // TODO set chunked streaming for when we know the length

        conn.setConnectTimeout(10000);

        conn.setRequestMethod(method.getType());
        conn.setDoInput(true);
        if ("POST".equals(method.getType())) {
            conn.setDoOutput(true);
        }
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Basic bWptYXVyZXI6cGFzcw==");

        try (BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"))) {
            Log.i(HttpConstants.LOG_TAG, method.toJson());
            wr.write(method.toJson());
            wr.flush();
        }

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
