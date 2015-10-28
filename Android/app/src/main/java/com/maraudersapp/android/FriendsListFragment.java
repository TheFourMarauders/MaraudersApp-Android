package com.maraudersapp.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.maraudersapp.android.FriendsListAdapter.FriendsListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 10/26/2015.
 */
public class FriendsListFragment extends ListFragment {

    private static final String FRIENDS_LIST_TAG = "FRIENDS_LIST";

    private ArrayAdapter<FriendsListItem> mAdapter;
    private Fragment mapFrag;

    public FriendsListFragment(Fragment mapFrag) {
        this.mapFrag = mapFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new FriendsListAdapter(getActivity(), new ArrayList<FriendsListItem>());

        ServerCommManager.getCommForContext(getContext()).getFriendsFor(
                new SharedPrefsUserAccessor(getContext()).getUsername(),
                new RemoteCallback<Set<UserInfo>>() {
                    @Override
                    public void onSuccess(Set<UserInfo> response) {
                        Log.i(FRIENDS_LIST_TAG, "Got friends: " + response.size());
                        populateListItems(response);
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        Log.i(FRIENDS_LIST_TAG, message);
                        //TODO
                    }
                });

        setListAdapter(mAdapter);
        // TODO loading screen?
    }

    private void populateListItems(Set<UserInfo> response) {
        mAdapter.clear();

        for (UserInfo user : response) {
            mAdapter.insert(new FriendsListItem(null, user.getFirstName() + " " + user.getLastName(),
                    user.getUsername()), mAdapter.getCount());
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // remove the dividers from the ListView of the ListFragment
        getListView().setDivider(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // retrieve theListView item
        // TODO clear back stack?
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().show(mapFrag).commit();
        fm.popBackStack();
    }
}
