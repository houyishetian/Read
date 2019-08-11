package com.lin.read.bookmark

import android.content.Context
import com.google.gson.GsonBuilder
import com.lin.read.filter.BookMark
import com.lin.read.utils.Constants
import com.lin.read.utils.SharedUtil
import com.lin.read.utils.SingleInstanceHolder

class BookMarkUtil private constructor(private val ctx: Context) {
    companion object : SingleInstanceHolder<BookMarkUtil, Context>(::BookMarkUtil)

    private var markKeysList: String by SharedUtil(ctx, Constants.BOOK_MARK_LIST, "")

    private var newMarksList: MutableList<BookMarkBean> by SharedUtil(ctx, Constants.BOOK_MARK_NEW_LIST, mutableListOf())

    private var newMarksList2: MutableList<BookMark> by SharedUtil(ctx, Constants.BOOK_MARK_NEW_LIST2, mutableListOf())

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
        syncOldData2()
    }

    private fun syncOldData2() {
        newMarksList.takeIf { it.isNotEmpty() && newMarksList2.isEmpty() }?.let {
            newMarksList2 = mutableListOf<BookMark>().apply {
                it.forEach {
                    add(BookMark(it.bookInfo.webType, it.bookInfo.bookName, it.bookInfo.authorName, it.bookInfo.bookLink, it.page, it.index, it.lastReadTime, it.lastReadChapter))
                }
            }
        }
    }

    fun saveBookMark(bookMark: BookMark) {
        bookMark.markKey.let { markKey ->
            newMarksList2.firstOrNull { it.markKey == markKey }?.let {
                // this is an existing bookmark, update it
                it.takeIf { it.page != bookMark.page || it.index != bookMark.index }?.let {
                    newMarksList2 = mutableListOf<BookMark>().apply {
                        newMarksList2.forEach {
                            if (it.markKey != markKey) {
                                add(it)
                            }
                        }
                        add(bookMark)
                    }
                }
                return
            } ?: let {
                // this is a new bookmark, save it
                newMarksList2 = mutableListOf<BookMark>().apply {
                    addAll(newMarksList2)
                    add(bookMark)
                }
            }
        }
    }

    fun getAllBookMarks(): List<BookMark> {
        return newMarksList2.apply {
            sortByDescending { it.lastReadTime }
        }
    }

    fun deleteBookMarks(deletedItems: List<BookMark>) {
        newMarksList2 = mutableListOf<BookMark>().apply {
            addAll(newMarksList2.filter { pendingAddedItem ->
                deletedItems.firstOrNull { it.markKey == pendingAddedItem.markKey } == null
            })
        }
    }
}