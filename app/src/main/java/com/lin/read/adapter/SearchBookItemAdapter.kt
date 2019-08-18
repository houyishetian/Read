package com.lin.read.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.activity.ReadBookActivity
import com.lin.read.filter.ReadBookBean
import com.lin.read.filter.SearchBookBean
import com.lin.read.utils.Constants
import kotlinx.android.synthetic.main.item_search_book.view.*

class SearchBookItemAdapter(private val context: Context, private val allBookData: List<SearchBookBean>) : RecyclerView.Adapter<SearchBookItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_book, null).apply {
            setOnClickListener{
                readBook(allBookData[it.tag as Int])
            }
        })
    }

    override fun getItemCount(): Int {
        return allBookData.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.view?.tag = position
        allBookData[position].run {
            holder?.itemView?.book_item_bookname?.text = bookName
            holder?.itemView?.book_item_authorname?.text = authorName
            holder?.itemView?.book_item_lastupdate?.text = lastUpdate
            holder?.itemView?.book_item_lastcontent?.text = lastChapter
            holder?.itemView?.book_item_read?.setOnClickListener {
                readBook(this)
            }
        }
    }

    private fun readBook(searchBookBean: SearchBookBean) {
        searchBookBean.run {
            val intent = Intent(context, ReadBookActivity::class.java)
            intent.putExtra(Constants.KEY_SKIP_TO_READ, ReadBookBean(webType, bookName, authorName, chapterLink) as Parcelable)
            context.startActivity(intent)
        }
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}