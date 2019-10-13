package com.lin.read.filter.search

import com.lin.read.filter.SearchBookBean
import com.lin.read.utils.Constants
import com.lin.read.utils.StringKtUtil
import com.lin.read.utils.readLinesOfHtml

class SearchBookResolveUtil {
    companion object {
        fun resolveFromBIQUGE(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            val result = mutableListOf<SearchBookBean>()
            var bookInfo: SearchBookBean? = null
            searchResolveBean.responseBody.readLinesOfHtml().forEach {
                val current = it.trim()
                if (current == "<tr>") {
                    bookInfo = SearchBookBean(Constants.RESOLVE_FROM_BIQUGE)
                    return@forEach
                }
                bookInfo?.let {
                    it.takeIf { it.bookName.isEmpty() }?.let {
                        //<td class="odd"><a href="https://www.biquge5200.cc/80_80767/">凌仙神帝</a></td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\"><a href=\"([^\"]+)\">([\\s\\S]+?)(?=</a></td>)", listOf(1, 2))?.run {
                            it.chapterLink = this[0]
                            it.bookName = this[1]
                        }
                    } ?: it.takeIf { it.lastChapter.isEmpty() }?.let {
                        //<td class="even"><a href="https://www.biquge5200.cc/80_80767/154713700.html" target="_blank"> 第六百一十七章</a></td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"even\"><a href=\"([^\"]+)\" target=\"_blank\">([\\s\\S]+?)(?=</a></td>)", listOf(2))?.run {
                            it.lastChapter = StringKtUtil.removeUnusefulCharsFromChapter(this[0])
                        }
                    } ?: it.takeIf { it.authorName.isEmpty() }?.let {
                        //<td class="odd">逆仙龙</td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\">([\\s\\S]+?)(?=</td>)", listOf(1))?.run {
                            it.authorName = this[0]
                        }
                    } ?: it.takeIf { it.lastUpdate.isEmpty() }?.let {
                        //<td class="odd" align="center">2018-06-20</td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\" align=\"center\">([\\s\\S]+?)(?=</td>)", listOf(1))?.run {
                            it.lastUpdate = this[0]
                            result.add(it)
                        }
                    } ?: takeIf { current == "</tr>" }?.run {
                        bookInfo = null
                    }
                }
            }
            return if (result.isEmpty()) null else result
        }

        fun resolveFromBIQUGE2(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            val result = mutableListOf<SearchBookBean>()
            var bookInfo: SearchBookBean? = null
            searchResolveBean.responseBody.readLinesOfHtml().forEach {
                val current = it.trim()
                //<span class="s1">1</span>
                if (StringKtUtil.matchesRegex(current,"<span\\s+class=\"s1\">\\d+</span>")) {
                    bookInfo = SearchBookBean(Constants.RESOLVE_FROM_BIQUGE2)
                    return@forEach
                }
                bookInfo?.let {
                    it.takeIf { it.bookName.isEmpty() }?.let {
                        //<span class="s2"><a href="http://www.biqugex.com/book/goto/id/64640" target="_blank">超维术士</a></span>
                        StringKtUtil.getDataFromContentByRegex(current, "<span\\s+class=\"s2\"><a href=\"([^\"]+)\"\\s+target=\"_blank\">([\\s\\S]+?)(?=</a></span>)", listOf(1, 2))?.run {
                            it.chapterLink = this[0]
                            it.bookName = this[1]
                        }
                    } ?: it.takeIf { it.authorName.isEmpty() }?.let {
                        //<span class="s4">牧狐</span>
                        StringKtUtil.getDataFromContentByRegex(current, "<span\\s+class=\"s4\">([\\s\\S]+?)(?=</span>)", listOf(1))?.run {
                            it.authorName = this[0]
                            it.lastChapter = "--"
                            it.lastUpdate = "--"
                            result.add(it)
                        }
                    } ?: takeIf { current == "</li>" }?.run {
                        bookInfo = null
                    }
                }
            }
            return if (result.isEmpty()) null else result
        }

        fun resolveFromDINGDIAN(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            var bookInfo: SearchBookBean? = null
            val result = mutableListOf<SearchBookBean>()
            val content = searchResolveBean.responseBody.readLinesOfHtml()
            content.forEach {
                var current = it.trim()
                if (current == "<tr>") {
                    bookInfo = SearchBookBean(Constants.RESOLVE_FROM_DINGDIAN)
                    return@forEach
                }
                current = current.replace("<b style=\"color:red\">", "").replace("</b>", "")
                bookInfo?.let {
                    it.takeIf { it.bookName.isEmpty() }?.let {
                        //<td class="odd"><a href="https://www.x23us.com/book/352"><b style="color:red">仙逆</b></a></td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\"><a href=\"[^\"]+\">([\\s\\S]+?)(?=</a></td>)", listOf(1))?.run {
                            it.bookName = this[0]
                        }
                    } ?: it.takeIf { it.lastChapter.isEmpty() }?.let {
                        //<td class="even"><a href="https://www.x23us.com/html/0/352/" target="_blank"> 后记</a></td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"even\"><a href=\"([^\"]+)\" target=\"_blank\">([\\s\\S]+?)(?=</a></td>)", listOf(1, 2))?.run {
                            it.chapterLink = this[0]
                            it.lastChapter = StringKtUtil.removeUnusefulCharsFromChapter(this[1])
                        }
                    } ?: it.takeIf { it.authorName.isEmpty() }?.let {
                        //<td class="odd">耳根</td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\">([\\s\\S]+?)(?=</td>)", listOf(1))?.run {
                            it.authorName = this[0]
                        }
                    } ?: it.takeIf { it.lastUpdate.isEmpty() }?.let {
                        //<td class="odd" align="center">14-11-07</td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\" align=\"center\">([\\s\\S]+?)(?=</td>)", listOf(1))?.run {
                            it.lastUpdate = this[0]
                            result.add(it)
                        }
                    } ?: takeIf { current == "</tr>" }?.apply {
                        bookInfo = null
                    }
                }
            }
            result.takeIf { it.isEmpty() }?.let {
                bookInfo = SearchBookBean(Constants.RESOLVE_FROM_DINGDIAN)
                bookInfo?.run {
                    bookName = searchResolveBean.bookName
                }
                content.forEach {
                    val current = it.trim()
                    bookInfo?.let {
                        it.takeIf { it.authorName.isEmpty() && current.contains("</td><th>") }?.let {
                            //98<th>文章作者</th><td>&nbsp;忘语</td><th>文章状态</th><td>&nbsp;已完成</td></tr>
                            StringKtUtil.getDataFromContentByRegex(current, "<th>文章作者</th><td>&nbsp;([\\s\\S]+?)(?=</td>)", listOf(1))?.run {
                                it.authorName = this[0]
                            }
                        }
                                ?: it.takeIf { it.authorName.isNotEmpty() && it.lastUpdate.isEmpty() && current.contains("</td><th>") }?.let {
                                    //99<th>最后更新</th><td>&nbsp;2018-03-31</td></tr>
                                    StringKtUtil.getDataFromContentByRegex(current, "<th>最后更新</th><td>&nbsp;([\\s\\S]+?)(?=</td>)", listOf(1))?.run {
                                        it.lastUpdate = this[0]
                                    }
                                } ?: it.takeIf { it.authorName.isNotEmpty() && it.lastUpdate.isNotEmpty() }?.let {
                                    //137最新章节：<a href="https://www.x23us.com/html/45/45945/">关于小凡人完结和新书</a></p>
                                    StringKtUtil.getDataFromContentByRegex(current, "最新章节：<a href=\"([^\n]+)\">([\\s\\S]+?)(?=</a></p>)", listOf(1, 2))?.run {
                                        it.chapterLink = this[0]
                                        it.lastChapter = StringKtUtil.removeUnusefulCharsFromChapter(this[1])
                                        result.add(it)
                                    }
                                }
                    }
                }
            }
            return if (result.isEmpty()) null else result
        }

        fun resolveFromBIXIA(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            val result = mutableListOf<SearchBookBean>()
            var bookInfo: SearchBookBean? = null
            searchResolveBean.responseBody.readLinesOfHtml().forEach {
                val current = it.trim()
                if (current == "<span class=\"s2\">") {
                    bookInfo = SearchBookBean(Constants.RESOLVE_FROM_BIXIA)
                    return@forEach
                }
                bookInfo?.let {
                    it.takeIf { it.bookName.isEmpty() }?.let {
                        //<a href="http://www.bxwx666.org/txt/251446/" target="_blank">仙逆</a></span>
                        StringKtUtil.getDataFromContentByRegex(current, "<a href=\"([^\"^\n]+)\" target=\"_blank\">([^\"^\n]+)</a></span>", listOf(1, 2), true)?.run {
                            it.chapterLink = this[0]
                            it.bookName = this[1]
                        }
                    } ?: it.takeIf { it.lastChapter.isEmpty() }?.let {
                        //<span class="s3"><a href="http://www.bxwx666.org/txt/251446/1156956.htm" target="_blank">测试章节，不要订阅！！！！</a></span>
                        StringKtUtil.getDataFromContentByRegex(current, "<span class=\"s3\"><a href=\"([^\"^\n]+)\" target=\"_blank\">([^\"^\n]+)</a></span>", listOf(2), true)?.run {
                            it.lastChapter = StringKtUtil.removeUnusefulCharsFromChapter(this[0])
                        }
                    } ?: it.takeIf { it.lastUpdate.isEmpty() }?.let {
                        //<span class="s4">耳根</span><span class="s5">2018/4/18 </span></li>
                        StringKtUtil.getDataFromContentByRegex(current, "<span class=\"s4\">([^\"^\n]+)</span><span class=\"s5\">([^\"^\n]+)</span></li>", listOf(1, 2), true)?.run {
                            it.authorName = this[0]
                            it.lastUpdate = this[1]
                            result.add(it)
                            bookInfo = null
                        }
                    }
                }
            }
            return if (result.isEmpty()) null else result
        }

        fun resolveFromAISHUWANG(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            var bookInfo: SearchBookBean? = null
            val result = mutableListOf<SearchBookBean>()
            searchResolveBean.responseBody.readLinesOfHtml().forEach {
                var current = it.trim()
                if (current == "<ul>") {
                    bookInfo = SearchBookBean(Constants.RESOLVE_FROM_AISHU)
                    return@forEach
                }
                current = current.replace("<b style=\"color:red\">", "").replace("</b>", "")
                bookInfo?.let {
                    it.takeIf { it.bookName.isEmpty() }?.let {
                        //<li class="neirong1"><a href="/xs/125988/">仙逆</a><a href="/xs/125988.txt" title="仙逆TXT下载">TXT下载</a></li>
                        StringKtUtil.getDataFromContentByRegex(current, "<li class=\"neirong1\"><a href=\"([^\"]+)\">([\\s\\S]+?)(</a><a href=\")", listOf(1, 2))?.run {
                            it.chapterLink = StringKtUtil.parseLineForIncompletedLinks(Constants.SEARCH_WEB_BASEURL_MAP[Constants.RESOLVE_FROM_AISHU]!!,this[0])
                            it.bookName = this[1]
                        }
                    } ?: it.takeIf { it.lastChapter.isEmpty() }?.let {
                        //<li class="neirong2"><a href="/xs/125988/6114823/">第一卷 平庸少年 第二十一章 夺灵</a></li>
                        StringKtUtil.getDataFromContentByRegex(current, "<li class=\"neirong2\"><a href=\"([^\"]+)\">([\\s\\S]+?)(?=</a></li>)", listOf(2))?.run {
                            it.lastChapter = StringKtUtil.removeUnusefulCharsFromChapter(this[0])
                        }
                    } ?: it.takeIf { it.authorName.isEmpty() }?.let {
                        //<li class="neirong4"><a href="//www.22ff.org/author/洛山">洛山</a></li>
                        StringKtUtil.getDataFromContentByRegex(current, "<li class=\"neirong4\"><a href=\"([^\"]+)\">([\\s\\S]+?)(?=</a></li>)", listOf(2))?.run {
                            it.authorName = this[0]
                        }
                    } ?: it.takeIf { it.lastUpdate.isEmpty() }?.let {
                        //<li class="neirong3">06-17 13:49</li>
                        StringKtUtil.getDataFromContentByRegex(current, "<li class=\"neirong3\">([\\s\\S]+?)(?=</li>)", listOf(1))?.run {
                            it.lastUpdate = this[0]
                            result.add(it)
                        }
                    } ?: takeIf { current == "</ul>" }?.apply {
                        bookInfo = null
                    }
                }
            }
            return if (result.isEmpty()) null else result
        }

        fun resolveFromQINGKAN(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            var bookInfo: SearchBookBean? = null
            val result = mutableListOf<SearchBookBean>()
            searchResolveBean.responseBody.readLinesOfHtml().forEach {
                val current = it.trim()
                takeIf { bookInfo == null }?.let {
                    //<span class="sp_name"><a class="sp_bookname" href="https://www.qk6.org/book/xiannichangsheng/info.html" target="_blank">仙逆长生</a> / 莺歌</span>
                    StringKtUtil.getDataFromContentByRegex(current, "<span class=\"sp_name\"><a class=\"sp_bookname\" href=\"([^\"]+)\" target=\"_blank\">([\\s\\S]+?)</a>([\\s\\S]+?)</span>",
                            listOf(1, 2, 3))?.run {
                        bookInfo = SearchBookBean(Constants.RESOLVE_FROM_QINGKAN)
                        bookInfo?.let {
                            it.chapterLink = this[0].replace("info.html", "txt.html")
                            it.bookName = this[1]
                            it.authorName = this[2].replace("[ /]".toRegex(), "")
                        }
                    }
                } ?: let {
                    //<a class="sp_chaptername" href="https://www.qk6.org/book/yixiannitianzhilv_shenjun_biepao_/69036030.html" target="_blank">9 6</a>（2018-12-09 12:12:01）</h4>
                    StringKtUtil.getDataFromContentByRegex(current, "<a class=\"sp_chaptername\" href=\"([^\"]+)\" target=\"_blank\">([\\s\\S]+?)</a>([\\s\\S]+?)</h4>", listOf(2, 3))?.run {
                        bookInfo?.let {
                            it.lastChapter = this[0]
                            it.lastUpdate = StringKtUtil.removeSeconds(this[1].replace("[（）]".toRegex(), ""))
                            result.add(it)
                            bookInfo = null
                        }
                    }
                }
            }
            return if (result.isEmpty()) null else result
        }

        fun resolveFromSANQI(searchResolveBean: SearchResolveBean): List<SearchBookBean>? {
            var lastNonEmptyLine = ""
            val result = mutableListOf<SearchBookBean>()
            var searchBookBean:SearchBookBean? = null
            searchResolveBean.responseBody.readLinesOfHtml().forEach {
                val current = it.trim()
                if(lastNonEmptyLine == "<tr>"){
                    searchBookBean = SearchBookBean(Constants.RESOLVE_FROM_SANQI)
                }
                if(current == "</tr>"){
                    searchBookBean = null
                    return@forEach
                }
                searchBookBean?.let {
                    it.takeIf { it.bookName.isEmpty() }?.let {
                        //<td class="odd"><a href="https://www.37shuwu.com/html/65/65143/index.html">凡人修仙之仙界篇</a></td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\"><a href=\"([^\"]+)\">([\\s\\S]+?)</a></td>", listOf(1, 2))?.run {
                            it.chapterLink = this[0]
                            it.bookName = this[1]
                        }
                    } ?: it.takeIf { it.lastChapter.isEmpty() }?.let {
                        //<td class="even"><a href="/html/65/65143/20979788.html" target="_blank"> 第一千零六十八章 出手</a></td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"even\"><a href=\"([^\"]+)\" target=\"_blank\">([\\s\\S]+?)</a></td>", listOf(2))?.run {
                            it.lastChapter = StringKtUtil.removeUnusefulCharsFromChapter(this[0])
                        }
                    } ?: it.takeIf { it.authorName.isEmpty() }?.let {
                        //<td class="odd">忘语</td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\">([\\s\\S]+?)</td>", listOf(1))?.run {
                            it.authorName = this[0]
                        }
                    } ?: it.takeIf { it.lastUpdate.isEmpty() }?.let {
                        //<td class="odd" align="">19-08-17</td>
                        StringKtUtil.getDataFromContentByRegex(current, "<td class=\"odd\" align=\"\">([\\s\\S]+?)</td>", listOf(1))?.run {
                            it.lastUpdate = this[0]
                            result.add(it)
                        }
                    }
                }
                lastNonEmptyLine = if (it.trim().isEmpty()) lastNonEmptyLine else it.trim()
            }
            return if (result.isEmpty()) null else result
        }
    }
}