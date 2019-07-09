package com.lin.read.filter.scan.qidian

import com.lin.read.filter.scan.BookLinkInfo
import com.lin.read.utils.StringKtUtil
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
                val bookLink = StringKtUtil.getBookLinkFromRankPageForQiDian(current)
                if (bookLink != null) {
                    val bookLinkInfo = BookLinkInfo(bookLink, null, position)
                    result.add(bookLinkInfo)
                    position++
                } else {
                    current.takeIf { !alreadyGetMaxPage && it.contains("page-container") }?.apply {
                        StringKtUtil.getCurrentAndMaxPageForQiDian(this)?.takeIf { it.isNotEmpty() }?.apply {
                            currentPage = this[0].toInt()
                            maxPage = this[1].toInt()
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