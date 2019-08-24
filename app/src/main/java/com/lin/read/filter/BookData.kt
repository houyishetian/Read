package com.lin.read.filter

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class BookMark(val webType: String, val bookName: String, val authorName: String, val chapterLink: String,
                    var page: Int = 0, var index: Int = 0, var lastReadTime: Long = 0, var lastReadChapter: String = "") : Serializable, Parcelable {
    val markKey: String
        get() = arrayOf(webType, bookName, authorName, chapterLink).joinToString("_")
    @IgnoredOnParcel
    var isChecked: Boolean = false
    @IgnoredOnParcel
    var isShowCheckBox: Boolean = false
}

class BookMarkKey {
    companion object {
        const val MARK_KEY = "markKey"
        const val WEB_TYPE = "webType"
        const val BOOK_NAME = "bookName"
        const val AUTHOR_NAME = "authorName"
        const val CHAPTER_LINK = "chapterLink"
        const val PAGE = "page"
        const val INDEX = "bookIndex"
        const val LAST_READ_TIME = "lastReadTime"
        const val LAST_READ_CHAPTER = "lastReadChapter"
    }
}

@Parcelize
data class ReadBookBean(val webType: String, val bookName: String, val authorName: String, val chapterLink: String) : Serializable, Parcelable {
    val markKey: String
        get() = arrayOf(webType, bookName, authorName, chapterLink).joinToString("_")
}

@Parcelize
data class ScanBookBean(val scanWebName: String, var bookName: String = "", var authorName: String = "", var wordsNum: String = "", var score: String = "", var scoreNum: String = "",
                        var lastUpdate: String = "", var lastChapter: String = "", var position: Int = 0) : Serializable, Parcelable

@Parcelize
data class SearchBookBean(val webType: String, var bookName: String = "", var authorName: String = "",
                          var lastUpdate: String = "", var lastChapter: String = "", var chapterLink: String = "") : Serializable, Parcelable