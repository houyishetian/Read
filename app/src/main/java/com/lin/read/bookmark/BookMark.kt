package com.lin.read.bookmark

import com.lin.read.filter.BookInfo
import java.io.Serializable

class BookMarkBean() : Serializable {
    var page: Int = 0
    var index: Int = 0
    lateinit var bookInfo: BookInfo
    var lastReadTime: Long = 0
    lateinit var lastReadChapter: String
    var isChecked: Boolean = false
    var isShowCheckBox: Boolean = false
}