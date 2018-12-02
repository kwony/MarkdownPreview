package com.kwony.mdpreview.Database.Tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.kwony.mdpreview.Database.DatabaseManager;
import com.kwony.mdpreview.Database.TableManager;
import com.kwony.mdpreview.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class RecentFileManager implements TableManager {
    public static final String FILE_TABLE_NAME = "file_table";
    public static final String KEY_FILE_ID = "file_id";
    public static final String KEY_FILE_NAME = "file_name";
    public static final String KEY_FILE_PATH = "file_path";
    public static final String KEY_FILE_DATE = "file_date";

    public static final int FILE_ID_IDX = 0;
    public static final int FILE_NAME_IDX = 1;
    public static final int FILE_PATH_IDX = 2;
    public static final int FILE_DATE_IDX = 3;


    public void createTable(SQLiteDatabase db) {
        String CREATE_FILE_TABLE = "CREATE TABLE IF NOT EXISTS " + FILE_TABLE_NAME + "("
                + KEY_FILE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_FILE_NAME + " TEXT,"
                + KEY_FILE_PATH + " TEXT, " + KEY_FILE_DATE + " TEXT"
                + ");";

        db.execSQL(CREATE_FILE_TABLE);
    }

    public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<FileInfo> getAllFileInfo() {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        List<FileInfo> fileInfoList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + FILE_TABLE_NAME;

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor == null)
            return null;

        if (cursor.moveToFirst()) {
            do {
                FileInfo fileInfo = new FileInfo(
                        cursor.getInt(FILE_ID_IDX),
                        cursor.getString(FILE_NAME_IDX),
                        cursor.getString(FILE_PATH_IDX),
                        cursor.getString(FILE_DATE_IDX)
                );
            } while(cursor.moveToNext());
        }

        DatabaseManager.getInstance().closeDatabase();
        return fileInfoList;
    }

    /* Note:
     * RecentFileTable does not have 'update' function to keep table entries
     * in ascending order. For example, when user opened a file which is already
     * registered in RecentFileTable then table deletes existing row and recreate it
     * at the bottom of table.
     * */
    public FileInfo insertFileInfo(FileInfo fileInfo) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        long newFileId = -1;
        FileInfo newFileInfo;

        db.beginTransaction();

        if (fileInfo.getFileId() >= 0) { deleteFileInfo(db, fileInfo); }

        newFileId = addFileInfo(db, fileInfo);
        db.setTransactionSuccessful();
        db.endTransaction();

        DatabaseManager.getInstance().closeDatabase();

        newFileInfo = new FileInfo(newFileId, fileInfo.getFileName(),
                fileInfo.getFilePath(), fileInfo.getFileDate());

        return newFileInfo;
    }

    private long addFileInfo(SQLiteDatabase db, FileInfo fileInfo) {
        long newFileId = -1;
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_FILE_NAME, fileInfo.getFileName());
        contentValues.put(KEY_FILE_PATH, fileInfo.getFilePath());
        contentValues.put(KEY_FILE_DATE, fileInfo.getFileDate());

        newFileId = db.insert(FILE_TABLE_NAME, null, contentValues);

        return newFileId;
    }

    private void deleteFileInfo(SQLiteDatabase db, FileInfo fileInfo) {
        db.delete(FILE_TABLE_NAME, KEY_FILE_ID + " =?",
                new String[] { String.valueOf(fileInfo.getFileId())});
    }
}
