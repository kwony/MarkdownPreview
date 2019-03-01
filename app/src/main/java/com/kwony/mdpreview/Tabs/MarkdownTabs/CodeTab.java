package com.kwony.mdpreview.Tabs.MarkdownTabs;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Utilities.FileManager;

import java.io.File;

public class CodeTab extends Fragment implements IMarkdownTab {
    private EditText etCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_code, container,false);

        etCode = v.findViewById(R.id.etCode);

        etCode.setText(FileManager.readFileValue(
                Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.storage_folder_name),
                getString(R.string.mirror_file_md)), TextView.BufferType.EDITABLE);

        return v;
    }

    public void cbPageSelected() {
//        TODO: find flag which has soft keyboard is enabled
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void cbPageUnSelected() {
//        TODO: find flag which has soft keyboard is enabled
//        InputMethodManager inputMethodManager =
//                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        saveFile();
    }

    public void cbSetTabView() {
        etCode.setText(FileManager.readFileValue(
                Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.storage_folder_name),
                getString(R.string.mirror_file_md)), TextView.BufferType.EDITABLE);
    }

    private void saveFile() {
        FileManager.writeFileValue(Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.storage_folder_name),
                getString(R.string.mirror_file_md),
                etCode.getText().toString());
    }
}
