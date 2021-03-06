package com.kwony.mdpreview.Builders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kwony.mdpreview.Database.SharedPreferenceManager;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;
import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.MainActivity;
import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Utilities.FileManager;

import java.io.IOException;
import java.util.List;

public class SaveFileDialog {
    private Activity mActivity;
    private RecentFileManager rctFileManager;

    public SaveFileDialog(Activity activity) {
        mActivity = activity;
        rctFileManager = new RecentFileManager();
    }

    /* Get file name input and create new file.
     * @srcFileInfo: Source file information for new one.
     * @openFileId: file to open after save.
     */
    public FileInfo saveFileDialog(final FileInfo srcFileInfo, final long openFileId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_file, null);
        final TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvTitle);
        final EditText etFileName = (EditText) dialogView.findViewById(R.id.etFileName);
        final List<FileInfo> listFileInfo = rctFileManager.getAllFileInfo();

        tvTitle.setText(new String(mActivity.getString(R.string.ask_user_type_file_name)));
        etFileName.setText(new String(mActivity.getString(R.string.default_file_name)));
        builder.setView(dialogView);

        builder.setPositiveButton(mActivity.getString(R.string.builder_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = etFileName.getText().toString();

                if (fileName == null || fileName.length() == 0) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            mActivity.getString(R.string.invalid_file_name), Toast.LENGTH_LONG);
                    return;
                }

                for (FileInfo fileInfo: listFileInfo) {
                    if (fileInfo.getFileName().equals(fileName)) {
                        AskDialog askDialog = new AskDialog(mActivity,
                                mActivity.getString(R.string.ask_user_overwrite_file));

                        askDialog.askOverwriteDialog(fileInfo.getFileId(), fileInfo.getFileId());
                        return;
                    }
                }

                // NOTE: Destination file follows source file path.
                FileInfo dstFileInfo = new FileInfo(-1, etFileName.getText().toString(),
                        srcFileInfo.getFilePath(), srcFileInfo.getFileDate());

                long newFileId = rctFileManager.insertFileInfo(dstFileInfo);
                Intent intent = new Intent(MainActivity.BR_SAVE_OPEN);
                intent.putExtra(MainActivity.KEY_OPEN_FILE_ID,
                        openFileId == -1 ? newFileId : openFileId);
                intent.putExtra(MainActivity.KEY_SAVE_FILE_ID, newFileId);

                mActivity.sendBroadcast(intent);
            }
        });

        builder.setNegativeButton(mActivity.getString(R.string.builder_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

        return srcFileInfo;
    }
}
