package com.maraudersapp.android.remote;

import android.content.Context;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.datamodel.UserCreationInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
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
 * Class that communicates to server using url's and adds these requests to a queue of requests
 *
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

    /**
     * Uses a post request to create a new user
     *
     * @param u json for the user that will be created
     * @param callback
     */
    @Override
    public void createUser(UserCreationInfo u, final RemoteCallback<String> callback) {
        PushRequest<UserCreationInfo> req = new PushRequest<>(PushRequestType.POST, callback,
                conf.getUserCreationUrl(), u);

        queue.add(req);

    }

    /**
     * Push request to send a friend request
     *
     * @param sourceUsername currently logged in user
     * @param targetUsername user that we want to send friend request to
     * @param callback
     */
    @Override
    public void sendFriendRequest(String sourceUsername, String targetUsername,
                                  RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getSendFriendRequestUrl(sourceUsername, targetUsername), "");
        queue.add(req);
    }

    /**
     * Get request to get the friend requests for the logged in user
     *
     * @param username currently logged in user
     * @param callback
     */
    @Override
    public void getFriendRequestsFor(String username, RemoteCallback<Set<UserInfo>> callback) {
        GetRequest<Set<UserInfo>> req = new GetRequest<>(callback,
                conf.getIncomingFriendReqUrl(username),
                new ObjectMapper().getTypeFactory().constructCollectionType(HashSet.class, UserInfo.class));
        queue.add(req);
    }

    /**
     * Push request to accept a pending friend request
     *
     * @param username currently logged in user
     * @param targetFriend friend to accept request of
     * @param callback
     */
    @Override
    public void acceptFriendRequest(String username, String targetFriend, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getAcceptFriendReqUrl(username, targetFriend), "");
        queue.add(req);
    }

    /**
     * Push request to remove friend
     *
     * @param username currently logged in user
     * @param targetFriend friend to remove
     * @param callback
     */
    @Override
    public void removeFriend(String username, String targetFriend, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.DELETE, callback,
                conf.getRemoveFriendUrl(username, targetFriend), "");
        queue.add(req);
    }

    /**
     * Get request to get friends of currently logged in user
     *
     * @param username currently logged in user
     * @param callback
     */
    @Override
    public void getFriendsFor(String username, RemoteCallback<Set<UserInfo>> callback) {
        GetRequest<Set<UserInfo>> req = new GetRequest<>(callback,
                conf.getFriendsUrl(username),
                new ObjectMapper().getTypeFactory().constructCollectionType(HashSet.class, UserInfo.class));
        queue.add(req);
    }

    /**
     * Get request to get the locations for the user
     *
     * @param username user to get locations of
     * @param start start time
     * @param end end time
     * @param callback
     */
    @Override
    public void getLocationsFor(String username, Date start, Date end, RemoteCallback<List<LocationInfo>> callback) {
        GetRequest<List<LocationInfo>> req = new GetRequest<>(callback,
                conf.getLocationsUrl(username, TimeUtil.dateToString(start), TimeUtil.dateToString(end)),
                new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, LocationInfo.class));
        Log.d("remote location", conf.getLocationsUrl(username, TimeUtil.dateToString(start), TimeUtil.dateToString(end)));
        queue.add(req);
    }

    /**
     * Push request to put new locations for the user
     *
     * @param username user
     * @param locations list of locations to put
     * @param callback
     */
    @Override
    public void putLocationsFor(String username, List<LocationInfo> locations, RemoteCallback<String> callback) {
        PushRequest<List<LocationInfo>> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getLocationsUrl(username, null, null), locations);

        queue.add(req);
    }

    /**
     * Push request to create a new group
     *
     * @param groupName name of group to create
     * @param callback
     */
    @Override
    public void createGroup(String groupName, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.POST, callback,
                conf.getGroupCreationUrl(groupName), "");

        queue.add(req);
    }

    /**
     * Get request to get the groups for the currently logged in user
     *
     * @param username user to get groups for
     * @param callback
     */
    @Override
    public void getGroupsFor(String username, RemoteCallback<Set<GroupInfo>> callback) {
        GetRequest<Set<GroupInfo>> req = new GetRequest<>(callback,
                conf.getUserGroupsUrl(username),
                new ObjectMapper().getTypeFactory().constructCollectionType(HashSet.class, GroupInfo.class));
        queue.add(req);
    }

    /**
     * Get request to get the group from the group id
     *
     * @param groupId id of group
     * @param callback
     */
    @Override
    public void getGroupById(String groupId, RemoteCallback<GroupInfo> callback) {
        GetRequest<GroupInfo> req = new GetRequest<>(callback,
                conf.getGroupInfoUrl(groupId),
                new ObjectMapper().getTypeFactory().constructType(GroupInfo.class));

        queue.add(req);
    }

    /**
     * Push request to add a user to a group
     *
     * @param groupId id of the group
     * @param username user to add to group
     * @param callback
     */
    @Override
    public void addUserToGroup(String groupId, String username, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.PUT, callback,
                conf.getUserGroupModUrl(groupId, username), "");

        queue.add(req);
    }

    /**
     * Push request to remove user from group
     *
     * @param groupId id of group
     * @param username user to remove from group
     * @param callback
     */
    @Override
    public void removeUserFromGroup(String groupId, String username, RemoteCallback<String> callback) {
        PushRequest<String> req = new PushRequest<>(PushRequestType.DELETE, callback,
                conf.getUserGroupModUrl(groupId, username), "");

        queue.add(req);
    }

    /**
     * Get request to get the locations of group members
     *
     * @param groupId id of group
     * @param start start time
     * @param end end time
     * @param callback
     */
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

    /**
     * Specifics the format of a get request
     *
     * @param <T> generic type
     */
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

    /**
     * Specifies format of push request
     *
     * @param <B> generic type
     */
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
                if (error.networkResponse == null) {
                    callback.onFailure(444, "No Response");
                    return;
                }
                callback.onFailure(error.networkResponse.statusCode,
                        new String(error.networkResponse.data,
                                HttpHeaderParser.parseCharset(error.networkResponse.headers)));
            } catch(IOException io) {
                Log.d("Volley", io.toString());
            }
        }
    }

    /**
     * Authentication method
     *
     * @param headers
     */
    private void addAuth(Map<String, String> headers) {
        SharedPrefsAccessor storage = new SharedPrefsAccessor(context);
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
