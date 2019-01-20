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

public class SelectFileDialog {
    private Activity mActivity;
    private RecentFileManager rctFileManager;

    public SelectFileDialog(Activity activity) {
        mActivity = activity;
        rctFileManager = new RecentFileManager();
    }

    public void selectFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        List<FileInfo> rctFileInfoList = rctFileManager.getAllFileInfo();
        CharSequence[] itemsRctFileName = new CharSequence[rctFileInfoList.size()];
        final long[] itemsRctFileId = new long[rctFileInfoList.size()];

        for (int i = 0; i < rctFileInfoList.size(); i++) {
            itemsRctFileName[i] = rctFileInfoList.get(i).getFileName();
            itemsRctFileId[i] = rctFileInfoList.get(i).getFileId();
        }

        builder.setSingleChoiceItems(itemsRctFileName, 0, null)
                .setTitle(mActivity.getResources().getString(R.string.builder_select_file_title))
                .setPositiveButton(mActivity.getResources().getString(R.string.builder_ok),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceManager sharedPrefManager = SharedPreferenceManager.getInstance();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        long selectedFileId = itemsRctFileId[selectedPosition];
                        sharedPrefManager.setCurrentFileId(selectedFileId);

                        mActivity.sendBroadcast(new Intent(MainActivity.BR_SELECT_DIALOG));
                    }
                });

        builder.show();
    }
}
