package com.maraudersapp.android.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;

/**
 * Multiple helper methods for communicating with server endpoints.
 */
public class ServerUtil {

    private static final String SERVER_TAG = "SERVER";

    /**
     * Sends a friend request from a user to another user. Displays Toast on result.
     *
     * @param usernameTo Username for user sending the friend request.
     * @param usernameFrom Username for user receiving the friend request.
     */
    public static void sendFriendRequest(String usernameTo, String usernameFrom, ServerComm remote, final Context ctx) {
        Log.i(SERVER_TAG, "Sending request to: " + usernameTo);
        remote.sendFriendRequest(usernameFrom, usernameTo,
                new RemoteCallback<String>() {

                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(ctx, "Friend request sent!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        // TODO look at possible failures
                        Toast.makeText(ctx, "Friend request could not be sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Creates a group with no users. Displays Toast on result.
     *
     * @param groupName name of group to create
     */
    public static void addGroup(String groupName, ServerComm remote, final Context ctx) {
        Log.i(SERVER_TAG, "Creating group: " + groupName);
        remote.createGroup(groupName, new RemoteCallback<String>() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(ctx, "Group created!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                // TODO look at possible failures
                Toast.makeText(ctx, "Group could not be created", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds a user to a group. Displays Toast on result.
     *
     * @param groupId group ID for the group
     * @param friend username to add to the group
     */
    public static void addFriendToGroup(String groupId, String friend, ServerComm remote, final Context ctx) {
        Log.i(SERVER_TAG, "Adding user " + friend + " to group: " + groupId);
        remote.addUserToGroup(groupId, friend,
                new RemoteCallback<String>() {

                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(ctx, "Friend added!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        // TODO look at possible failures
                        Toast.makeText(ctx, "Friend could not be added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Removes a friend from a certain user. Displays Toast on result.
     *
     * @param username Username to remove the friend from.
     * @param target Username of friend to remove.
     */
    public static void removeFriend(String username, String target, ServerComm remote, final Context ctx) {
        Log.i(SERVER_TAG, "Removing friend: " + target);
        remote.removeFriend(username, target, new RemoteCallback<String>() {

            @Override
            public void onSuccess(String response) {
                Toast.makeText(ctx, "Friend removed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int errorCode, String message) {
                // TODO look at possible failures
                Toast.makeText(ctx, "Friend could not be removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Removes user from a group.  Displays Toast on result.
     *
     * @param groupId group ID of group.
     * @param friend username of person to remove.
     */
    public static void removeUserFromGroup(String groupId, String friend, ServerComm remote, final Context ctx) {
        Log.i(SERVER_TAG, "Removing user from group " + groupId);
        remote.removeUserFromGroup(groupId, friend,
                new RemoteCallback<String>() {

                    @Override
                    public void onSuccess(String response) {
                        Toast.makeText(ctx, "User from group removed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        // TODO look at possible failures
                        Toast.makeText(ctx, "User from group could not be removed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
