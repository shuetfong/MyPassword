package my.edu.utar.mypassword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteAdapter {
    public static final String DATABASE_NAME = "MY_PASSWORD";
    public static final int DATABASE_VERSION = 3;

    // Password
    public static final String PASSWORD_TABLE = "PASSWORD";
    public static final String KEY_PID = "pid";
    public static final String KEY_TITLE = "title";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_URL = "url";
    public static final String KEY_NOTE = "note";

    // Category
    public static final String CATEGORY_TABLE = "CATEGORY";
    public static final String KEY_CID = "cid";
    public static final String KEY_CNAME = "cname";

    // Index
    private int index_PID;
    private int index_TITLE;
    private int index_USERNAME;
    private int index_PASSWORD;
    private int index_URL;
    private int index_NOTE;
    private int index_CID;
    private int index_CNAME;

    private static final String SCRIPT_CREATE_PASSWORD_TABLE = "create table " + PASSWORD_TABLE
            + " (pid INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TITLE + " text not null,"
            + KEY_USERNAME + " text not null,"
            + KEY_PASSWORD + " text not null,"
            + KEY_CID + " integer,"
            + KEY_URL + " text,"
            + KEY_NOTE + " text,"
            + "FOREIGN KEY(" + KEY_CID + ") REFERENCES " + CATEGORY_TABLE + "(" + KEY_CID + ") ON DELETE SET NULL);";

    private static final String SCRIPT_CREATE_CATEGORY_TABLE = "create table " + CATEGORY_TABLE
            + " (cid INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_CNAME + " text not null);";

    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public SQLiteAdapter(Context c) {
        context = c;
    }

    public SQLiteAdapter openToRead() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getReadableDatabase();
        return this;
    }

    public SQLiteAdapter openToWrite() throws android.database.SQLException {
        sqLiteHelper = new SQLiteHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqLiteHelper.close();
    }

    public long insert(Data data) {
        ContentValues contentValues = new ContentValues();
        String table = null;

        if (data instanceof Password) {
            Password p = ((Password) data);
            contentValues.put(KEY_TITLE, p.getTitle());
            contentValues.put(KEY_USERNAME, p.getUsername());
            contentValues.put(KEY_PASSWORD, p.getPassword());
            contentValues.put(KEY_CID, p.getCategoryID());
            contentValues.put(KEY_URL, p.getUrl());
            contentValues.put(KEY_NOTE, p.getNote());
            table = PASSWORD_TABLE;
        } else if (data instanceof Category){
            Category c = ((Category) data);
            contentValues.put(KEY_CNAME, c.getCategoryName());
            table = CATEGORY_TABLE;
        }

        return sqLiteDatabase.insert(table, null, contentValues);
    }

    public Integer updateAll(Data data) {
        ContentValues contentValues = new ContentValues();
        String table = null;

        if (data instanceof Password) {
            Password p = ((Password) data);
            contentValues.put(KEY_TITLE, p.getTitle());
            contentValues.put(KEY_USERNAME, p.getUsername());
            contentValues.put(KEY_PASSWORD, p.getPassword());
            contentValues.put(KEY_CID, p.getCategoryID());
            contentValues.put(KEY_URL, p.getUrl());
            contentValues.put(KEY_NOTE, p.getNote());
            table = PASSWORD_TABLE;
        } else if (data instanceof Category){
            Category c = ((Category) data);
            contentValues.put(KEY_CNAME, c.getCategoryName());
            table = CATEGORY_TABLE;
        }

        return sqLiteDatabase.update(table, contentValues, null, null);
    }

    public Integer updateWhere(Data data, String whereClause, String[] whereArgs) {
        ContentValues contentValues = new ContentValues();
        String table = null;

        if (data instanceof Password) {
            Password p = ((Password) data);
            contentValues.put(KEY_TITLE, p.getTitle());
            contentValues.put(KEY_USERNAME, p.getUsername());
            contentValues.put(KEY_PASSWORD, p.getPassword());
            contentValues.put(KEY_CID, p.getCategoryID());
            contentValues.put(KEY_URL, p.getUrl());
            contentValues.put(KEY_NOTE, p.getNote());
            table = PASSWORD_TABLE;
        } else if (data instanceof Category){
            Category c = ((Category) data);
            contentValues.put(KEY_CNAME, c.getCategoryName());
            table = CATEGORY_TABLE;
        }

        return sqLiteDatabase.update(table, contentValues, whereClause, whereArgs);
    }

    public int deleteAll(String table) {
        return sqLiteDatabase.delete(table.toUpperCase(), null, null);
    }

    public int deleteWhere(String table, String whereClause, String[] whereArgs) {
        return sqLiteDatabase.delete(table.toUpperCase(), whereClause, whereArgs);
    }

    public ArrayList<Data> queryAll(String table) {
        Cursor cursor = sqLiteDatabase.query(table.toUpperCase(), null, null, null, null, null, null);

        ArrayList<Data> result = new ArrayList<Data>();

        if (table.toUpperCase() == PASSWORD_TABLE) {
            index_PID = cursor.getColumnIndex(KEY_PID);
            index_TITLE = cursor.getColumnIndex(KEY_TITLE);
            index_USERNAME = cursor.getColumnIndex(KEY_USERNAME);
            index_PASSWORD = cursor.getColumnIndex(KEY_PASSWORD);
            index_CID = cursor.getColumnIndex(KEY_CID);
            index_URL = cursor.getColumnIndex(KEY_URL);
            index_NOTE = cursor.getColumnIndex(KEY_NOTE);

            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                Password tp = new Password(cursor.getInt(index_PID), cursor.getString(index_TITLE), cursor.getString(index_USERNAME), cursor.getString(index_PASSWORD), cursor.getInt(index_CID), cursor.getString(index_URL), cursor.getString(index_NOTE));

                result.add(tp);
            }

        } else if (table.toUpperCase() == CATEGORY_TABLE) {
            index_CID = cursor.getColumnIndex(KEY_CID);
            index_CNAME = cursor.getColumnIndex(KEY_CNAME);

            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                Category tc = new Category(cursor.getInt(index_CID), cursor.getString(index_CNAME));

                result.add(tc);
            }
        }

        return result;
    }

    public ArrayList<Data> queryWhere(String table, String selection, String[] selectionArgs) {
        Cursor cursor = sqLiteDatabase.query(table.toUpperCase(), null, selection, selectionArgs, null, null, null);

        ArrayList<Data> result = new ArrayList<Data>();

        if (table.toUpperCase() == PASSWORD_TABLE) {
            index_PID = cursor.getColumnIndex(KEY_PID);
            index_TITLE = cursor.getColumnIndex(KEY_TITLE);
            index_USERNAME = cursor.getColumnIndex(KEY_USERNAME);
            index_PASSWORD = cursor.getColumnIndex(KEY_PASSWORD);
            index_CID = cursor.getColumnIndex(KEY_CID);
            index_URL = cursor.getColumnIndex(KEY_URL);
            index_NOTE = cursor.getColumnIndex(KEY_NOTE);

            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                Password tp = new Password(cursor.getInt(index_PID), cursor.getString(index_TITLE), cursor.getString(index_USERNAME), cursor.getString(index_PASSWORD), cursor.getInt(index_CID), cursor.getString(index_URL), cursor.getString(index_NOTE));

                result.add(tp);
            }

        } else if (table.toUpperCase() == CATEGORY_TABLE) {
            index_CID = cursor.getColumnIndex(KEY_CID);
            index_CNAME = cursor.getColumnIndex(KEY_CNAME);

            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                Category tc = new Category(cursor.getInt(index_CID), cursor.getString(index_CNAME));

                result.add(tc);
            }
        }

        return result;
    }

    public ArrayList<Data> queryLimit(String table, String[] columns, String selection, String[] selectionArgs, String limit) {
        Cursor cursor = sqLiteDatabase.query(table.toUpperCase(), columns, selection, selectionArgs, null, null, null, limit);

        ArrayList<Data> result = new ArrayList<Data>();

        if (table.toUpperCase() == PASSWORD_TABLE) {
            index_PID = cursor.getColumnIndex(KEY_PID);
            index_TITLE = cursor.getColumnIndex(KEY_TITLE);
            index_USERNAME = cursor.getColumnIndex(KEY_USERNAME);
            index_PASSWORD = cursor.getColumnIndex(KEY_PASSWORD);
            index_CID = cursor.getColumnIndex(KEY_CID);
            index_URL = cursor.getColumnIndex(KEY_URL);
            index_NOTE = cursor.getColumnIndex(KEY_NOTE);

            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                Password tp = new Password(cursor.getInt(index_PID), cursor.getString(index_TITLE), cursor.getString(index_USERNAME), cursor.getString(index_PASSWORD), cursor.getInt(index_CID), cursor.getString(index_URL), cursor.getString(index_NOTE));

                result.add(tp);
            }

        } else if (table.toUpperCase() == CATEGORY_TABLE) {
            index_CID = cursor.getColumnIndex(KEY_CID);
            index_CNAME = cursor.getColumnIndex(KEY_CNAME);

            for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
                Category tc = new Category(cursor.getInt(index_CID), cursor.getString(index_CNAME));

                result.add(tc);
            }
        }

        return result;
    }

    public class SQLiteHelper extends SQLiteOpenHelper {
        public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SCRIPT_CREATE_CATEGORY_TABLE);
            db.execSQL(SCRIPT_CREATE_PASSWORD_TABLE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+ CATEGORY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ PASSWORD_TABLE);
            onCreate(db);
        }
    }
}

