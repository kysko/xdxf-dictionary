package com.xdxf.dictionary;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.xdxf.dictionary.sqlite.Book;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.sqlite.HistoryObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 4/1/13
 * Time: 6:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryListAdapter extends ArrayAdapter<HistoryObject> {
    private final Activity context;

    public HistoryListAdapter(Activity context,List<HistoryObject> list) {
        super(context, R.layout.row_history_list,list);
        this.context = context;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        final HistoryObject item = getItem(position);
        ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.row_history_list, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.word = (TextView) v.findViewById(R.id.word_history);
            viewHolder.timestamp = (TextView) v.findViewById(R.id.timestamp_history);
            viewHolder.favourite = (CheckBox) v.findViewById(R.id.checkBox_history);
            viewHolder.favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    DBHelper.getInstance(null).setFavourite(item,b);
                   // notifyDataSetChanged();
                }
            });
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        if (item != null) {
            viewHolder.word.setText(item.getWord());
            viewHolder.timestamp.setText(item.getTimeStamp());
            viewHolder.favourite.setChecked(item.isFavourite());
        }
        return v;

    }
    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
    static class ViewHolder {
        public TextView word;
        public TextView timestamp;
        public CheckBox favourite;
    }
}
