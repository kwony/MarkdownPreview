package com.kwony.mdpreview.Tabs.MarkdownTabs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import android.print.PdfPrint;

import com.kwony.mdpreview.MainActivity;
import com.kwony.mdpreview.R;
import com.kwony.mdpreview.Utilities.FileManager;
import com.webviewtopdf.PdfView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PreviewTab extends Fragment implements IMarkdownTab {
    private WebView wvPreview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_preview, container,false);
        wvPreview = v.findViewById(R.id.wvPreview);

        showParsedMarkdown();
        return v;
    }

    public void cbPageSelected() {
        showParsedMarkdown();
    }

    public void cbPageUnSelected() {
        convertPreviewToPng();
        convertPreviewToPdf();

    }

    public void cbSetTabView() {
        showParsedMarkdown();
    }

    private void showParsedMarkdown() {
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
    }

    private void convertPreviewToPng() {
        wvPreview.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        wvPreview.layout(0, 0, wvPreview.getMeasuredWidth(), wvPreview.getMeasuredHeight());
        wvPreview.setDrawingCacheEnabled(true);
        wvPreview.buildDrawingCache();

        // TODO: Set appropriate width, height parameter value based on device spec
        Bitmap bm = Bitmap.createBitmap(wvPreview.getWidth(), wvPreview.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();

        Log.d(PreviewTab.class.getSimpleName(), "height: " + wvPreview.getHeight()
                + " width: " + wvPreview.getWidth());

        canvas.drawBitmap(bm, 0, iHeight, paint);
        wvPreview.draw(canvas);

        if (bm != null) {
            try {
                String path = Environment.getExternalStorageDirectory()
                                + File.separator + getString(R.string.app_name) + File.separator;
                OutputStream fOut = null;
                File file = new File(path, "test.png");
                fOut = new FileOutputStream(file);

                bm.compress(Bitmap.CompressFormat.PNG, 50, fOut);
                fOut.flush();
                fOut.close();
                bm.recycle();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void convertPreviewToPdf() {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + getString(R.string.app_name) + File.separator;
        File file = new File(path);
        String fileName = "test.pdf";

        PdfView.createWebPrintJob(getActivity(), wvPreview, file, fileName, new PdfView.Callback(){
            @Override
            public void success(String path) {
                Log.d(PreviewTab.class.getSimpleName(), "create web print job succeeded");
            }
            @Override
            public void failure() {
                Log.d(PreviewTab.class.getSimpleName(), "create web print job failed");

            }
        });
    }
}
