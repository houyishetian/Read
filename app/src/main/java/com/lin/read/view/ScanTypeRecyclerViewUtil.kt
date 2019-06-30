package com.lin.read.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
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
import com.lin.read.utils.UIUtils
import java.util.*
import kotlin.collections.HashMap

class ScanTypeRecyclerViewUtil private constructor(var context: Context, var parentLayout: LinearLayout, val readScanBean: ReadScanBean) {
    private var scanTypeViews: HashMap<String, HashMap<String, ScanTypeView>>
    private var scanInputViews: HashMap<String, ScanInputView>
    private var scanViewVisis:HashMap<String,MutableList<Int>>

    companion object {
        @Volatile
        private var instance: ScanTypeRecyclerViewUtil? = null

        fun getInstance(context: Context, parentLayout: LinearLayout, readScanBean: ReadScanBean): ScanTypeRecyclerViewUtil {
            if (instance == null)
                synchronized(ScanTypeRecyclerViewUtil::class) {
                    if (instance == null) instance = ScanTypeRecyclerViewUtil(context, parentLayout, readScanBean)
                }
            return instance!!
        }
    }

    init {
        scanTypeViews = hashMapOf();
        scanViewVisis = hashMapOf()
        scanInputViews = hashMapOf();

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
        if (currentScanType?.isNotEmpty()!!) {
            val scanTypeViews: HashMap<String, ScanTypeView> = hashMapOf()
            currentScanType.forEach {
                val scanTypeView = ScanTypeView(context, it)
                scanTypeView.parent.tag = webType
                scanTypeView.parent.setTag(R.integer.read_scan_view_id, it.key)
                parentLayout.addView(scanTypeView.parent)
                scanTypeViews.put(it.key, scanTypeView)
            }
            this.scanTypeViews.put(webType, scanTypeViews)
        }
    }

    private fun initInputViews(webType: String) {
        val currentScanInputBean = ReflectUtil.getProperty(readScanBean, webType, ReadScanDetailsInfo::class.java)
        if (currentScanInputBean != null && currentScanInputBean.inputTypes != null) {
            val scanInputView = ScanInputView(context, currentScanInputBean.inputTypes)
            scanInputView.parent.tag = webType
            parentLayout.addView(scanInputView.parent)
            this.scanInputViews.put(webType, scanInputView)
        }
    }

