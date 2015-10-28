package com.maraudersapp.android.drawer;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.mikepenz.materialdrawer.Drawer;

import java.util.Stack;

/**
 * This classes only responsiblity is to add and remove from drawer views.
 *
 * Navigation flow should be implemented through DrawerItem click handlers
 */
public class DrawerManager {

    private final Drawer drawer;
    private final Toolbar toolbar;

    private boolean atMainView;

    public DrawerManager(Drawer drawer, Toolbar toolbar, Context ctx) {
        this.drawer = drawer;
        this.toolbar = toolbar;

        DrawerView mainDrawerView = new MainDrawerView(ServerCommManager.getCommForContext(ctx),
                new SharedPrefsUserAccessor(ctx), this);
        drawer.setItems(mainDrawerView.getAllItems());
        drawer.setOnDrawerItemClickListener(mainDrawerView);
        atMainView = true;
    }

    public void switchView(DrawerView drawerView) {
        Log.i("Drawer", drawerView.getAllItems().size() + " ");
        drawer.switchDrawerContent(drawerView, drawerView.getAllItems(), 0);
        atMainView = false;
    }

    public void setBarHeader(String header) {
        toolbar.setTitle(header);
    }

    public boolean onBackPressed() {
        if (drawer == null) {
            Log.w("Drawer", "Why is this null?");
            return false; // should never happen
        }

        if (!drawer.isDrawerOpen()) {
            return false;
        } else if (!atMainView) {
            drawer.resetDrawerContent();
            atMainView = true;
            return true;
        } else {
            drawer.closeDrawer();
            return true;
        }
    }
}
