package com.lin.read.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lin.read.R;
import com.lin.read.filter.ScanBookBean;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.utils.Constants;

import java.util.ArrayList;

/**
 * Created by lisonglin on 2017/10/17.
 */

public class ScanBookItemAdapter extends RecyclerView.Adapter<ScanBookItemAdapter.ViewHolder> {
    private ArrayList<ScanBookBean> allBookData;
    private Context context;

    public ScanBookItemAdapter(Context context,ArrayList<ScanBookBean> allBookData) {
        this.allBookData = allBookData;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_book, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ScanBookBean bookInfo =allBookData.get(position);
        holder.bookName.setText(bookInfo.getBookName());
        holder.authorName.setText(bookInfo.getAuthorName());
        holder.webType.setText(bookInfo.getScanWebName());
        holder.lastUpdate.setText(StringUtils.formatLastUpdate(bookInfo.getLastUpdate()));
        if(StringUtils.isEmpty(bookInfo.getWordsNum())){
            holder.wordsNum.setVisibility(View.INVISIBLE);
        }else{
            holder.wordsNum.setVisibility(View.VISIBLE);
            holder.wordsNum.setText(bookInfo.getWordsNum()+"万字");
        }
        holder.recommend.setVisibility(View.INVISIBLE);
        holder.vipClick.setVisibility(View.INVISIBLE);
        if (View.VISIBLE != holder.wordsNum.getVisibility() && View.VISIBLE != holder.recommend.getVisibility() && View.VISIBLE != holder.vipClick.getVisibility()) {
            holder.viewBookItemInfos.setVisibility(View.GONE);
        } else {
            holder.viewBookItemInfos.setVisibility(View.VISIBLE);
        }
        if(StringUtils.isEmpty(bookInfo.getScanWebName())){
            holder.viewScoreInfos.setVisibility(View.INVISIBLE);
            holder.viewCommentInfos.setVisibility(View.INVISIBLE);
        }else{
            switch (bookInfo.getScanWebName()) {
                case Constants.WEB_QIDIAN:
                case Constants.WEB_QIDIAN_FINISH:
                case Constants.WEB_YOU_SHU:
                    holder.viewScoreInfos.setVisibility(View.VISIBLE);
                    holder.viewCommentInfos.setVisibility(View.INVISIBLE);
                    holder.score.setText(bookInfo.getScore());
                    holder.scoreNum.setText(bookInfo.getScoreNum()+"人");
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
        void onBookItemClick(ScanBookBean scanBookBean);
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

        private ViewHolder(View itemView) {
            super(itemView);
            layouView=itemView;
            bookName=  itemView.findViewById(R.id.book_item_bookname);
            authorName=  itemView.findViewById(R.id.book_item_authorname);
            webType=  itemView.findViewById(R.id.book_item_web);
            lastUpdate=  itemView.findViewById(R.id.book_item_lastupdate);
            wordsNum=  itemView.findViewById(R.id.book_item_wordsnum);
            recommend=  itemView.findViewById(R.id.book_item_recommend);
            vipClick=  itemView.findViewById(R.id.book_item_vipclick);
            score=  itemView.findViewById(R.id.book_item_score);
            scoreNum=  itemView.findViewById(R.id.book_item_scorenum);
            viewBookItemInfos = itemView.findViewById(R.id.book_item_infos);
            viewScoreInfos = itemView.findViewById(R.id.book_item_score_info);
            viewCommentInfos = itemView.findViewById(R.id.book_item_comment_info);
            commentNum =  itemView.findViewById(R.id.book_item_comment);
            raiseNum =  itemView.findViewById(R.id.book_item_raise);
        }

        public View getLayouView(){
            return layouView;
        }
    }
}
