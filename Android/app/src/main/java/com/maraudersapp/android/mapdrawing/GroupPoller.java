package com.maraudersapp.android.mapdrawing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maraudersapp.android.InputDialog;
import com.maraudersapp.android.R;
import com.maraudersapp.android.datamodel.LocationInfo;
import com.maraudersapp.android.datamodel.UserInfo;
import com.maraudersapp.android.location.AndroidCompressor;
import com.maraudersapp.android.location.HaversineCompressor;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.util.ServerUtil;
import com.maraudersapp.android.util.TimeUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Michael on 10/29/2015.
 */
public class GroupPoller extends Poller {

    private final String groupId;
    private final String groupName;
    private List<Marker> currentMarkers;
    private final Collection<String> members;

    GroupPoller(Handler handler, GoogleMap googleMap, Context ctx, String groupId, String groupName, Collection<String> members) {
        super(handler, googleMap, ctx);
        this.groupId = groupId;
        this.members = members;
        this.groupName = groupName;
        currentMarkers = new ArrayList<>();
    }

    @Override
    public void run() {
        remote.getGroupLocations(groupId,
                storage.getStartTime(),
                storage.getEndTime(),
                new RemoteCallback<Map<String, List<LocationInfo>>>() {
                    @Override
                    public void onSuccess(Map<String, List<LocationInfo>> response) {
                        if (!response.isEmpty()) {
                            removeAllMarkings();
                            float hue = response.size() != 1 ? 359.0f / response.size() : DEFAULT_HUE;
                            float hStep = hue;
                            for (String user : response.keySet()) {
                                List<LocationInfo> userLocs = new HaversineCompressor().filter(response.get(user));
                                float opacity = 1.0f / userLocs.size();
                                float oStep = opacity;
                                for (LocationInfo locInfo : userLocs) {
                                    currentMarkers.add(gMap.addMarker(new MarkerOptions().position(
                                            new LatLng(locInfo.getLatitude(), locInfo.getLongitude()))
                                            .alpha(opacity).icon(BitmapDescriptorFactory.defaultMarker(hue))));
                                    opacity += oStep;
                                }
                                hue += hStep;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {

                    }
                });
        handler.postDelayed(this, PollingManager.POLL_INTERVAL);
    }

    @Override
    void removeAllMarkings() {
        for (Marker marker : currentMarkers) {
            marker.remove();
        }
        currentMarkers.clear();
    }

    @Override
    public void onPlusPressed(final Activity ctx) {
        remote.getFriendsFor(storage.getUsername(),
                new RemoteCallback<Set<UserInfo>>() {
                    @Override
                    public void onSuccess(Set<UserInfo> response) {
                        showFriendPicker(ctx, new ArrayList<>(response));
                    }

                    @Override
                    public void onFailure(int errorCode, String message) {
                        // TODO
                    }
                });
    }

    /**
     * Shows add friend dialog
     */
    private void showFriendPicker(final Activity ctx, final List<UserInfo> friends) {

        BottomSheet sheet = new BottomSheet.Builder(ctx).title("Add friend to " + groupName + ":").sheet(R.menu.dynamic_menu)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String friendToAdd = friends.get(which).getUsername();
                        ServerUtil.addFriendToGroup(groupId, friendToAdd, remote, ctx);
                    }
                }).build();

        Menu menu = sheet.getMenu();

        // Populate pop-up menu
        for (int i = 0; i < friends.size(); i++) {
            UserInfo user = friends.get(i);
            if (!members.contains(user.getUsername())) {
                menu.add(Menu.NONE, i, Menu.NONE, user.getFirstName() + " " + user.getLastName());
            }
        }

        sheet.show();
    }
}
