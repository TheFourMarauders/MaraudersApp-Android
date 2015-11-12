package com.maraudersapp.android;

import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.maraudersapp.android.drawer.DrawerManager;
import com.maraudersapp.android.mapdrawing.PollingManager;
import com.mikepenz.materialdrawer.Drawer;

/**
 * Created by Michael on 11/4/2015.
 */
public class StateFragment extends Fragment {

    // data object we want to retain
    private InstanceData data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(InstanceData data) {
        this.data = data;
    }

    public InstanceData getData() {
        return data;
    }

    public static class InstanceData {

        private String title;
        private PollingManager pollingManager;

        public InstanceData(String title, PollingManager pm) {
            pollingManager = pm;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public PollingManager getPollingManager() {
            return pollingManager;
        }
    }
}
