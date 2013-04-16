package com.xdxf.dictionary;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.xdxf.dictionary.fragments.BookListFragment;
import com.xdxf.dictionary.fragments.HistoryFragment;
import com.xdxf.dictionary.fragments.XdxfDictionaryFragment;
import com.xdxf.dictionary.service.ConfigureFirstRunTask;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.utils.MySharedPreferences;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load history items

        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        ActionBar.Tab tab = actionBar
                .newTab()
                .setText(getString(R.string.search))
                .setIcon(R.drawable.book)
                .setTabListener(new XdxfTabListener<XdxfDictionaryFragment>(this, getString(R.string.search),
                        XdxfDictionaryFragment.class));

        actionBar.addTab(tab);

        tab = actionBar
                .newTab()
                .setText(getString(R.string.history))
                .setIcon(R.drawable.history2)
                .setTabListener(new XdxfTabListener<HistoryFragment>(this, getString(R.string.history),
                        HistoryFragment.class));
        actionBar.addTab(tab);
        tab = actionBar
                .newTab()
                .setText(getString(R.string.book_list))
                .setIcon(R.drawable.book_gear)
                .setTabListener(new XdxfTabListener<BookListFragment>(this, getString(R.string.book_list),
                        BookListFragment.class));
        actionBar.addTab(tab);
        //instantiate the DBHelper singleton
        DBHelper.getInstance(this);//.exec(DBHelper.CREATE_HISTORY_TABLE);


        SharedPreferences sharedPref = getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, MODE_PRIVATE);
        if (!sharedPref.getBoolean(MySharedPreferences.FIRSTRUN_DATABASE_SETUP, false)) {
            new ConfigureFirstRunTask(this).execute();
        }
    }
//
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        // Save UI state changes to the savedInstanceState.
//        // This bundle will be passed to onCreate if the process is
//        // killed and restarted.
////        savedInstanceState.putString("Word", actv.getText().toString());
//
//        // etc.
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        // Restore UI state from the savedInstanceState.
//        // This bundle has also been passed to onCreate.
////        String word = savedInstanceState.getString("Word", "");
////        if (!word.isEmpty()) {
////            actv.setText(word);
////            //buttonPressed();
////        }
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add_dict:
//                DialogFactory.showFileChooser(this, FILE_SELECT_ADD_DICT);
//                break;
//            case R.id.action_select_dict:
//                DialogFactory.showBookSelectionDialog(this, dbHelper);
//                break;
//            case R.id.action_history_dict:
//                break;
//            case R.id.action_database_export:
//                if (ServiceUtils.isServiceRunning(this, AddXDXFDocumentService.class.getName())) {
//                    boolean result = DialogFactory.showDialog(this, "Warning!", "A dictionary is being added now. \n Cancel?", "Yes, Cancel it", "No. I'll wait");
//                    if (!result) {
//                        //stop the service
//                        if (stopService(addXdxfService)) {
//                            DialogFactory.showFileChooser(this, FILE_SELECT_EXPORT_DB);
//                        }
//                    }
//                }
//                DialogFactory.showFileChooser(this, FILE_SELECT_EXPORT_DB);
//                // dbHelper.exportDatabase(Environment.getExternalStorageDirectory() + "/export_dict.db");
//                break;
//            case R.id.action_database_import:
//                if (ServiceUtils.isServiceRunning(this, AddXDXFDocumentService.class.getName())) {
//                    boolean result = DialogFactory.showDialog(this, "Warning!", "A dictionary is being added now. \n Cancel?", "Yes, Cancel it", "No. I'll wait");
//                    if (!result) {
//                        //stop the service
//                        if (stopService(addXdxfService)) {
//                            DialogFactory.showFileChooser(this, FILE_SELECT_IMPORT_DB);
//                        }
//                    }
//                }
//
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case FILE_SELECT_ADD_DICT:
//                if (resultCode == RESULT_OK) {
//                    // Get the path
//                    String path = Utils.File.getPath(this, data.getData());
//                    Log.d(TAG, "File Path: " + path);
//
//                    Toast.makeText(this, "Adding Dictionary", Toast.LENGTH_SHORT).show();
//                    // Create a new Messenger for the communication back
//                    Messenger messenger = new Messenger(handler);
//                    addXdxfService.putExtra("Messenger", messenger);
//                    //intent.setData(Uri.parse("http://www.vogella.com/index.html"));
//                    addXdxfService.putExtra("path", path);
//                    startService(addXdxfService);
//
//                }
//                break;
//            case FILE_SELECT_EXPORT_DB:
//                if (resultCode == RESULT_OK) {   // Get the path
//                    String path = Utils.File.getPath(this, data.getData());
//                    Log.d(TAG, "File Path: " + path);
//
//                    String date = simpleDateFormat.format(new Date());
//                    dbHelper.exportDatabase(path + "/export_dict_" + date + ".db");
//                }
//                break;
//            case FILE_SELECT_IMPORT_DB:
//                if (resultCode == RESULT_OK) {
//                    String path = Utils.File.getPath(this, data.getData());
//                    Log.d(TAG, "File Path: " + path);
//                    dbHelper.importDatabase(new File(path));
//                }
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
