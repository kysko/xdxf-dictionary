package com.xdxf.dictionary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;


/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/27/13
 * Time: 3:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class DialogFactory {


    /*public static void showBookSelectionDialog(final Activity ctx, final DBHelper dbHelper) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        final BookListAdapter bookListAdapter = new BookListAdapter(ctx, BookModel.getInstance(dbHelper.getAllBooks()),null);
        SharedPreferences prefs = ctx.getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        int count = prefs.getInt(MySharedPreferences.BOOKMODEL_COUNT, 0);
        if (count != 0) {
            long[] selectedIds = new long[count];
            String[] strIds = prefs.getString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, "").split(",");
            for (String strId : strIds) {
                long id = Long.parseLong(strId);
                for (int i = 0; i < bookListAdapter.getCount(); i++) {
                    BookModel bm = bookListAdapter.getItem(i);
                    if (bm.getBook().getId() == id) {
                        bm.setSelected(true);
                        break;
                    }
                }
            }
            //  listView.setAdapter(adapter);
        }
        builder.setAdapter(bookListAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                //ignore
                //the adapter has getter/setter for the checkbox state
                //so we can get the checkbox states all at once
                //on dialog save
            }
        })
                .setTitle("Select Books")
                        // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor prefEditor = ctx.getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE).edit();
                        prefEditor.putInt(MySharedPreferences.BOOKMODEL_COUNT, bookListAdapter.getCount());
                        String selectedIds = "";
                        for (int i = 0; i < bookListAdapter.getCount(); i++) {
                            BookModel bm = (BookModel) bookListAdapter.getItem(i);
                            if (bm.isSelected())
                                selectedIds += bm.getBook().getId() + ",";
                        }
                        if (selectedIds.isEmpty()) {
//
                        } else {
                            prefEditor.putString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, selectedIds);
                            prefEditor.apply();
                            dialog.dismiss();
                        }
                    }
                })

                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }*/

    public static void showFileChooser(Fragment fragment, int activityCode,String title) {


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            fragment.startActivityForResult(
                    Intent.createChooser(intent, title),
                    activityCode);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(fragment.getActivity(), "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean showDialog(Activity activity, String title, String content, String positiveBtn, String negativeBtn, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        final BooleanByReference result = new BooleanByReference();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(content)
                .setTitle(title);
        if (positiveBtn != null) {
            builder.setPositiveButton(positiveBtn, positiveListener == null ? new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    result.setValue(true);
                    dialog.dismiss();
                }
            } : positiveListener);
        }
        if (negativeBtn != null) {
            builder.setNegativeButton(negativeBtn, negativeListener == null ? new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    result.setValue(false);
                    dialog.cancel();
                }
            } : negativeListener);
        }
        // Create the AlertDialog object and show it
        builder.create().show();
        return result.getValue();
    }

    private static class BooleanByReference {
        private boolean value;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
        }
    }
}
