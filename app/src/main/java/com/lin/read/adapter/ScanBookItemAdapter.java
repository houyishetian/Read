package com.lin.read.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2017/10/17.
 */

public class ScanBookItemAdapter extends RecyclerView.Adapter<ScanBookItemAdapter.ViewHolder> {
    private ArrayList<BookInfo> allBookData;
    private Context context;

    public ScanBookItemAdapter(Context context,ArrayList<BookInfo> allBookData) {
        this.allBookData = allBookData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_book, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final BookInfo bookInfo =allBookData.get(position);
        holder.bookName.setText(bookInfo.getBookName());
        holder.authorName.setText(bookInfo.getAuthorName());
        holder.webType.setText("起点");
        holder.lastUpdate.setText(StringUtils.formatLastUpdate(bookInfo.getLastUpdate()));
        holder.wordsNum.setText(bookInfo.getWordsNum()+"万字");
        holder.recommend.setText(bookInfo.getRecommend()+"万推荐");
        holder.vipClick.setText(bookInfo.getVipClick()+"万点击");
        holder.score.setText(bookInfo.getScore());
        holder.scoreNum.setText(bookInfo.getScoreNum()+"人");
    }

    @Override
    public int getItemCount() {
        return allBookData==null?0:allBookData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView bookName;
        private TextView authorName;
        private TextView webType;
        private TextView lastUpdate;
        private TextView wordsNum;
        private TextView recommend;
        private TextView vipClick;
        private TextView score;
        private TextView scoreNum;
        public ViewHolder(View itemView) {
            super(itemView);
            bookName= (TextView) itemView.findViewById(R.id.book_item_bookname);
            authorName= (TextView) itemView.findViewById(R.id.book_item_authorname);
            webType= (TextView) itemView.findViewById(R.id.book_item_web);
            lastUpdate= (TextView) itemView.findViewById(R.id.book_item_lastupdate);
            wordsNum= (TextView) itemView.findViewById(R.id.book_item_wordsnum);
            recommend= (TextView) itemView.findViewById(R.id.book_item_recommend);
            vipClick= (TextView) itemView.findViewById(R.id.book_item_vipclick);
            score= (TextView) itemView.findViewById(R.id.book_item_score);
            scoreNum= (TextView) itemView.findViewById(R.id.book_item_scorenum);
        }
    }
}
