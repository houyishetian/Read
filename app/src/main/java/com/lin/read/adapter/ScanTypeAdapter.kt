package com.lin.read.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.lin.read.R
import com.lin.read.filter.scan.ScanTypeInfo

class ScanTypeAdapter @JvmOverloads constructor(val context: Context, val data: List<ScanTypeInfo>, val use4Words: Boolean = false) : RecyclerView.Adapter<ScanTypeAdapter.ViewHolder>() {
    var isBinding = false
    var layoutId: Int
    var onScanItemClickListener:OnScanItemClickListener? = null

    init {
        if (use4Words) layoutId = R.layout.item_scan_type_4_chars else layoutId = R.layout.item_scan_type
    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(layoutId, null))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        isBinding = true
        holder?.text?.text = data[position].text
        holder?.text?.tag = position
        holder?.checkBox?.isChecked = data[position].isChecked
        isBinding = false
    }

    fun getCheckedInfo(): ScanTypeInfo? {
        for(item in data){
            if(item.isChecked) return item
        }
        return null
    }

    fun getCheckedText():String?{
        for(item in data){
            if(item.isChecked) return item.text
        }
        return null
    }

    private fun setCheckedStatus(text:String){
        for(item in data){
            item.isChecked = text.equals(item.text)
        }
        notifyDataSetChanged()
    }

    interface OnScanItemClickListener {
        fun onItemClick(position: Int, clickText: String)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var text:TextView
        var checkBox: CheckBox
        init {
            text = view.findViewById(R.id.scan_type_text) as TextView
            checkBox = view.findViewById(R.id.scan_type_checkbox) as CheckBox
            checkBox.setOnTouchListener { _, _ -> checkBox.isChecked }
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if(!isBinding){
                    if(isChecked) onScanItemClickListener?.onItemClick(text.tag as Int, text.text.toString())
                    setCheckedStatus(text.text.toString())
                }
            }
        }
    }
}