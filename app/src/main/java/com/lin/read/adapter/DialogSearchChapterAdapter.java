package com.lin.read.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.search.BookChapterInfo;
import com.lin.read.utils.NoDoubleClickListener;

import java.util.List;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class DialogSearchChapterAdapter extends BaseAdapter {
    private List<BookChapterInfo> allInfo;
    private Context context;

    public DialogSearchChapterAdapter(Context context, List<BookChapterInfo> allInfo) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search_chapter_dialog, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BookChapterInfo info = allInfo.get(position);
        holder.name.setText(info.getChapterName());
        if(position==allInfo.size()-1){
            holder.divider.setVisibility(View.GONE);
        }else{
            holder.divider.setVisibility(View.VISIBLE);
        }
        holder.name.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (onItemChapterClickListener != null) {
                    onItemChapterClickListener.onItemClick(info);
                }
            }
        });
        return convertView;
    }

    public interface OnItemChapterClickListener {
        void onItemClick(BookChapterInfo info);
    }

    private OnItemChapterClickListener onItemChapterClickListener;

    public void setOnItemChapterClickListener(OnItemChapterClickListener onItemChapterClickListener) {
        this.onItemChapterClickListener = onItemChapterClickListener;
    }

    class ViewHolder {
        private TextView name;
        private View divider;
        public ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.dialog_chapter_name);
            divider=itemView.findViewById(R.id.dialog_chapter_divider);
        }
    }
}
