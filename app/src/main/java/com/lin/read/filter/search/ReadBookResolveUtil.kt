package com.lin.read.filter.search

import com.lin.read.filter.ReadBookBean
import com.lin.read.utils.Constants
import com.lin.read.utils.StringKtUtil
import com.lin.read.utils.baseUrl
import com.lin.read.utils.readLinesOfHtml
import okhttp3.ResponseBody

class ReadBookResolveUtil {
    companion object {
        fun getChapterContentFromBIQUGE(bookChapterInfo: BookChapterInfo,responseBody: ResponseBody): BookChapterContent {
            responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.contains("\"content\"") }?.let {
                    //<div id="content">
                    StringKtUtil.getDataFromContentByRegex(it, "<div id=\"content\">([^\n]+)", listOf(1))?.let {
                        return BookChapterContent(it[0])
                    }
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromDINGDIAN(bookChapterInfo: BookChapterInfo,responseBody: ResponseBody): BookChapterContent {
            var lastNonEmptyLine = ""
            responseBody.readLinesOfHtml().forEach {
                if (lastNonEmptyLine == "<div class=\"read_share\"><script>show_share();</script></div>") {
                    //<div class="read_share"><script>show_share();</script></div>
                    StringKtUtil.getDataFromContentByRegex(it, "<dd id=\"contents\">([^\n]+)</dd>", listOf(1))?.let {
                        return BookChapterContent(it[0])
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromBIXIA(bookChapterInfo: BookChapterInfo,responseBody: ResponseBody): BookChapterContent {
            var start = false
            responseBody.readLinesOfHtml().forEach {
                //<div id="zjneirong">
                //章节
                it.trim().takeIf { !start && it == "<div id=\"zjneirong\">" }?.let {
                    start = true
                    return@forEach
                }
                it.trim().takeIf { start }?.let {
                    return BookChapterContent(it)
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromAISHUWANG(bookChapterInfo: BookChapterInfo,responseBody: ResponseBody): BookChapterContent {
            responseBody.readLinesOfHtml().forEach {
                //<div id="chapter_content">
                it.trim().takeIf { it.startsWith("<div id=\"chapter_content\">") }?.let {
                    return BookChapterContent(it.replace("<div id=\"chapter_content\">", "").replace("<script language=\"javascript\">setFontSize();</script>", ""))
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromQINGKAN(bookChapterInfo: BookChapterInfo, responseBody: ResponseBody): BookChapterContent {
            var lastNonEmptyLine = ""
            val result = BookChapterContent("")
            responseBody.readLinesOfHtml().forEach {
                if (lastNonEmptyLine == "<div id=\"readbox\">") {
                    //"<div id=\"content\"><div id=\"txtright\"><script src=\""+domain+"/file/script/9.js\"></script></div><!--go-->"
                    result.content = it.replace("<div id=\"content\"><div id=\"txtright\"><script src=\"${bookChapterInfo.chapterLink.baseUrl()}file/script/9.js\"></script></div><!--go-->", "")
                }
                if (lastNonEmptyLine.matches("<a class=\"current\"><b>\\d+</b></a>".toRegex())) {
                    StringKtUtil.getDataFromContentByRegex(it.trim(), "<a href=\"([^\n]+)\">(\\d)+</a>", listOf(1, 2))?.let {
                        result.isComplete = false
                        result.nextLink = it[0]
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result.apply {
                val pendingRemovedStr = "<br />"
                if (!isComplete && content.endsWith(pendingRemovedStr)) {
                    while (content.endsWith(pendingRemovedStr)) {
                        content = content.substring(0, content.length - pendingRemovedStr.length)
                    }
                }
            }
        }

        fun getChapterContentFromSANQI(bookChapterInfo: BookChapterInfo, responseBody: ResponseBody): BookChapterContent {
            var start = false
            var resultContent: String? = null
            responseBody.readLinesOfHtml().forEach {
                val current = it.trim()
                //<div id="neirong">
                if (!start && current.startsWith("<div id=\"neirong\">")) {
                    start = true
                    resultContent = current.replace("<div id=\"neirong\">", "")
                    return@forEach
                }
                if (start) {
                    if (!current.endsWith("</div>")) {
                        resultContent += current
                    } else {
                        resultContent += current.replace("</div>", "")
                        return BookChapterContent(resultContent ?: "")
                    }
                }
            }
            return BookChapterContent("")
        }

        fun getChapterListFromBIQUGE(readBookBean: ReadBookBean, responseBody: ResponseBody): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            var startResolving = false
            val result = mutableListOf<BookChapterInfo>()
            responseBody.readLinesOfHtml().forEach {
                if (!startResolving && lastNonEmptyLine == "<dt>《${readBookBean.bookName}》正文</dt>") {
                    startResolving = true
                }
                if (startResolving && lastNonEmptyLine == "</dl>") {
                    return result
                }
                if (startResolving) {
                    //<dd><a href="http://www.biquge5200.com/51_51647/19645389.html">第一章 十六年</a></dd>
                    StringKtUtil.getDataFromContentByRegex(it, "<dd><a href=\"([^\"^\n]+)\">([^\"^\n]+)</a></dd>", listOf(1, 2))?.let {
                        result.add(BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!,
                                it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result
        }

        fun getChapterListFromDINGDIAN(readBookBean: ReadBookBean, responseBody: ResponseBody): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.startsWith("<dd><h3>") }?.let {
                    //<div class="adlist"><script>show_list();</script></div>
                    if (lastNonEmptyLine == "<div class=\"adlist\"><script>show_list();</script></div>") {
                        //<td class="L"><a href=
                        StringKtUtil.getDataListFromContentByRegex(it, "<td class=\"L\"><a href=\"([^\"^\n]+)\">([^\"^\n]+)</a></td>", listOf(1, 2))?.let {
                            return mutableListOf<BookChapterInfo>().apply {
                                it.forEach {
                                    add(BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!,
                                            readBookBean.chapterLink + it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                                }
                            }
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return listOf()
        }

        fun getChapterListFromBIXIA(readBookBean: ReadBookBean, responseBody: ResponseBody): List<BookChapterInfo> {
            responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.startsWith("<dd><h3>") }?.let {
                    ////<dd><a href="http://www.bxwx666.org/txt/267294/1215770.htm">第五章 观思神</a></dd>
                    StringKtUtil.getDataListFromContentByRegex(it, "<td class=\"L\"><a href=\"([^\"^\n]+)\">([^\"^\n]+)</a></td>", listOf(1, 2))?.let {
                        return mutableListOf<BookChapterInfo>().apply {
                            it.forEach {
                                add(BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!,
                                        it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                            }
                        }
                    }
                }
            }
            return listOf()
        }

        fun getChapterListFromAISHUWANG(readBookBean: ReadBookBean, responseBody: ResponseBody): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            responseBody.readLinesOfHtml().forEach {
                if (lastNonEmptyLine == "<div class=\"neirong\">") {
                    //<div class="clc"><a href="/xs/180445/20519569/">第一章 黄山真君和九洲一号群</a></div>
                    StringKtUtil.getDataListFromContentByRegex(it, "<div class=\"clc\"><a href=\"([^\"^\n]+)\">([^\"^\n]+)</a></div>", listOf(1, 2))?.let {
                        return mutableListOf<BookChapterInfo>().apply {
                            it.forEach {
                                add(BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!,
                                        StringKtUtil.parseLineForIncompletedLinks(Constants.SEARCH_WEB_BASEURL_MAP[readBookBean.webType]!!, it[0]),
                                        StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                            }
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return listOf()
        }

        fun getChapterListFromQINGKAN(readBookBean: ReadBookBean, responseBody: ResponseBody): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            var startResolving = false
            val result = mutableListOf<BookChapterInfo>()
            responseBody.readLinesOfHtml().forEach {
                //<DIV id="Chapters">仙逆长生[章节列表]
                if (!startResolving && lastNonEmptyLine == "<DIV id=\"Chapters\">${readBookBean.bookName}[章节列表]") {
                    startResolving = true
                }
                if (startResolving && lastNonEmptyLine == "</DIV>") {
                    return result
                }
                if (startResolving) {
                    //<UL>	<li><a href="https://www.qk6.org/book/xiannichangsheng/71633199.html">第19章 神乌魂印</a></li>
                    StringKtUtil.getDataFromContentByRegex(it.trim(), "<li><a href=\"([^\"^\n]+)\">([^\"^\n]+)</a></li>", listOf(1, 2))?.let {
                        result.add(BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!,
                                it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result
        }

        fun getChapterListFromSANQI(readBookBean: ReadBookBean, responseBody: ResponseBody): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            responseBody.readLinesOfHtml().forEach {
                if(lastNonEmptyLine == "<div class=\"liebiao_bottom\">"){
                    //<dd><a href="/html/65/65143/17223065.html">仙界篇外传一</a></dd>
                    StringKtUtil.getDataListFromContentByRegex(it, "<dd><a href=\"([^\"^\n]+)\">([^\"^\n]+)</a></dd>", listOf(1, 2))?.let {
                        return mutableListOf<BookChapterInfo>().apply {
                            it.forEach {
                                add(BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!,
                                        StringKtUtil.parseLineForIncompletedLinks(Constants.SEARCH_WEB_BASEURL_MAP[readBookBean.webType]!!, it[0]),
                                        StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                            }
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return listOf()
        }
    }
}