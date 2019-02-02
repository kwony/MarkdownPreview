package com.kwony.mdpreview;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kwony.mdpreview.Builders.AskDialog;
import com.kwony.mdpreview.Builders.SaveFileDialog;
import com.kwony.mdpreview.Database.DatabaseHelper;
import com.kwony.mdpreview.Database.DatabaseManager;
import com.kwony.mdpreview.Database.SharedPreferenceManager;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;
import com.kwony.mdpreview.Tabs.MarkdownTabs.IMarkdownTab;
import com.kwony.mdpreview.Tabs.Pager.MarkdownPagerAdapter;
import com.kwony.mdpreview.Tabs.SlidingTabLib.SlidingTabLayout;
import com.kwony.mdpreview.Utilities.FileManager;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String BR_SAVE_DIALOG = "com.kwony.mdpreview.br.savedialog";
    public final static String BR_SELECT_DIALOG = "com.kwony.mdpreview.br.selectdialog";
    public final static String BR_ASK_DIALOG = "com.kwony.mdpreview.br.askdialog";
    public final static String RC_FILE_ID = "result_code_file_id";



    private ViewPager viewPager;
    private MarkdownPagerAdapter adapter;
    private SlidingTabLayout tabs;

    private DialogReceiver dialogReceiver;
    private IntentFilter dialogIntentFilter;

    private TextView tvTitle;

    private ImageButton ibShare;
    private ImageButton ibOpen;
    private ImageButton ibSave;
    private ImageButton ibAdd;
    CharSequence Titles[] = { "Preview", "Code" };
    private int selectedImgSrc[] = { R.drawable.ic_code_black_24dp, R.drawable.ic_mode_edit_black_24dp };
    private int unselectedImgSrc [] = { R.drawable.ic_code_gray_24dp, R.drawable.ic_mode_edit_gray_24dp };

    private final static int ASK_OPEN_PERMISSION = 0;

    private final static int PICK_FILE_RESULT_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTitle = findViewById(R.id.tvTitle);
        paletteSetup();

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ASK_OPEN_PERMISSION);

        adapter = new MarkdownPagerAdapter(getSupportFragmentManager(), Titles,
                selectedImgSrc, unselectedImgSrc, Titles.length);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int prePosition = -1;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int curPosition) {
                IMarkdownTab curImt = adapter.getRegisteredTab(curPosition);
                IMarkdownTab preImt = adapter.getRegisteredTab(prePosition);

                if (curPosition == prePosition || curImt == null)
                    return;

                if (preImt!=null)
                    preImt.cbPageUnSelected();

                curImt.cbPageSelected();
                prePosition = curPosition;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabs = findViewById(R.id.sliding_tab);
        tabs.setDistributeEvenly(true);
        tabs.setShowImage(true);
        tabs.setSelectedIndicatorColors(getResources().getColor(R.color.slidingBar, null));

        // XXX: setViewPage should be called at last to apply settings.
        tabs.setViewPager(viewPager);

        initializeDatabase();

        prepareWorkspace();

        dialogIntentFilter = new IntentFilter();
        dialogIntentFilter.addAction(BR_SAVE_DIALOG);
        dialogIntentFilter.addAction(BR_SELECT_DIALOG);
        dialogIntentFilter.addAction(BR_ASK_DIALOG);

        dialogReceiver = new DialogReceiver();

        registerReceiver(dialogReceiver, dialogIntentFilter);
    }

    private void paletteSetup() {
        ibShare = findViewById(R.id.ib_share);
        ibOpen = findViewById(R.id.ib_open);
        ibSave = findViewById(R.id.ib_save);
        ibAdd = findViewById(R.id.ib_note_add);

        /* Every image buttons has same animation effect on touch */
        ibShare.setOnTouchListener(ibTouchListener);
        ibSave.setOnTouchListener(ibTouchListener);
        ibOpen.setOnTouchListener(ibTouchListener);
        ibAdd.setOnTouchListener(ibTouchListener);

        ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ibOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivityForResult(intent, PICK_FILE_RESULT_CODE);
            }
        });

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceManager sharedPrefMgr = SharedPreferenceManager.getInstance();
                SaveFileDialog saveFileBuilder = new SaveFileDialog(MainActivity.this);

                FileInfo mirrorFile = getMirrorFileInfo();

                if (sharedPrefMgr.getCurrentFileId() == -1) {
                    /* Case name is required */
                    saveFileBuilder.saveFileDialog(mirrorFile);
                }
                else {
                    /* Case source file exist */
                    FileInfo srcFileInfo = getRecentFileInfo();

                    try {
                        FileManager.copyFile(mirrorFile, srcFileInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: Ask already working file needs save */

                SharedPreferenceManager shrPrefMgr = SharedPreferenceManager.getInstance();
                shrPrefMgr.setCurrentFileId(-1);
                prepareWorkspace();
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

                    LinearLayout llDenied = findViewById(R.id.llDenied);
                    llDenied.setVisibility(View.VISIBLE);

                    return;
                }
            }

            prepareWorkspace();

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

                // default file id and mirror file id should not be same
                long openFileId = data.getLongExtra(RC_FILE_ID, -1);

                if (isFileModified()) {
                    /* Ask user to save it or not */
                    AskDialog askDialog = new AskDialog(MainActivity.this,
                            getResources().getString(R.string.ask_user_save_file), openFileId);

                    askDialog.askDialog();
                    return;
                }

                prepareWorkspace(openFileId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dialogReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    private void prepareWorkspace() {
        FileInfo rctFile = getRecentFileInfo();

        if (rctFile == null || !FileManager.checkFileExist(rctFile)) {
            tvTitle.setText("NoTitle");
            createWorkspaceFile();
        }
        else {
            tvTitle.setText(rctFile.getFileName());
            updateWorkspaceFile(rctFile);
        }

        for (int i = 0; i < adapter.getCount(); i++) {
            IMarkdownTab imt = adapter.getRegisteredTab(i);
            if (imt != null)
                imt.cbSetTabView();
        }
    }

    private void prepareWorkspace(long newMirrorId) {
        SharedPreferenceManager sharedPrefMgr = SharedPreferenceManager.getInstance();
        sharedPrefMgr.setCurrentFileId(newMirrorId);

        prepareWorkspace();
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

    private FileInfo getMirrorFileInfo() {
        String mirrorFilePath =
                Environment.getExternalStorageDirectory() + "/" + getString(R.string.app_name) + "/";
        String mirrorFileName = getString(R.string.mirror_file_md);

        return new FileInfo(-1, mirrorFileName, mirrorFilePath,null);
    }

    /* Return 'true' on reading recent file, 'false' on failure */
    private FileInfo getRecentFileInfo() {
        SharedPreferenceManager sharedPrefMgr = SharedPreferenceManager.getInstance();
        RecentFileManager rctFileMgr = new RecentFileManager();
        long rctFileId = sharedPrefMgr.getCurrentFileId();

        if (rctFileId == -1)
            return null;

        FileInfo rctFileInfo = rctFileMgr.getFileInfo(rctFileId);

        return rctFileInfo;
    }

    private void updateWorkspaceFile(FileInfo fileInfo) {
        boolean updated = false;

        FileInfo mirrorFile = getMirrorFileInfo();

        try {
            updated = FileManager.copyFile(fileInfo, mirrorFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!updated) {
            createWorkspaceFile();
        }
    }

    private void initializeDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.registerTableManager(new RecentFileManager());

        /* Initialize database instance */
        DatabaseManager.initInstance(databaseHelper);
        SharedPreferenceManager.initInstance(getApplicationContext());

        /* Synchronize database file info list with system file */
        RecentFileManager rctFileMgr = new RecentFileManager();
        List<FileInfo> listFileInfo = rctFileMgr.getAllFileInfo();

//        /***
//         * TODO: Unable to find file problem.
//         * This is issue. Synchronizing database with real file data works until
//         * system power down. After rebooted checkFileExist always return false
//         * although it exists. It causes whole database files to be deleted
//         ***/
//        for (FileInfo fileInfo: listFileInfo) {
//            if (!FileManager.checkFileExist(fileInfo)) {
//                rctFileMgr.removeFileInfo(fileInfo);
//                Log.d(MainActivity.class.getSimpleName(),
//                        "Not found File name : " + fileInfo.getFileName());
//            }
//        }
    }

    /* Is currently workspace file different from original one? */
    private boolean isFileModified() {
        FileInfo mirrorFile = getMirrorFileInfo();
        FileInfo originFile = getRecentFileInfo();

        if (!FileManager.compareFileContent(mirrorFile, originFile) )
            return true;

        return false;
    }

    private View.OnTouchListener ibTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.getBackground().setColorFilter(0x93939393, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    v.getBackground().clearColorFilter();
                    v.invalidate();
                    break;
                }
            }
            return false;
        }
    };

    private class DialogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BR_ASK_DIALOG.equals(intent.getAction())) {
                boolean retStatus = intent.getBooleanExtra(AskDialog.RETURN_STATUS, false);
                long openFileId = intent.getLongExtra(AskDialog.OPEN_FILE_ID, -1);

                if (retStatus) {
                    /* Copy mirror file to original one to save it */

                    FileInfo mirrorFile = getMirrorFileInfo();
                    FileInfo originFile = getRecentFileInfo();

                    try {
                        FileManager.copyFile(mirrorFile, originFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                prepareWorkspace(openFileId);
            }
            else if (BR_SAVE_DIALOG.equals(intent.getAction())) {
                long openFileId = intent.getLongExtra(SaveFileDialog.OPEN_FILE_ID, -1);
                prepareWorkspace(openFileId);
            }
        }
    }
}
