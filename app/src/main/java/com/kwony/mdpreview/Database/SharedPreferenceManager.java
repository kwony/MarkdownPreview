package com.kwony.mdpreview.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    public static final String PREF_NAME = "mdpreview_pref";
    public static final String FILE_ID = "file_id";

    private static SharedPreferenceManager instance;

    private static SharedPreferences settings;

    public static synchronized void initInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferenceManager();
            settings = context.getSharedPreferences(PREF_NAME, 0);
        }
    }

    public static SharedPreferenceManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(SharedPreferenceManager.class.getSimpleName() +
                    " is not initialized, call initInstance(..) method first ");
        }

        return instance;
    }

    public long getCurrentFileId() {
        return settings.getLong(FILE_ID, -1);
    }

    public void setCurrentFileId(long fileId) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(FILE_ID, fileId);
        editor.commit();
    }
}
