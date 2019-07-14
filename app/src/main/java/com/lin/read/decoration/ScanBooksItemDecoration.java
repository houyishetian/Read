package com.lin.read.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.lin.read.utils.UIUtils;

/**
 * Created by lisonglin on 2017/10/18.
 */

public class ScanBooksItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Paint paint;

    public ScanBooksItemDecoration(Context context) {
        this.mContext = context;
        paint=new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setColor(Color.parseColor("#CFCFCF"));
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizontalLine(c, parent, state);
    }

    //画横线, 这里的parent其实是显示在屏幕显示的这部分
    private void drawHorizontalLine(Canvas c, RecyclerView parent, RecyclerView.State state) {
//        int left = parent.getPaddingLeft();
//        int right = parent.getWidth() - parent.getPaddingRight();
        int left = UIUtils.dip2px(mContext,15f);
        int right = parent.getWidth() - UIUtils.dip2px(mContext,15f);

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);

            //获得child的布局信息
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;

            final int bottom = top +2;
            c.drawLine(left,top,right,bottom,paint);
            //Log.d("wnw", left + " " + top + " "+right+"   "+bottom+" "+i);
        }
    }

    //由于Divider也有长宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position=parent.getChildLayoutPosition(view);
        int count=parent.getAdapter().getItemCount();
        if(position!=count-1){
            //画横线，就是往下偏移一个分割线的高度
            outRect.set(0, 0, 0, 2);
        }
    }
}
