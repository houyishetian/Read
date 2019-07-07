package com.lin.read.adapter

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.lin.read.R
import com.lin.read.activity.ReadBookActivity
import com.lin.read.bookmark.BookMarkBean
import com.lin.read.utils.Constants
import com.lin.read.utils.DateUtils

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
            holder?.bookName?.text = bookInfo.bookName
            holder?.authorName?.text = bookInfo.authorName
            holder?.bookType?.text = bookInfo.bookType
            holder?.webName?.text = bookInfo.webName
            holder?.lastReadChapter?.text = lastReadChapter
            holder?.lastReadTime?.text = DateUtils.formatTime(lastReadTime)
            holder?.markSelectCb?.tag = this
            holder?.continueReading?.tag = this
            if(isShowCheckBox){
                holder?.markReadingLayout?.visibility = View.GONE
                holder?.markSelectCb?.visibility = View.VISIBLE
                holder?.markSelectCb?.isChecked = isChecked
            }else{
                holder?.markReadingLayout?.visibility = View.VISIBLE
                holder?.markSelectCb?.visibility = View.GONE
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookName: TextView
        val authorName: TextView
        val lastReadChapter: TextView
        val bookType: TextView
        val lastReadTime: TextView
        val webName: TextView
        val continueReading: TextView
        val markReadingLayout: View
        val markSelectCb: CheckBox

        init {
            itemView.run {
                bookName = findViewById(R.id.mark_item_bookname) as TextView
                authorName = findViewById(R.id.mark_item_authorname) as TextView
                lastReadChapter = findViewById(R.id.mark_item_lastchapter) as TextView
                bookType = findViewById(R.id.mark_item_booktype) as TextView
                lastReadTime = findViewById(R.id.mark_item_lastread) as TextView
                webName = findViewById(R.id.mark_item_web_name) as TextView
                continueReading = (findViewById(R.id.mark_item_continue_read) as TextView).apply {
                    setOnClickListener{
                        val intent = Intent(context, ReadBookActivity::class.java)
                        intent.putExtra(Constants.KEY_SKIP_TO_READ, (tag as BookMarkBean).bookInfo)
                        fragment.startActivityForResult(intent, Constants.READ_REQUEST_CODE)
                    }
                }
                markReadingLayout = findViewById(R.id.mark_reading_layout)
                markSelectCb = (findViewById(R.id.mark_select_cb) as CheckBox).apply {
                    setOnCheckedChangeListener { _, isChecked ->
                        if(!isBinding){
                            (tag as BookMarkBean).isChecked = isChecked
                            notifyDataSetChanged()
                            onCheckBoxClickListener.onCheckBoxClick()
                        }
                    }
                }
            }
        }
    }
}