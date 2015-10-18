package com.maraudersapp.android.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.datamodel.UserCreationInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.net.HttpCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matthew on 10/16/2015.
 */
public class HttpVolleyDispatcher implements ServerComm {

    private ServerConfig conf;

    private RequestQueue queue;
    private Context context;

    public HttpVolleyDispatcher(Context context, ServerConfig conf) {
        this.conf = conf;
        this.queue = Volley.newRequestQueue(context);
    }

    @Override
    public void createUser(UserCreationInfo u, final RemoteCallback<String> callback) {
        String url = conf.getUserCreationUrl();
        final String json = u.writeToJson();
        StringRequest req = new StringRequest(
                Request.Method.POST, url,
                new Response.Listener<String> () {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse res = error.networkResponse;
                        if (res != null && res.data != null) {
                            callback.onFailure(res.statusCode,
                                    new String(res.data));
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return json.getBytes();
            }
        };

        queue.add(req);

    }

    @Override
    public void sendFriendRequestTo(String username, RemoteCallback<String> callback) {

    }

    @Override
    public void getFriendRequestsFor(String username, RemoteCallback<Set<UserInfo>> callback) {

    }

    @Override
    public void acceptFriendRequestFrom(String username, RemoteCallback<String> callback) {

    }

    @Override
    public void removeFriend(String username, RemoteCallback<String> callback) {

    }

    @Override
    public void getFriendsFor(String username, RemoteCallback<Set<UserInfo>> callback) {

    }

    @Override
    public void getLocationsFor(String username, RemoteCallback<List<LocationInfo>> callback) {

    }

    @Override
    public void putLocationsFor(String username, RemoteCallback<String> callback) {

    }

    @Override
    public void createGroup(String groupName, RemoteCallback<String> callback) {

    }

    @Override
    public void getGroupsFor(String username, RemoteCallback<Set<GroupInfo>> callback) {

    }

    @Override
    public void getGroupById(String groupId, RemoteCallback<GroupInfo> callback) {

    }

    @Override
    public void addUserToGroup(String groupId, String username, RemoteCallback<String> callback) {

    }

    @Override
    public void removeUserFromGroup(String groupId, String username, RemoteCallback<String> callback) {

    }

    @Override
    public void getGroupLocations(String groupId, RemoteCallback<Map<String, List<LocationInfo>>> callback) {

    }


    private class GetRequest<T> extends Request<T> {
        private HttpCallback<T> callback;
        private String url;
        private Class<T> type;

        public GetRequest(final HttpCallback<T> callback, String url,
                          Class<T> type) {
            super(Method.GET, url, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        callback.handleFailure(error.networkResponse.statusCode,
                                new String(error.networkResponse.data,
                                        HttpHeaderParser.parseCharset(error.networkResponse.headers)));
                    }catch(IOException io) {
                        Log.d("Volley", io.toString());
                    }
                }
            });
            this.callback = callback;
            this.url = url;
            this.type = type;
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                T res = mapper.readValue(response.data, type);
                return Response.success(res, HttpHeaderParser.parseCacheHeaders(response));
            } catch (IOException e) {
                Log.d("Volley", e.toString());
            }
            return null;
        }

        @Override
        protected void deliverResponse(T response) {
            if (response == null) return;
            this.callback.handleSuccess(response);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError{
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            //auth stuff
            return headers;
        }
    }


}
