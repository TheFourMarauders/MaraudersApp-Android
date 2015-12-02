package com.maraudersapp.android.mapdrawing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.maraudersapp.android.InputDialog;
import com.maraudersapp.android.R;
import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.maraudersapp.android.util.ServerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Class that represents a screen which can be drawable
 *
 * Created by Michael on 10/28/2015.
 */
public abstract class Poller implements Runnable {

    protected static final float DEFAULT_HUE = BitmapDescriptorFactory.HUE_BLUE;

    protected Handler handler;
    protected GoogleMap gMap;

    protected final SharedPrefsAccessor storage;
    protected final ServerComm remote;

    Poller(Handler handler, GoogleMap googleMap, Context ctx) {
        this.handler = handler;
        gMap = googleMap;
        storage = new SharedPrefsAccessor(ctx);
        remote = ServerCommManager.getCommForContext(ctx);
    }

    abstract void removeAllMarkings();

    void changeMap(GoogleMap map) {
        gMap = map;
    }

    /**
     * Default method for when the plus button is pressed
     *
     * @param ctx
     */
    public void onPlusPressed(final Activity ctx) {
        // Display "Create" dialog at the bottom of the screen.
        // https://github.com/soarcn/BottomSheet
        new BottomSheet.Builder(ctx).title("Create new...").sheet(R.menu.create).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(PollingManager.POLL_TAG, "Create new clicked: " + which);
                switch (which) {
                    case R.id.add_friend:
                        new InputDialog(ctx, "Send friend request by username", new InputDialog.OnTextEntered() {
                            @Override
                            public void onTextEntered(String text) {
                                if (text != null && !text.isEmpty()) {
                                    ServerUtil.sendFriendRequest(text, storage.getUsername(), remote, ctx);
                                }
                            }
                        }).show();
                        break;
                    case R.id.create_group:
                        new InputDialog(ctx, "Create new group by name", new InputDialog.OnTextEntered() {
                            @Override
                            public void onTextEntered(String text) {
                                if (text != null && !text.isEmpty()) {
                                    ServerUtil.addGroup(text, remote, ctx);
                                }
                            }
                        }).show();
                        break;
                }
            }
        }).show();
    }

    /**
     * Default method for when the minus button is pressed
     *
     * @param ctx
     */
    public void onMinusPressed(final Activity ctx) {
        new BottomSheet.Builder(ctx).title("Remove...").sheet(R.menu.remove).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(PollingManager.POLL_TAG, "Remove clicked " + which);
                switch (which) {
                    case R.id.remove_friend:
                        remote.getFriendsFor(storage.getUsername(),
                                new RemoteCallback<Set<UserInfo>>() {
                                    @Override
                                    public void onSuccess(Set<UserInfo> response) {
                                        showRemoveFriendPicker(ctx, new ArrayList<>(response));
                                    }

                                    @Override
                                    public void onFailure(int errorCode, String message) {
                                        // TODO
                                    }
                                });
                        break;
                }
            }
        }).show();
    }

    /**
     * Helper method for the minus button
     *
     * @param ctx
     * @param friends list of friends to remove from the current user's friend list
     */
    private void showRemoveFriendPicker(final Activity ctx, final List<UserInfo> friends) {

        BottomSheet sheet = new BottomSheet.Builder(ctx).title("Click friend to remove").sheet(R.menu.dynamic_menu)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String friendToRemove = friends.get(which).getUsername();
                        ServerUtil.removeFriend(storage.getUsername(), friendToRemove, remote, ctx);
                    }
                }).build();

        Menu menu = sheet.getMenu();

        // Populate pop-up menu
        for (int i = 0; i < friends.size(); i++) {
            UserInfo user = friends.get(i);
            //if (!members.contains(user.getUsername())) {
                menu.add(Menu.NONE, i, Menu.NONE, user.getFirstName() + " " + user.getLastName());
         //   }
        }

        sheet.show();
    }
}
