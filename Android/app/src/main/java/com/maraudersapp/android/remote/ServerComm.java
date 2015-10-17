package com.maraudersapp.android.remote;

import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.datamodel.UserCreationInfo;
import com.maraudersapp.android.datamodel.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Matthew on 10/16/2015.
 */
public interface ServerComm {

    void createUser(UserCreationInfo u, RemoteCallback<String> callback);

    void sendFriendRequestTo(String username, RemoteCallback<String> callback);

    void getFriendRequestsFor(String username, RemoteCallback<Set<UserInfo>> callback);

    void acceptFriendRequestFrom(String username, RemoteCallback<String> callback);

    void removeFriend(String username, RemoteCallback<String> callback);

    void getFriendsFor(String username, RemoteCallback<Set<UserInfo>> callback);

    void getLocationsFor(String username, RemoteCallback<List<LocationInfo>> callback);

    void putLocationsFor(String username, RemoteCallback<String> callback);

    void createGroup(String groupName, RemoteCallback<String> callback);

    void getGroupsFor(String username, RemoteCallback<Set<GroupInfo>> callback);

    void getGroupById(String groupId, RemoteCallback<GroupInfo> callback);

    void addUserToGroup(String groupId, String username, RemoteCallback<String> callback);

    void removeUserFromGroup(String groupId, String username, RemoteCallback<String> callback);

    void getGroupLocations(String groupId, RemoteCallback<Map<String, List<LocationInfo>>> callback);
}
