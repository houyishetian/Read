package com.lin.read.filter.scan.qidian

import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.BookLinkInfo
import com.lin.read.filter.scan.ScanInfo
import com.lin.read.utils.StringKtUtil
import okhttp3.ResponseBody

class QiDianResolveUtil {
    companion object {
        fun getMaxPageAndBookInfoFromRandPage(responseBody: ResponseBody, needMaxPage: Boolean = false): MutableList<Any> {
            val result = mutableListOf<Any>()
            responseBody.byteStream().bufferedReader().useLines {
                var alreadyGetMaxPage = false
                var position = 0
                var currentPage = 0
                var maxPage = 0
                it.forEach {
                    val current = it.trim()
                    val bookLink = StringKtUtil.getBookLinkFromRankPageForQiDian(current)
                    bookLink?.apply {
                        val bookLinkInfo = BookLinkInfo(this, null, position)
                        result.add(bookLinkInfo)
                        position++
                    } ?: current.takeIf { !alreadyGetMaxPage && it.contains("page-container") }?.apply {
                        StringKtUtil.getCurrentAndMaxPageForQiDian(this)?.takeIf { it.isNotEmpty() }?.apply {
                            currentPage = this[0].toInt()
                            maxPage = this[1].toInt()
                            alreadyGetMaxPage = true
                        }
                    }
                    result.forEach { (it as BookLinkInfo).page = currentPage }
                }
                if (needMaxPage) result.add(0, maxPage)
            }
            return result
        }

        fun getBookDetailsInfo(scanInfo: ScanInfo, responseBody: ResponseBody): BookInfo? {
            responseBody.byteStream().bufferedReader().useLines {
                val bookInfo = BookInfo()
                it.forEach {
                    val current = it.trim()
                    bookInfo.takeIf { it.lastChapter == null }?.let {
                        //<p class="gray ell" id="ariaMuLu" role="option">2小时前<span class="char-dot">·</span>连载至第717章 外来的尚</p>
                        StringKtUtil.getDataFromContentByRegex(current, "id=\"ariaMuLu\" role=\"option\">([^\n]+)<span class=\"char-dot\">·</span>连载至([^\n]+)</p>", listOf(1, 2))?.apply {
                            it.lastUpdate = this[0]
                            it.lastChapter = this[1]
                        }
                    } ?: bookInfo.takeIf { it.wordsNum == null }?.let {
                        //<p class="book-meta" role="option">182.22万字<span class="char-pipe">|</span>连载</p>
                        StringKtUtil.getDataFromContentByRegex(current, "<p class=\"book-meta\" role=\"option\">([0-9.]+)万字", listOf(1))?.apply {
                            it.wordsNum = this[0]
                        }
                    } ?: bookInfo.takeIf { it.authorName == null }?.let {
                        //<a href="/author/4362633" role="option"><aria>作者：</aria>志鸟村<aria>级别：</aria>
                        StringKtUtil.getDataFromContentByRegex(current, "role=\"option\"><aria>作者：</aria>([^\n^<]+)<aria>", listOf(1))?.apply {
                            it.authorName = this[0]
                        }
                    } ?: bookInfo.takeIf { it.bookName == null }?.let {
                        //<h2 class="book-title">大医凌然</h2>
                        StringKtUtil.getDataFromContentByRegex(current, "<h2 class=\"book-title\">([^\n^<]+)</h2>", listOf(1), true)?.apply {
                            it.bookName = this[0]
                        }
                    } ?: bookInfo.takeIf { it.score == null }?.let {
                        //<span class="gray">9分/618人评过</span>
                        StringKtUtil.getDataFromContentByRegex(current, "<span class=\"gray\">([0-9.]+)分/([0-9]+)人评过</span>", listOf(1, 2), true)?.apply {
                            it.score = this[0]
                            it.scoreNum = this[1]
                        }
                    }
                    // end resolving
                    if (bookInfo.lastChapter != null && bookInfo.wordsNum != null && bookInfo.authorName != null && bookInfo.bookName != null && bookInfo.score != null) {
                        return@forEach
                    }
                }
                return if (StringKtUtil.compareFilterInfo(scanInfo, bookInfo)) bookInfo else null
            }
        }
    }
}