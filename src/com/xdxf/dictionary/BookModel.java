package com.xdxf.dictionary;

import java.util.ArrayList;
import java.util.List;

import com.xdxf.dictionary.sqlite.Book;

public class BookModel {

	private Book book;
	private boolean selected;

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static List<BookModel> getInstance(List<Book> books)
	{
		List<BookModel> bm = new ArrayList<BookModel>();
		
		for (Book book : books) {
			BookModel model = new BookModel();
			model.setBook(book);
			bm.add(model);
		}
		return bm;
	}
}
