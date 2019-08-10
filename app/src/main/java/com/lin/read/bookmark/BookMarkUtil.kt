package com.lin.read.bookmark

import android.content.Context
import com.google.gson.GsonBuilder
import com.lin.read.filter.BookInfo
import com.lin.read.utils.Constants
import com.lin.read.utils.SharedUtil
import com.lin.read.utils.SingleInstanceHolder

class BookMarkUtil private constructor(private val ctx: Context) {
    companion object : SingleInstanceHolder<BookMarkUtil, Context>(::BookMarkUtil)

    private var markKeysList: String by SharedUtil(ctx, Constants.BOOK_MARK_LIST, "")

    fun saveBookMark(bookMarkBean: BookMarkBean) {
        bookMarkBean.bookInfo.key?.let { markKey ->
            getBookMark(bookMarkBean.bookInfo).takeIf { it == null || it.page != bookMarkBean.page || it.index != bookMarkBean.index }?.run {
                var existBookMark: String by SharedUtil(ctx, markKey, "")
                existBookMark = GsonBuilder().create().toJson(bookMarkBean)
            }
            addBookMarkKeyToList(markKey)
        }
    }

    fun getAllBookMarks(): List<BookMarkBean> {
        return mutableListOf<BookMarkBean>().apply {
            getBookMarkKeysList().forEach {
                val existBookMark: String by SharedUtil(ctx, it, "")
                existBookMark.takeIf { it != "" }?.let {
                    add(GsonBuilder().create().fromJson(it, BookMarkBean::class.java))
                }
            }
            sortByDescending { it.lastReadTime }
        }
    }

    fun getBookMark(bookInfo: BookInfo): BookMarkBean? {
        return bookInfo.key.let {
            val existBookMark: String by SharedUtil(ctx, it, "")
            existBookMark.takeIf { it != "" }?.run {
                GsonBuilder().create().fromJson(this, BookMarkBean::class.java)
            }
        }
    }

    fun deleteBookMarks(bookMarks: List<BookMarkBean>) {
        bookMarks.map { it.bookInfo.key }.forEach {
            SharedUtil(ctx, it, "").removeKey()
            removeBookMarkKeyFromList(it)
        }
    }

    private fun addBookMarkKeyToList(key: String) {
        getBookMarkKeysList().takeIf { !it.contains(key) }?.let {
            it.add(key)
            markKeysList = GsonBuilder().create().toJson(it)
        }
    }

    private fun getBookMarkKeysList(): MutableList<String> {
        return markKeysList.takeIf { it != "" }?.let {
            GsonBuilder().create().fromJson(it, MutableList::class.java) as MutableList<String>
        } ?: mutableListOf()
    }

    private fun removeBookMarkKeyFromList(key: String) {
        getBookMarkKeysList().takeIf { it.contains(key) }?.let {
            it.remove(key)
            markKeysList = GsonBuilder().create().toJson(it)
        }
    }
}