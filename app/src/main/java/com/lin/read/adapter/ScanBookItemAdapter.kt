package com.lin.read.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.activity.MainActivity
import com.lin.read.filter.ScanBookBean
import com.lin.read.utils.setOnNoDoubleClickListener
import kotlinx.android.synthetic.main.item_scan_book.view.*

class ScanBookItemAdapter(private val ctx: Context,private val bookList:List<ScanBookBean> ) : RecyclerView.Adapter<ScanBookItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.item_scan_book, null).apply {
            setOnNoDoubleClickListener {
                (ctx as MainActivity).clickScanBookItem(bookList[it.tag as Int].bookName)
            }
        })
    }

    override fun getItemCount(): Int = bookList.size

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.view?.tag = position
        holder?.itemView?.run {
            bookList[position].let {
                book_item_bookname.text = it.bookName
                book_item_authorname.text = it.authorName
                book_item_web.text = it.scanWebName
                book_item_lastupdate.text = it.lastUpdate
                book_item_wordsnum.text = String.format("%s万字",it.wordsNum)
                book_item_recommend.visibility = View.INVISIBLE
                book_item_vipclick.visibility = View.INVISIBLE
                book_item_score.text = it.score
                book_item_scorenum.text = String.format("%s人",it.scoreNum)
            }
        }
    }


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}