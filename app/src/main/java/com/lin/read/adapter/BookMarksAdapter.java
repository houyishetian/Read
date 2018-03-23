package com.lin.read.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.activity.ReadBookActivity;
import com.lin.read.bookmark.BookMarkBean;
import com.lin.read.filter.BookInfo;
import com.lin.read.utils.Constants;
import com.lin.read.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/19.
 */

public class BookMarksAdapter extends BaseAdapter {
    private List<BookMarkBean> allInfo;
    private Context context;
    private Fragment fragment;

    public BookMarksAdapter(Context context, Fragment fragment, List<BookMarkBean> allInfo) {
        this.allInfo = allInfo;
        this.context = context;
        this.fragment=fragment;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_book_mark, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final BookMarkBean markBean = allInfo.get(position);
        final BookInfo bookInfo = markBean.getBookInfo();
        holder.bookName.setText(bookInfo.getBookName());
        holder.authorName.setText(bookInfo.getAuthorName());
        holder.lastReadChapter.setText(markBean.getLastReadChapter());
        holder.bookType.setText(bookInfo.getBookType());
        holder.lastReadTime.setText(DateUtils.formatTime(markBean.getLastReadTime()));
        holder.webName.setText(bookInfo.getWebName());
        if(markBean.isShowCheckBox()){
            holder.markReadingLayout.setVisibility(View.GONE);
            holder.markSelectCb.setVisibility(View.VISIBLE);
            holder.markSelectCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.e("Test","check changed:"+isChecked);
                    markBean.setChecked(isChecked);
                    if(onCheckBoxClickListener!=null){
                        onCheckBoxClickListener.onCheckBoxClick();
                    }
                }
            });
            holder.markSelectCb.setChecked(markBean.isChecked());
        }else{
            holder.markReadingLayout.setVisibility(View.VISIBLE);
            holder.markSelectCb.setVisibility(View.GONE);
        }
        holder.continueReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadBookActivity.class);
                intent.putExtra(Constants.KEY_SKIP_TO_READ, bookInfo);
                fragment.startActivityForResult(intent, Constants.READ_REQUEST_CODE);
            }
        });
        return convertView;
    }

    public interface OnCheckBoxClickListener{
        void onCheckBoxClick();
    }

    private OnCheckBoxClickListener onCheckBoxClickListener;

    public void setOnCheckBoxClickListener(OnCheckBoxClickListener onCheckBoxClickListener) {
        this.onCheckBoxClickListener = onCheckBoxClickListener;
    }

    public void selectAll(boolean isSelectAll){
        if(allInfo!=null){
            for(BookMarkBean item:allInfo){
                item.setChecked(isSelectAll);
                item.setShowCheckBox(true);
            }
            notifyDataSetChanged();
            if(onCheckBoxClickListener!=null){
                onCheckBoxClickListener.onCheckBoxClick();
            }
        }
    }

    public List<BookMarkBean> getAllSelectMarks(){
        if(allInfo==null||allInfo.size()==0){
            return null;
        }
        List<BookMarkBean> result=new ArrayList<>();
        for(BookMarkBean item:allInfo){
            if(item.isChecked()){
                result.add(item);
            }
        }
        return result;
    }

    public void cancelSelect(){
        if(allInfo!=null){
            for(BookMarkBean item:allInfo){
                item.setChecked(false);
                item.setShowCheckBox(false);
            }
            notifyDataSetChanged();
        }
    }

    class ViewHolder {
        private TextView bookName;
        private TextView authorName;
        private TextView lastReadChapter;
        private TextView bookType;
        private TextView lastReadTime;
        private TextView webName;
        private TextView continueReading;
        private View markReadingLayout;
        private CheckBox markSelectCb;

        public ViewHolder(View itemView) {
            bookName = (TextView) itemView.findViewById(R.id.mark_item_bookname);
            authorName = (TextView) itemView.findViewById(R.id.mark_item_authorname);
            lastReadChapter = (TextView) itemView.findViewById(R.id.mark_item_lastchapter);
            bookType = (TextView) itemView.findViewById(R.id.mark_item_booktype);
            lastReadTime = (TextView) itemView.findViewById(R.id.mark_item_lastread);
            webName = (TextView) itemView.findViewById(R.id.mark_item_web_name);
            continueReading = (TextView) itemView.findViewById(R.id.mark_item_continue_read);
            markReadingLayout=itemView.findViewById(R.id.mark_reading_layout);
            markSelectCb= (CheckBox) itemView.findViewById(R.id.mark_select_cb);
        }
    }
}
