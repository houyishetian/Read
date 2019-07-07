package com.lin.read.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.lin.read.R
import com.lin.read.activity.ReadBookActivity
import com.lin.read.filter.BookInfo
import com.lin.read.utils.Constants

class SearchBookItemAdapter(val context: Context, val allBookData: List<BookInfo>) : RecyclerView.Adapter<SearchBookItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_book, null))
    }

    override fun getItemCount(): Int {
        return allBookData.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        allBookData[position].run {
            holder?.bookName?.text = bookName
            holder?.authorName?.text = authorName
            holder?.bookType?.text = bookType
            holder?.lastUpdate?.text = lastUpdate
            holder?.lastContent?.text = lastChapter
            holder?.download?.setEnabled(!TextUtils.isEmpty(downloadLink));

            holder?.download?.setOnClickListener {
                val intent = Intent()
                intent.setAction("android.intent.action.VIEW")
                val content_url = Uri.parse(downloadLink)
                intent.setData(content_url)
                context.startActivity(intent)
            }
            holder?.read?.setOnClickListener {
                val intent = Intent(context, ReadBookActivity::class.java)
                intent.putExtra(Constants.KEY_SKIP_TO_READ, this)
                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var bookName: TextView
        var authorName: TextView
        var bookType: TextView
        var lastUpdate: TextView
        var lastContent: TextView
        var download: Button
        var read: Button

        init {
            with(itemView) {
                bookName = findViewById(R.id.book_item_bookname) as TextView
                authorName = findViewById(R.id.book_item_authorname) as TextView
                bookType = findViewById(R.id.book_item_booktype) as TextView
                lastUpdate = findViewById(R.id.book_item_lastupdate) as TextView
                lastContent = findViewById(R.id.book_item_lastcontent) as TextView
                download = findViewById(R.id.book_item_download) as Button
                read = findViewById(R.id.book_item_read) as Button
            }
        }
    }
}