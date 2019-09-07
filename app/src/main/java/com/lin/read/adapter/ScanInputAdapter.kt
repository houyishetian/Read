package com.lin.read.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.TextView
import com.lin.read.R
import com.lin.read.filter.scan.ScanInputItemBean
import com.lin.read.utils.textWatcher
import kotlinx.android.synthetic.main.item_scan_sub_input_type.view.*

class ScanInputAdapter(private val context: Context, private val data: List<ScanInputItemBean>,
                       private val onInputDataChanged: ((LinkedHashMap<String, String>) -> Unit)? = null) : RecyclerView.Adapter<ScanInputAdapter.InputViewHolder>() {
    private var isBinding = false

    private val inputDatas: LinkedHashMap<String, String>

    init {
        inputDatas = linkedMapOf<String, String>().apply {
            data.forEach {
                put(it.id, it.defaultValue.toString())
            }
            onInputDataChanged?.invoke(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): InputViewHolder = InputViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scan_sub_input_type, null))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: InputViewHolder?, position: Int) {
        isBinding = true
        holder?.itemView?.run {
            data[position].let {
                scan_sub_item_et.hint = it.hint
                scan_sub_item_et.setText(it.defaultValue.toString())
                scan_sub_item_et.inputType = when (it.inputType) {
                    "int" -> EditorInfo.TYPE_CLASS_NUMBER
                    "float" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_VARIATION_NORMAL
                    else -> throw Exception("unsupported inputType")
                }
                scan_sub_item_et.textWatcher { editable ->
                    inputDatas.put(it.id, editable.toString())
                    onInputDataChanged?.invoke(inputDatas)
                }
                scan_sub_item_unit.text = it.unitText
            }
        }
        isBinding = false
    }

    inner class DataViews(val layout: View, val textView: TextView, val checkBox: CheckBox) {
        operator fun component1(): TextView = textView
        operator fun component2(): CheckBox = checkBox
    }

    class InputViewHolder(view: View) : RecyclerView.ViewHolder(view)
}