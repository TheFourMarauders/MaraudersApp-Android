package com.maraudersapp.android.drawer;

import android.view.View;

import com.maraudersapp.android.datamodel.GroupInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 10/27/2015.
 */
public class GroupsDrawerView extends DrawerView {

    private List<DrawerItem> drawerItems;

    public GroupsDrawerView(ServerComm remote, SharedPrefsUserAccessor storage,
                             DrawerManager drawerManager, Set<GroupInfo> groups) {
        super(remote, storage, drawerManager);

        List<DrawerItem> items = new ArrayList<>();

        // TODO back arrow
        for (GroupInfo group : groups) {
            items.add(new DrawerItem(new PrimaryDrawerItem().withName(group.getName())){
                @Override
                public void handleClick(View view, IDrawerItem drawerItem) {
                    // TODO
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
