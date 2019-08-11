package com.lin.read.bookmark

import android.content.Context
import com.google.gson.GsonBuilder
import com.lin.read.utils.Constants
import com.lin.read.utils.SharedUtil
import com.lin.read.utils.SingleInstanceHolder

class BookMarkUtil private constructor(private val ctx: Context) {
    companion object : SingleInstanceHolder<BookMarkUtil, Context>(::BookMarkUtil)

    private var markKeysList: String by SharedUtil(ctx, Constants.BOOK_MARK_LIST, "")

    private var newMarksList: MutableList<BookMarkBean> by SharedUtil(ctx, Constants.BOOK_MARK_NEW_LIST, mutableListOf())

    fun syncOldData() {
        markKeysList.takeIf { it != "" }?.let { keyListStr ->
            // get all old keys
            val keyList = GsonBuilder().create().fromJson(keyListStr, MutableList::class.java) as MutableList<String>
            // get all old bookmarks
            mutableListOf<BookMarkBean>().apply {
                keyList.forEach { markKey ->
                    val existBookMark: String by SharedUtil(ctx, markKey, "")
                    existBookMark.takeIf { it != "" }?.let {
                        add(GsonBuilder().create().fromJson(it, BookMarkBean::class.java))
                    }
                }
            }.takeIf { newMarksList.isEmpty() && it.isNotEmpty() }?.let {
                // sync to new field if needed
                newMarksList = mutableListOf<BookMarkBean>().apply {
                    addAll(it)
                }
                // remove the old fields
                SharedUtil(ctx, Constants.BOOK_MARK_LIST, "").removeKey()
                keyList.forEach {
                    SharedUtil(ctx, it, "").removeKey()
                }
            }
        }
    }

    fun saveBookMark(bookMarkBean: BookMarkBean) {
        bookMarkBean.bookInfo.key?.let { markKey ->
            newMarksList.firstOrNull { it.bookInfo.key == markKey }?.let {
                // this is an existing bookmark, update it
                it.takeIf { it.page != bookMarkBean.page || it.index != bookMarkBean.index }?.let {
                    newMarksList = mutableListOf<BookMarkBean>().apply {
                        newMarksList.forEach {
                            if (it.bookInfo.key != markKey) {
                                add(it)
                            }
                        }
                        add(bookMarkBean)
                    }
                }
                return
            } ?: let {
                // this is a new bookmark, save it
                newMarksList = mutableListOf<BookMarkBean>().apply {
                    addAll(newMarksList)
                    add(bookMarkBean)
                }
            }
        }
    }

    fun getAllBookMarks(): List<BookMarkBean> {
        return newMarksList.apply {
            sortByDescending { it.lastReadTime }
        }
    }

    fun deleteBookMarks(deletedItems: List<BookMarkBean>) {
        newMarksList = mutableListOf<BookMarkBean>().apply {
            addAll(newMarksList.filter { pendingAddedItem ->
                deletedItems.firstOrNull { it.bookInfo.key == pendingAddedItem.bookInfo.key } == null
            })
        }
    }
}