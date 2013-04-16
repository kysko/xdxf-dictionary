package com.xdxf.dictionary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;




public class HistoryRowLayout extends RelativeLayout {
    private TextView word;
    private TextView timestamp;
    private CheckBox favourite;

    public HistoryRowLayout(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        // RelativeLayout intializations happen here.
        LayoutInflater.from(context).inflate(R.layout.row_history_list, this);

        // Store the views.
        favourite = (CheckBox) findViewById(R.id.checkBox_history);
        word = (TextView) findViewById(R.id.word_history);
        timestamp = (TextView) findViewById(R.id.timestamp_history);
    }

    public HistoryRowLayout(Context context, AttributeSet attrs) {
        this(context, attrs,R.layout.row_history_list);

    }

    public HistoryRowLayout(Context context, int checkableId) {
        this(context, null,checkableId);
    }
    public HistoryRowLayout(Context context) {
        this(context,null);

    }

    public void setFavourite(boolean favourite) {
        this.favourite.setSelected(favourite);
    }

    public void setTimestamp(String timestamp) {
        this.timestamp.setText(timestamp);
    }

    public void setWord(String word) {
        this.word.setText(word);
    }


}
