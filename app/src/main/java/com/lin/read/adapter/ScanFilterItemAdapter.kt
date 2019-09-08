package com.lin.read.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.decoration.ScanItemDecoration
import com.lin.read.filter.scan.*
import com.lin.read.utils.isNotNull
import com.lin.read.utils.pairBean
import com.lin.read.utils.tripleBean
import kotlinx.android.synthetic.main.item_scan_group_type.view.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class ScanFilterItemAdapter(private val ctx: Context, private var scanBean: ScanBean) : RecyclerView.Adapter<ScanFilterItemAdapter.GroupViewHolder>() {
    private val allSplitData: MutableList<Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>>
    private var selectWeb: String

    private val webData: Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>
    private val optionData: HashMap<String, MutableList<Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>>>
    private val inputData: HashMap<String, Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>>

    private val allInputValues: HashMap<String, LinkedHashMap<String, String>>

    private val optionItemSelectedListener = fun(selectedId: String?, selectedName: String) {
        updateAllSplitData()
        notifyDataSetChanged()
    }

    init {
        allSplitData = mutableListOf()
        allInputValues = hashMapOf()
        // add default web's options
        selectWeb = scanBean.webs.keys.first().tripleBean.third.let { defaultWeb ->
            defaultWeb.takeIf { scanBean.webs.values.first().firstOrNull { it.id == defaultWeb }.isNotNull() }
                    ?: scanBean.webs.values.first().first().id!!
        }
        // only need to resolve the first element
        webData = Pair(scanBean.webs.keys.first(), scanBean.webs.values.first().map { (it as ScanBaseItemBean) toPair (selectWeb == it.id) })
        optionData = hashMapOf<String, MutableList<Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>>>().apply {
            scanBean.details.forEach { webKey, detailItem ->
                put(webKey, mutableListOf<Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>>().apply {
                    detailItem.options.forEach { optionKey, optionData ->
                        // dynamically add the month type
                        if (optionKey.pairBean.first == "monthType" && optionData.isEmpty()) {
                            optionData.run {
                                addAll(getLatest10MonthsType())
                            }
                        }
                        val defaultSelectId = optionKey.tripleBean.third.let { defaultId ->
                            defaultId.takeIf { optionData.firstOrNull { it.id == defaultId }.isNotNull() }
                                    ?: optionData.first().id
                        }
                        add(Pair(optionKey, optionData.map { (it as ScanBaseItemBean) toPair (defaultSelectId == it.id) }))
                    }
                })
            }
        }
        inputData = hashMapOf<String, Pair<String, List<VarPair<ScanBaseItemBean, Boolean>>>>().apply {
            scanBean.details.forEach { webKey, detailItem ->
                detailItem.inputs?.run {
                    put(webKey, this.let {
                        // only need to resolve the first element
                        Pair(it.keys.first(), it.values.first().map { (it as ScanBaseItemBean) toPair false })
                    })
                }
            }
        }
        updateAllSplitData()
    }

    // get latest 10 months data
    private fun getLatest10MonthsType(): List<ScanOptionItemBean> = mutableListOf<ScanOptionItemBean>().apply {
        val calendar = Calendar.getInstance()
        for (index in 0 until 10) {
            "${calendar.get(Calendar.YEAR)}-${String.format("%02d", calendar.get(Calendar.MONTH) + 1)}".let {
                add(ScanOptionItemBean(it, it))
            }
            calendar.add(Calendar.MONTH, -1)
        }
    }

    private fun getExcludeKeysList(): List<String> = mutableListOf<String>().apply {
        optionData[selectWeb]?.forEach {
            val groupKey = it.first.tripleBean.first
            val selectedId = (it.second.first { it.second }.first as ScanOptionItemBean).id
            scanBean.details[selectWeb]!!.hasNoKeys?.get("${groupKey}_$selectedId")?.let {
                addAll(it)
            }
        }
    }

    fun getScanDataBean(): ScanDataBean? {
        allSplitData.withIndex().let {
            val mainUrl = scanBean.details[selectWeb]!!.mainUrl
            val webName = webData.second.first { (it.first as ScanOptionItemBean).id == selectWeb }.first.let { (it as ScanOptionItemBean).name }
            var rolePathValue = ""
            val roleParamMap = hashMapOf<String, String?>()
            val roleParamsKeys = scanBean.details[selectWeb]!!.roleParamsKeys
            val inputData = mutableListOf<ScanInputData>()
            it.forEach { (index, pair) ->
                if (index > 0) {
                    when (pair.second.first().first) {
                        is ScanOptionItemBean -> {
                            val selectedRelatedGroupKey = pair.first.tripleBean.first
                            val selectedId = (pair.second.first { it.second }.first as ScanOptionItemBean).id
                            if (scanBean.details[selectWeb]!!.rolePathKey == selectedRelatedGroupKey) {
                                rolePathValue = selectedId!!
                            } else if (roleParamsKeys.containsKey(selectedRelatedGroupKey) && selectedId.isNotNull()) {
                                roleParamMap.put(roleParamsKeys[selectedRelatedGroupKey]!!, selectedId)
                            }
                        }
                        is ScanInputItemBean -> {
                            pair.second.map { it.first as ScanInputItemBean }.forEach {
                                allInputValues[selectWeb]?.get(it.id)?.apply {
                                    val value: Number = when (it.inputType) {
                                        "int" -> this.toInt()
                                        "float" -> this.toFloat()
                                        else -> throw Exception("unsupported input type")
                                    }
                                    inputData.add(ScanInputData(it.id, value, it.min, it.max))
                                }
                            }
                        }
                    }
                }
            }
            return ScanDataBean(selectWeb, webName, mainUrl, rolePathValue, roleParamMap, inputData)
        }
    }

    private fun updateAllSplitData() {
        val excludeKeys = getExcludeKeysList()
        allSplitData.clear()
        // add webs
        allSplitData.add(webData)
        if (excludeKeys.isEmpty()) {
            optionData[selectWeb]?.let { allSplitData.addAll(it) }
            inputData[selectWeb]?.let { allSplitData.add(it) }
        } else {
            optionData[selectWeb]?.forEach {
                it.takeIf { !(it.first.tripleBean.first in excludeKeys) }?.let {
                    allSplitData.add(it)
                }
            }
            inputData[selectWeb]?.takeIf { !(it.first.tripleBean.first in excludeKeys) }?.let {
                allSplitData.add(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder = GroupViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_scan_group_type, parent, false))

    override fun getItemCount(): Int = allSplitData.size

    override fun onBindViewHolder(holder: GroupViewHolder?, position: Int) {
        holder?.itemView?.run {
            allSplitData[position].let { pairBean ->
                tv_scan_group_type.text = pairBean.first.pairBean.second
                if (position == 0) {
                    rcv_scan_group_type.run {
                        this.setHasFixedSize(true)
                        layoutManager = GridLayoutManager(ctx, 4)
                        getItemDecorationAt(0)
                                ?: addItemDecoration(ScanItemDecoration(ctx, top = 15, left = 10))
                        adapter = ScanDataAdapter(ctx, pairBean.second, false).apply {
                            onScanItemClickListener = fun(id, name) {
                                Log.e("select web item", "${Pair(name, id)}")
                                selectWeb = id!!
                                optionItemSelectedListener.invoke(id, name)
                            }
                        }
                    }
                } else {
                    when (pairBean.second[0].first) {
                        is ScanOptionItemBean -> {
                            val use4Words = scanBean.details[selectWeb]!!.use4WordsList?.contains(pairBean.first.pairBean.first)
                                    ?: false
                            rcv_scan_group_type.run {
                                this.setHasFixedSize(true)
                                layoutManager = GridLayoutManager(ctx, if (use4Words) 3 else 4)
                                getItemDecorationAt(0)
                                        ?: addItemDecoration(ScanItemDecoration(ctx, top = 15, left = 10))
                                adapter = ScanDataAdapter(ctx, pairBean.second, use4Words).apply {
                                    onScanItemClickListener = fun(id, name) {
                                        Log.e("select item", "${Pair(name, id)}")
                                        optionItemSelectedListener.invoke(id, name)
                                    }
                                }
                            }
                        }
                        is ScanInputItemBean -> {
                            rcv_scan_group_type.run {
                                setHasFixedSize(true)
                                layoutManager = GridLayoutManager(ctx, 2)
                                getItemDecorationAt(0)
                                        ?: addItemDecoration(ScanItemDecoration(ctx, top = 15, left = 10))
                                adapter = ScanInputAdapter(ctx, pairBean.second.map { it.first as ScanInputItemBean }, fun(inputDatas) {
                                    allInputValues.put(selectWeb, inputDatas)
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view)
}