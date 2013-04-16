package com.xdxf.dictionary.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Messenger;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.xdxf.dictionary.BookListAdapter;
import com.xdxf.dictionary.R;
import com.xdxf.dictionary.service.AddXDXFDocumentService;
import com.xdxf.dictionary.service.RemoveDictionary;
import com.xdxf.dictionary.sqlite.Book;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.utils.DialogFactory;
import com.xdxf.dictionary.utils.MessageHandler;
import com.xdxf.dictionary.utils.MySharedPreferences;
import com.xdxf.dictionary.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class BookListFragment extends Fragment {

    private static final int FILE_SELECT_ADD_DICT = 0;
    private static final int FILE_SELECT_EXPORT_DB = 1;
    private static final int FILE_SELECT_IMPORT_DB = 2;
    private ListView listView;
    private String TAG = BookListFragment.class.getName();
    private Intent addXdxfService;
    private MessageHandler handler;
    private DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_book_list,
                container, false);
        setHasOptionsMenu(true);
        handler = new MessageHandler(getActivity());
        dbHelper = DBHelper.getInstance(getActivity());
        listView = (ListView) view.findViewById(R.id.listView1);
        addXdxfService = new Intent(getActivity(), AddXDXFDocumentService.class);
        ArrayAdapter<Book> adapter = new BookListAdapter(getActivity(), dbHelper.getAllBooks());
        SharedPreferences prefs = getActivity().getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        int count = prefs.getInt(MySharedPreferences.BOOKMODEL_COUNT, 0);
        // listView.setItemsCanFocus(true);
        registerForContextMenu(listView);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                if (listView.getCheckedItemCount() <= 0) {
                    Book b = dbHelper.getBook((long) 1);
                    DialogFactory.showDialog(getActivity(),
                            "No Book is selected!",
                            "Do you want me select the \n" + b.getBookname(),
                            "Yes, Do it",
                            "No, Leave it",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    listView.setItemChecked(Utils.getItemPositionByAdapterId(listView.getAdapter(), 1), true);
                                }
                            },
                            null
                    );

                }
                saveBookSelection();
            }
        });

        if (count != 0) {
            String[] strIds = prefs.getString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, "").split(",");
            for (String strId : strIds) {
                if (strId.isEmpty()) {
                    Log.i(TAG, "Empty string id ...");
                    continue;
                }
                long id = Long.parseLong(strId.trim());
                for (int i = 0; i < adapter.getCount(); i++) {
                    Book bm = adapter.getItem(i);
                    if (bm.getId() == id) {
                        //bm.setSelected(true);
                        listView.setItemChecked(i, true);
                    }
                }
            }
        }
        return view;
    }

    private void saveBookSelection() {
        SharedPreferences.Editor prefEditor = getActivity().getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE).edit();
        prefEditor.putInt(MySharedPreferences.BOOKMODEL_COUNT, listView.getAdapter().getCount());
        prefEditor.putString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, Utils.Csv.toCsvString(listView.getCheckedItemIds()));
        prefEditor.apply();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.listview_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        SharedPreferences prefs = getActivity().getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE);

        long[] l = listView.getCheckedItemIds();
        switch (item.getItemId()) {
            case R.id.remove_item:
                if (dbHelper.getBooksCount() == 1) {
                    DialogFactory.showDialog(getActivity(), "Warning!", "This is the only book in the database!", "I know, I'll add later", "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new RemoveDictionary(getActivity(), (BookListAdapter) listView.getAdapter()).execute(info.id);
                        }
                    }, null);
                } else
                {
                    //uncheck the listitem before removing, if checked
                    long[] ids = Utils.Csv.toLongArray(prefs.getString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, ""));
                    if(Utils.contains(ids,info.id))
                    {
                        ids = Utils.remove(ids,info.id);
                        prefs.edit().putString(MySharedPreferences.BOOKMODEL_SELECTED_IDS,Utils.Csv.toCsvString(ids))
                        .putInt(MySharedPreferences.BOOKMODEL_COUNT, listView.getAdapter().getCount() - 1)
                                     .apply();
                    }
                    new RemoveDictionary(getActivity(), (BookListAdapter) listView.getAdapter()).execute(info.id);
                }
                return true;
            case R.id.remove_checked_items:

                new RemoveDictionary(getActivity(), (BookListAdapter) listView.getAdapter()).execute(Utils.toObjects(l));
                return true;
            case R.id.remove_unchecked_items:
                ArrayList<Long> uncheckedIds = new ArrayList<Long>();
                for (int i = 0; i < listView.getAdapter().getCount(); i++) {
                    long value = listView.getAdapter().getItemId(i);
                    if (!Utils.contains(l, value)) {
                        uncheckedIds.add(value);
                    }
                }
                new RemoveDictionary(getActivity(), (BookListAdapter) listView.getAdapter(), true).execute(uncheckedIds.toArray(new Long[uncheckedIds.size()]));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_dict:
                DialogFactory.showFileChooser(this, FILE_SELECT_ADD_DICT, "Select XDXF Document");
                break;
            case R.id.action_database_export:
                if (Utils.Service.isServiceRunning(getActivity(), AddXDXFDocumentService.class.getName())) {
                    DialogFactory.showDialog(getActivity(), "Warning!", "A dictionary is being added now. \n Cancel?", "Yes, Cancel it", "No. I'll wait", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (getActivity().stopService(addXdxfService)) {
                                exportDatabase();
                            }
                        }
                    }, null);

                }
                exportDatabase();
                // dbHelper.exportDatabase(Environment.getExternalStorageDirectory() + "/export_dict.db");
                break;
            case R.id.action_database_import:
                if (Utils.Service.isServiceRunning(getActivity(), AddXDXFDocumentService.class.getName())) {
                    DialogFactory.showDialog(getActivity(), "Warning!", "A dictionary is being added now. \n Cancel?", "Yes, Cancel it", "No. I'll wait", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (getActivity().stopService(addXdxfService)) {
                                DialogFactory.showFileChooser(BookListFragment.this, FILE_SELECT_IMPORT_DB, "TODO: ");
                            }
                        }
                    }, null);
                }
                DialogFactory.showFileChooser(this, FILE_SELECT_IMPORT_DB, "Select database to import ");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void exportDatabase() {
        try {
            String date = Utils.DATEFORMAT_NUMERIC.format(new Date());
            String path = Environment.getExternalStorageDirectory() + "/export_dict_" + date + ".db";
            dbHelper.exportDatabase(path);
            Toast.makeText(getActivity(), "Database exported to " + path, Toast.LENGTH_LONG).show();
        } catch (SecurityException se) {
            Toast.makeText(getActivity(), "unable to write on the sd card " + se.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_ADD_DICT:
                if (resultCode == Activity.RESULT_OK) {
                    // Get the path
                    String path = Utils.File.getPath(getActivity(), data.getData());
                    Log.d(TAG, "File Path: " + path);

                    Toast.makeText(getActivity(), "Adding Dictionary", Toast.LENGTH_SHORT).show();
                    // Create a new Messenger for the communication back
                    Messenger messenger = new Messenger(handler);
                    addXdxfService.putExtra("Messenger", messenger);
                    //intent.setData(Uri.parse("http://www.vogella.com/index.html"));
                    addXdxfService.putExtra("path", path);
                    getActivity().startService(addXdxfService);

                }
                break;
            case FILE_SELECT_EXPORT_DB:
                if (resultCode == Activity.RESULT_OK) {   // Get the path
                    String path = Utils.File.getPath(getActivity(), data.getData());
                    Log.d(TAG, "File Path: " + path);

                    String date = Utils.DATEFORMAT_NUMERIC.format(new Date());
                    dbHelper.exportDatabase(path + "/export_dict_" + date + ".db");
                }
                break;
            case FILE_SELECT_IMPORT_DB:
                if (resultCode == Activity.RESULT_OK) {
                    String path = Utils.File.getPath(getActivity(), data.getData());
                    Log.d(TAG, "File Path: " + path);
                    dbHelper.importDatabase(new File(path));
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}



