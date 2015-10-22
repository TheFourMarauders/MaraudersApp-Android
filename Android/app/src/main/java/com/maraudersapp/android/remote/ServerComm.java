package com.maraudersapp.android.remote;

import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.datamodel.UserCreationInfo;
import com.maraudersapp.android.datamodel.UserInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matthew on 10/16/2015.
 */
public interface ServerComm {

    void createUser(UserCreationInfo u, RemoteCallback<String> callback);

    void sendFriendRequest(String sourceUsername, String targetUsername, RemoteCallback<String> callback);

    void getFriendRequestsFor(String username, RemoteCallback<Set<UserInfo>> callback);

    void acceptFriendRequest(String username, String targetFriend, RemoteCallback<String> callback);

    void removeFriend(String username, String targetFriend, RemoteCallback<String> callback);

    void getFriendsFor(String username, RemoteCallback<Set<UserInfo>> callback);

    void getLocationsFor(String username, Date start, Date end, RemoteCallback<List<LocationInfo>> callback);

    void putLocationsFor(String username, List<LocationInfo> locations, RemoteCallback<String> callback);

    void createGroup(String groupName, RemoteCallback<String> callback);

    void getGroupsFor(String username, RemoteCallback<Set<GroupInfo>> callback);

    void getGroupById(String groupId, RemoteCallback<GroupInfo> callback);

    void addUserToGroup(String groupId, String username, RemoteCallback<String> callback);

    void removeUserFromGroup(String groupId, String username, RemoteCallback<String> callback);

    void getGroupLocations(String groupId, Date start, Date end, RemoteCallback<Map<String, List<LocationInfo>>> callback);
}
