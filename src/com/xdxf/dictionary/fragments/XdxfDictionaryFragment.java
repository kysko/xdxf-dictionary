package com.xdxf.dictionary.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import com.xdxf.dictionary.R;
import com.xdxf.dictionary.WordAutoTextAdapter;
import com.xdxf.dictionary.sqlite.Book;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.sqlite.HistoryObject;
import com.xdxf.dictionary.utils.Utils;

public class XdxfDictionaryFragment extends Fragment {

    private AutoCompleteTextView actv;
    private WebView webview;
    private DBHelper dbHelper;
    /**
     * There is no easy way to get the html back from the WebView
     * http://stackoverflow.com/a/5264263/722965
     * http://stackoverflow.com/a/8201246/722965
     * so just caching the html to enable me
     * to save the state of the webview on orientation
     * change
     */
    private String _cacheHtml;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_main, container, false);
        dbHelper = DBHelper.getInstance(getActivity());
      if(actv == null)
        actv = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView1);
        webview = (WebView) view.findViewById(R.id.webView1);
        webview.getSettings().setBuiltInZoomControls(true);
        WordAutoTextAdapter adapter = new WordAutoTextAdapter(view, dbHelper);
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(adapter);
//        SharedPreferences sharedPref = getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, MODE_PRIVATE);
//        if (!sharedPref.getBoolean(MySharedPreferences.FIRSTRUN_DATABASE_SETUP, false)) {
//            new ConfigureFirstRunTask(this).execute();
//        }
//
//        //dbHelper.exportDatabase(Environment.getExternalStorageDirectory().getAbsolutePath() + "/backup.db");
        ImageButton button = (ImageButton) view.findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonPressed();
            }
        });
        setHasOptionsMenu(true);
        if(!actv.getText().toString().isEmpty())
        {
            webview.loadDataWithBaseURL(null, _cacheHtml, "text/html", "UTF-8", null);
        }
        else if (savedInstanceState != null) {
            actv.setText(savedInstanceState.getString("Word"));
            String html = savedInstanceState.getString("html", "");
            if (html.isEmpty()) {
                buttonPressed();
                actv.dismissDropDown();
            } else
                webview.loadDataWithBaseURL(null, savedInstanceState.getString("html"), "text/html", "UTF-8", null);
        }
        else if (getArguments() != null) {
            Bundle args = getArguments();
            String word = args.getString("Word",null);
            if (word != null) {
                actv.setText(args.getString("Word"));
                buttonPressed();
                args.putString("Word",null);
                actv.dismissDropDown();
            }
        }


        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        Save UI state changes to the savedInstanceState.
//        This bundle will be passed to onCreate if the process is
//        killed and restarted.
        outState.putString("Word", actv.getText().toString());
        outState.putString("html", _cacheHtml);
    }

    private void buttonPressed() {
        if (actv.getText().toString().isEmpty())
            return;
        Cursor cursor = dbHelper.lookupWord(actv.getText().toString());
        int rgb;
        String hexColor;
        String word = cursor.getString(1);
        dbHelper.addHistoryObject(new HistoryObject(word));
        StringBuilder sb = new StringBuilder();
        sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><style type=\"text/css\">")
                .append("body{line-height: 1.6em;}")
                .append("#box-table-b{font-family: \"Lucida Sans Unicode\", \"Lucida Grande\", Sans-Serif;margin: 5px auto;width: 97%;text-align: center;border-collapse: collapse;}")
                .append("#box-table-b th{font-size: 13px;font-weight: normal;padding: 8px;text-align: left;color: #669;}")
                .append("#box-table-b td{padding: 8px;text-align: justify;color: #669;}")
                .append("</style></head><body>");
        do {
            rgb = Utils.Color.generateRandomBlueShade();
            hexColor = Utils.Color.toHexColor(rgb);
            Book b = dbHelper.getBook(Long.parseLong(cursor.getString(3)));
            sb.append(String.format("<table id=\"box-table-b\" style=\"border-top: 7px solid %s;border-bottom: 7px solid %s\">", hexColor, hexColor));
            sb.append(String.format("<thead><tr> <th style=\"border-right: 1px solid %s;border-left: 1px solid %s;\" >", hexColor, hexColor));
            sb.append(word);
            sb.append("<br /><span style=\"font-style: italic;font-size: smaller;\">");
            sb.append(String.format("%s [%s - %s]", b.getBookname(), b.getFromLang(), b.getToLang()));
            sb.append("</span></th></tr></thead>");
            sb.append("<tbody><tr>");
            sb.append(String.format("<td  style=\"border-right: 1px solid %s;border-left: 1px solid %s;\">%s</td></tr></tbody></table>", hexColor, hexColor, cursor.getString(2)));
        } while (cursor.moveToNext());
        sb.append("</body></html>");
        Log.v("XdxfDictionary", sb.toString());
        _cacheHtml = sb.toString();
        webview.loadDataWithBaseURL(null, _cacheHtml, "text/html", "UTF-8", null);
        Utils.hideSoftKeyboard(getActivity(), actv.getWindowToken());
    }

}
