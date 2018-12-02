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
    private Button btnSave;
    private EditText etCode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_code, container,false);

        btnSave = v.findViewById(R.id.btnSave);
        etCode = v.findViewById(R.id.etCode);

        etCode.setText(FileManager.readFileValue(
                Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.app_name),
                getString(R.string.mirror_file_md)), TextView.BufferType.EDITABLE);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

        return v;
    }

    public void cbPageSelected() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void cbPageUnSelected() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        saveFile();
    }

    private void saveFile() {
        FileManager.writeFileValue(Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.app_name),
                getString(R.string.mirror_file_md),
                etCode.getText().toString());
    }
}
