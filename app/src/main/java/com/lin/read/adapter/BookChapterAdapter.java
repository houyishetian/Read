package com.lin.read.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.filter.search.GetChapterInfoTask;
import com.lin.read.utils.NoDoubleClickListener;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class BookChapterAdapter extends BaseAdapter {
    private ArrayList<BookChapterInfo> allInfo;
    private Context context;

    public BookChapterAdapter(Context context, ArrayList<BookChapterInfo> allInfo) {
        this.allInfo = allInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.allInfo == null ? 0 : allInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_book_chapter, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BookChapterInfo info = allInfo.get(position);
        holder.chapterName.setText(info.getChapterName());
        if (position == allInfo.size() - 1) {
            holder.chapterDivider.setVisibility(View.GONE);
        } else {
            holder.chapterDivider.setVisibility(View.VISIBLE);
        }
        if(info.isCurrentReading()){
            holder.chapterName.setTextColor(context.getResources().getColor(android.R.color.holo_blue_light));
        }else{
            holder.chapterName.setTextColor(context.getResources().getColor(android.R.color.black));
        }
        holder.chapterName.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (onChapterClickListener != null) {
                    onChapterClickListener.onChapterClick(info);
                }
            }
        });
        return convertView;
    }

    public interface OnChapterClickListener {
        void onChapterClick(BookChapterInfo bookChapterInfo);
    }

    private OnChapterClickListener onChapterClickListener;

    public void setOnChapterClickListener(OnChapterClickListener onChapterClickListener) {
        this.onChapterClickListener = onChapterClickListener;
    }

    class ViewHolder {
        private TextView chapterName;
        private View chapterDivider;

        public ViewHolder(View itemView) {
            chapterName = (TextView) itemView.findViewById(R.id.item_chapter_name);
            chapterDivider = itemView.findViewById(R.id.item_chapter_divider);
        }
    }

    public void notifyData(int currentPage, BookChapterInfo currentChapterInfo){
        setCurrentReading(currentPage, currentChapterInfo);
        notifyDataSetChanged();
    }

    private void setCurrentReading(int currentPage, BookChapterInfo currentChapterInfo) {
        if (currentChapterInfo == null || currentChapterInfo.getIndex() < 0 || currentChapterInfo.getPage() < 0 || currentPage != currentChapterInfo.getPage()) {
            for (int i = 0; i < allInfo.size(); i++) {
                allInfo.get(i).setCurrentReading(false);
            }
            return;
        }
        int readingPositon = currentChapterInfo.getIndex() - currentChapterInfo.getPage() * GetChapterInfoTask.eachLen;
        for (int i = 0; i < allInfo.size(); i++) {
            if (i == readingPositon) {
                allInfo.get(i).setCurrentReading(true);
            } else {
                allInfo.get(i).setCurrentReading(false);
            }
        }
    }

    public int getCurrentPosition(int currentPage, BookChapterInfo currentChapterInfo) {
        if (currentChapterInfo == null || currentChapterInfo.getIndex() < 0 || currentChapterInfo.getPage() < 0 || currentPage != currentChapterInfo.getPage()) {
            return 0;
        }
        return currentChapterInfo.getIndex() - currentChapterInfo.getPage() * GetChapterInfoTask.eachLen;
    }
}
