package com.lin.read.filter.scan.qidian

import com.lin.read.filter.scan.BookLinkInfo
import java.io.BufferedReader
import java.io.Reader

class QiDianResolveUtil {
    companion object {
        fun getMaxPageAndBookInfoFromRandPage(reader: Reader, needMaxPage: Boolean = false): MutableList<Any> {
            val bufferedReader = BufferedReader(reader)
            var current: String? = bufferedReader.readLine()
            var alreadyGetMaxPage = false
            var position = 0
            var currentPage = 0
            var maxPage = 0
            val result = mutableListOf<Any>()
            while (current != null) {
                val bookLink = QiDianRegexUtils.getBookUrlFromRankPage(current)
                if (bookLink != null) {
                    val bookLinkInfo = BookLinkInfo(bookLink, null, position)
                    result.add(bookLinkInfo)
                    position++
                } else {
                    if (!alreadyGetMaxPage && current.contains("page-container")) {
                        val currentAndMaxPage = QiDianRegexUtils.getCurrentAndMaxPage(current)
                        if (currentAndMaxPage != null && currentAndMaxPage.isNotEmpty()) {
                            currentPage = currentAndMaxPage[0].toInt()
                            maxPage = currentAndMaxPage[1].toInt()
                            alreadyGetMaxPage = true
                        }
                    }
                }
                result.forEach { (it as BookLinkInfo).page = currentPage }
                current = bufferedReader.readLine()
            }
            if (needMaxPage) result.add(0, maxPage)
            bufferedReader.close()
            reader.close()
            return result
        }

    }
}