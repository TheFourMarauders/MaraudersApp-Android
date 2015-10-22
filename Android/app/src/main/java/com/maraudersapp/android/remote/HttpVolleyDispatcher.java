package com.maraudersapp.android.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.maraudersapp.android.LoginActivity;
import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.datamodel.UserCreationInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.maraudersapp.android.util.TimeUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
        this.context = context;

        queue.start();
    }

    @Override
    public void createUser(UserCreationInfo u, final RemoteCallback<String> callback) {
        PushRequest<UserCreationInfo> req = new PushRequest<>(PushRequestType.POST, callback,
                conf.getUserCreationUrl(), u);

        queue.add(req);

    }

    @Override
    public void sendFriendRequest(String sourceUsername, String targetUsername,
                                  RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getSendFriendRequestUrl(sourceUsername, targetUsername), "");
        queue.add(req);
    }

    @Override
    public void getFriendRequestsFor(String username, RemoteCallback<Set<UserInfo>> callback) {
        GetRequest<Set<UserInfo>> req = new GetRequest<>(callback,
                conf.getIncomingFriendReqUrl(username),
                new ObjectMapper().getTypeFactory().constructCollectionType(HashSet.class, UserInfo.class));
        queue.add(req);
    }

    @Override
    public void acceptFriendRequest(String username, String targetFriend, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getAcceptFriendReqUrl(username, targetFriend), "");
        queue.add(req);
    }

    @Override
    public void removeFriend(String username, String targetFriend, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.DELETE, callback,
                conf.getRemoveFriendUrl(username, targetFriend), "");
        queue.add(req);
    }

    @Override
    public void getFriendsFor(String username, RemoteCallback<Set<UserInfo>> callback) {
        GetRequest<Set<UserInfo>> req = new GetRequest<>(callback,
                conf.getFriendsUrl(username),
                new ObjectMapper().getTypeFactory().constructCollectionType(HashSet.class, UserInfo.class));
        queue.add(req);
    }

    @Override
    public void getLocationsFor(String username, Date start, Date end, RemoteCallback<List<LocationInfo>> callback) {
        GetRequest<List<LocationInfo>> req = new GetRequest<>(callback,
                conf.getLocationsUrl(username, TimeUtil.dateToString(start), TimeUtil.dateToString(end)),
                new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, LocationInfo.class));

        queue.add(req);
    }

    @Override
    public void putLocationsFor(String username, List<LocationInfo> locations, RemoteCallback<String> callback) {
        PushRequest<List<LocationInfo>> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getLocationsUrl(username, null, null), locations);

        queue.add(req);
    }

    @Override
    public void createGroup(String groupName, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.POST, callback,
                conf.getGroupCreationUrl(groupName), "");

        queue.add(req);
    }

    @Override
    public void getGroupsFor(String username, RemoteCallback<Set<GroupInfo>> callback) {
        GetRequest<Set<GroupInfo>> req = new GetRequest<>(callback,
                conf.getUserGroupsUrl(username),
                new ObjectMapper().getTypeFactory().constructCollectionType(HashSet.class, GroupInfo.class));
        queue.add(req);
    }

    @Override
    public void getGroupById(String groupId, RemoteCallback<GroupInfo> callback) {
        GetRequest<GroupInfo> req = new GetRequest<>(callback,
                conf.getGroupInfoUrl(groupId),
                new ObjectMapper().getTypeFactory().constructType(GroupInfo.class));

        queue.add(req);
    }

    @Override
    public void addUserToGroup(String groupId, String username, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getUserGroupModUrl(groupId, username), "");

        queue.add(req);
    }

    @Override
    public void removeUserFromGroup(String groupId, String username, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.DELETE, callback,
                conf.getUserGroupModUrl(groupId, username), "");

        queue.add(req);
    }

    @Override
    public void getGroupLocations(String groupId, Date start, Date end, RemoteCallback<Map<String, List<LocationInfo>>> callback) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType stringType = mapper.getTypeFactory().constructType(String.class);
        JavaType jt = mapper.getTypeFactory().constructCollectionType(ArrayList.class, LocationInfo.class);

        GetRequest<Map<String, List<LocationInfo>>> req = new GetRequest<>(callback,
                conf.getGroupLocationsUrl(groupId, TimeUtil.dateToString(start), TimeUtil.dateToString(end)),
                mapper.getTypeFactory().constructMapType(HashMap.class, stringType, jt));

        queue.add(req);
    }

    private class GetRequest<T> extends Request<T> {
        private RemoteCallback<T> callback;
        private String url;
        private JavaType type;

        public GetRequest(RemoteCallback<T> callback, String url, JavaType type) {
            super(Method.GET, url, new BaseErrorListener(callback));
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
            this.callback.onSuccess(response);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError{
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            addAuth(headers);
            return headers;
        }
    }

    private class PushRequest<B> extends Request<String> {
        private RemoteCallback<String> callback;
        private String url;
        private B body;

        public PushRequest(PushRequestType t, RemoteCallback<String> callback, String url, B body) {
            super(t.getMethod(), url, new BaseErrorListener(callback));
            this.body = body;
            this.callback = callback;
            this.url = url;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            try {
                return Response.success(new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers)),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                Log.d("Volley", e.toString());
            }
            return null;
        }

        @Override
        protected void deliverResponse(String response) {
            if (response == null) return;
            this.callback.onSuccess(response);
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            addAuth(headers);
            return headers;
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            try {
                return new ObjectMapper().writeValueAsBytes(body);
            } catch (JsonProcessingException e) {
                Log.d("Volley", e.toString());
            }
            return new byte[0];
        }
    }

    private enum PushRequestType {
        POST(Request.Method.POST), PUT(Request.Method.PUT), DELETE(Request.Method.DELETE);

        private final int method;

        PushRequestType(int method) {
            this.method = method;
        }

        public int getMethod() {
            return method;
        }
    }

    private class BaseErrorListener implements Response.ErrorListener {
        private RemoteCallback callback;

        public BaseErrorListener(RemoteCallback callback) {
            this.callback = callback;
        }
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                callback.onFailure(error.networkResponse.statusCode,
                        new String(error.networkResponse.data,
                                HttpHeaderParser.parseCharset(error.networkResponse.headers)));
            } catch(IOException io) {
                Log.d("Volley", io.toString());
            }
        }
    }

    private void addAuth(Map<String, String> headers) {
        SharedPrefsUserAccessor storage = new SharedPrefsUserAccessor(context);
        String username = storage.getUsername();
        String password = storage.getPassword();

        StringBuilder basicAuth = new StringBuilder();
        basicAuth.append(username);
        basicAuth.append(":");
        basicAuth.append(password);
        try {
            byte[] data = basicAuth.toString().getBytes("UTF-8");
            String base64Encoded = Base64.encodeToString(data, Base64.DEFAULT);
            String authToken = "Basic " + base64Encoded;
            headers.put("Authorization", authToken);
        } catch (UnsupportedEncodingException e) {
            Log.d("Volley", e.toString());
        }
    }
}
