package com.xdxf.dictionary.sqlite;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import com.xdxf.dictionary.utils.CsvReader;
import com.xdxf.dictionary.utils.MySharedPreferences;
import com.xdxf.dictionary.utils.Utils;
import com.xdxf.dictionary.utils.Zipper;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * http://touchlabblog.tumblr.com/post/24474750219/single-sqlite-connection
 */
public class DBHelper extends SQLiteOpenHelper {

    // table names
    public static final String TABLE_BOOKS = "Books";
    public static final String TABLE_WORDS = "Words";
    public static final String TABLE_HISTORY = "History";
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "dictionary";
    // Table Columns names
    private static final String COL_ROW_ID = "_id";
    private static final String COL_BOOKS_FROM_LANG = "FROM_LANG";
    private static final String COL_BOOKS_TO_LANG = "TO_LANG";
    private static final String COL_BOOKS_NAME = "BOOKNAME";
    private static final String COL_BOOKS_DESCR = "DESCRIPTION";
    private static final String COL_BOOKS_ISDIRTY = "ISDIRTY";
    private static final String COL_WORDS_WORD = "WORD";
    private static final String COL_WORDS_MEANING = "MEANING";
    private static final String COL_WORDS_BOOK_ID = "BID";
    private static final String COL_HISTORY_WORD = "WORD";
    private static final String COL_HISTORY_TIMESTAMP = "TIMESTAMP";
    private static final String COL_HISTORY_FAVOURITE = "FAVOURITE";
    private static final String CREATE_HISTORY_TABLE = String.format("CREATE TABLE %s (%s INTEGER primary key AUTOINCREMENT,%s VARCHAR(100) not null,%s VARCHAR(100) not null,%s INTEGER not null);", TABLE_HISTORY, COL_ROW_ID, COL_HISTORY_WORD, COL_HISTORY_TIMESTAMP, COL_HISTORY_FAVOURITE);
    private static final String TAG = "DBHelper";
    private static DBHelper helper;
    private static String DATABASE_PATH = "/data/com.xdxf.dictionary/databases/" + DATABASE_NAME;
    private final Context ctx;
    private SQLiteDatabase wdb_t;
    private DatabaseUtils.InsertHelper ih;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (helper == null) {
            helper = new DBHelper(context);
        }

        return helper;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(String.format("create table %s (	"
//                + "%s INTEGER primary key AUTOINCREMENT,	"
//                + "%s VARCHAR(16) not null,	" + "%s VARCHAR(16) not null,	"
//                + "%s VARCHAR(16) not null,	" + "%s VARCHAR(100) not null, "
//                + "%s VARCHAR(1))", TABLE_BOOKS, COL_ROW_ID,
//                COL_BOOKS_FROM_LANG, COL_BOOKS_TO_LANG, COL_BOOKS_NAME, COL_BOOKS_DESCR,
//                COL_BOOKS_ISDIRTY));
//        db.execSQL(String.format("create table %s ( "
//                + "	%s INTEGER primary key AUTOINCREMENT,	"
//                + "%s VARCHAR(100) not null,	" + "%s VARCHAR(30000) not null,	"
//                + "%s INTEGER not null, " + "FOREIGN KEY(%s) REFERENCES %s(%s)"
//                + ")", TABLE_WORDS, COL_ROW_ID, COL_WORDS_WORD,
//                COL_WORDS_MEANING, COL_WORDS_BOOK_ID, COL_WORDS_BOOK_ID,
//                TABLE_BOOKS, COL_ROW_ID));
        //importDatabase();
        //fillDatabaseFromCsvFile(db);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        // Create tables again
        onCreate(db);
    }

    //<editor-fold desc="History">
    public long containsWordInHistory(String word) {
        long id = -1;
        SQLiteDatabase rdb = getReadableDatabase();
        Cursor c = rdb.query(true, TABLE_HISTORY, new String[]{COL_ROW_ID}, COL_HISTORY_WORD + "=?", new String[]{word}, null, null, null, null);
        if (c.moveToFirst()) {
            id = c.getLong(0);
        }
        return id;
    }

    public void addHistoryObject(HistoryObject historyObject) {
        long id = containsWordInHistory(historyObject.getWord());
        if (id != -1) {
            setTimeStampToNowForHistoryObject(historyObject.getWord());
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_HISTORY_WORD, historyObject.getWord());
            values.put(COL_HISTORY_TIMESTAMP, Utils.toISODateFormat(Utils.DATEFORMAT_STRING, historyObject.getTimeStamp()));
            values.put(COL_HISTORY_FAVOURITE, 0);
            // Inserting Row
            db.insert(TABLE_HISTORY, null, values);
            db.close();
        }
    }

