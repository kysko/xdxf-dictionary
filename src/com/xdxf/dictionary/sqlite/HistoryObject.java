package com.xdxf.dictionary.sqlite;

import com.xdxf.dictionary.utils.Utils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: comspots
 * Date: 4/1/13
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryObject implements Serializable {
    private String word;
    private String timeStamp;
    private boolean favourite;
    private long id;
    private boolean changed;

    public void setChanged(boolean b)
    {
        changed = b;
    }

    public boolean isChanged() {
        return changed;
    }


    public HistoryObject() {
    }

    public HistoryObject(String word) {
        this(word, Utils.DATEFORMAT_STRING.format(new Date()), false);
    }


    public HistoryObject(String word, String timeStamp, boolean favourite) {
        this.word = word;
        this.timeStamp = timeStamp;
        this.favourite = favourite;
        this.id = -1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favorite) {
        this.favourite = favorite;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