    private fun saveScanViewVisis(webType: String? = null) {
        if(webType == null){
            //save all data
            //clear old data
            scanViewVisis.forEach { (_, item) ->
                item.clear()
            }
            for (index in 0 until parentLayout.childCount) {
                scanViewVisis[parentLayout.getChildAt(index).tag]?.add(parentLayout.getChildAt(index).visibility)
            }
        }else{
            //only save the web data
            scanViewVisis[webType]?.clear()
            for (index in 0 until parentLayout.childCount) {
                if(parentLayout.getChildAt(index).tag == webType){
                    scanViewVisis[parentLayout.getChildAt(index).tag]?.add(parentLayout.getChildAt(index).visibility)
                }
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
        scanTypeData.forEach{
            if(it.default) return it
        }
        return null
    }

    private fun setScanTypeClickListener(){
        readScanBean.webs.forEach { webInfo ->
            ReflectUtil.getProperty(readScanBean, webInfo.key, ReadScanDetailsInfo::class.java)?.scanTypes?.forEach{scanTypeBean ->
                var lastHiddenItems: MutableList<String> = mutableListOf()
                scanTypeBean.data?.forEach{scanTypeData ->
                    if(scanTypeData.hasNoSubItems != null && scanTypeData.hasNoSubItems.isNotEmpty()){
                        if(scanTypeData.default) lastHiddenItems.addAll(scanTypeData.hasNoSubItems)
                        scanTypeViews[webInfo.key]?.get(scanTypeBean.key)?.adapter?.onScanItemClickListenerMap?.put(scanTypeData.name, object : ScanTypeAdapter.OnScanItemClickListener {
                            override fun onItemClick(position: Int, clickText: String) {
                                Log.e("Test", "${webInfo.key} -> ${clickText} / ${scanTypeData.name} -> lastHidden ${lastHiddenItems}")
                                lastHiddenItems.forEach {
                                    Log.e("Test", "show ${it}")
                                    scanTypeViews[webInfo.key]?.get(it)?.parent?.visibility = View.VISIBLE
                                }
                                lastHiddenItems.clear()
                                if (clickText == scanTypeData.name) {
                                    scanTypeData.hasNoSubItems.forEach {
                                        Log.e("Test", "hide ${it}")
                                        scanTypeViews[webInfo.key]?.get(it)?.parent?.visibility = View.GONE
                                        lastHiddenItems.add(it)
                                    }
                                }
                                saveScanViewVisis(webInfo.key)
                            }
                        })
                    }else{
                        scanTypeViews[webInfo.key]?.get(scanTypeBean.key)?.adapter?.onScanItemClickListenerMap?.put(scanTypeData.name, object : ScanTypeAdapter.OnScanItemClickListener {
                            override fun onItemClick(position: Int, clickText: String) {
                                Log.e("Test", "${webInfo.key} -> ${clickText} / ${scanTypeData.name} -> lastHidden ${lastHiddenItems}")
                                lastHiddenItems.forEach {
                                    Log.e("Test", "show ${it}")
                                    scanTypeViews[webInfo.key]?.get(it)?.parent?.visibility = View.VISIBLE
                                }
                                lastHiddenItems.clear()
                                saveScanViewVisis(webInfo.key)
                            }
                        })
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

    private fun getSelectedFields(webType: String):List<ReadScanSelectedBean>{
        val result = mutableListOf<ReadScanSelectedBean>()
        for (index in 0 until parentLayout.childCount) {
            if (parentLayout.getChildAt(index).tag == webType && parentLayout.getChildAt(index).visibility == View.VISIBLE) {
                val scanTypeView = scanTypeViews[webType]?.get(parentLayout.getChildAt(index).getTag(R.integer.read_scan_view_id))
                if (scanTypeView != null) {
                    val selectedItem = scanTypeView.adapter.getCheckedInfo()
                    if (selectedItem != null) {
                        val typeName = scanTypeView.readScanTypeBean.typeName
                        val key = scanTypeView.readScanTypeBean.key
                        val name = selectedItem.name
                        val roleInUrl = scanTypeView.readScanTypeBean.roleInUrl
                        val roleKeyInUrl = scanTypeView.readScanTypeBean.roleKeyInUrl
                        val roleValueInUrl = selectedItem.roleValueInUrl
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
            var dataItem: ReadScanInputData? = null
            scanInputViews[webType]?.readScanInputBean?.data?.forEach ergodic@{
                if (it.key == key) {
                    dataItem = it
                    return@ergodic
                }
            }
            if (dataItem != null) {
                if (TextUtils.isEmpty(editText.text.toString())) {
                    editText.setText(dataItem!!.default?.toString())
                }
                var value:Number? = null
                if(!TextUtils.isEmpty(editText.text.toString())){
                    when(dataItem!!.inputType){
                        Constants.INPUT_FLOAT -> value = editText.text.toString().toFloat()
                        Constants.INPUT_INT -> value = editText.text.toString().toInt()
                    }
                }
                result.add(ReadScanInputtedBean(dataItem!!.typeName, dataItem!!.key, dataItem!!.inputType, value, dataItem!!.min, dataItem!!.max))
            }
        }
        return result
    }

    private fun getWebName(webType: String):String?{
        readScanBean.webs?.forEach{
            if(it.key == webType) return it.name
        }
        return null
    }

    fun getSearchInfo(webType: String): ScanInfo? {
        val typeFields = getSelectedFields(webType)
        var rolePathValue: String? = null
        var roleParamPairs: MutableList<String> = mutableListOf()
        typeFields.forEach {
            when (it.roleInUrl) {
                Constants.ROLE_PATH -> rolePathValue = it.roleValueInUrl
                Constants.ROLE_PARAM -> {
                    if (it.roleKeyInUrl != null && it.roleValueInUrl != null) {
                        roleParamPairs.add(it.roleKeyInUrl + "=" + it.roleValueInUrl)
                    }
                }
            }
        }
        return ScanInfo(getWebName(webType), ReflectUtil.getProperty(readScanBean, webType, ReadScanDetailsInfo::class.java)?.mainUrl, rolePathValue, roleParamPairs, getInputtedFields(webType))
    }

    fun getFocusOfEditText(webType: String):EditText?{
        scanInputViews[webType]?.inputViews?.forEach { (_, editText) ->
            if(editText.hasFocus()) return editText
        }
        return null
    }

    class ScanTypeView(var context: Context, var readScanTypeBean: ReadScanTypeBean) {
        private lateinit var promptTv: TextView
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
            promptTv.setText(readScanTypeBean.typeName)
            parent.addView(promptTv)

            val use4Words = readScanTypeBean.use4Words
            var spanCount = 4
            if (use4Words) spanCount = 3

            recyclerView = RecyclerView(context)
            recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            recyclerView.layoutManager = GridLayoutManager(context, spanCount)
            recyclerView.addItemDecoration(ScanTypeItemDecoration(context, 15));
            parent.addView(recyclerView)

            adapter = ScanTypeAdapter(context, readScanTypeBean.data, use4Words)
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
                "float" -> inputEt.inputType = EditorInfo.TYPE_NUMBER_FLAG_DECIMAL
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
            for ((index, item) in readScanInputBean.data?.withIndex()) {
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