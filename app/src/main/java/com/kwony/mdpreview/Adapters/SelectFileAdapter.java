package com.kwony.mdpreview.Adapters;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.R;
import com.kwony.mdpreview.SelectActivity;
import com.kwony.mdpreview.Utilities.FileManager;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.util.List;

public class SelectFileAdapter extends RecyclerView.Adapter<SelectFileAdapter.ViewHolder> {
    private Context mContext;
    private List<FileInfo> mListFileInfo;


    public SelectFileAdapter(Context context, List<FileInfo> listFileInfo) {
        mContext = context;
        mListFileInfo = listFileInfo;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        CardView cardView;
        WebView wvPreview;

        public ViewHolder(View view) {
            super(view);

            tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            cardView = (CardView) view.findViewById(R.id.cardView);
            wvPreview = (WebView) view.findViewById(R.id.wvPreview);
        }
    }

    @NonNull
    @Override
    public SelectFileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recycler_file_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileInfo fileInfo = mListFileInfo.get(position);
        holder.tvFileName.setText(fileInfo.getFileName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Return to MainActivity with leaving sending file id.
            }
        });

        StringBuffer fileValue = FileManager.readFileValue(
                Environment.getExternalStorageDirectory()
                        + File.separator + mContext.getString(R.string.app_name),
                fileInfo.getFileName());

        Log.d(SelectFileAdapter.class.getSimpleName(), "fileValue: " + fileValue);

        if (fileValue != null) {
            Parser parser = Parser.builder().build();
            Node document = parser.parse(fileValue.toString());
            HtmlRenderer renderer = HtmlRenderer.builder().build();

            Log.d(SelectFileAdapter.class.getSimpleName(), "I am here 1");

            holder.wvPreview.loadData(renderer.render(document),
                    "text/html; charset=utf-8", "UTF-8");

            Log.d(SelectFileAdapter.class.getSimpleName(), "I am here 2");
        }
    }

    @Override
    public int getItemCount() {
        return mListFileInfo.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
