package com.kwony.mdpreview;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kwony.mdpreview.Utilities.ImgTouchListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final static String BR_SAVE_OPEN = "com.kwony.mdpreview.br.save_open";
    public final static String BR_OPEN = "com.kwony.mdpreview.br.open";
    public final static String RC_FILE_ID = "result_code_file_id";

    public final static String KEY_OPEN_FILE_ID = "key_open_file_id";
    public final static String KEY_SAVE_FILE_ID = "key_save_file_id";

    private ViewPager viewPager;
    private MarkdownPagerAdapter adapter;
    private SlidingTabLayout tabs;

    private WorkspaceReceiver wsReceiver;
    private IntentFilter dialogIntentFilter;

    private TextView tvTitle;

    private ImageButton ibShare;
    private ImageButton ibOpen;
    private ImageButton ibSave;
    private ImageButton ibAdd;
    CharSequence Titles[] = { "Preview", "Code" };
    private int selectedImgSrc[] = { R.drawable.ic_code_black_preview, R.drawable.ic_code_black_edit_24dp };
    private int unselectedImgSrc [] = { R.drawable.ic_code_gray_preview, R.drawable.ic_code_gray_edit_24dp };

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

        dialogIntentFilter = new IntentFilter();
        dialogIntentFilter.addAction(BR_OPEN);
        dialogIntentFilter.addAction(BR_SAVE_OPEN);

        wsReceiver = new WorkspaceReceiver();

        registerReceiver(wsReceiver, dialogIntentFilter);
    }

    private void paletteSetup() {
        ibShare = findViewById(R.id.ib_share);
        ibOpen = findViewById(R.id.ib_open);
        ibSave = findViewById(R.id.ib_save);
        ibAdd = findViewById(R.id.ib_note_add);

        /* Every image buttons has same animation effect on touch */
        ibShare.setOnTouchListener(new ImgTouchListener());
        ibSave.setOnTouchListener(new ImgTouchListener());
        ibOpen.setOnTouchListener(new ImgTouchListener());
        ibAdd.setOnTouchListener(new ImgTouchListener());

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
                    saveFileBuilder.saveFileDialog(mirrorFile, -1);
                }
                else {
                    /* Case source file exist */
                    FileInfo srcFileInfo = getRecentFileInfo();
                    FileInfo[] args = { mirrorFile, srcFileInfo };

                    FileCopyOpenTask fileCopyOpenTask = new FileCopyOpenTask();
                    fileCopyOpenTask.execute(args);
                }
            }
        });

        ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: Ask already working file needs save */

                prepareWorkspace(true);
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

            /* XXX:
             * onRequestPermissionResult is called whenever application is launched
             * although user already gave permission. It's because Android checks permission again.
             * Storage accesses before arriving here are denied by Android policy.
             *
             * To access storage file on application start, add code here.
             */

            prepareWorkspace(!FileManager.checkFileExist(getMirrorFileInfo()));
            syncDatabaseWithSystem();

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
                SharedPreferenceManager sharedPrefMgr = SharedPreferenceManager.getInstance();

                if (sharedPrefMgr.getCurrentFileId() == -1 || isFileModified()) {
                    /* Ask user to save it or not */
                    AskDialog askDialog = new AskDialog(MainActivity.this,
                            getResources().getString(R.string.ask_user_save_file),
                            sharedPrefMgr.getCurrentFileId(), openFileId);

                    askDialog.askSaveDialog();
                    return;
                }

                RecentFileManager rctFileMgr = new RecentFileManager();
                FileInfo mirrorFile = getMirrorFileInfo();
                FileInfo openFile = rctFileMgr.getFileInfo(openFileId);

                FileInfo[] args = { null, null, openFile, mirrorFile };
                new FileCopyOpenTask().execute(args);
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
        unregisterReceiver(wsReceiver);
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

    private void prepareWorkspace(boolean addNew) {
        if (addNew) {
            SharedPreferenceManager shrPrefMgr = SharedPreferenceManager.getInstance();
            shrPrefMgr.setCurrentFileId(-1);
            new CreateMirrorFileTask().execute();

            return;
        }

        FileInfo rctFile = getRecentFileInfo();

        if (rctFile == null || !FileManager.checkFileExist(rctFile)) {
            tvTitle.setText("NoTitle");
        }
        else {
            tvTitle.setText(rctFile.getFileName());
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

        prepareWorkspace(false);
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

    private void initializeDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.registerTableManager(new RecentFileManager());

        /* Initialize database instance */
        DatabaseManager.initInstance(databaseHelper);
        SharedPreferenceManager.initInstance(getApplicationContext());
    }

    private void syncDatabaseWithSystem() {
        /* Synchronize database file info list with system file */
        RecentFileManager rctFileMgr = new RecentFileManager();
        List<FileInfo> listFileInfo = rctFileMgr.getAllFileInfo();

        for (FileInfo fileInfo: listFileInfo) {
            if (!FileManager.checkFileExist(fileInfo)) {
                rctFileMgr.removeFileInfo(fileInfo);
                Log.d(MainActivity.class.getSimpleName(),
                        "Not found File name : " + fileInfo.getFileName());
            }
        }
    }

    /* Is currently workspace file different from original one? */
    private boolean isFileModified() {
        FileInfo mirrorFile = getMirrorFileInfo();
        FileInfo originFile = getRecentFileInfo();

        if (!FileManager.compareFileContent(mirrorFile, originFile) )
            return true;

        return false;
    }

    private class WorkspaceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BR_OPEN.equals(intent.getAction())) {
                long openFileId = intent.getLongExtra(KEY_OPEN_FILE_ID, -1);

                if (openFileId < 0) {
                    Log.e(MainActivity.class.getSimpleName(), "Invalid openFileId");
                    return;
                }

                RecentFileManager rctFileMgr = new RecentFileManager();
                FileInfo mirrorFile = getMirrorFileInfo();
                FileInfo openFile = rctFileMgr.getFileInfo(openFileId);
                FileInfo[] args = { null, null, openFile, mirrorFile };
                new FileCopyOpenTask().execute(args);
            }
            else if (BR_SAVE_OPEN.equals(intent.getAction())) {
                /*
                 * origFileId: Original file to be saved
                 * openFileId: File to open after saving.
                 */

                RecentFileManager rctFileMgr = new RecentFileManager();
                long origFileId = intent.getLongExtra(KEY_SAVE_FILE_ID, -1);
                long openFileId = intent.getLongExtra(KEY_OPEN_FILE_ID, -1);

                FileInfo mirrorFile = getMirrorFileInfo();
                FileInfo originFile = rctFileMgr.getFileInfo(origFileId);
                FileInfo openFile = rctFileMgr.getFileInfo(openFileId);

                if (origFileId == -1) {
                    SaveFileDialog saveFileDialog = new SaveFileDialog(MainActivity.this);
                    saveFileDialog.saveFileDialog(mirrorFile, openFileId);
                    return;
                }

                if (openFileId == -1
                        || mirrorFile == null || originFile == null) {
                    Log.e(MainActivity.class.getSimpleName(), "Something Wrong!");
                    return;
                }

                FileInfo[] args = { mirrorFile, originFile, openFile, mirrorFile };
                FileCopyOpenTask fileCopyOpenTask = new FileCopyOpenTask();
                fileCopyOpenTask.execute(args);
            }

        }
    }

    private class CreateMirrorFileTask extends AsyncTask<Void, Void, Void> {
        private int WAIT_BUFFER = 300;
        private AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        private AlertDialog dialog;

        @Override
        protected void onPreExecute() {
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(R.layout.dialog_file_copy_task);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean folderCreated;

            folderCreated = FileManager.createFolder(
                    Environment.getExternalStorageDirectory() + File.separator,
                    getString(R.string.app_name));

            if (folderCreated) {
                FileManager.createFile(
                        Environment.getExternalStorageDirectory() + File.separator + "/" + getString(R.string.app_name) + "/"
                        , getString(R.string.mirror_file_md), getString(R.string.initial_mirror_value));
            }

            try {
                Thread.sleep(WAIT_BUFFER);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            prepareWorkspace(false);
            dialog.dismiss();
            super.onPostExecute(aVoid);
        }
    }

    private class FileCopyOpenTask extends AsyncTask<FileInfo, Void, Boolean> {
        private int WAIT_BUFFER = 700;
        private AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        private AlertDialog dialog;
        private long openFileId = -1;

        @Override
        protected void onPreExecute() {
            builder.setCancelable(false); // if you want user to wait for some process to finish,
            builder.setView(R.layout.dialog_file_copy_task);
            dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(FileInfo... fileInfos) {
            Boolean status = false;

            /* Elements in array should be ordered in this way
             * @ first element is source file to copy
             * @ second element is destination file to be copied
             * @ third element is the file to be opened at last.
             * @ forth element is mirror file which is used for workspace
             */
            FileInfo srcFile = fileInfos.length < 1 ? null : fileInfos[0];
            FileInfo dstFile = fileInfos.length < 2 ? null : fileInfos[1];
            FileInfo openFile = fileInfos.length < 3 ? null : fileInfos[2];
            FileInfo mirrorFile = fileInfos.length < 4 ? null : fileInfos[3];

            try {
                Thread.sleep(WAIT_BUFFER);

                if (srcFile != null && dstFile != null)
                    status = FileManager.copyFile(srcFile, dstFile);

                if (openFile != null && mirrorFile != null) {
                    status = FileManager.copyFile(openFile, mirrorFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (openFile != null)
                openFileId = openFile.getFileId();

            return status;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dialog.dismiss();

            if (!result) {
                Toast.makeText(MainActivity.this, "Failed to copy file",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (openFileId != -1)
                prepareWorkspace(openFileId);

            super.onPostExecute(result);
        }
    }
}
