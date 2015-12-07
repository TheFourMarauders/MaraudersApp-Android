package com.maraudersapp.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

/**
 * Creates a dialog to display to the user with a input text box and an enter button.
 */
public class InputDialog extends AlertDialog.Builder {

    /**
     * Used as a callback for when text is entered.
     */
    public interface OnTextEntered {

        /**
         * Called when text is entered.
         *
         * @param text Text that was entered.
         */
        public void onTextEntered(String text);
    }

    /**
     * @param title Title to display on dialog
     * @param callback Callback for when text is entered.
     */
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
