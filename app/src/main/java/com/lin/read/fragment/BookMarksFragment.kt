package com.lin.read.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.adapter.BookMarksAdapter
import com.lin.read.bookmark.BookMarkUtil
import com.lin.read.decoration.ScanBooksItemDecoration
import com.lin.read.filter.BookMark
import com.lin.read.utils.Constants
import com.lin.read.view.DialogUtil
import kotlinx.android.synthetic.main.fragment_book_marks.*

class BookMarksFragment : Fragment() {
    private lateinit var allBookmarks: MutableList<BookMark>
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_book_marks,null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        getData()
    }

    private fun initData(){
        allBookmarks = mutableListOf()
        book_marks_rcv.run {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ScanBooksItemDecoration(activity))
            adapter = BookMarksAdapter(this@BookMarksFragment, allBookmarks)
            (adapter as BookMarksAdapter).onItemLongClickListener = object :BookMarksAdapter.OnItemLongClickListener{
                override fun onItemLongClick(view: View) {
                    book_marks_select_all.visibility = View.VISIBLE
                    book_marks_delete.visibility = View.VISIBLE
                    book_marks_delete.isEnabled = false
                    (adapter as BookMarksAdapter).handleAll(false)
                }
            }
            (adapter as BookMarksAdapter).onCheckBoxClickListener = object : BookMarksAdapter.OnCheckBoxClickListener {
                override fun onCheckBoxClick() {
                    book_marks_delete.isEnabled = (book_marks_rcv.adapter as BookMarksAdapter).getAllSelectedItems().isNotEmpty()
                }
            }
        }
        book_marks_select_all.setOnClickListener {
            val adapter = book_marks_rcv.adapter as BookMarksAdapter
            adapter.handleAll(adapter.getAllSelectedItems().size != allBookmarks.size)
            book_marks_delete.isEnabled = adapter.getAllSelectedItems().isNotEmpty()
        }
        book_marks_delete.setOnClickListener{
            getData(true)
        }
    }

    fun isEditMode():Boolean{
        return book_marks_select_all.visibility == View.VISIBLE
    }

    fun exitEditMode(){
        book_marks_select_all.visibility = View.GONE
        book_marks_delete.visibility = View.GONE
        (book_marks_rcv.adapter as BookMarksAdapter).exitEditMode()
    }

    private fun getData(afterDelete:Boolean = false){
        DialogUtil.getInstance().showLoadingDialog(activity)
        Handler().post{
            if (afterDelete) BookMarkUtil.getInstance(activity).deleteBookMarks((book_marks_rcv.adapter as BookMarksAdapter).getAllSelectedItems())
            val bookMarksList = BookMarkUtil.getInstance(activity).getAllBookMarks()
            activity.runOnUiThread{
                DialogUtil.getInstance().hideLoadingView()
                if(!bookMarksList.isNullOrEmpty()){
                    empty_view.visibility = View.GONE
                    book_marks_rcv.visibility = View.VISIBLE
                    book_marks_total_num.text = String.format("共%s条书签", "" + bookMarksList.size)
                    allBookmarks.clear()
                    allBookmarks.addAll(bookMarksList)
                    book_marks_rcv.adapter.notifyDataSetChanged()
                }else{
                    empty_view.visibility = View.VISIBLE
                    book_marks_rcv.visibility = View.GONE
                }
                exitEditMode()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            getData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.READ_REQUEST_CODE) {
            getData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}