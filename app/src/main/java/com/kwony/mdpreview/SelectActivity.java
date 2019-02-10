package com.kwony.mdpreview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kwony.mdpreview.Adapters.SelectFileAdapter;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;
import com.kwony.mdpreview.Utilities.FileManager;

public class SelectActivity extends AppCompatActivity {
    public final static String BR_DELETE = "com.kwony.mdpreview.selectactivity.br.delete";

    public final static String KEY_DELETE_FILE_ID = "key_delete_file_id";

    private RecyclerView recyclerView;
    private RecentFileManager recentFileManager;
    private SelectFileAdapter selectFileAdapter;

    private SelectActivityReceiver saReceiver;
    private IntentFilter saIntentFilter;

    private static final int GRID_SPAN_NUM = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        recyclerView = findViewById(R.id.recyclerView);
        recentFileManager = new RecentFileManager();

        saIntentFilter = new IntentFilter();
        saIntentFilter.addAction(BR_DELETE);
        saReceiver = new SelectActivityReceiver();
        registerReceiver(saReceiver, saIntentFilter);

        setUserInterface();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(saReceiver);
        super.onDestroy();
    }

    private void setUserInterface() {
        selectFileAdapter = new SelectFileAdapter(this,
                recentFileManager.getAllFileInfo());

        GridLayoutManager glManager = new GridLayoutManager(SelectActivity.this, GRID_SPAN_NUM);
        recyclerView.setLayoutManager(glManager);
        recyclerView.setAdapter(selectFileAdapter);
    }

    private class SelectActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BR_DELETE.equals(intent.getAction())) {
                long deleteFileId = intent.getLongExtra(KEY_DELETE_FILE_ID, -1);

                if (deleteFileId == -1) return;

                FileInfo deleteFileInfo = recentFileManager.getFileInfo(deleteFileId);
                recentFileManager.removeFileInfo(deleteFileInfo);

                FileManager.deleteFile(deleteFileInfo);

                setUserInterface();
            }
            else {
                /* Dumps... */
            }
        }
    }

}
