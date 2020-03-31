package com.lin.read.filter.search

import com.lin.read.utils.Constants
import com.lin.read.utils.StringKtUtil
import com.lin.read.utils.baseUrl
import com.lin.read.utils.readLinesOfHtml

class ReadBookResolveUtil {
    companion object {
        fun getChapterContentFromBIQUGE(readResolveBean: ReadResolveBean): BookChapterContent {
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.contains("\"content\"") }?.let {
                    //<div id="content">
                    StringKtUtil.getDataFromContentByRegex(it, "<div id=\"content\">([^\n]+)", listOf(1))?.let {
                        return BookChapterContent(it[0])
                    }
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromBIQUGE2(readResolveBean: ReadResolveBean): BookChapterContent {
            var lastNonEmptyLine = ""
            var resultContent = ""
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                it.trim().let {
                    if(it.contains("<div id=\"content\" class=\"showtxt\">")){
                        StringKtUtil.getDataFromContentByRegex(it, "<div id=\"content\" class=\"showtxt\">([^\n]+)", listOf(1))?.let {
                            resultContent = it[0]
                            return@forEach
                        }
                    }
                    resultContent.takeIf { it.isNotEmpty() }?.run {
                        if (it.contains("chaptererror")) {
                            return StringKtUtil.getDataFromContentByRegex(it, "([\\s\\S]+?)(?=\\(${readResolveBean.bookChapterInfo?.chapterLink}\\)<br /><br /><script>chaptererror)", listOf(1))?.let {
                                resultContent += it[0]
                                BookChapterContent(resultContent)
                            } ?: run {
                                resultContent += it
                                BookChapterContent(resultContent)
                            }
                        }
                        resultContent += it
                    }
                    lastNonEmptyLine = it
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromBIQUGE3(readResolveBean: ReadResolveBean): BookChapterContent {
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.contains("\"content\"") }?.let {
                    //<div id="content">
                    StringKtUtil.getDataFromContentByRegex(it, "<div id=\"content\">([^\n]+)", listOf(1))?.let {
                        return BookChapterContent(it[0])
                    }
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromDINGDIAN(readResolveBean: ReadResolveBean): BookChapterContent {
            var lastNonEmptyLine = ""
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                if (lastNonEmptyLine == "<div class=\"adhtml\"><script>show_htm();</script></div>") {
                    //<div class="read_share"><script>show_share();</script></div>
                    StringKtUtil.getDataFromContentByRegex(it, "<dd id=\"contents\">([^\n]+)</dd>", listOf(1))?.let {
                        return BookChapterContent(it[0])
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromBIXIA(readResolveBean: ReadResolveBean): BookChapterContent {
            var start = false
            readResolveBean.responseBody.readLinesOfHtml().forEach {
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

        fun getChapterContentFromAISHUWANG(readResolveBean: ReadResolveBean): BookChapterContent {
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                //<div id="chapter_content">
                it.trim().takeIf { it.startsWith("<div id=\"chapter_content\">") }?.let {
                    return BookChapterContent(it.replace("<div id=\"chapter_content\">", "").replace("<script language=\"javascript\">setFontSize();</script>", ""))
                }
            }
            return BookChapterContent("")
        }

        fun getChapterContentFromQINGKAN(readResolveBean: ReadResolveBean): BookChapterContent {
            var lastNonEmptyLine = ""
            val result = BookChapterContent("")
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                if (lastNonEmptyLine == "<div id=\"readbox\">") {
                    //"<div id=\"content\"><div id=\"txtright\"><script src=\""+domain+"/file/script/9.js\"></script></div><!--go-->"
                    result.content = it.replace("<div id=\"content\"><div id=\"txtright\"><script src=\"${readResolveBean.bookChapterInfo?.chapterLink?.baseUrl()}file/script/9.js\"></script></div><!--go-->", "")
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

        fun getChapterContentFromSANQI(readResolveBean: ReadResolveBean): BookChapterContent {
            var start = false
            var resultContent: String? = null
            readResolveBean.responseBody.readLinesOfHtml().forEach {
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

        fun getChapterListFromBIQUGE(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            var startResolving = false
            val result = mutableListOf<BookChapterInfo>()
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                if (!startResolving && lastNonEmptyLine == "<dt>《${readResolveBean.readBookBean?.bookName}》正文</dt>") {
                    startResolving = true
                }
                if (startResolving && lastNonEmptyLine == "</dl>") {
                    return result
                }
                if (startResolving) {
                    //<dd><a href="http://www.biquge5200.com/51_51647/19645389.html">第一章 十六年</a></dd>
                    readResolveBean.readBookBean?.run {
                        StringKtUtil.getDataFromContentByRegex(it, "<dd><a href=\"([^\"]+)\">([\\s\\S]+?)</a></dd>", listOf(1, 2))?.let {
                            result.add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                    it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result
        }

        fun getChapterListFromBIQUGE2(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            var startResolving = false
            val result = mutableListOf<BookChapterInfo>()
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                if (!startResolving && lastNonEmptyLine == "<dt>《${readResolveBean.readBookBean?.bookName}》正文卷</dt>") {
                    startResolving = true
                }
                if (startResolving && lastNonEmptyLine == "</dl>") {
                    return result
                }
                if (startResolving) {
                    //<dd><a href ="/book_64640/20467850.html">超维术士</a></dd>
                    readResolveBean.readBookBean?.run {
                        StringKtUtil.getDataFromContentByRegex(it, "<dd><a href\\s*=\"([^\"]+)\">([\\s\\S]+?)(?=</a></dd>)", listOf(1, 2))?.let {
                            result.add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                    "https://www.biqugex.com" + it[0],
                                    StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result
        }

        fun getChapterListFromBIQUGE3(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            val result = mutableListOf<BookChapterInfo>()
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                //<dt>《超维术士》正文</dt>
                if (lastNonEmptyLine == "<dt>《${readResolveBean.readBookBean?.bookName}》正文</dt>") {
                    readResolveBean.readBookBean?.run {
                        //<dd><a href="/76_76069/1332439.html">第2097节 疑问</a></dd>
                        StringKtUtil.getDataListFromContentByRegex(it, "<dd><a href=\"([^\"]+)\">([\\s\\S]+?)</a></dd>", listOf(1, 2))?.let {
                            return mutableListOf<BookChapterInfo>().apply {
                                it.forEach {
                                    add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                            StringKtUtil.parseLineForIncompletedLinks(Constants.SEARCH_WEB_BASEURL_MAP[webType]!!, it[0]),
                                            StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                                }
                            }
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result
        }

        fun getChapterListFromDINGDIAN(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.startsWith("<dd><h3>") }?.let {
                    //<div class="adlist"><script>show_list();</script></div>
                    if (lastNonEmptyLine == "<div class=\"adlist\"><script>show_list();</script></div>") {
                        readResolveBean.readBookBean?.run {
                            //<td class="L"><a href=
                            StringKtUtil.getDataListFromContentByRegex(it, "<td class=\"L\"><a href=\"([^\"]+)\">([\\s\\S]+?)</a></td>", listOf(1, 2))?.let {
                                return mutableListOf<BookChapterInfo>().apply {
                                    it.forEach {
                                        add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                                chapterLink.takeIf { it.endsWith("index.html") }?.replace("index.html", "") + it[0],
                                                StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                                    }
                                }
                            }
                        }

                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return listOf()
        }

        fun getChapterListFromBIXIA(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                it.trim().takeIf { it.startsWith("<dd><h3>") }?.let {
                    readResolveBean.readBookBean?.run {
                        ////<dd><a href="http://www.bxwx666.org/txt/267294/1215770.htm">第五章 观思神</a></dd>
                        StringKtUtil.getDataListFromContentByRegex(it, "<td class=\"L\"><a href=\"([^\"]+)\">([\\s\\S]+?)</a></td>", listOf(1, 2))?.let {
                            return mutableListOf<BookChapterInfo>().apply {
                                it.forEach {
                                    add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                            it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                                }
                            }
                        }
                    }

                }
            }
            return listOf()
        }

        fun getChapterListFromAISHUWANG(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                if (lastNonEmptyLine == "<div class=\"neirong\">") {
                    readResolveBean.readBookBean?.run {
                        //<div class="clc"><a href="/xs/180445/20519569/">第一章 黄山真君和九洲一号群</a></div>
                        StringKtUtil.getDataListFromContentByRegex(it, "<div class=\"clc\"><a href=\"([^\"]+)\">([\\s\\S]+?)</a></div>", listOf(1, 2))?.let {
                            return mutableListOf<BookChapterInfo>().apply {
                                it.forEach {
                                    add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                            StringKtUtil.parseLineForIncompletedLinks(Constants.SEARCH_WEB_BASEURL_MAP[webType]!!, it[0]),
                                            StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                                }
                            }
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return listOf()
        }

        fun getChapterListFromQINGKAN(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            var startResolving = false
            val result = mutableListOf<BookChapterInfo>()
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                //<DIV id="Chapters">仙逆长生[章节列表]
                if (!startResolving && lastNonEmptyLine == "<DIV id=\"Chapters\">${readResolveBean.readBookBean?.bookName}[章节列表]") {
                    startResolving = true
                }
                if (startResolving && lastNonEmptyLine == "</DIV>") {
                    return result
                }
                if (startResolving) {
                    readResolveBean.readBookBean?.run {
                        //<UL>	<li><a href="https://www.qk6.org/book/xiannichangsheng/71633199.html">第19章 神乌魂印</a></li>
                        StringKtUtil.getDataFromContentByRegex(it.trim(), "<li><a href=\"([^\"]+)\">([\\s\\S]+?)</a></li>", listOf(1, 2))?.let {
                            result.add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                    it[0], StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return result
        }

        fun getChapterListFromSANQI(readResolveBean: ReadResolveBean): List<BookChapterInfo> {
            var lastNonEmptyLine = ""
            readResolveBean.responseBody.readLinesOfHtml().forEach {
                if(lastNonEmptyLine == "<div class=\"liebiao_bottom\">"){
                    readResolveBean.readBookBean?.run {
                        //<dd><a href="/html/65/65143/18844843.html">第三百一十二章 雷豆（六一快乐^^）</a></dd>
                        //<dd><a href="/html/65/65143/17223065.html">仙界篇外传一</a></dd>
                        StringKtUtil.getDataListFromContentByRegex(it, "<dd><a href=\"([^\"]+)\">([\\s\\S]+?)(?=</a></dd>)", listOf(1, 2))?.let {
                            return mutableListOf<BookChapterInfo>().apply {
                                it.forEach {
                                    add(BookChapterInfo(webType, Constants.SEARCH_WEB_NAME_MAP[webType]!!,
                                            StringKtUtil.parseLineForIncompletedLinks(Constants.SEARCH_WEB_BASEURL_MAP[webType]!!, it[0]),
                                            StringKtUtil.removeUnusefulCharsFromChapter(it[1]), it[1]))
                                }
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