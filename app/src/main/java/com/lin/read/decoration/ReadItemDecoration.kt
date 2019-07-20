package com.lin.read.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.lin.read.utils.UIUtils

class ReadItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val paint: Paint
    private var dividerHeight: Int = 0

    init {
        paint = Paint().apply {
            isAntiAlias = true
            strokeWidth = 2f
            color = Color.parseColor("#CFCFCF")
        }
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        parent?.takeIf { it.getChildAdapterPosition(view) != 0 }?.let {
            outRect?.top = 1
            dividerHeight = 3
        }
        parent?.takeIf { it.getChildAdapterPosition(view) == it.childCount - 1 }?.let {

        }
    }

    override fun onDraw(c: Canvas?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        for (index in 0 until (parent?.childCount ?: 0)) {
            if (index == 0) continue
            parent?.getChildAt(index)?.run {
                c?.drawRect(paddingLeft.toFloat() + UIUtils.dip2px(context, 20f), (top - dividerHeight).toFloat(), (width - paddingRight) - UIUtils.dip2px(context, 20f).toFloat(), top.toFloat(), paint)
            }
            parent?.takeIf { index == it.childCount - 1 }?.getChildAt(index)?.run {
                c?.drawRect(paddingLeft.toFloat() + UIUtils.dip2px(context, 20f), paddingBottom.toFloat(), (width - paddingRight) - UIUtils.dip2px(context, 20f).toFloat(), (paddingBottom + dividerHeight).toFloat(), paint)
            }
        }
    }
}