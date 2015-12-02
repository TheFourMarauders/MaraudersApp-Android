package com.maraudersapp.android.drawer;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 10/27/2015.
 *
 * Drawer that is displayed when the user clicks on the friends button in the main drawer view
 */
public class FriendsDrawerView extends DrawerView {

    private List<DrawerItem> drawerItems;

    /**
     * Constructor for drawer displayed when we click "friends"
     *
     * @param remote
     * @param storage
     * @param drawerManager
     * @param pm
     * @param ctx
     * @param friends our current friends
     * @param friendRequests friendrequest that are displayed in our new drawer
     */
    public FriendsDrawerView(final ServerComm remote, final SharedPrefsAccessor storage,
                             final DrawerManager drawerManager, PollingManager pm, final Context ctx,
                             Set<UserInfo> friends, Set<UserInfo> friendRequests) {
        super(remote, storage, drawerManager, pm, ctx);

        final List<DrawerItem> items = new ArrayList<>();
        // TODO back arrow
        for (final UserInfo user : friends) {
            final String username = user.getUsername();
            items.add(new DrawerItem(new PrimaryDrawerItem().withName(user.getFirstName() + " " + user.getLastName())) {
                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Specific friends clicked");
                    drawerManager.onBackPressed();
                    drawerManager.onBackPressed();
                    drawerManager.setBarHeader(user.getFirstName() + " " + user.getLastName() + "'s History");
                    pollingManager.changePoller(pollingManager.newFriendPoller(username, ctx));
                }
            });
        }

        items.add(new DrawerItem(new DividerDrawerItem().withEnabled(false)) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {}
        });

        items.add(new DrawerItem(new SecondaryDrawerItem().withEnabled(false).withSelectable(false).withName("Friend Requests")) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {}
        });

        for (final UserInfo request : friendRequests) {
            final String username = request.getUsername();
            items.add(new DrawerItem(new PrimaryDrawerItem().withSelectable(false).withName(request.getFirstName() + " " + request.getLastName())) {
                @Override
                public void handleClick(View view, final IDrawerItem drawerItem) {
                    Log.i(DRAWER_TAG, "Friend Request clicked");
                    final DrawerItem outer = this;
                    remote.acceptFriendRequest(storage.getUsername(), username, new RemoteCallback<String>() {
                        @Override
                        public void onSuccess(String response) {
                            items.remove(outer);
                            items.add(0, new DrawerItem(new PrimaryDrawerItem().withName(request.getFirstName() + " " + request.getLastName())) {

                                @Override
                                public void handleClick(View view, IDrawerItem drawerItem) {
                                    Log.i(DRAWER_TAG, "Specific friends clicked");
                                    drawerManager.onBackPressed();
                                    drawerManager.onBackPressed();
                                    drawerManager.setBarHeader(request.getFirstName() + " " + request.getLastName());
                                    pollingManager.changePoller(pollingManager.newFriendPoller(username, ctx));
                                }
                            });
                            drawerManager.updateAllItems();
                        }

                        @Override
                        public void onFailure(int errorCode, String message) {

                        }
                    });
                }
            });
        }
        drawerItems = items;
    }

    @Override
    public ArrayList<IDrawerItem> getAllItems() {
        return addItemsToList(drawerItems);
    }

    @Override
    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
        drawerItems.get(position - 1).handleClick(view, drawerItem);
        return true;
    }
}
