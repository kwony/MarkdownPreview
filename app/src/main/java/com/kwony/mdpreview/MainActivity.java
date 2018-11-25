package com.kwony.mdpreview;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.kwony.mdpreview.Tabs.Pager.MainPagerAdapter;
import com.kwony.mdpreview.Tabs.Pager.MainViewPager;
import com.kwony.mdpreview.Utilities.FileManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    MainViewPager viewPager;

    private ImageButton ibShare;
    private ImageButton ibOpen;
    private ImageButton ibSave;

    private final static int ASK_OPEN_PERMISSION = 0;

    private final static int PICK_FILE_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionbarSetup();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ASK_OPEN_PERMISSION);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                case R.id.action_markdown:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.action_recent:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.action_theme:
                    viewPager.setCurrentItem(2);
                    break;
                }
                return true;
            }
        });

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));

        createWorkspaceFile();
    }

    private void actionbarSetup() {
        View v;

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom);
        v = getSupportActionBar().getCustomView();

        ibShare = v.findViewById(R.id.ib_share);
        ibOpen = v.findViewById(R.id.ib_open);
        ibSave = v.findViewById(R.id.ib_save);

        ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ibOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileContent();
            }
        });

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode) {
        case ASK_OPEN_PERMISSION:
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(getApplicationContext(), R.string.toast_unable_contiue_without_perm,
                            Toast.LENGTH_LONG).show();
                    return;
                }
            }

            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case PICK_FILE_RESULT_CODE:
            if (resultCode == RESULT_OK) {
                /*
                 * TODO List.
                 *
                 * 1. Check current file is under modified
                 *  1.1 Ask user save it or not.
                 * 2. Read designated file and get buffers
                 * 3. Create new file 'currentFile.md' and use it as mirror.
                 * 4. Run a thread to check whether two files are different
                 * */

            }
        }
    }

    private void openFileContent() {
        Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        fileIntent.setType("*/*");
        try {
            startActivityForResult(fileIntent, PICK_FILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createWorkspaceFile() {
        boolean folderCreated;

        folderCreated = FileManager.createFolder(
                Environment.getExternalStorageDirectory() + File.separator,
                getString(R.string.app_name));

        if (folderCreated) {
            FileManager.createFile(
                    Environment.getExternalStorageDirectory() + File.separator + "/" + getString(R.string.app_name) + "/"
                    , getString(R.string.mirror_file_md), getString(R.string.initial_mirror_value));
        }
    }
}
