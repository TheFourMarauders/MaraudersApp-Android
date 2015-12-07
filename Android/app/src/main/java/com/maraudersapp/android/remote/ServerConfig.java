package com.maraudersapp.android.remote;

/**
 * Class for getting the url strings to communicate with server
 *
 * Created by Matthew on 10/16/2015.
 */
public class ServerConfig {
    private final String SERVICES;
    private final String BASE_URL;


    private int port;
    private String url;

    public ServerConfig(String url, int port) {
        this.port = port;
        this.url = url;
        BASE_URL = url + ":" + port;
        SERVICES = BASE_URL + "/api/services";
    }

    public String getUserCreationUrl() {
        return BASE_URL + "/api/create-user";
    }

    public String getSendFriendRequestUrl(String username, String targetusername) {
        return SERVICES +  "/user/" + username + "/send-friend-request/" + targetusername;
    }

    public String getIncomingFriendReqUrl(String username) {
        return SERVICES + "/user/" + username + "/incoming-friend-requests";
    }

    public String getAcceptFriendReqUrl(String username, String targetusername) {
        return SERVICES + "/user/" + username + "/accept-friend/" + targetusername;
    }

    public String getRemoveFriendUrl(String username, String targetusername) {
        return SERVICES + "/user/" + username + "/delete-friend/" + targetusername;
    }

    public String getFriendsUrl(String username) {
        return SERVICES + "/user/" + username + "/friends";
    }

    public String getLocationsUrl(String username, String start, String end) {
        return SERVICES + "/user/" + username + "/locations" + queryParams(start, end);
    }

    public String getGroupCreationUrl(String groupName) {
        return SERVICES + "/group/create" + "?groupname=" + groupName;
    }

    public String getUserGroupsUrl(String username) {
        return SERVICES + "/user/" + username + "/groups";
    }

    public String getGroupInfoUrl(String groupId) {
        return SERVICES + "/group/" + groupId;
    }

    public String getUserGroupModUrl(String groupId, String username) {
        return SERVICES + "/group/" + groupId + "/user/" + username;
    }

    public String getGroupLocationsUrl(String groupId, String start, String end) {
        return SERVICES + "/group/" + groupId + "/locations" + queryParams(start, end);
    }

    private String queryParams(String start, String end) {
        StringBuilder sb = new StringBuilder();
        if (start != null && !start.isEmpty()) {
            sb.append("?start=" + start);
            if (end != null && !end.isEmpty()) {
                sb.append("&end=" + end);
            }
        } else if (end != null && !end.isEmpty()) {
            sb.append("?end=" + end);
        }
        return sb.toString();
    }
}
