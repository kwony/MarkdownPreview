package com.kwony.mdpreview.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kwony.mdpreview.FileInfo;
import com.kwony.mdpreview.R;

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

        public ViewHolder(View view) {
            super(view);

            tvFileName = (TextView) view.findViewById(R.id.tvFileName);
            cardView = (CardView) view.findViewById(R.id.cardView);
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
