package com.lin.read.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lin.read.R
import com.lin.read.filter.search.SearchWebBean

class DialogWebTypeAdapter(private val context: Context, private val webInfos: List<SearchWebBean>) : RecyclerView.Adapter<DialogWebTypeAdapter.ViewHolder>() {
    lateinit var onItemWebClickListener:OnItemWebClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_select_web_dialog, null))
    }

    override fun getItemCount(): Int {
        return webInfos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        webInfos[position].run {
            holder.let {
                it.webName.text = webName
                it.downloadText.visibility = if (canDownload) View.VISIBLE else View.GONE
                it.webName.setOnClickListener{
                    onItemWebClickListener.onItemWebClick(this)
                }
            }
        }
    }

    interface OnItemWebClickListener{
        fun onItemWebClick(searchWebBean: SearchWebBean)
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val webName:TextView
        val downloadText:TextView
        init {
            webName = itemView.findViewById(R.id.dialog_web_name) as TextView
            downloadText = itemView.findViewById(R.id.dialog_download_text) as TextView

        }
    }
}