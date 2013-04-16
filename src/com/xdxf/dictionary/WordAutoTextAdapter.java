package com.xdxf.dictionary;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.*;
import com.xdxf.dictionary.sqlite.DBHelper;

public class WordAutoTextAdapter extends CursorAdapter implements android.widget.AdapterView.OnItemClickListener {

    private DBHelper db;

    private final AutoCompleteTextView actv;
    private final ImageButton button;

    public WordAutoTextAdapter(View view, DBHelper db) {
        super(view.getContext(), null, true);
        this.db = db;

        actv = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView1);
        button = (ImageButton)view.findViewById(R.id.button1);
        // TODO Auto-generated constructor stub
    }

    /**
     * Invoked by the AutoCompleteTextView field to get completions for the
     * current input.
     * <p/>
     * NOTE: If this method either throws an exception or returns null, the
     * Filter class that invokes it will log an error with the traceback,
     * but otherwise ignore the problem. No choice list will be displayed.
     * Watch those error logs!
     *
     * @param constraint The input entered thus far. The resulting query will
     *                   search for Items whose description begins with this string.
     * @return A Cursor that is positioned to the first row (if one exists)
     *         and managed by the activity.
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }
        Cursor cursor = db.lookupSimilarWords(
                (constraint != null ? constraint.toString() : "@@@@"));
        return cursor;
    }

    @Override
    public View newView(Context context, Cursor paramCursor,
                        ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.textview_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context paramContext,
                         Cursor cursor) {
        final String text = convertToString(cursor);
        ((TextView) view).setText(text);

    }

    /**
     * Called by the AutoCompleteTextView field to get the text that will be
     * entered in the field after a choice has been made.
     *
     * @param cursor The cursor, positioned to a particular row in the list.
     * @return A String representing the row's text value. (Note that this
     *         specializes the base class return value for this method,
     *         which is {@link CharSequence}.)
     */
    @Override
    public String convertToString(Cursor cursor) {
//        final int columnIndex = cursor.getColumnIndexOrThrow("description");
        final String str = cursor.getString(1);
        return str;
    }

    /**
     * Called by the AutoCompleteTextView field when a choice has been made
     * by the user.
     *
     * @param listView The ListView containing the choices that were displayed to
     *                 the user.
     * @param view     The field representing the selected choice
     * @param position The position of the choice within the list (0-based)
     * @param id       The id of the row that was chosen (as provided by the _id
     *                 column in the cursor.)
     */
    @Override
    public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
        // Get the cursor, positioned to the corresponding row in the result set
        Cursor cursor = (Cursor) listView.getItemAtPosition(position);
        String word = cursor.getString(1);
        actv.setText(word);
        button.performClick();
    }
}
