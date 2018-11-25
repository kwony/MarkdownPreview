package com.kwony.mdpreview.Tabs.MarkdownTabs;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Utilities.FileManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;

public class PreviewTab extends Fragment {
    private WebView wvPreview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_preview, container,false);
        wvPreview = v.findViewById(R.id.wvPreview);

        StringBuffer fileValue = FileManager.readFileValue(
                Environment.getExternalStorageDirectory()
                        + File.separator + getString(R.string.app_name),
                getString(R.string.mirror_file_md));;

        if (fileValue != null) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(fileValue.toString());
            HtmlRenderer renderer = HtmlRenderer.builder().build();

            wvPreview.loadData(renderer.render(document),
                    "text/html; charset=utf-8", "UTF-8");
        }

        return v;
    }
}
