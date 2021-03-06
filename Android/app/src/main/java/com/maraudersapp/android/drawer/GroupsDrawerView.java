package com.maraudersapp.android.drawer;

import android.content.Context;
import android.view.View;

import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 10/27/2015.
 *
 * Drawer view displayed when the user clicks on the groups tab in the main drawer view
 */
public class GroupsDrawerView extends DrawerView {

    private List<DrawerItem> drawerItems;

    /**
     *
     * @param remote
     * @param storage
     * @param drawerManager
     * @param pm
     * @param ctx
     * @param groups groups that are presented in our new groups drawer view
     */
    public GroupsDrawerView(ServerComm remote, SharedPrefsAccessor storage,
                             final DrawerManager drawerManager, PollingManager pm, final Context ctx, Set<GroupInfo> groups) {
        super(remote, storage, drawerManager, pm, ctx);

        List<DrawerItem> items = new ArrayList<>();

        // TODO back arrow
        for (final GroupInfo group : groups) {
            items.add(new DrawerItem(new PrimaryDrawerItem().withName(group.getName())){
                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    drawerManager.onBackPressed();
                    drawerManager.onBackPressed();
                    drawerManager.setBarHeader(group.getName() + "'s Group History");
                    pollingManager.changePoller(
                            pollingManager.newGroupPoller(group.get_id(), group.getName(), group.getMembers(), ctx));
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
