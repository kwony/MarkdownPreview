package com.kwony.mdpreview.Builders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.kwony.mdpreview.Database.SharedPreferenceManager;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;
import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.MainActivity;
import com.kwony.mdpreview.R;

import java.util.List;

public class AskDialog {

    private Activity mActivity;
    private String mTitle;
    private long mOpenFileId = -1;

    public AskDialog(Activity activity, String title, long openFileId) {
        mActivity = activity;
        mTitle = title;
        mOpenFileId = openFileId;
    }

    public void askDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        final SharedPreferenceManager sharedPrefMgr = SharedPreferenceManager.getInstance();

        builder.setTitle(mTitle)
                .setPositiveButton(mActivity.getResources().getString(R.string.builder_yes),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.BR_SAVE_OPEN);
                        intent.putExtra(MainActivity.KEY_SAVE_FILE_ID, sharedPrefMgr.getCurrentFileId());
                        intent.putExtra(MainActivity.KEY_OPEN_FILE_ID, mOpenFileId);

                        mActivity.sendBroadcast(intent);
                    }
                })
                .setNegativeButton(mActivity.getResources().getString(R.string.builder_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.BR_OPEN);
                        intent.putExtra(MainActivity.KEY_OPEN_FILE_ID, mOpenFileId);

                        mActivity.sendBroadcast(intent);
                    }
                });

        builder.show();
    }
}
