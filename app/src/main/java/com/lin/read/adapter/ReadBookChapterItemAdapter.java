package com.lin.read.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.utils.NoDoubleClickListener;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2017/10/17.
 */

public class ReadBookChapterItemAdapter extends RecyclerView.Adapter<ReadBookChapterItemAdapter.ViewHolder> {
    private ArrayList<BookChapterInfo> allInfo;
    private Context context;

    public ReadBookChapterItemAdapter(Context context, ArrayList<BookChapterInfo> allInfo) {
        this.allInfo = allInfo;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_read_chapter, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BookChapterInfo info = allInfo.get(position);
        holder.chapterName.setText(info.getChapterName());

        holder.chapterName.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if(onChapterClickListener!=null){
                    onChapterClickListener.onChapterClick(info);
                }
            }
        });
    }

    public interface OnChapterClickListener{
        void onChapterClick(BookChapterInfo bookChapterInfo);
    }

    private OnChapterClickListener onChapterClickListener;

    public void setOnChapterClickListener(OnChapterClickListener onChapterClickListener) {
        this.onChapterClickListener = onChapterClickListener;
    }

    @Override
    public int getItemCount() {
        return allInfo == null ? 0 : allInfo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView chapterName;

        public ViewHolder(View itemView) {
            super(itemView);
            chapterName = (TextView) itemView.findViewById(R.id.item_chapter_name);
        }
    }
}
