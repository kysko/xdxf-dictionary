package com.xdxf.dictionary.utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/27/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class MessageHandler extends Handler {
    private Activity ctx;

    public MessageHandler(Activity ctx) {
        this.ctx = ctx;
    }

    public void handleMessage(Message message) {
        if (message.arg1 == Activity.RESULT_OK) {
            Toast.makeText(ctx,
                    "Added", Toast.LENGTH_LONG)
                    .show();
        } else {
            Toast.makeText(ctx, "failed.",
                    Toast.LENGTH_LONG).show();
        }

    }


}
