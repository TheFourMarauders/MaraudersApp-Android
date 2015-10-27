package com.maraudersapp.android;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Michael on 10/26/2015.
 */
class FriendsListAdapter extends ArrayAdapter<FriendsListAdapter.FriendsListItem> {

    public FriendsListAdapter(Context context, List<FriendsListItem> items) {
        super(context, R.layout.friends_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null) {
            // inflate the GridView item layout
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.friends_list_item, parent, false);

            // initialize the view holder
            viewHolder = new ViewHolder();
            viewHolder.userIcon = (ImageView) convertView.findViewById(R.id.userIcon);
            viewHolder.userFullname = (TextView) convertView.findViewById(R.id.userFullname);
            viewHolder.username = (TextView) convertView.findViewById(R.id.username);
            convertView.setTag(viewHolder);
        } else {
            // recycle the already inflated view
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // update the item view
        FriendsListItem item = getItem(position);
        viewHolder.userIcon.setImageDrawable(item.icon);
        viewHolder.userFullname.setText(item.name);
        viewHolder.username.setText(item.name);

        return convertView;
    }

    /**
     * The view holder design pattern prevents using findViewById()
     * repeatedly in the getView() method of the adapter.
     *
     * http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
     */
    private static class ViewHolder {
        ImageView userIcon;
        TextView userFullname;
        TextView username;
    }

    public static class FriendsListItem {
        private final Drawable icon;       // TODO user pictures
        private final String name;
        private final String username;

        public FriendsListItem(Drawable icon, String name, String username) {
            this.icon = icon;
            this.name = name;
            this.username = username;
        }

        public Drawable getIcon() {
            return icon;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }
    }
}
