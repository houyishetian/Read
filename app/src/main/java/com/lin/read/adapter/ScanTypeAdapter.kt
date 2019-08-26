package com.lin.read.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.lin.read.R
import com.lin.read.filter.scan.ReadScanTypeData
import com.lin.read.filter.scan.VarPair

class ScanTypeAdapter @JvmOverloads constructor(private val context: Context, private val data: List<VarPair<ReadScanTypeData,Boolean>>, use4Words: Boolean = false) : RecyclerView.Adapter<ScanTypeAdapter.ViewHolder>() {
    private var isBinding = false
    private var layoutId: Int
    var onScanItemClickListener:OnScanItemClickListener? = null
    val onScanItemClickListenerMap:HashMap<String,OnScanItemClickListener>

    init {
        layoutId = if (use4Words) R.layout.item_scan_type_4_chars else R.layout.item_scan_type
        onScanItemClickListenerMap = hashMapOf()
        initCheckedStatus()
        data.takeIf { getCheckedInfo() == null && it.isNotEmpty() }?.run { this[0].second = true }
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(layoutId, null))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        isBinding = true
        holder?.run {
            text.text = data[position].first.name
            text.tag = position
            checkBox.isChecked = data[position].second
        }
        isBinding = false
    }

    fun getCheckedInfo(): ReadScanTypeData? = data.firstOrNull { it.second }?.first

    fun getCheckedText():String? = data.firstOrNull { it.second }?.first?.name

    private fun setCheckedStatus(text: String) {
        data.forEach { it.second = it.first.name == text }
        notifyDataSetChanged()
    }

    private fun initCheckedStatus() = data.forEach { it.second = it.first.default }

    interface OnScanItemClickListener {
        fun onItemClick(position: Int, clickText: String)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text:TextView
        var checkBox: CheckBox
        init {
            text = view.findViewById(R.id.scan_type_text) as TextView
            checkBox = (view.findViewById(R.id.scan_type_checkbox) as CheckBox).apply {
                setOnTouchListener { _, _ -> isChecked }
                setOnCheckedChangeListener { _, isChecked ->
                    if (!isBinding) {
                        setCheckedStatus(this@ViewHolder.text.text.toString())
                        if (isChecked) {
                            onScanItemClickListener?.onItemClick(this@ViewHolder.text.tag as Int, this@ViewHolder.text.text.toString())
                            onScanItemClickListenerMap[this@ViewHolder.text.text.toString()]?.onItemClick(this@ViewHolder.text.tag as Int, this@ViewHolder.text.text.toString())
                        }
                    }
                }
            }
        }
    }
}