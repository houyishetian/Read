package com.lin.read.bookmark

import android.content.Context
import com.lin.read.filter.BookMark
import com.lin.read.utils.Constants
import com.lin.read.utils.SharedUtil
import com.lin.read.utils.SingleInstanceHolder

class BookMarkUtil private constructor(ctx: Context) {
    companion object : SingleInstanceHolder<BookMarkUtil, Context>(::BookMarkUtil)

    private var newMarksList2: MutableList<BookMark> by SharedUtil(ctx, Constants.BOOK_MARK_NEW_LIST2, mutableListOf())

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