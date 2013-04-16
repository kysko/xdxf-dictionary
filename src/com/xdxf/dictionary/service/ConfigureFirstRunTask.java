package com.xdxf.dictionary.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.utils.MySharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/23/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureFirstRunTask extends AsyncTask<Void, Integer, Exception> {
    private Activity ctx;
    private ProgressDialog progressDialog;

    public ConfigureFirstRunTask(Activity ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {
        /*
		 * This is executed on UI thread before doInBackground(). It is the
		 * perfect place to show the progress dialog.
		 */
        progressDialog = ProgressDialog.show(ctx, "Preparing for first use.",
                "Please Wait...");
    }

    @Override
    protected Exception doInBackground(Void... v) {
        Exception result = null;
        try {
//            SQLiteDatabase database = DBHelper.getInstance(ctx).getWritableDatabase();//.close();
//            Cursor c = database.rawQuery("select name from sqlite_master where type='table'",null);
//            while(c.moveToNext())
//                Log.i("*****","******" + c.getString(0));
//            database.close();
            DBHelper.getInstance(ctx).importDatabase();
        } catch (Exception e) {
            Log.e("ConfigureFirstRun", e.getMessage(), e);
            result = e;
        }

        return result;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onPostExecute(Exception error) {
        progressDialog.cancel();
        if (error == null) {
            ctx
                    .getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE)
                    .edit()
                    .putBoolean(MySharedPreferences.FIRSTRUN_DATABASE_SETUP, true)
                    .putString(MySharedPreferences.BOOKMODEL_SELECTED_IDS,"1")
                    .apply();
        } else {
            //error dialog
            Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
        super.onPostExecute(error);
    }
}
