package com.lin.read.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.lin.read.R
import com.lin.read.adapter.ScanTypeAdapter
import com.lin.read.decoration.ScanTypeItemDecoration
import com.lin.read.filter.scan.*
import com.lin.read.utils.Constants
import com.lin.read.utils.ReflectUtil
import com.lin.read.utils.SingleInstanceHolderWith3Params
import com.lin.read.utils.UIUtils
import java.util.*
import android.text.InputType


class ScanTypeRecyclerViewUtil private constructor(private var context: Context, private var parentLayout: LinearLayout, private val readScanBean: ReadScanBean) {
    private var scanTypeViews: HashMap<String, HashMap<String, ScanTypeView>>
    private var scanInputViews: HashMap<String, ScanInputView>
    private var scanViewVisis:HashMap<String,MutableList<Int>>

    companion object : SingleInstanceHolderWith3Params<ScanTypeRecyclerViewUtil, Context, LinearLayout, ReadScanBean>(::ScanTypeRecyclerViewUtil)

    init {
        scanTypeViews = hashMapOf()
        scanViewVisis = hashMapOf()
        scanInputViews = hashMapOf()

        for (item in readScanBean.webs) {
            scanViewVisis.put(item.key, mutableListOf())
            initTypeViews(item.key)
            initInputViews(item.key)
            showFirstState(item.key)
        }
        saveScanViewVisis()
        setScanTypeClickListener()
    }

    private fun initTypeViews(webType: String) {
        val currentScanType = ReflectUtil.getProperty(readScanBean, webType, ReadScanDetailsInfo::class.java)?.scanTypes
        currentScanType?.takeIf { it.isNotEmpty() }?.run {
            val scanTypeViews: HashMap<String, ScanTypeView> = hashMapOf()
            this.forEach {
                val scanTypeView = ScanTypeView(context, it)
                scanTypeView.parent.tag = webType
                scanTypeView.parent.setTag(R.integer.read_scan_view_id, it.key)
                parentLayout.addView(scanTypeView.parent)
                scanTypeViews.put(it.key, scanTypeView)
            }
            this@ScanTypeRecyclerViewUtil.scanTypeViews.put(webType, scanTypeViews)
        }
    }

    private fun initInputViews(webType: String) {
        val currentScanInputBean = ReflectUtil.getProperty(readScanBean, webType, ReadScanDetailsInfo::class.java)
        currentScanInputBean?.inputTypes?.run {
            val scanInputView = ScanInputView(context, this)
            scanInputView.parent.tag = webType
            parentLayout.addView(scanInputView.parent)
            this@ScanTypeRecyclerViewUtil.scanInputViews.put(webType, scanInputView)
        }
    }

    private fun saveScanViewVisis(webType: String? = null) {
        webType?.run {
            //only save the web data
            scanViewVisis[this]?.clear()
            for (index in 0 until parentLayout.childCount) {
                if (parentLayout.getChildAt(index).tag == this) {
                    scanViewVisis[parentLayout.getChildAt(index).tag]?.add(parentLayout.getChildAt(index).visibility)
                }
            }
        } ?: let {
            //save all data
            //clear old data
            scanViewVisis.forEach { (_, item) ->
                item.clear()
            }
            for (index in 0 until parentLayout.childCount) {
                scanViewVisis[parentLayout.getChildAt(index).tag]?.add(parentLayout.getChildAt(index).visibility)
            }
        }
    }

    private fun showFirstState(webType: String){
        ReflectUtil.getProperty(readScanBean,webType,ReadScanDetailsInfo::class.java)?.scanTypes?.forEach {
            getDefaultChecked(it.data)?.hasNoSubItems?.forEach{
                scanTypeViews[webType]?.get(it)?.parent?.visibility = View.GONE
            }
        }
    }

    private fun getDefaultChecked(scanTypeData:List<ReadScanTypeData>):ReadScanTypeData?{
        return scanTypeData.firstOrNull { it.default }
    }