    public boolean removeHistoryObject(HistoryObject historyObject) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = db.delete(TABLE_HISTORY, COL_ROW_ID + "=? ", new String[]{String.valueOf(historyObject.getId())}) > 0;
        db.close();
        return success;
    }

    public void setFavourite(HistoryObject history, boolean value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String strFilter = COL_HISTORY_WORD + "= '" + history.getWord() + "'";
        ContentValues args = new ContentValues();
        args.put(COL_HISTORY_FAVOURITE, value ? 1 : 0);
        db.update(TABLE_HISTORY, args, strFilter, null);
        db.close();
    }



    public void setTimeStampToNowForHistoryObject(String word) {
        setTimeStampForHistoryObject(word, Utils.DATEFORMAT_ISO8601FORMAT.format(new Date()));
    }

    public void setTimeStampForHistoryObject(String word, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COL_HISTORY_TIMESTAMP, time);
        db.update(TABLE_HISTORY, args, COL_HISTORY_WORD + "=?", new String[] {word});
        db.close();

    }

    public List<HistoryObject> getAllHistoryObjects() {
        List<HistoryObject> historyObjects = new ArrayList<HistoryObject>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HistoryObject historyObject = new HistoryObject();
                historyObject.setId(cursor.getLong(0));
                historyObject.setWord(cursor.getString(1));
                historyObject.setTimeStamp(Utils.toLongDateFormat(Utils.DATEFORMAT_ISO8601FORMAT, cursor.getString(2)));
                historyObject.setFavourite(cursor.getInt(3) == 1);

                // Adding book to list
                historyObjects.add(historyObject);
            } while (cursor.moveToNext());
        }

        // return books
        return historyObjects;
    }
    //</editor-fold>

    //<editor-fold desc="Book">
    public void addBook(Book b) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_BOOKS_FROM_LANG, b.getFromLang());
        values.put(COL_BOOKS_TO_LANG, b.getToLang());
        values.put(COL_BOOKS_NAME, b.getBookname());
        values.put(COL_BOOKS_DESCR, b.getDescription());
        values.put(COL_BOOKS_ISDIRTY, TRUE);
        // Inserting Row
        long id = db.insert(TABLE_BOOKS, null, values);
        if (id != -1)
            b.setId(id);
        db.close(); // Closing database connection
    }

    public void commitBook(Book b) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_BOOKS_ISDIRTY, FALSE);
        db.update(TABLE_BOOKS, values, COL_ROW_ID + "=?",
                new String[]{String.valueOf(b.getId())});
        db.close();
    }

    // Getting single contact
    public Book getBook(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKS, new String[]{
                COL_BOOKS_FROM_LANG, COL_BOOKS_TO_LANG, COL_BOOKS_NAME,
                COL_BOOKS_DESCR, COL_BOOKS_ISDIRTY}, COL_ROW_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        // return book
        return new Book(id, cursor.getString(0), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4));
    }

    // Getting All Books
    public List<Book> getAllBooks() {
        List<Book> bookList = new ArrayList<Book>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BOOKS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToNext()) {
            do {
                Book book = new Book();
                book.setId(Long.parseLong(cursor.getString(0)));
                book.setFromLang(cursor.getString(1));
                book.setToLang(cursor.getString(2));
                book.setBookname(cursor.getString(3));
                book.setDirty(cursor.getInt(5) == TRUE);
                book.setDescription(cursor.getString(4));
                // Adding book to list
                bookList.add(book);
            } while (cursor.moveToNext());
        }

        // return books
        return bookList;
    }

    public void deleteBook(long bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, COL_WORDS_BOOK_ID + " = ?",
                new String[]{String.valueOf(bookId)});
        db.delete(TABLE_BOOKS, COL_ROW_ID + " = ?",
                new String[]{String.valueOf(bookId)});
        db.close();
    }

    // Getting Books Count
    public int getBooksCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_BOOKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }
    //</editor-fold>

    //<editor-fold desc="Word">
    public void addWord(Word w) {

        ContentValues values = new ContentValues();
        values.put(COL_WORDS_MEANING, w.getMeaning());
        values.put(COL_WORDS_WORD, w.getWord());
        values.put(COL_WORDS_BOOK_ID, w.getBid());
        // Inserting Row
        getWritableDatabase().insert(TABLE_WORDS, null, values);
    }

    public Cursor lookupWord(String word) {
        SQLiteDatabase db = this.getReadableDatabase();
        SharedPreferences pref = ctx.getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String where = Utils.Csv.fromCsvStringToWhereClause(pref.getString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, ""), "BID");
        StringBuilder sb = new StringBuilder();
        sb.append(COL_WORDS_WORD).append(" = '").append(word).append("' ");
        if (!where.isEmpty()) {
            sb.append(" AND ").append("( ").append(where).append(" )");
        }
        // sb.append(" COLLATE NOCASE");
        Cursor mCursor = db.query(false, TABLE_WORDS, new String[]{COL_ROW_ID,
                COL_WORDS_WORD, COL_WORDS_MEANING, COL_WORDS_BOOK_ID}, sb.toString(), null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor lookupSimilarWords(String constraint) {
        SQLiteDatabase db = this.getReadableDatabase();
        SharedPreferences pref = ctx.getSharedPreferences(MySharedPreferences.SHARED_PREF_NAME, Activity.MODE_PRIVATE);
        String where = Utils.Csv.fromCsvStringToWhereClause(pref.getString(MySharedPreferences.BOOKMODEL_SELECTED_IDS, ""), "BID");
        StringBuilder sb = new StringBuilder();
        sb.append(COL_WORDS_WORD).append(" like '").append(constraint).append("%' ");
        if (!where.isEmpty()) {
            sb.append(" AND ").append("( ").append(where).append(" )");
        }
        //sb.append(" COLLATE NOCASE");
        Cursor mCursor = db.query(true, TABLE_WORDS, new String[]{COL_ROW_ID,
                COL_WORDS_WORD}, sb.toString(), null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //</editor-fold>

    //<editor-fold desc="Import/Export Database, Helper Functions">
    private void fillDatabaseFromCsvFile(SQLiteDatabase db) {
        BufferedReader bis = null;
        CsvReader reader = null;
        try {
            String temp = unzipAssetToTemp("dict.zip");
            File tempDir = new File(temp);
            //extracted file is dict.csv
            boolean firstRow = true;
            reader = new CsvReader(new FileReader(tempDir.getAbsolutePath() + "/dict.csv"));
            DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, TABLE_WORDS);
            boolean newQuery = true;
            String query = "";
            int count = 0;
            while (reader.hasMoreElements()) {
                String[] values = reader.nextElement();
                if (firstRow) {
                    //("1", fromLang, toLang, name, description, "N");
                    ContentValues cv = new ContentValues();
                    cv.put(COL_ROW_ID, Integer.parseInt(values[0]));
                    cv.put(COL_BOOKS_FROM_LANG, values[1]);
                    cv.put(COL_BOOKS_TO_LANG, values[2]);
                    cv.put(COL_BOOKS_NAME, values[3]);
                    cv.put(COL_BOOKS_DESCR, values[4]);
                    cv.put(COL_BOOKS_ISDIRTY, values[5]);
                    db.insert(TABLE_BOOKS, null, cv);
                    firstRow = false;
                    continue;
                }
                if (newQuery) {
                    query = String.format("INSERT INTO %s (%s, %s, %s)", TABLE_WORDS, COL_WORDS_WORD, COL_WORDS_MEANING, COL_WORDS_BOOK_ID);
                    newQuery = false;
                } else {
                    query += " UNION";
                }
                //query += ' SELECT "'+values[i][0]+'", "'+values[i][1]+'", "'+values[i][2]+'"';
                query += String.format(" SELECT \"%s\",\"%s\",%s", values[0], values[1], values[2]);
                if (count != 0 && count % 499 == 0) {
                    db.execSQL(query);
                    newQuery = true;
                }
                count++;
            }

//executing remaining lines
            if (count % 499 != 0) {
                db.execSQL(query);
            }

        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT);
        } catch (Exception ex) {
            Log.i(TAG, ex.getMessage());
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT);
                }
        }
    }

    /**
     * Extracts a zip file in the assets to a temp dir on the external storage
     *
     * @param filename name of the file in the asset dir
     * @return absolute path to temp
     * @throws IOException
     */
    private String unzipAssetToTemp(String filename) throws IOException {
        if (!Utils.File.isStorageWritable())
            throw new IOException("External Storage media not available");
        String temp = Environment.getExternalStorageDirectory() + "/temp";
        File tempDir = new File(temp);
        if (tempDir.exists()) {
            tempDir.delete();
        } else {
            tempDir.mkdirs();
        }
        Zipper.unzip(ctx.getAssets().open(filename), tempDir.getAbsolutePath());
        return temp;
    }

    public void importDatabase() throws IOException {
        String temp = unzipAssetToTemp("db.zip");
        //extracted file is backup.db
        File dbFile = new File(new File(temp), "backup.db");
        importDatabase(dbFile);
        SQLiteDatabase wdb = getWritableDatabase();
        wdb.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        wdb.execSQL(CREATE_HISTORY_TABLE);
        if (!dbFile.delete())
            dbFile.deleteOnExit();
    }

    public void cleanupDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(true, TABLE_BOOKS, new String[]{COL_ROW_ID}, COL_BOOKS_ISDIRTY + "=?", new String[]{String.valueOf(TRUE)}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long bookId = Long.parseLong(cursor.getString(0));
            db.delete(TABLE_WORDS, COL_WORDS_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
            db.delete(TABLE_BOOKS, COL_ROW_ID + "=?", new String[]{String.valueOf(bookId)});
        }


    }

    public void exportDatabase(String path) {
        File data = Environment.getDataDirectory();
        File currentDb = new File(data, DATABASE_PATH);
        copy(currentDb, new File(path));
    }

    public void importDatabase(File path) {
        try {
            close();
        } catch (Exception e) {
            Log.w(TAG, "Error closing database connection: Details  " + e.getMessage());
        }
        File data = Environment.getDataDirectory();
        File currentDb = new File(data, DATABASE_PATH);
        copy(path, currentDb);
    }

    private void copy(File from, File to) {
        try {

            if (from.exists()) {
                FileChannel src = new FileInputStream(from).getChannel();
                FileChannel dst = new FileOutputStream(to).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();

                dst.close();

            } else {
                //TODO: Toast.makeText(ctx, "Path ' " + from + " ' not writable.", Toast.LENGTH_SHORT);
                Log.e(TAG, "Path ' " + from + " ' not writable.");
            }
        } catch (IOException e) {
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Unused">
    public SQLiteDatabase beginTransaction() {
        SQLiteDatabase wdb = this.getWritableDatabase();
        wdb.beginTransaction();
        return wdb;
    }

    public void truncateTable(String tableName) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("TRUNCATE TABLE " + tableName);
    }

    public void beginTransactionEx() {
        wdb_t = getWritableDatabase();
        wdb_t.beginTransaction();
    }

    public void prepareForBulkInsert() {
        ih = new DatabaseUtils.InsertHelper(wdb_t, TABLE_WORDS);
    }

    public void insertWord(Word w) {
        ih.prepareForInsert();
        ih.bind(1, w.getWord());
        ih.bind(2, w.getMeaning());
        ih.bind(3, w.getBid());
        ih.execute();
    }

    public void commitTransaction() {
        if (ih != null)
            ih.close();
        wdb_t.endTransaction();
        wdb_t.setTransactionSuccessful();
        wdb_t.close();
    }

    public void exec(CharSequence sql) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql.toString());
        db.close();
    }
    //</editor-fold>
}
