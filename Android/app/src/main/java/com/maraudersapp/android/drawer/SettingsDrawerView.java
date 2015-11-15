package com.maraudersapp.android.drawer;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.maraudersapp.android.R;
import com.maraudersapp.android.mapdrawing.PollingManager;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.storage.SharedPrefsAccessor;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 11/11/15.
 */
public class SettingsDrawerView extends DrawerView {
    private ToggleDrawerItem oldest, newest;
    private PrimaryDrawerItem customStart, customEnd;
    private DrawerItem customStartDrawerItem, customEndDrawerItem;
    private List<DrawerItem> drawerItems;
    private boolean oldestChecked, newestChecked;

    public SettingsDrawerView(ServerComm remote, SharedPrefsAccessor storage, final DrawerManager drawerManager, PollingManager pollingManager, Context ctx) {
        super(remote, storage, drawerManager, pollingManager, ctx);
        final List<DrawerItem> items = new ArrayList<>();

        items.add(new DrawerItem(new SecondaryDrawerItem()
                .withEnabled(false).withSelectable(false).withName("Locations Since:")
                .withTextColor(Color.BLACK).withTextColorRes(R.color.md_dark_primary_text)) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {}
        });

        oldestChecked = storage.isStartTimeNull();
        newestChecked = storage.isEndTimeNull();

        oldest = new ToggleDrawerItem().withEnabled(true).withName("Oldest Available").withSelectable(false)
                .withChecked(oldestChecked);


        oldest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                if (oldestChecked != isChecked) {
                    customStart.withEnabled(!isChecked);
                    drawerManager.updateAllItems();
                    oldestChecked = isChecked;
                }
            }
        });

        items.add(new DrawerItem(oldest) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {
                Log.d(DRAWER_TAG, "Oldest Available clicked");
            }
        });

        customStart = new PrimaryDrawerItem().withEnabled(true).withName("Custom")
                .withSelectable(false).withDisabledTextColor(Color.GRAY)
                .withDisabledTextColorRes(R.color.md_light_disabled).withEnabled(!storage.isStartTimeNull());

        customStartDrawerItem = new DrawerItem(customStart) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {
                // start a fragment that has date time pickers
                Log.d(DRAWER_TAG, "Custom start time clicked");
            }
        };
        items.add(customStartDrawerItem);



        items.add(new DrawerItem(new DividerDrawerItem().withEnabled(false)) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {}
        });
        items.add(new DrawerItem(new SecondaryDrawerItem().withEnabled(false).withSelectable(false)
                .withName("Locations Until:").withTextColor(Color.DKGRAY).withTextColorRes(R.color.md_dark_primary_text)) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {}
        });



        newest = new ToggleDrawerItem().withEnabled(true).withName("Newest Available")
                .withSelectable(false).withChecked(newestChecked);
        newest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                if (isChecked != newestChecked) {
                    customEnd.withEnabled(!isChecked);
                    drawerManager.updateAllItems();
                    newestChecked = isChecked;
                }
            }
        });

        items.add(new DrawerItem(newest) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {
                Log.d(DRAWER_TAG, "Newest Available clicked");
            }
        });

        customEnd = new PrimaryDrawerItem().withEnabled(true).withName("Custom")
                .withSelectable(false).withDisabledTextColor(Color.GRAY)
                .withDisabledTextColorRes(R.color.md_light_disabled).withEnabled(!storage.isEndTimeNull());

        customEndDrawerItem = new DrawerItem(customEnd) {
            @Override
            public void handleClick(View view, IDrawerItem drawerItem) {
                // start fragment with date time pickers for end
                Log.d(DRAWER_TAG, "Custom end time clicked");
            }
        };
        items.add(customEndDrawerItem);

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

    @Override
    public void cleanup() {
        customStart.withEnabled(!storage.isStartTimeNull());
        customEnd.withEnabled(!storage.isEndTimeNull());
        oldest.setOnCheckedChangeListener(null);
        newest.setOnCheckedChangeListener(null);
    }

}
