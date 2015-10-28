package com.maraudersapp.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Michael on 10/28/2015.
 */
public class InputDialog extends AlertDialog.Builder {

    public interface OnTextEntered {
        public void onTextEntered(String text);
    }

    public InputDialog(Context ctx, String title, final OnTextEntered callback) {
        super(ctx);
        setTitle(title);
        final EditText input = new EditText(ctx);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        setView(input);
        setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("Input dialog", "Text entered: " + input.getText().toString());
                callback.onTextEntered(input.getText().toString());
            }
        });
    }

}
