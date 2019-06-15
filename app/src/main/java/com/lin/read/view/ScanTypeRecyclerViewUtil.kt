package com.lin.read.view

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.lin.read.R
import com.lin.read.adapter.ScanTypeAdapter
import com.lin.read.decoration.ScanTypeItemDecoration
import com.lin.read.filter.scan.ScanTypeInfo
import com.lin.read.filter.scan.qidian.QiDianConstants
import com.lin.read.filter.scan.youshu.YouShuConstants
import com.lin.read.utils.Constants
import com.lin.read.utils.UIUtils

class ScanTypeRecyclerViewUtil private constructor(var context: Context, var parentLayout: LinearLayout) {
    lateinit var qiDianScanTypeViews: HashMap<String,ScanTypeView>
    lateinit var youShuScanTypeViews: HashMap<String,ScanTypeView>

    companion object {
        @Volatile
        private var instance: ScanTypeRecyclerViewUtil? = null

        fun getInstance(context: Context, parentLayout: LinearLayout): ScanTypeRecyclerViewUtil {
            if (instance == null)
                synchronized(ScanTypeRecyclerViewUtil::class) {
                    if (instance == null) instance = ScanTypeRecyclerViewUtil(context, parentLayout)
                }
            return instance!!
        }
    }

    init {
        initQiDianView()
        initYouShuView()
    }

    private fun initQiDianView() {
        qiDianScanTypeViews = hashMapOf()
        if (!QiDianConstants.filterMap.isEmpty()) {
            for ((key, item) in QiDianConstants.filterMap) {
                val scanTypeView = ScanTypeView(context, item)
                scanTypeView.promptTv.setText(key)
                scanTypeView.parent.tag = Constants.WEB_QIDIAN
                parentLayout.addView(scanTypeView.parent)
                scanTypeView.parent.visibility = View.GONE
                qiDianScanTypeViews.put(key,scanTypeView)
            }
        }
//        var conditionsView = LayoutInflater.from(context).inflate(R.layout.sub_layout_filter_qidian, null, false)
//        conditionsView.tag = Constants.WEB_QIDIAN
//        parentLayout.addView(conditionsView)
    }

    private fun initYouShuView() {
        youShuScanTypeViews = hashMapOf()
        if (!YouShuConstants.filterMap.isEmpty()) {
            for ((key, item) in YouShuConstants.filterMap) {
                val scanTypeView: ScanTypeView
                when (key) {
                    YouShuConstants.YS_FILTER_CATE, YouShuConstants.YS_FILTER_WORDS -> scanTypeView = ScanTypeView(context, item, spanCount = 3, layoutId = R.layout.item_scan_type_4_chars)
                    else -> scanTypeView = ScanTypeView(context, item)
                }
                scanTypeView.promptTv.setText(key)
                scanTypeView.parent.setTag(Constants.WEB_YOU_SHU)
                parentLayout.addView(scanTypeView.parent)
                scanTypeView.parent.visibility = View.GONE
                youShuScanTypeViews.put(key,scanTypeView)
            }
        }
    }

    fun showWebLayout(webType: String) {
        for (index in 0 until parentLayout.childCount) {
            if (webType.equals(parentLayout.getChildAt(index).tag)) {
                parentLayout.getChildAt(index).visibility = View.VISIBLE
            } else {
                parentLayout.getChildAt(index).visibility = View.GONE
            }
        }
    }

    class ScanTypeView @JvmOverloads constructor(var context: Context, var data: List<ScanTypeInfo>,var spanCount: Int = 4, var layoutId: Int = R.layout.item_scan_type) {
        lateinit var promptTv: TextView
        lateinit var recyclerView: RecyclerView
        lateinit var parent: LinearLayout
        lateinit var adapter: ScanTypeAdapter

        init {
            initView(context)
        }

        private fun initView(context: Context) {
            parent = LinearLayout(context)
            var parentParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            parentParams.layoutDirection = LinearLayout.HORIZONTAL
            parentParams.setMargins(UIUtils.dip2px(context,10f), UIUtils.dip2px(context,15f), 0, 0)
            parent.layoutParams = parentParams

            promptTv = TextView(context)
            var promptParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(UIUtils.dip2px(context,30f), LinearLayout.LayoutParams.MATCH_PARENT)
            promptParams.setMargins(0, UIUtils.dip2px(context,5f), 0, 0)
            promptTv.layoutParams = promptParams
            parent.addView(promptTv)

            recyclerView = RecyclerView(context)
            recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            recyclerView.layoutManager = GridLayoutManager(context, spanCount)
            recyclerView.addItemDecoration(ScanTypeItemDecoration(context, 15));
            parent.addView(recyclerView)

            adapter = ScanTypeAdapter(context, data, layoutId)
            recyclerView.adapter = adapter
        }
    }
}