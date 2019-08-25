package com.lin.read.bookmark

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import android.util.Log
import com.lin.read.filter.BookMark
import com.lin.read.filter.BookMarkKey
import com.lin.read.utils.SingleInstanceHolder
import com.lin.read.utils.forEachRow
import com.lin.read.utils.getAllColumnsByString
import org.jetbrains.anko.db.*
import java.io.File

class BookMarkExportUtil(private val ctx: Context) {
    companion object : SingleInstanceHolder<BookMarkExportUtil, Context>(::BookMarkExportUtil)

    private val TAG = "BookMarkExportUtil"
    private val exportedPath = "/Books/read.db"
    private val bookmarkTable = "bookmark"
    private val sqlHelper by lazy { MySQHelper(ctx, createExportedPathIfNotExists()) }
    private val exportedFilePath by lazy { Environment.getExternalStorageDirectory().absolutePath + exportedPath }
    private fun createExportedPathIfNotExists(): String {
        return exportedFilePath.apply {
            File(this).takeIf { !it.exists() || it.isDirectory }?.run {
                parentFile.mkdirs()
                createNewFile()
            }
        }
    }

    fun getExportedBookMarkPathIfExists(): String? {
        return exportedFilePath.takeIf { File(it).exists() }
    }

    fun exportBookMark2Local(bookMarksList: List<BookMark>): String {
        sqlHelper.use {
            delete(bookmarkTable, null, null)
            bookMarksList.forEach {
                val values = arrayOf(
                        Pair(BookMarkKey.MARK_KEY, it.markKey),
                        Pair(BookMarkKey.WEB_TYPE, it.webType),
                        Pair(BookMarkKey.BOOK_NAME, it.bookName),
                        Pair(BookMarkKey.AUTHOR_NAME, it.authorName),
                        Pair(BookMarkKey.CHAPTER_LINK, it.chapterLink),
                        Pair(BookMarkKey.PAGE, it.page.toString()),
                        Pair(BookMarkKey.INDEX, it.index.toString()),
                        Pair(BookMarkKey.LAST_READ_TIME, it.lastReadTime.toString()),
                        Pair(BookMarkKey.LAST_READ_CHAPTER, it.lastReadChapter)
                )
                insert(bookmarkTable, *values)
            }
            Log.e(TAG,"export book successfully!")
        }
        return exportedPath
    }

    fun getBookMarksFromLocal(): List<BookMark> {
        return mutableListOf<BookMark>().also {
            sqlHelper.use {
                val queryColumns = arrayOf(BookMarkKey.WEB_TYPE, BookMarkKey.BOOK_NAME, BookMarkKey.AUTHOR_NAME, BookMarkKey.CHAPTER_LINK, BookMarkKey.PAGE,
                        BookMarkKey.INDEX, BookMarkKey.LAST_READ_TIME, BookMarkKey.LAST_READ_CHAPTER)
                query(bookmarkTable, queryColumns, null, null, null, null, null, null).forEachRow { rowCursor ->
                    rowCursor.run {
                        getAllColumnsByString().run {
                            it.add(BookMark(getOrDefault(queryColumns[0], ""),
                                    getOrDefault(queryColumns[1], ""),
                                    getOrDefault(queryColumns[2], ""),
                                    getOrDefault(queryColumns[3], ""),
                                    getOrDefault(queryColumns[4], "0").toInt(),
                                    getOrDefault(queryColumns[5], "0").toInt(),
                                    getOrDefault(queryColumns[6], System.currentTimeMillis().toString()).toLong(),
                                    getOrDefault(queryColumns[7], "")
                            ))
                        }
                    }
                }
                Log.e(TAG,"import book successfully!")
            }
        }
    }

    inner class MySQHelper(ctx: Context, fileName: String) : ManagedSQLiteOpenHelper(ctx, fileName, null, 1) {
        override fun onCreate(db: SQLiteDatabase?) {
            db?.createTable(bookmarkTable, true,
                    Pair(BookMarkKey.MARK_KEY, TEXT + PRIMARY_KEY),
                    Pair(BookMarkKey.WEB_TYPE, TEXT),
                    Pair(BookMarkKey.BOOK_NAME, TEXT),
                    Pair(BookMarkKey.AUTHOR_NAME, TEXT),
                    Pair(BookMarkKey.CHAPTER_LINK, TEXT),
                    Pair(BookMarkKey.PAGE, TEXT),
                    Pair(BookMarkKey.INDEX, TEXT),
                    Pair(BookMarkKey.LAST_READ_TIME, TEXT),
                    Pair(BookMarkKey.LAST_READ_CHAPTER, TEXT)
            )
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        }
    }
}