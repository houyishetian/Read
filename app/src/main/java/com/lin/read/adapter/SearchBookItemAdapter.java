package com.lin.read.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lin.read.R;
import com.lin.read.filter.BookInfo;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2017/10/17.
 */

public class SearchBookItemAdapter extends RecyclerView.Adapter<SearchBookItemAdapter.ViewHolder> {
    private ArrayList<BookInfo> allBookData;
    private Context context;

    public SearchBookItemAdapter(Context context, ArrayList<BookInfo> allBookData) {
        this.allBookData = allBookData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_book, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BookInfo bookInfo=allBookData.get(position);
        holder.bookName.setText(bookInfo.getBookName());
        holder.authorName.setText(bookInfo.getAuthorName());
        holder.bookType.setText(bookInfo.getBookType());
        holder.lastUpdate.setText(bookInfo.getLastUpdate());
        holder.lastContent.setText(bookInfo.getLastChapter());
        holder.download.setEnabled(!TextUtils.isEmpty(bookInfo.getDownloadLink()));

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(bookInfo.getDownloadLink())){
                    Toast.makeText(context,"未找到下载资源",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(bookInfo.getDownloadLink());
                    intent.setData(content_url);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allBookData==null?0:allBookData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView bookName;
        private TextView authorName;
        private TextView bookType;
        private TextView lastUpdate;
        private TextView lastContent;
        private Button download;
        public ViewHolder(View itemView) {
            super(itemView);
            bookName= (TextView) itemView.findViewById(R.id.book_item_bookname);
            authorName= (TextView) itemView.findViewById(R.id.book_item_authorname);
            bookType= (TextView) itemView.findViewById(R.id.book_item_booktype);
            lastUpdate= (TextView) itemView.findViewById(R.id.book_item_lastupdate);
            lastContent= (TextView) itemView.findViewById(R.id.book_item_lastcontent);
            download= (Button) itemView.findViewById(R.id.book_item_download);
        }
    }
}
