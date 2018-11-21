package com.kwony.mdpreview.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "mdpreview.db";

    private List<TableManager> tableManagerList = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void registerTableManager(TableManager tableManager) {
        if (tableManagerList == null)
            tableManagerList = new ArrayList<TableManager>();

        tableManagerList.add(tableManager);
    }

    public void unregisterTableManager(TableManager tableManager) {
        tableManagerList.remove(tableManager);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (TableManager tableManager : tableManagerList)
            tableManager.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (TableManager tableManager : tableManagerList)
            tableManager.upgradeTable(db, oldVersion, newVersion);

        onCreate(db);
    }
}

