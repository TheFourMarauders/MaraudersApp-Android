package com.maraudersapp.android;

import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

import java.util.List;
import java.util.Set;

/**
 * Created by Michael on 10/26/2015.
 */
public class FriendsListFragment extends ListFragment {

    private List<FriendsListItem> mItems;        // ListView items list

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServerCommManager.getCommForContext(getContext()).getFriendsFor(
                new SharedPrefsUserAccessor(getContext()).getUsername(),
                new RemoteCallback<Set<UserInfo>>() {
                    @Override
                    public void onSuccess(Set<UserInfo> response) {
                        populateListItems(response);
                        setListAdapter(new FriendsListAdapter(getActivity(), mItems));
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        //TODO
                    }
                });

        // TODO loading screen?
    }

    private void populateListItems(Set<UserInfo> response) {
        for (UserInfo user : response) {
            mItems.add(new FriendsListItem(null, user.getFirstName() + " " + user.getLastName(),
                    user.getUsername()));
        }
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
        FriendsListItem item = mItems.get(position);
        FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.replace(R.id.main_parent_view, Frag);
        tx.commit();
    }
}
