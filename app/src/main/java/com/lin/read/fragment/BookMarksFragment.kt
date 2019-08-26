package com.lin.read.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lin.read.R
import com.lin.read.adapter.BookMarksAdapter
import com.lin.read.bookmark.BookMarkExportUtil
import com.lin.read.bookmark.BookMarkUtil
import com.lin.read.decoration.ScanBooksItemDecoration
import com.lin.read.filter.BookMark
import com.lin.read.utils.*
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
                    book_marks_export.visibility = View.GONE
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

        book_marks_export.setOnClickListener {
            mutableListOf<String>().apply {
                if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.takeIf { it.isNotEmpty() }?.run {
                requestPermissions(this.toTypedArray(), Constants.CODE_FROM_STORAGE_PERMISSIONS)
            } ?: showExportDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.CODE_FROM_STORAGE_PERMISSIONS) {
            val readIndex = permissions.withIndex().firstOrNull { it.value == Manifest.permission.READ_EXTERNAL_STORAGE }?.index
                    ?: throw Exception("cannot get READ_EXTERNAL_STORAGE permission")
            val writeIndex = permissions.withIndex().firstOrNull { it.value == Manifest.permission.WRITE_EXTERNAL_STORAGE }?.index
                    ?: throw Exception("cannot get WRITE_EXTERNAL_STORAGE permission")
            val permissionAllowed = grantResults[readIndex] == PackageManager.PERMISSION_GRANTED && grantResults[writeIndex] == PackageManager.PERMISSION_GRANTED
            if(permissionAllowed){
                activity.makeMsg("权限申请成功！")
                showExportDialog()
            }else{
                activity.makeMsg("权限申请失败！")
            }
        }
    }

    private fun showExportDialog() {
        activity.showFullScreenDialog(R.layout.dialog_export_bookmark, true) { dialog, view ->
            val onNoDoubleClickListenerBlocker = fun(v: View) {
                dialog.dismiss()
                when (v.id) {
                    R.id.dialog_export_manage_export -> BookMarkUtil.getInstance(activity).getAllBookMarks().takeIf { it.isNotEmpty() }?.let {
                        BookMarkExportUtil.getInstance(activity).exportBookMark2Local(it).run {
                            activity.makeMsg("书签已导出到 $this!")
                        }
                    } ?: activity.makeMsg("没有书签可供导出！")
                    R.id.dialog_export_manage_import -> BookMarkExportUtil.getInstance(activity).getBookMarksFromLocal().takeIf { it.isNotEmpty() }?.run {
                        BookMarkUtil.getInstance(activity).let {
                            // if the existing list and new list have no same items, directly import
                            it.getAllBookMarks().takeIf { it.isEmpty() || it.size == (it.minusBy(this) { it.markKey }).size }?.let { _ ->
                                it.saveBookMarksList(this).let {
                                    activity.makeMsg("导入成功!")
                                    getData()
                                }
                            } ?: showCoverTypeExportDialog(this)
                        }
                    } ?: activity.makeMsg("本地不存在其他书签记录!")
                    R.id.dialog_export_manage_share -> BookMarkExportUtil.getInstance(activity).getExportedBookMarkPathIfExists()?.let {
                        shareFile(it)
                    } ?: makeMsg("未找到本地书签，请先导出！")
                }
            }
            view.findViewById<View>(R.id.dialog_export_manage_cancel).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
            view.findViewById<View>(R.id.dialog_export_manage_export).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
            view.findViewById<View>(R.id.dialog_export_manage_import).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
            view.findViewById<View>(R.id.dialog_export_manage_share).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
        }
    }

    private fun showCoverTypeExportDialog(newBookMarkList: List<BookMark>) {
        activity.showFullScreenDialog(R.layout.dialog_bookmark_select_cover_type) { dialog, view ->
            val onNoDoubleClickListenerBlocker = fun(v: View) {
                dialog.dismiss()
                val coverType = when (v.id) {
                    R.id.dialog_bookmark_cover_keep_exist -> BookMarkUtil.CoverBookMarkType.KEEP_EXIST
                    R.id.dialog_bookmark_cover_cover_exist -> BookMarkUtil.CoverBookMarkType.COVER_EXIST
                    R.id.dialog_bookmark_cover_newer -> BookMarkUtil.CoverBookMarkType.SAVE_NEWER
                    R.id.dialog_bookmark_cover_older -> BookMarkUtil.CoverBookMarkType.SAVE_OLDER
                    else -> throw java.lang.Exception("not found the id")
                }
                BookMarkUtil.getInstance(activity).saveBookMarksList(newBookMarkList, coverType).let {
                    activity.makeMsg("导入成功!")
                    getData()
                }
            }
            view.findViewById<View>(R.id.dialog_bookmark_cover_keep_exist).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
            view.findViewById<View>(R.id.dialog_bookmark_cover_cover_exist).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
            view.findViewById<View>(R.id.dialog_bookmark_cover_newer).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
            view.findViewById<View>(R.id.dialog_bookmark_cover_older).setOnNoDoubleClickListener(onNoDoubleClickListenerBlocker)
        }
    }

    fun isEditMode():Boolean{
        return book_marks_select_all.visibility == View.VISIBLE
    }

    fun exitEditMode(){
        book_marks_export.visibility = View.VISIBLE
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
                book_marks_total_num.text = String.format("共%s条书签", "" + bookMarksList.size)
                if(!bookMarksList.isNullOrEmpty()){
                    empty_view.visibility = View.GONE
                    book_marks_rcv.visibility = View.VISIBLE
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
        when(requestCode){
            Constants.CODE_FROM_READING_UPDATE_BOOKMARK -> getData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}