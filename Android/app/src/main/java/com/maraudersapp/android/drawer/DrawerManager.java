package com.maraudersapp.android.drawer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * This classes only responsiblity is to add and remove from drawer views.
 *
 * Navigation flow should be implemented through DrawerItem click handlers
 */
public class DrawerManager {

    private final Drawer drawer;
    private final Toolbar toolbar;

    private boolean atMainView;

    private DrawerView currentDrawerView;
    private DrawerView mainDrawerView;

    public DrawerManager(Drawer drawer, Toolbar toolbar, Context ctx, PollingManager pm) {
        this.drawer = drawer;
        this.toolbar = toolbar;

        mainDrawerView = new MainDrawerView(ServerCommManager.getCommForContext(ctx),
                new SharedPrefsAccessor(ctx), this, pm, ctx);
        drawer.setItems(mainDrawerView.getAllItems());
        drawer.setOnDrawerItemClickListener(mainDrawerView);
        atMainView = true;
        this.currentDrawerView = mainDrawerView;
    }

    public void switchView(DrawerView drawerView) {
        Log.i("Drawer", drawerView.getAllItems().size() + " ");
        //drawer.switchDrawerContent(drawerView, drawerView.getAllItems(), 0);
        drawer.setItems(drawerView.getAllItems());
        drawer.setOnDrawerItemClickListener(drawerView);
        atMainView = false;
        this.currentDrawerView = drawerView;
    }

    public void updateAllItems() {
//        try {
//            drawer.resetDrawerContent();
//            drawer.switchDrawerContent(currentDrawerView, currentDrawerView.getAllItems(), 0);
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//            Log.d("Drawer", e.toString());
//        }
        drawer.setItems(currentDrawerView.getAllItems());
    }

    public void setBarHeader(String header) {
        toolbar.setTitle(header);
    }

    public boolean onBackPressed() {
        if (drawer == null) {
            Log.wtf("Drawer", "Why is this null?");
            return false; // should never happen
        }

        if (!drawer.isDrawerOpen()) {
            return false;
        } else if (!atMainView) {
            currentDrawerView.cleanup();
            //drawer.resetDrawerContent();
            drawer.setItems(mainDrawerView.getAllItems());
            drawer.setOnDrawerItemClickListener(mainDrawerView);
            atMainView = true;
            currentDrawerView = mainDrawerView;
            return true;
        } else {
            drawer.closeDrawer();
            return true;
        }
    }
}
