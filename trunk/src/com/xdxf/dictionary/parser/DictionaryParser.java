package com.xdxf.dictionary.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import android.util.Log;

import com.xdxf.dictionary.sqlite.Book;
import com.xdxf.dictionary.sqlite.DBHelper;
import com.xdxf.dictionary.sqlite.Word;

public abstract class DictionaryParser {

	protected DBHelper dbhandler;

    public DictionaryParser(DBHelper dbhandler) {
        this.dbhandler = dbhandler;

    }

    public abstract void parse(InputStream f,WordAddedCallback callback);

    public void parse(String filename,WordAddedCallback callback) throws FileNotFoundException {
        parse(new FileInputStream(filename),callback);
    }
    protected Book addBook(String fromLang, String toLang, String name, String description) {
    	Book book = new Book();
    	book.setBookname(name);
    	book.setDescription(description);
    	book.setFromLang(fromLang);
    	book.setToLang(toLang);
    	dbhandler.addBook(book);
    	Log.v("DPARSER", book.toString());
    	return book;

    }

    

    public synchronized void addWord(Book book, String word, String meaning) {
    	Word w = new Word();
    	w.setBid(book.getId());
    	w.setMeaning(meaning);
    	w.setWord(word);
    	dbhandler.addWord(w);
    }

    protected class DictionaryAlreadyExistsException extends Exception {

        
		private static final long serialVersionUID = 5432394963882409548L;

		public DictionaryAlreadyExistsException(String message) {
            super(message);
        }
    }
    
    public interface WordAddedCallback
    {
    	public void wordAdded(String word);
    }
}