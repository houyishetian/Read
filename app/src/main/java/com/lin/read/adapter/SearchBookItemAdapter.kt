package com.lin.read.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.activity.ReadBookActivity
import com.lin.read.filter.BookInfo
import com.lin.read.utils.Constants
import kotlinx.android.synthetic.main.item_search_book.view.*

class SearchBookItemAdapter(private val context: Context, private val allBookData: List<BookInfo>) : RecyclerView.Adapter<SearchBookItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_book, null))
    }

    override fun getItemCount(): Int {
        return allBookData.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        allBookData[position].run {
            holder?.itemView?.book_item_bookname?.text = bookName
            holder?.itemView?.book_item_authorname?.text = authorName
            holder?.itemView?.book_item_booktype?.text = bookType
            holder?.itemView?.book_item_lastupdate?.text = lastUpdate
            holder?.itemView?.book_item_lastcontent?.text = lastChapter
            holder?.itemView?.book_item_download?.isEnabled = !TextUtils.isEmpty(downloadLink)

            holder?.itemView?.book_item_download?.setOnClickListener {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse(downloadLink)
                context.startActivity(intent)
            }
            holder?.itemView?.book_item_read?.setOnClickListener {
                val intent = Intent(context, ReadBookActivity::class.java)
                intent.putExtra(Constants.KEY_SKIP_TO_READ, this as Parcelable)
                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}