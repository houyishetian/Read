package com.lin.read.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.lin.read.R
import com.lin.read.adapter.ScanTypeAdapter
import com.lin.read.decoration.ScanTypeItemDecoration
import com.lin.read.filter.scan.ScanInputInfo
import com.lin.read.filter.scan.ScanTypeInfo
import com.lin.read.utils.ScanInputTypeEnum
import com.lin.read.utils.UIUtils
import java.util.LinkedHashMap

class ScanTypeRecyclerViewUtil private constructor(var context: Context, var parentLayout: LinearLayout, val scanWebTypeList: List<ScanTypeInfo>, val allScanTypeData: java.util.HashMap<String, LinkedHashMap<String, List<ScanTypeInfo>>>, val allScanInputData: java.util.HashMap<String, List<ScanInputInfo>>) {
    var scanTypeViews: HashMap<String, HashMap<String, ScanTypeView>>
    var scanInputViews: HashMap<String, HashMap<String, ScanInputView>>

    companion object {
        @Volatile
        private var instance: ScanTypeRecyclerViewUtil? = null

        fun getInstance(context: Context, parentLayout: LinearLayout, scanWebTypeList: List<ScanTypeInfo>, allScanTypeData: java.util.HashMap<String, LinkedHashMap<String, List<ScanTypeInfo>>>, allScanInputData: java.util.HashMap<String, List<ScanInputInfo>>): ScanTypeRecyclerViewUtil {
            if (instance == null)
                synchronized(ScanTypeRecyclerViewUtil::class) {
                    if (instance == null) instance = ScanTypeRecyclerViewUtil(context, parentLayout, scanWebTypeList, allScanTypeData, allScanInputData)
                }
            return instance!!
        }
    }

    init {
        scanTypeViews = hashMapOf();
        scanInputViews = hashMapOf();

        for(item in scanWebTypeList){
            initTypeViews(item.text)
            initInputViews(item.text)
        }
    }

    private fun initTypeViews(webType: String){
        if(!containsWeb(webType) || !allScanTypeData.containsKey(webType)){
            return;
        }
        if (!allScanTypeData.get(webType)!!.isEmpty()) {
            val scanTypeViews: HashMap<String, ScanTypeView> = hashMapOf()
            for ((key, item) in allScanTypeData.get(webType)!!) {
                val scanTypeView = ScanTypeView(context, item)
                scanTypeView.promptTv.setText(key)
                scanTypeView.parent.tag = webType
                parentLayout.addView(scanTypeView.parent)
                scanTypeView.parent.visibility = View.GONE
                scanTypeViews.put(key, scanTypeView)
            }
            this.scanTypeViews.put(webType,scanTypeViews)
        }
    }

    private fun containsWeb(webType: String): Boolean {
        for (item in scanWebTypeList) {
            if (item.text.equals(webType)) return true;
        }
        return false;
    }

    private fun initInputViews(webType:String){
        if(!containsWeb(webType) || !allScanInputData.containsKey(webType)){
            return;
        }
        val scanInputViews: HashMap<String, ScanInputView> = hashMapOf()

        val parent = LinearLayout(context)
        val parentParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        parent.orientation = LinearLayout.HORIZONTAL
        parentParams.setMargins(UIUtils.dip2px(context, 10f), UIUtils.dip2px(context, 15f), 0, 0)
        parent.tag = webType
        parent.layoutParams = parentParams
        parent.visibility = View.GONE
        parentLayout.addView(parent)

        val promptTv = TextView(context)
        val promptParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(UIUtils.dip2px(context, 30f), LinearLayout.LayoutParams.MATCH_PARENT)
        promptParams.setMargins(0, UIUtils.dip2px(context, 5f), 0, 0)
        promptTv.layoutParams = promptParams
        promptTv.text = "筛选"
        parent.addView(promptTv)

        val inputParent = LinearLayout(context)
        val inputParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        inputParent.orientation = LinearLayout.VERTICAL
        inputParams.setMargins(UIUtils.dip2px(context, 10f), 0, 0, 0)
        inputParent.layoutParams = inputParams
        parent.addView(inputParent)

        var subParent: LinearLayout? = null
        for ((index, item) in allScanInputData.get(webType)!!.withIndex()) {
            if (index % 2 == 0) {
                subParent = LinearLayout(context)
                val subParentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(context, 25f))
                subParent.layoutParams = subParentParams
                if (index > 0) subParentParams.setMargins(0, UIUtils.dip2px(context, 10f), 0, 0)
                subParent.orientation = LinearLayout.HORIZONTAL
                inputParent.addView(subParent)
            }
            val scanInputView = ScanInputView(context, item)
            scanInputViews.put(item.tag, scanInputView)
            subParent?.addView(scanInputView.inputLayout)
        }
        this.scanInputViews.put(webType,scanInputViews)
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