    private fun setScanTypeClickListener(){
        readScanBean.webs.forEach { webInfo ->
            ReflectUtil.getProperty(readScanBean, webInfo.key, ReadScanDetailsInfo::class.java)?.scanTypes?.forEach{scanTypeBean ->
                val lastHiddenItems: MutableList<String> = mutableListOf()
                scanTypeBean.data.forEach{scanTypeData ->
                    if(scanTypeData.hasNoSubItems != null && scanTypeData.hasNoSubItems.isNotEmpty()){
                        if(scanTypeData.default) lastHiddenItems.addAll(scanTypeData.hasNoSubItems)
                        scanTypeViews[webInfo.key]?.get(scanTypeBean.key)?.adapter?.onScanItemClickListenerMap?.put(scanTypeData.name) { _, clickText ->
                            Log.e("Test", "${webInfo.key} -> $clickText / ${scanTypeData.name} -> lastHidden $lastHiddenItems")
                            lastHiddenItems.forEach {
                                Log.e("Test", "show $it")
                                scanTypeViews[webInfo.key]?.get(it)?.parent?.visibility = View.VISIBLE
                            }
                            lastHiddenItems.clear()
                            if (clickText == scanTypeData.name) {
                                scanTypeData.hasNoSubItems.forEach {
                                    Log.e("Test", "hide $it")
                                    scanTypeViews[webInfo.key]?.get(it)?.parent?.visibility = View.GONE
                                    lastHiddenItems.add(it)
                                }
                            }
                            saveScanViewVisis(webInfo.key)
                        }
                    }else{
                        scanTypeViews[webInfo.key]?.get(scanTypeBean.key)?.adapter?.onScanItemClickListenerMap?.put(scanTypeData.name) { _, clickText ->
                            Log.e("Test", "${webInfo.key} -> $clickText / ${scanTypeData.name} -> lastHidden $lastHiddenItems")
                            lastHiddenItems.forEach {
                                Log.e("Test", "show $it")
                                scanTypeViews[webInfo.key]?.get(it)?.parent?.visibility = View.VISIBLE
                            }
                            lastHiddenItems.clear()
                            saveScanViewVisis(webInfo.key)
                        }
                    }
                }
            }
        }
    }

    fun showWebLayout(webType: String) {
        val tempVisibilities = mutableListOf<Int>()
        tempVisibilities.addAll(scanViewVisis[webType]!!)
        for (index in 0 until parentLayout.childCount) {
            if(parentLayout.getChildAt(index).tag == webType){
                parentLayout.getChildAt(index).visibility = tempVisibilities[0]
                tempVisibilities.removeAt(0)
            }else{
                parentLayout.getChildAt(index).visibility = View.GONE
            }
        }
    }

    private fun getSelectedFields(webType: String): List<ReadScanSelectedBean> {
        val result = mutableListOf<ReadScanSelectedBean>()
        for (index in 0 until parentLayout.childCount) {
            parentLayout.getChildAt(index).takeIf { it.tag == webType && it.visibility == View.VISIBLE }?.run {
                scanTypeViews[webType]?.get(getTag(R.integer.read_scan_view_id))?.let {
                    //only get the visible and selected items
                    it.adapter.getCheckedInfo()?.run {
                        val typeName = it.readScanTypeBean.typeName
                        val key = it.readScanTypeBean.key
                        val roleInUrl = it.readScanTypeBean.roleInUrl
                        val roleKeyInUrl = it.readScanTypeBean.roleKeyInUrl
                        result.add(ReadScanSelectedBean(typeName, key, name, roleInUrl, roleKeyInUrl, roleValueInUrl))
                    }
                }
            }
        }
        return result
    }

    private fun getInputtedFields(webType: String): List<ReadScanInputtedBean> {
        val result = mutableListOf<ReadScanInputtedBean>()
        scanInputViews[webType]?.inputViews?.forEach { (key, editText) ->
            scanInputViews[webType]?.readScanInputBean?.data?.firstOrNull { it.key == key }?.run {
                editText.text.toString().takeIf { it.isEmpty() }?.let { editText.setText(this.default?.toString()) }
                val value: Number = editText.text.toString().let {
                    when (this.inputType) {
                        Constants.INPUT_FLOAT -> it.toFloat()
                        Constants.INPUT_INT -> it.toInt()
                        else -> throw Exception("input type Error, this field must not be null!")
                    }
                }
                result.add(ReadScanInputtedBean(typeName, key, inputType, value, min, max))
            }
        }
        return result
    }

    private fun getWebName(webType: String): String? {
        return readScanBean.webs.firstOrNull { it.key == webType }?.name
    }

    fun getSearchInfo(webType: String): ScanInfo? {
        val typeFields = getSelectedFields(webType)
        var rolePathValue: String? = null
        val roleParamPairs: HashMap<String,String> = hashMapOf()
        typeFields.forEach {
            when (it.roleInUrl) {
                Constants.ROLE_PATH -> rolePathValue = it.roleValueInUrl
                Constants.ROLE_PARAM -> {
                    if (it.roleKeyInUrl != null && it.roleValueInUrl != null) {
                        roleParamPairs.put(it.roleKeyInUrl,it.roleValueInUrl)
                    }
                }
            }
        }
        return ScanInfo(getWebName(webType), ReflectUtil.getProperty(readScanBean, webType, ReadScanDetailsInfo::class.java)?.mainUrl, rolePathValue, roleParamPairs, getInputtedFields(webType))
    }

