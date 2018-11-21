package com.kwony.mdpreview.Database;

import android.database.sqlite.SQLiteDatabase;

public interface TableManager {
    public void createTable(SQLiteDatabase db);
    public void upgradeTable(SQLiteDatabase db, int oldVersion, int newVersion);
}
