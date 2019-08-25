package com.lin.read.bookmark

import android.content.Context
import com.lin.read.filter.BookMark
import com.lin.read.utils.Constants
import com.lin.read.utils.SharedUtil
import com.lin.read.utils.SingleInstanceHolder
import com.lin.read.utils.minusBy

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

    fun saveBookMarksList(newBookMarksList: List<BookMark>, coverBookMarkType: CoverBookMarkType = CoverBookMarkType.COVER_EXIST) {
        if (newBookMarksList.isEmpty()) return
        if (newMarksList2.isEmpty()) {
            newMarksList2 = mutableListOf<BookMark>().apply { addAll(newBookMarksList) }
            return
        }
        val tempExistingList = mutableListOf<BookMark>().apply { addAll(newMarksList2) }
        newMarksList2 = mutableListOf<BookMark>().apply {
            val existDifferentList = tempExistingList.minusBy(newBookMarksList) { it.markKey }
            addAll(existDifferentList)
            val existSameList = tempExistingList.minusBy(existDifferentList){it.markKey}

            val newDifferentList = newBookMarksList.minusBy(tempExistingList) { it.markKey }
            addAll(newDifferentList)
            val newSameList = newBookMarksList.minusBy(newDifferentList){it.markKey}

            newSameList.forEach { newBookItem->
                existSameList.first { it.markKey == newBookItem.markKey }.let {
                    add(when (coverBookMarkType) {
                        CoverBookMarkType.KEEP_EXIST -> it
                        CoverBookMarkType.COVER_EXIST -> newBookItem
                        CoverBookMarkType.SAVE_OLDER -> if (it.lastReadTime > newBookItem.lastReadTime) newBookItem else it
                        CoverBookMarkType.SAVE_NEWER -> if (it.lastReadTime > newBookItem.lastReadTime) it else newBookItem
                    })
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

    enum class CoverBookMarkType {
        //KEEP_EXIST:the legacy one is kept, abandon the new save item;
        KEEP_EXIST,
        //COVER_EXIST: the new save item is saved, abandon the legacy one;
        COVER_EXIST,
        //SAVE_OLDER: sort by time, save the older one;
        SAVE_OLDER,
        //SAVE_NEWER: sort by time, save the newer one
        SAVE_NEWER
    }
}