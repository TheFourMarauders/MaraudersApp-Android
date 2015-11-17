package com.maraudersapp.android.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;

/**
 * Created by Michael on 11/3/2015.
 */
public class ServerUtil {

    private static final String SERVER_TAG = "SERVER";

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
}