    fun getFocusOfEditText(webType: String): EditText? = scanInputViews[webType]?.inputViews?.values?.firstOrNull { it.hasFocus() }

    class ScanTypeView(context: Context, var readScanTypeBean: ReadScanTypeBean) {
        private lateinit var promptTv: TextView
        private lateinit var recyclerView: RecyclerView
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
            promptTv.text = readScanTypeBean.typeName
            parent.addView(promptTv)

            val use4Words = readScanTypeBean.use4Words
            var spanCount = 4
            if (use4Words) spanCount = 3

            recyclerView = RecyclerView(context)
            recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            recyclerView.layoutManager = GridLayoutManager(context, spanCount)
            recyclerView.addItemDecoration(ScanTypeItemDecoration(context, 15))
            parent.addView(recyclerView)

            adapter = ScanTypeAdapter(context, readScanTypeBean.data.map { it toPair false }, use4Words)
            recyclerView.adapter = adapter
        }
    }

    class ScanInputView(val context: Context, val readScanInputBean: ReadScanInputBean) {
        val inputViews: LinkedHashMap<String, EditText>
        lateinit var parent: LinearLayout

        init {
            inputViews = linkedMapOf()
            initView()
        }

        private fun initItemInputView(readScanInputData: ReadScanInputData): LinearLayout {
            val inputLayout = LinearLayout(context)
            val inputLayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            inputLayoutParams.weight = 1f
            inputLayout.orientation = LinearLayout.HORIZONTAL
            inputLayout.layoutParams = inputLayoutParams

            val inputEt = EditText(context)
            val inputEtParams = LinearLayout.LayoutParams(UIUtils.dip2px(context, 60f), LinearLayout.LayoutParams.MATCH_PARENT)
            inputEt.layoutParams = inputEtParams
            inputEt.setBackgroundResource(R.drawable.shape_scan_type_item_unselected)
            inputEt.hint = readScanInputData.hint
            when (readScanInputData.inputType) {
                "int" -> inputEt.inputType = EditorInfo.TYPE_CLASS_NUMBER
                "float" -> inputEt.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_VARIATION_NORMAL
            }
            inputEt.setSingleLine()
            inputEt.setPadding(UIUtils.dip2px(context, 5f), 0, UIUtils.dip2px(context, 5f), 0)
            inputEt.textSize = 12f
            //TODO
//            if (scanInputInfo.inputFilters != null) inputEt.filters = scanInputInfo.inputFilters
            inputLayout.addView(inputEt)
            inputViews.put(readScanInputData.key, inputEt)

            val unitTv = TextView(context)
            unitTv.text = readScanInputData.unitPrompt
            unitTv.setTextColor(Color.BLACK)
            val unitParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            unitParams.setMargins(UIUtils.dip2px(context, 5f), 0, 0, 0)
            unitTv.layoutParams = unitParams
            inputLayout.addView(unitTv)

            return inputLayout
        }


        private fun initView() {
            parent = LinearLayout(context)
            val parentParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            parent.orientation = LinearLayout.HORIZONTAL
            parentParams.setMargins(UIUtils.dip2px(context, 10f), UIUtils.dip2px(context, 15f), 0, 0)
            parent.layoutParams = parentParams

            val promptTv = TextView(context)
            val promptParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(UIUtils.dip2px(context, 30f), LinearLayout.LayoutParams.MATCH_PARENT)
            promptParams.setMargins(0, UIUtils.dip2px(context, 5f), 0, 0)
            promptTv.layoutParams = promptParams
            promptTv.text = readScanInputBean.typeName
            parent.addView(promptTv)

            val inputParent = LinearLayout(context)
            val inputParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            inputParent.orientation = LinearLayout.VERTICAL
            inputParams.setMargins(UIUtils.dip2px(context, 10f), 0, 0, 0)
            inputParent.layoutParams = inputParams
            parent.addView(inputParent)

            var subParent: LinearLayout? = null
            for ((index, item) in readScanInputBean.data.withIndex()) {
                if (index % 2 == 0) {
                    subParent = LinearLayout(context)
                    val subParentParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, UIUtils.dip2px(context, 25f))
                    subParent.layoutParams = subParentParams
                    if (index > 0) subParentParams.setMargins(0, UIUtils.dip2px(context, 10f), 0, 0)
                    subParent.orientation = LinearLayout.HORIZONTAL
                    inputParent.addView(subParent)
                }
                subParent?.addView(initItemInputView(item))
            }
        }
    }
}