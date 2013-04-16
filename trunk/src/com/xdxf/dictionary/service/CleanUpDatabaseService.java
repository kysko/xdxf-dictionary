package com.xdxf.dictionary.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import com.xdxf.dictionary.sqlite.DBHelper;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/11/13
 * Time: 2:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class CleanUpDatabaseService extends IntentService{

    public CleanUpDatabaseService() {
        super("CleanUpDatabase");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        DBHelper helper = DBHelper.getInstance(this);
        helper.cleanupDatabase();
    }
}
