package com.xdxf.dictionary.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.xdxf.dictionary.R;
import com.xdxf.dictionary.parser.DictionaryParser;
import com.xdxf.dictionary.parser.DictionaryParser.WordAddedCallback;
import com.xdxf.dictionary.parser.XDXFParser;
import com.xdxf.dictionary.sqlite.DBHelper;

import java.io.IOException;

public class AddXDXFDocumentService extends IntentService {


    public AddXDXFDocumentService() {
        super("AddXDXFDocumentService");


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        DBHelper dbHelper;
        Messenger messenger;
        if (extras == null) {
            //TODO: possibly throw exception
            return;
        }
        dbHelper = DBHelper.getInstance(this);
        messenger = (Messenger) extras.get("Messenger");

        if (dbHelper == null) {
            //TODO: possibly throw exception
            return;
        }
        final NotificationManager mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final android.support.v4.app.NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        mBuilder.setContentTitle("Adding Dictionary")
                .setContentText("Please wait...")
                .setSmallIcon(R.drawable.book_gear);
        //Uri data = intent.getData();
        String urlPath = intent.getStringExtra("path");
        DictionaryParser dp = new XDXFParser(dbHelper);
        try {
            dp.parse(urlPath, new WordAddedCallback() {

                @Override
                public void wordAdded(String word) {
                    mBuilder.setProgress(0, 0, true);
                    mBuilder.setSubText("Adding " + word);
                    mNotifyManager.notify(0, mBuilder.build());
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // When the loop is finished, updates the notification
            mBuilder.setContentText("Done")
                    // Removes the progress bar
                    .setProgress(0, 0, false);
            mNotifyManager.notify(0, mBuilder.build());

            if (messenger != null) {

                Message msg = Message.obtain();
                msg.arg1 = 1;
                try {
                    messenger.send(msg);
                } catch (android.os.RemoteException e1) {
                    Log.w(getClass().getName(), "Exception sending message", e1);
                }

            }
        }
    }

}
