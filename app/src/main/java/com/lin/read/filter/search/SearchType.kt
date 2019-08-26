package com.lin.read.filter.search

import com.lin.read.filter.ReadBookBean
import okhttp3.ResponseBody
import java.io.Serializable

data class SearchWebBean @JvmOverloads constructor(val webType: String, val webName: String, var canDownload: Boolean = false) : Serializable

data class SearchResolveBean(val baseUrl: String, val bookName: String, val responseBody: ResponseBody) : Serializable

data class BookChapterInfo @JvmOverloads constructor(var webType: String, var webName: String, var chapterLink: String, var chapterName: String, var chapterNameOri: String, var page: Int = -1, var index: Int = -1) : Serializable

data class BookChapterContent(var content: String) : Serializable {
    var isComplete: Boolean = true
    var nextLink: String = ""
}

data class ReadResolveBean(val bookChapterInfo: BookChapterInfo?, val readBookBean: ReadBookBean?, val responseBody: ResponseBody) : Serializable

data class CurrentReadInfo(var hasPreviousPage: Boolean, var hasNextPage: Boolean, var hasPreviousChapter: Boolean, var hasNextChapter: Boolean, var bookChapterInfo: BookChapterInfo) : Serializable