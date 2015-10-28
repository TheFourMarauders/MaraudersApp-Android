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

    private Stack<DrawerView> backStack;

    private final Drawer drawer;
    private final Toolbar toolbar;

    public DrawerManager(Drawer drawer, Toolbar toolbar, Context ctx) {
        this.drawer = drawer;
        this.toolbar = toolbar;
        this.backStack = new Stack<>();


        DrawerView initialView = new MainDrawerView(ServerCommManager.getCommForContext(ctx),
                new SharedPrefsUserAccessor(ctx), this);
        backStack.add(initialView);

        drawer.switchDrawerContent(initialView, initialView.getAllItems(), -1);
    }

    public void addView(DrawerView drawerView) {
        Log.i("Drawer", drawerView.getAllItems().size() + " ");
        backStack.push(drawerView);
        drawer.switchDrawerContent(drawerView, drawerView.getAllItems(), 0);
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
        } else if (backStack.size() > 1) {
            backStack.pop();
            DrawerView curView = backStack.peek();
            drawer.switchDrawerContent(curView, curView.getAllItems(), -1);
            return true;
        } else {
            drawer.closeDrawer();
            return true;
        }
    }
}
