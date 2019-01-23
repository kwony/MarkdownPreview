package com.kwony.mdpreview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kwony.mdpreview.Adapters.SelectFileAdapter;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;

public class SelectActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecentFileManager recentFileManager;
    private SelectFileAdapter selectFileAdapter;

    private static final int GRID_SPAN_NUM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        recyclerView = findViewById(R.id.recyclerView);
        recentFileManager = new RecentFileManager();
        selectFileAdapter = new SelectFileAdapter(this,
                recentFileManager.getAllFileInfo());

        GridLayoutManager glManager = new GridLayoutManager(SelectActivity.this, GRID_SPAN_NUM);
        recyclerView.setLayoutManager(glManager);
        recyclerView.setAdapter(selectFileAdapter);
    }
}
