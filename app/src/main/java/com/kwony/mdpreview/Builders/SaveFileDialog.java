package com.kwony.mdpreview.Builders;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kwony.mdpreview.Database.SharedPreferenceManager;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;
import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.MainActivity;
import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Utilities.FileManager;

import java.io.IOException;

public class SaveFileDialog {
    private Activity mActivity;
    private RecentFileManager rctFileManager;

    public SaveFileDialog(Activity activity) {
        mActivity = activity;
        rctFileManager = new RecentFileManager();
    }

    /* Get file name input and create new file.
     * @srcFileInfo: Source file information for new one.
     */
    public FileInfo saveFileDialog(final FileInfo srcFileInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_file, null);
        final EditText etFileName = (EditText) dialogView.findViewById(R.id.etFileName);

        etFileName.setText(new String(mActivity.getString(R.string.default_file_name)));
        builder.setView(dialogView);

        builder.setPositiveButton(mActivity.getString(R.string.builder_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (etFileName.getText().toString() == null
                        || etFileName.getText().toString().length() == 0) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            mActivity.getString(R.string.invalid_file_name), Toast.LENGTH_LONG);
                    return;
                }

                // NOTE: Destination file follows source file path.
                FileInfo dstFileInfo = new FileInfo(-1, etFileName.getText().toString(),
                        srcFileInfo.getFilePath(), srcFileInfo.getFileDate());

                /*
                 * TODO: Check there is file which has same name.
                 */

                long newFileId = rctFileManager.insertFileInfo(dstFileInfo);
                Intent intent = new Intent(MainActivity.BR_SAVE_OPEN);
                intent.putExtra(MainActivity.KEY_OPEN_FILE_ID, newFileId);
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
