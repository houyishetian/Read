package com.lin.read.decoration

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.lin.read.utils.UIUtils

class ScanItemDecoration(private val ctx: Context, private val top: Int? = null, private val left: Int? = null,
                         private val bottom: Int? = null, private val right: Int? = null) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State?) {

        val spanCount = when (parent.layoutManager) {
            is GridLayoutManager -> (parent.layoutManager as GridLayoutManager).spanCount
            is LinearLayoutManager -> 1
            else -> throw Exception("unsupported layout manager")
        }
        val position = parent.getChildAdapterPosition(view)
        top.takeIf { position >= spanCount }?.let { outRect.top = UIUtils.dip2px(ctx, it.toFloat()) }
        left.takeIf { position % spanCount != 0 }?.let {
            outRect.left = UIUtils.dip2px(ctx, it.toFloat())
        }
//        bottom?.let { outRect.bottom = UIUtils.dip2px(ctx, it.toFloat()) }
//        right?.let { outRect.right = UIUtils.dip2px(ctx, it.toFloat()) }
    }
}