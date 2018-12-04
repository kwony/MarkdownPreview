package com.kwony.mdpreview.Builders;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.kwony.mdpreview.Database.SharedPreferenceManager;
import com.kwony.mdpreview.Database.Tables.RecentFileManager;
import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Utilities.FileManager;

import java.io.IOException;

public class CreateFileBuilder {
    private Activity mActivity;
    private RecentFileManager rctFileManager;

    public CreateFileBuilder(Activity activity) {
        mActivity = activity;
        rctFileManager = new RecentFileManager();
    }

    /* Get file name input and create new file.
     * @srcFileInfo: Source file information for new one.
     */
    public FileInfo createFileDialog(final FileInfo srcFileInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_save_file, null);
        final EditText etFileName = (EditText) dialogView.findViewById(R.id.etFileName);

        etFileName.setText(new String(mActivity.getString(R.string.default_file_name)));
        builder.setView(dialogView);

        builder.setPositiveButton(mActivity.getString(R.string.builder_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceManager sharedPrefManager = SharedPreferenceManager.getInstance();

                if (etFileName.getText().toString() == null
                        || etFileName.getText().toString().length() == 0) {
                    Toast.makeText(mActivity.getApplicationContext(),
                            mActivity.getString(R.string.invalid_file_name), Toast.LENGTH_LONG);
                    return;
                }

                // NOTE: Destination file follows source file path.
                FileInfo dstFileInfo = new FileInfo(-1, etFileName.getText().toString(),
                        srcFileInfo.getFilePath(), srcFileInfo.getFileDate());

                long newFileId = rctFileManager.insertFileInfo(dstFileInfo);
                sharedPrefManager.setCurrentFileId(newFileId);

                try {
                    FileManager.copyFile(srcFileInfo, dstFileInfo);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
