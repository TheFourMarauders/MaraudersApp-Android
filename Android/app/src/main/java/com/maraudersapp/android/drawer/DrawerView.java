package com.maraudersapp.android.drawer;

import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Michael on 10/27/2015.
 */
public abstract class DrawerView implements Drawer.OnDrawerItemClickListener {

    protected static final String DRAWER_TAG = "Drawer";

    // TODO passing this everywhere is dumb. maybe factory?
    protected final SharedPrefsUserAccessor storage;
    protected final ServerComm remote;
    protected final DrawerManager drawerManager;
    protected final PollingManager pollingManager;
    protected final Context ctx;

    public DrawerView(ServerComm remote, SharedPrefsUserAccessor storage, DrawerManager drawerManager,
                      PollingManager pollingManager, Context ctx) {
        this.storage = storage;
        this.remote = remote;
        this.pollingManager = pollingManager;
        this.drawerManager = drawerManager;
        this.ctx = ctx;
    }

    public abstract ArrayList<IDrawerItem> getAllItems();

    @Override
    public abstract boolean onItemClick(View view, int position, IDrawerItem drawerItem);

    protected static ArrayList<IDrawerItem> addItemsToList(Collection<DrawerItem> items) {
        ArrayList<IDrawerItem> res = new ArrayList<>();
        for (DrawerItem item : items) {
            res.add(item.getDrawerItem());
        }
        return res;
    }
}
