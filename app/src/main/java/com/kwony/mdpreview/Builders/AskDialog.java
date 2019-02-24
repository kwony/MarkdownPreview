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
import com.kwony.mdpreview.SelectActivity;

import java.util.List;

public class AskDialog {

    private Activity mActivity;
    private String mTitle;

    public AskDialog(Activity activity, String title) {
        mActivity = activity;
        mTitle = title;
    }

    public void askSaveDialog(final long saveFileId, final long openFileId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mTitle)
                .setPositiveButton(mActivity.getResources().getString(R.string.builder_yes),
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.BR_SAVE_OPEN);
                        intent.putExtra(MainActivity.KEY_SAVE_FILE_ID, saveFileId);
                        intent.putExtra(MainActivity.KEY_OPEN_FILE_ID, openFileId);

                        mActivity.sendBroadcast(intent);
                    }
                })
                .setNegativeButton(mActivity.getResources().getString(R.string.builder_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.BR_OPEN);
                        intent.putExtra(MainActivity.KEY_OPEN_FILE_ID, openFileId);

                        mActivity.sendBroadcast(intent);
                    }
                });

        builder.show();
    }

    public void askOverwriteDialog(final long saveFileId, final long openFileId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mTitle)
                .setPositiveButton(mActivity.getResources().getString(R.string.builder_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.BR_SAVE_OPEN);
                                intent.putExtra(MainActivity.KEY_SAVE_FILE_ID, saveFileId);
                                intent.putExtra(MainActivity.KEY_OPEN_FILE_ID, openFileId);

                                mActivity.sendBroadcast(intent);
                            }
                        })
                .setNegativeButton(mActivity.getResources().getString(R.string.builder_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
    }

    public void askDeleteDialog(final long deleteFileId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mTitle)
                .setPositiveButton(mActivity.getResources().getString(R.string.builder_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SelectActivity.BR_DELETE);
                                intent.putExtra(SelectActivity.KEY_DELETE_FILE_ID, deleteFileId);
                                mActivity.sendBroadcast(intent);
                            }
                        })
                .setNegativeButton(mActivity.getResources().getString(R.string.builder_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
    }

    public void askShareType(CharSequence[] shareType, final MainActivity.CallbackShareType callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);


        builder.setTitle(mTitle)
                .setSingleChoiceItems(shareType, 0, null)
                .setPositiveButton(mActivity.getResources().getString(R.string.builder_yes),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                callback.typeSelected(((AlertDialog) dialog).getListView().getCheckedItemPosition());
                            }
                        })
                .setNegativeButton(mActivity.getResources().getString(R.string.builder_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builder.show();
    }
}
