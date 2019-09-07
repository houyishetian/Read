package com.lin.read.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lin.read.R;
import com.lin.read.utils.UIUtils;

/**
 * Created by lisonglin on 2017/10/12.
 */

public class ScanTypeItemDecoration extends RecyclerView.ItemDecoration {

    int spaceVertical;
    public ScanTypeItemDecoration(Context context, int spaceVertical){
        this.spaceVertical= UIUtils.dip2px(context,spaceVertical);
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//        int columns=((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
//        int position=parent.getChildLayoutPosition(view);
//        android.util.Log.e("Test"," columns="+columns+",position="+position);
//        if(position!=0){
//            outRect.top=spaceVertical;
//        }
//        outRect.top=spaceVertical;
//        outRect.bottom=spaceVertical;

        /**
         * 跳过索引为0的item
         */
        int position=parent.getChildLayoutPosition(view);
        int column=((GridLayoutManager)parent.getLayoutManager()).getSpanCount();
        if (position<column) {
            return;
        }
//        /**
//         * 设置item的相对偏移量
//         */
        outRect.top = spaceVertical;
    }
}

