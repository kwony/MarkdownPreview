package com.kwony.mdpreview.Database.Tables;

import android.database.sqlite.SQLiteDatabase;

import com.kwony.mdpreview.Database.TableManager;

public class RecentFileManager implements TableManager {
    public static final String FILE_TABLE_NAME = "file_table";
    public static final String KEY_FILE_ID = "file_id";
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_FILE_PATH = "file_path";
    public static final String KEY_FILE_DATE = "file_date";


    public void createTable(SQLiteDatabase db) {
        String CREATE_FILE_TABLE = "CREATE TABLE IF NOT EXISTS " + FILE_TABLE_NAME + "("
                + KEY_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_FILE_NAME + " TEXT,"
                + KEY_FILE_PATH + " TEXT, " + KEY_FILE_DATE + " TEXT"
                + ");";

        db.execSQL(CREATE_FILE_TABLE);
    }

    public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
