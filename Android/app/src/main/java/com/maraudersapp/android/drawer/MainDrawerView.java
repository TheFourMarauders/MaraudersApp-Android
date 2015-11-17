package com.maraudersapp.android.drawer;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.maraudersapp.android.LoginActivity;
import com.maraudersapp.android.R;
import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.location.LocationUpdaterService;
import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by Michael on 10/27/2015.
 */
public class MainDrawerView extends DrawerView {

    private final DrawerItem[] drawerItems = {
            // Yourself
            new DrawerItem(new PrimaryDrawerItem().withName(R.string.yourself_item_name)
                    .withIdentifier(1).withSelectable(false)) {

                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Yourself clicked");
                    drawerManager.setBarHeader("Your History");
                    drawerManager.onBackPressed();
                    pollingManager.changePoller(pollingManager.newUserPoller(ctx));
                }

            },

            // Friends
            new DrawerItem(new PrimaryDrawerItem().withName(R.string.friends_item_name)
                    .withIdentifier(2).withSelectable(false)) {

                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Friends clicked");
                    remote.getFriendsFor(
                        storage.getUsername(),
                        new RemoteCallback<Set<UserInfo>>() {
                            @Override
                            public void onSuccess(Set<UserInfo> response) {
                                final Set<UserInfo> friends = response;
                                remote.getFriendRequestsFor(storage.getUsername(), new RemoteCallback<Set<UserInfo>>() {
                                    @Override
                                    public void onSuccess(Set<UserInfo> response) {
                                        final DrawerView newView = new FriendsDrawerView(remote, storage,
                                                drawerManager, pollingManager, ctx, friends, response);

                                        Log.i(DRAWER_TAG, "Friend response received");
                                        Log.i(DRAWER_TAG, response.toString());
                                        drawerManager.switchView(newView);
                                    }
                                    @Override
                                    public void onFailure(int errorCode, String message) {
                                        // TODO
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int errorCode, String message) {
                                // TODO error view
                            }
                        }
                    );
                }

            },

            // Groups
            new DrawerItem(new PrimaryDrawerItem().withName(R.string.groups_item_name)
                    .withIdentifier(3).withSelectable(false)) {

                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Groups clicked");
                    remote.getGroupsFor(
                            storage.getUsername(),
                            new RemoteCallback<Set<GroupInfo>>() {
                                @Override
                                public void onSuccess(Set<GroupInfo> response) {
                                    DrawerView newView = new GroupsDrawerView(remote, storage,
                                            drawerManager, pollingManager, ctx, response);
                                    drawerManager.switchView(newView);
                                }

                                @Override
                                public void onFailure(int errorCode, String message) {
                                    // TODO error view
                                }
                            }
                    );
                }

            },

            // Divider
            new DrawerItem(new DividerDrawerItem().withEnabled(false)) {
                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {}
            },

            // Incognito
            new DrawerItem(new ToggleDrawerItem().withName(R.string.incognito_item_name)
                    .withIdentifier(4).withSelectable(false).withChecked(storage.isIncognito())
                    .withOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {
                            if (storage.isIncognito()) {
                                Log.i(DRAWER_TAG, "Stopping Incognito");
                                LocationUpdaterService.scheduleLocationPolling(ctx);
                            } else {
                                Log.i(DRAWER_TAG, "Incognito started");
                                LocationUpdaterService.stopLocationPolling(ctx);
                            }
                            storage.setIncognito(!storage.isIncognito());
                        }
                    })) {

                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {}

            },

            // Settings
            new DrawerItem(new PrimaryDrawerItem().withName(R.string.settings_item_name)
                    .withIdentifier(5).withSelectable(false)) {

                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Settings clicked");
                    DrawerView newView = new SettingsDrawerView(remote, storage,
                            drawerManager, pollingManager, ctx);
                    drawerManager.switchView(newView);
                }

            },

            // Logout
            new DrawerItem(new PrimaryDrawerItem().withName(R.string.logout_item_name)
                    .withIdentifier(6).withSelectable(false)) {

                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Logout clicked");
                    logout(view);
                }

            },
    };

    public MainDrawerView(ServerComm remote, SharedPrefsAccessor storage, DrawerManager drawerManager,
                          PollingManager pollingManager, Context ctx) {
        super(remote, storage, drawerManager, pollingManager, ctx);
    }

    @Override
    public ArrayList<IDrawerItem> getAllItems() {
        return addItemsToList(Arrays.asList(drawerItems));
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        drawerItems[position - 1].handleClick(view, drawerItem);
        return true;
    }

    private void logout(View view) {
        LocationUpdaterService.stopLocationPolling(ctx);
        pollingManager.stopPolling();
        Intent i = new Intent(view.getContext(), LoginActivity.class);
        i.putExtra("nullify", true);
        view.getContext().startActivity(i);
    }

}
