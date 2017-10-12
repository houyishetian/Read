package com.lin.read;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by lisonglin on 2017/10/12.
 */

public class ScanTypeItemDecoration extends RecyclerView.ItemDecoration {

    int spaceVertical;
    String tag;
    public ScanTypeItemDecoration(String tag,int spaceVertical){
        this.spaceVertical=spaceVertical;
        this.tag=tag;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int columns=((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
//        int position=parent.getChildLayoutPosition(view);
//        android.util.Log.e("Test",tag+" columns="+columns+",position="+position);
//        if(position!=0){
//            outRect.top=spaceVertical;
//        }
        outRect.top=spaceVertical;
    }
}

