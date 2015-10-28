package com.maraudersapp.android.drawer;

import android.view.View;

import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

/**
 * Structure for holding each item in the Navigation Drawer (left panel).
 *
 * Adding a new item to the drawer is as easy as adding a new enum instance here.
 * Just make sure that the identifier is unique. Items are added in the order they are
 * specified. handleClick will be called when the item is clicked in the UI.
 */
public abstract class DrawerItem {

    private IDrawerItem drawerItem;

    public DrawerItem(IDrawerItem drawerItem) {
        this.drawerItem = drawerItem;
    }

    public abstract void handleClick(View view, IDrawerItem drawerItem);

    public IDrawerItem getDrawerItem() {
        return drawerItem;
    }

}
