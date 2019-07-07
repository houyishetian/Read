package com.lin.read.adapter

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.activity.ReadBookActivity
import com.lin.read.bookmark.BookMarkBean
import com.lin.read.utils.Constants
import com.lin.read.utils.DateUtils
import kotlinx.android.synthetic.main.item_book_mark.view.*

class BookMarksAdapter(val fragment: Fragment, val bookMarksData: List<BookMarkBean>) : RecyclerView.Adapter<BookMarksAdapter.ViewHolder>() {
    private var isBinding = false
    lateinit var onCheckBoxClickListener: OnCheckBoxClickListener
    lateinit var onItemLongClickListener: OnItemLongClickListener
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(fragment.activity).inflate(R.layout.item_book_mark, null).apply {
            setOnLongClickListener{
                onItemLongClickListener?.onItemLongClick(this)
                return@setOnLongClickListener true
            }
        })
    }

    override fun getItemCount(): Int {
        return bookMarksData.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        isBinding = true
        bookMarksData[position].run {
            holder?.itemView?.mark_item_bookname?.text = bookInfo.bookName
            holder?.itemView?.mark_item_authorname?.text = bookInfo.authorName
            holder?.itemView?.mark_item_booktype?.text = bookInfo.bookType
            holder?.itemView?.mark_item_web_name?.text = bookInfo.webName
            holder?.itemView?.mark_item_lastchapter?.text = lastReadChapter
            holder?.itemView?.mark_item_lastread?.text = DateUtils.formatTime(lastReadTime)
            if(isShowCheckBox){
                holder?.itemView?.mark_reading_layout?.visibility = View.GONE
                holder?.itemView?.mark_select_cb?.visibility = View.VISIBLE
                holder?.itemView?.mark_select_cb?.isChecked = isChecked
            }else{
                holder?.itemView?.mark_reading_layout?.visibility = View.VISIBLE
                holder?.itemView?.mark_select_cb?.visibility = View.GONE
            }
            holder?.itemView?.mark_item_continue_read?.setOnClickListener {
                val intent = Intent(fragment.activity, ReadBookActivity::class.java)
                intent.putExtra(Constants.KEY_SKIP_TO_READ, bookInfo)
                fragment.startActivityForResult(intent, Constants.READ_REQUEST_CODE)
            }
            holder?.itemView?.mark_select_cb?.setOnCheckedChangeListener { _, isChecked ->
                if(!isBinding){
                    this.isChecked = isChecked
                    notifyDataSetChanged()
                    onCheckBoxClickListener.onCheckBoxClick()
                }
            }
        }
        isBinding = false
    }

    interface OnCheckBoxClickListener {
        fun onCheckBoxClick()
    }

    fun getAllSelectedItems():List<BookMarkBean>{
        val result = mutableListOf<BookMarkBean>()
        bookMarksData.forEach{
            if(it.isChecked) result.add(it)
        }
        return result
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(view:View)
    }

    fun handleAll(selectAll: Boolean) {
        bookMarksData.forEach {
            it.isChecked = selectAll
            it.isShowCheckBox = true
        }
        notifyDataSetChanged()
    }

    fun exitEditMode(){
        bookMarksData.forEach {
            it.isChecked = false
            it.isShowCheckBox = false
        }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}