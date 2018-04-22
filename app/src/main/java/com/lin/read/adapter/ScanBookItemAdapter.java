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
import com.lin.read.utils.Constants;

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
        holder.webType.setText(bookInfo.getWebName());
        holder.lastUpdate.setText(StringUtils.formatLastUpdate(bookInfo.getLastUpdate()));
        if(StringUtils.isEmpty(bookInfo.getWordsNum())){
            holder.wordsNum.setVisibility(View.INVISIBLE);
        }else{
            holder.wordsNum.setVisibility(View.VISIBLE);
            holder.wordsNum.setText(bookInfo.getWordsNum()+"万字");
        }
        if(StringUtils.isEmpty(bookInfo.getRecommend())){
            holder.recommend.setVisibility(View.INVISIBLE);
        }else{
            holder.recommend.setVisibility(View.VISIBLE);
            holder.recommend.setText(bookInfo.getRecommend()+"万推荐");
        }
        if(StringUtils.isEmpty(bookInfo.getClick())){
            holder.vipClick.setVisibility(View.INVISIBLE);
        }else{
            holder.vipClick.setVisibility(View.VISIBLE);
            holder.vipClick.setText(bookInfo.getClick()+"万点击");
        }
        if (View.VISIBLE != holder.wordsNum.getVisibility() && View.VISIBLE != holder.recommend.getVisibility() && View.VISIBLE != holder.vipClick.getVisibility()) {
            holder.viewBookItemInfos.setVisibility(View.GONE);
        } else {
            holder.viewBookItemInfos.setVisibility(View.VISIBLE);
        }
        if(StringUtils.isEmpty(bookInfo.getWebName())){
            holder.viewScoreInfos.setVisibility(View.INVISIBLE);
            holder.viewCommentInfos.setVisibility(View.INVISIBLE);
        }else{
            switch (bookInfo.getWebName()) {
                case Constants.WEB_QIDIAN:
                    holder.viewScoreInfos.setVisibility(View.VISIBLE);
                    holder.viewCommentInfos.setVisibility(View.INVISIBLE);
                    holder.score.setText(bookInfo.getScore());
                    holder.scoreNum.setText(bookInfo.getScoreNum()+"人");
                    break;
                case Constants.WEB_ZONGHENG:
                    holder.viewScoreInfos.setVisibility(View.INVISIBLE);
                    holder.viewCommentInfos.setVisibility(View.VISIBLE);
                    holder.raiseNum.setText(bookInfo.getRaiseNum());
                    holder.commentNum.setText(bookInfo.getCommentNum());
                    break;
                default:
                    holder.viewScoreInfos.setVisibility(View.INVISIBLE);
                    holder.viewCommentInfos.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        holder.layouView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onBookItemClickListener!=null){
                    onBookItemClickListener.onBookItemClick(bookInfo);
                }
            }
        });
    }

    public interface OnBookItemClickListener{
        void onBookItemClick(BookInfo bookInfo);
    }

    private OnBookItemClickListener onBookItemClickListener;

    public void setOnBookItemClickListener(OnBookItemClickListener onBookItemClickListener) {
        this.onBookItemClickListener = onBookItemClickListener;
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
        private View viewBookItemInfos;
        private TextView score;
        private TextView scoreNum;
        private View layouView;
        private View viewScoreInfos;
        private View viewCommentInfos;
        private TextView commentNum;
        private TextView raiseNum;

        public ViewHolder(View itemView) {
            super(itemView);
            layouView=itemView;
            bookName= (TextView) itemView.findViewById(R.id.book_item_bookname);
            authorName= (TextView) itemView.findViewById(R.id.book_item_authorname);
            webType= (TextView) itemView.findViewById(R.id.book_item_web);
            lastUpdate= (TextView) itemView.findViewById(R.id.book_item_lastupdate);
            wordsNum= (TextView) itemView.findViewById(R.id.book_item_wordsnum);
            recommend= (TextView) itemView.findViewById(R.id.book_item_recommend);
            vipClick= (TextView) itemView.findViewById(R.id.book_item_vipclick);
            score= (TextView) itemView.findViewById(R.id.book_item_score);
            scoreNum= (TextView) itemView.findViewById(R.id.book_item_scorenum);
            viewBookItemInfos = itemView.findViewById(R.id.book_item_infos);
            viewScoreInfos = itemView.findViewById(R.id.book_item_score_info);
            viewCommentInfos = itemView.findViewById(R.id.book_item_comment_info);
            commentNum = (TextView) itemView.findViewById(R.id.book_item_comment);
            raiseNum = (TextView) itemView.findViewById(R.id.book_item_raise);
        }

        public View getLayouView(){
            return layouView;
        }
    }
}
