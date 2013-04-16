package com.xdxf.dictionary.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import com.xdxf.dictionary.BookListAdapter;
import com.xdxf.dictionary.sqlite.Book;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 3/31/13
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoveDictionary extends AsyncTask<Long, Long, Boolean> {
    private final Activity ctx;
    private final BookListAdapter bookListAdapter;
    private final boolean invert;
    private ProgressDialog progressDialog;

    public RemoveDictionary(Activity ctx, BookListAdapter bookListAdapter) {
        this(ctx, bookListAdapter, false);

    }

    public RemoveDictionary(Activity ctx, BookListAdapter bookListAdapter, boolean invert) {
        this.ctx = ctx;
        this.bookListAdapter = bookListAdapter;
        this.invert = invert;
    }

    @Override
    protected void onPreExecute() {
        /*
         * This is executed on UI thread before doInBackground(). It is the
		 * perfect place to show the progress dialog.
		 */
        progressDialog = ProgressDialog.show(ctx, "Please Wait...", "Removing Selected Dictionary");

    }

    @Override
    protected Boolean doInBackground(Long... longs) {

        DBHelper dbHelper = DBHelper.getInstance(ctx);
        if(invert)
        {
            List<Long> temp = Arrays.asList(longs);
            List<Long> _invert = Arrays.asList(longs);
            for (Book book : dbHelper.getAllBooks()) {

            }

        }
        for (long l : longs) {
            dbHelper.deleteBook(l);
            publishProgress(l);
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        Book b = bookListAdapter.getItem(Utils.getItemPositionByAdapterId(bookListAdapter, values[0].longValue()));
        bookListAdapter.remove(b);
        bookListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPostExecute(Boolean result) {
        progressDialog.cancel();
        super.onPostExecute(result);
    }
}
