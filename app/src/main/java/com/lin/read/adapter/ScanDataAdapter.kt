package com.lin.read.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.lin.read.R
import com.lin.read.filter.scan.ScanBaseItemBean
import com.lin.read.filter.scan.ScanOptionItemBean
import com.lin.read.filter.scan.VarPair
import com.lin.read.filter.scan.toPair
import kotlinx.android.synthetic.main.item_scan_sub_option_type.view.*

class ScanDataAdapter(private val context: Context, private val data: List<VarPair<ScanBaseItemBean, Boolean>>, private val use4Words: Boolean = false) : RecyclerView.Adapter<ScanDataAdapter.OptionViewHolder>() {
    private var isBinding = false
    var onScanItemClickListener: ((String?, String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): OptionViewHolder = OptionViewHolder(LayoutInflater.from(context).inflate(R.layout.item_scan_sub_option_type, null))

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: OptionViewHolder?, position: Int) {
        isBinding = true
        data[position].let {
            val checkLogic = fun(checkBox: CheckBox) {
                if (it.first is ScanOptionItemBean) {
                    checkBox.run {
                        val optionItemBean = (it.first as ScanOptionItemBean)
                        text = optionItemBean.name
                        isChecked = it.second
                        setOnTouchListener { _, _ -> isChecked }
                        setOnCheckedChangeListener { _, isChecked ->
                            if (!isBinding) {
                                setCheckedStatus(optionItemBean.id)
                                if (isChecked) {
                                    onScanItemClickListener?.invoke(optionItemBean.id, optionItemBean.name)
                                }
                            }
                        }
                    }
                }
            }
            holder?.itemView?.run {
                if (use4Words) {
                    scan_sub_item_chbox0.visibility = View.GONE
                    checkLogic.invoke(scan_sub_item_chbox1)
                } else {
                    scan_sub_item_chbox1.visibility = View.GONE
                    checkLogic.invoke(scan_sub_item_chbox0)
                }
            }

        }
        isBinding = false
    }

    private fun setCheckedStatus(id: String?) {
        data.iterator().forEach {
            it.second = (it.first as ScanOptionItemBean).id == id
        }
        notifyDataSetChanged()
    }

    inner class DataViews(val layout: View, val textView: TextView, val checkBox: CheckBox) {
        operator fun component1(): TextView = textView
        operator fun component2(): CheckBox = checkBox
    }

    class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view)
}