    class ScanTypeView (var context: Context, var data: List<ScanTypeInfo>) {
        lateinit var promptTv: TextView
        lateinit var recyclerView: RecyclerView
        lateinit var parent: LinearLayout
        lateinit var adapter: ScanTypeAdapter

        init {
            initView(context)
        }

        private fun initView(context: Context) {
            parent = LinearLayout(context)
            val parentParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            parent.orientation = LinearLayout.HORIZONTAL
            parentParams.setMargins(UIUtils.dip2px(context, 10f), UIUtils.dip2px(context, 15f), 0, 0)
            parent.layoutParams = parentParams

            promptTv = TextView(context)
            val promptParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(UIUtils.dip2px(context, 30f), LinearLayout.LayoutParams.MATCH_PARENT)
            promptParams.setMargins(0, UIUtils.dip2px(context, 5f), 0, 0)
            promptTv.layoutParams = promptParams
            parent.addView(promptTv)

            val use4Words = data[0].use4Words
            var spanCount = 4
            if (use4Words) spanCount = 3

            recyclerView = RecyclerView(context)
            recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            recyclerView.layoutManager = GridLayoutManager(context, spanCount)
            recyclerView.addItemDecoration(ScanTypeItemDecoration(context, 15));
            parent.addView(recyclerView)

            adapter = ScanTypeAdapter(context, data, use4Words)
            recyclerView.adapter = adapter
        }
    }

    class ScanInputView(val context: Context, val scanInputInfo: ScanInputInfo) {
        lateinit var inputLayout: LinearLayout
        lateinit var inputEt: EditText
        lateinit var unitTv: TextView

        init {
            initView()
        }

        private fun initView() {
            inputLayout = LinearLayout(context)
            val inputLayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            inputLayoutParams.weight = 1f
            inputLayout.orientation = LinearLayout.HORIZONTAL
            inputLayout.layoutParams = inputLayoutParams

            inputEt = EditText(context)
            val inputEtParams = LinearLayout.LayoutParams(UIUtils.dip2px(context, 60f), LinearLayout.LayoutParams.MATCH_PARENT)
            inputEt.layoutParams = inputEtParams
            inputEt.setBackgroundResource(R.drawable.shape_scan_type_item_unselected)
            if (scanInputInfo.hint != null) inputEt.hint = scanInputInfo.hint
            when (scanInputInfo.inputType) {
                ScanInputTypeEnum.INT -> inputEt.inputType = EditorInfo.TYPE_CLASS_NUMBER
                ScanInputTypeEnum.FLOAT -> inputEt.inputType = EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
            }
            inputEt.setSingleLine()
            inputEt.setPadding(UIUtils.dip2px(context, 5f), 0, UIUtils.dip2px(context, 5f), 0)
            inputEt.textSize = 12f
            if (scanInputInfo.inputFilters != null) inputEt.filters = scanInputInfo.inputFilters
            inputLayout.addView(inputEt)

            unitTv = TextView(context)
            unitTv.text = scanInputInfo.unit
            unitTv.setTextColor(Color.BLACK)
            val unitParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            unitParams.setMargins(UIUtils.dip2px(context, 5f), 0, 0, 0)
            unitTv.layoutParams = unitParams
            inputLayout.addView(unitTv)
        }
    }
}