package com.maraudersapp.android.mapdrawing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.maraudersapp.android.InputDialog;
import com.maraudersapp.android.R;
import com.maraudersapp.android.remote.RemoteCallback;
import com.maraudersapp.android.remote.ServerComm;
import com.maraudersapp.android.remote.ServerCommManager;
import com.maraudersapp.android.storage.SharedPrefsUserAccessor;
import com.maraudersapp.android.util.ServerUtil;

/**
 * Created by Michael on 10/28/2015.
 */
public abstract class Poller implements Runnable {

    protected static final float DEFAULT_HUE = BitmapDescriptorFactory.HUE_BLUE;

    protected Handler handler;
    protected GoogleMap gMap;

    protected final SharedPrefsUserAccessor storage;
    protected final ServerComm remote;

    Poller(Handler handler, GoogleMap googleMap, Context ctx) {
        this.handler = handler;
        gMap = googleMap;
        storage = new SharedPrefsUserAccessor(ctx);
        remote = ServerCommManager.getCommForContext(ctx);
    }

    abstract void removeAllMarkings();

    /**
     * Assuming a poller represents a screen, what should happen when plus is pressed on that screen
     */
    public void onPlusPressed(final Activity ctx) {
        // Display "Create" dialog at the bottom of the screen.
        // https://github.com/soarcn/BottomSheet
        new BottomSheet.Builder(ctx).title("Create new...").sheet(R.menu.create).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(PollingManager.POLL_TAG, "Create new clicked: " + which);
                switch (which) {
                    case R.id.add_friend:
                        new InputDialog(ctx, "Send friend request by username", new InputDialog.OnTextEntered() {
                            @Override
                            public void onTextEntered(String text) {
                                if (text != null && !text.isEmpty()) {
                                    ServerUtil.sendFriendRequest(text, storage.getUsername(), remote, ctx);
                                }
                            }
                        }).show();
                        break;
                    case R.id.create_group:
                        new InputDialog(ctx, "Create new group by name", new InputDialog.OnTextEntered() {
                            @Override
                            public void onTextEntered(String text) {
                                if (text != null && !text.isEmpty()) {
                                    ServerUtil.addGroup(text, remote, ctx);
                                }
                            }
                        }).show();
                        break;
                }
            }
        }).show();
    }
}
