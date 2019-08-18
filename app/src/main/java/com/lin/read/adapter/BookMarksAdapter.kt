package com.lin.read.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.activity.ReadBookActivity
import com.lin.read.filter.BookMark
import com.lin.read.filter.ReadBookBean
import com.lin.read.utils.Constants
import com.lin.read.utils.StringKtUtil
import kotlinx.android.synthetic.main.item_book_mark.view.*

class BookMarksAdapter(private val fragment: Fragment, private val bookMarksData: List<BookMark>) : RecyclerView.Adapter<BookMarksAdapter.ViewHolder>() {
    private var isBinding = false
    lateinit var onCheckBoxClickListener: OnCheckBoxClickListener
    lateinit var onItemLongClickListener: OnItemLongClickListener
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(fragment.activity as Context).inflate(R.layout.item_book_mark, null).apply {
            setOnLongClickListener{
                onItemLongClickListener.onItemLongClick(this)
                return@setOnLongClickListener true
            }
            setOnClickListener {
                if ((it.getTag(R.integer.read_book_cb_view_id) as View).visibility != View.VISIBLE) {
                    readBook(bookMarksData[it.tag as Int])
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return bookMarksData.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        isBinding = true
        holder?.view?.tag = position
        holder?.view?.setTag(R.integer.read_book_cb_view_id, holder.itemView?.mark_select_cb)
        bookMarksData[position].run {
            holder?.itemView?.mark_item_bookname?.text = bookName
            holder?.itemView?.mark_item_authorname?.text = authorName
            holder?.itemView?.mark_item_web_name?.text = Constants.SEARCH_WEB_NAME_MAP[webType]
            holder?.itemView?.mark_item_lastchapter?.text = lastReadChapter
            holder?.itemView?.mark_item_lastread?.text = StringKtUtil.formatTime(lastReadTime)
            if(isShowCheckBox){
                holder?.itemView?.mark_reading_layout?.visibility = View.GONE
                holder?.itemView?.mark_select_cb?.visibility = View.VISIBLE
                holder?.itemView?.mark_select_cb?.isChecked = isChecked
            }else{
                holder?.itemView?.mark_reading_layout?.visibility = View.VISIBLE
                holder?.itemView?.mark_select_cb?.visibility = View.GONE
            }
            holder?.itemView?.mark_item_continue_read?.setOnClickListener {
                readBook(this)
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

    private fun readBook(bookMark: BookMark) {
        bookMark.run {
            val intent = Intent(fragment.activity, ReadBookActivity::class.java)
            intent.putExtra(Constants.KEY_SKIP_TO_READ, ReadBookBean(webType, bookName, authorName, chapterLink) as Parcelable)
            fragment.startActivityForResult(intent, Constants.READ_REQUEST_CODE)
        }
    }

    interface OnCheckBoxClickListener {
        fun onCheckBoxClick()
    }

    fun getAllSelectedItems(): List<BookMark> {
        return bookMarksData.filter { it.isChecked }
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

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}