package com.kwony.mdpreview.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

public class DatabaseManager {
    private Integer openCounter = 0;

    private static DatabaseManager instance;
    private static SQLiteOpenHelper databaseHelper;
    private SQLiteDatabase database;

    public static synchronized void initInstance(SQLiteOpenHelper helper) {
        if (instance == null) {
            instance = new DatabaseManager();
            databaseHelper = helper;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName() +
                    " is not initialized, call initInstance(..) method first ");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        openCounter += 1;
        if (openCounter == 1) {
            database = databaseHelper.getWritableDatabase();
        }

        return database;
    }

    public synchronized void closeDatabase() {
        openCounter -= 1;

        if (openCounter == 0) {
            database.close();
        }
    }
}
