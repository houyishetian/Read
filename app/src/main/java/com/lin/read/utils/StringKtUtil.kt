package com.lin.read.utils

import android.text.format.DateFormat
import com.lin.read.filter.ScanBookBean
import com.lin.read.filter.scan.ScanInfo
import java.util.*
import java.util.regex.Pattern

class StringKtUtil {
    companion object {
        /**
         * @param scanInfo standard value
         * @param scanBookBean pending for compare
         * @return true-the book is ok, false-the book is not ok
         */
        fun compareFilterInfo(scanInfo: ScanInfo, scanBookBean: ScanBookBean): Boolean = scanInfo.inputtedBeans?.takeIf { it.isNotEmpty() }?.let {
            loop@ for (item in it) {
                val thisInfo = ReflectUtil.getProperty(scanBookBean, item.key, String::class.java)
                when (item.inputType) {
                    Constants.INPUT_INT, Constants.INPUT_FLOAT -> {
                        val valueStandard = item.value?.takeIf { it.toString().isNotEmpty() }?.toFloat() ?: -1f
                        val valuePendingCompare = thisInfo?.takeIf { it.isNotEmpty() }?.toFloat() ?: -1f
                        if (valueStandard < 0 || valuePendingCompare < 0 || valueStandard > valuePendingCompare) return false
                    }
                    else -> continue@loop
                }
            }
            true
        } ?: true

        //<h4><a href="//book.qidian.com/info/1003438608"
        fun getBookLinkFromRankPageForQiDian(data: String): String? = getDataFromContentByRegex(data, "<h4><a href=\"([\\S^\"]+)\"", listOf(1))?.get(0)

        /**
         * get current page num and max page num for qidian
         * @param data page data
         * @return position 0 -> current page; position 1 -> max page num. null-> not found
         * data-page="2" data-pageMax="5"></div>
         * id="page-container" data-pageMax="5" data-page="1"
         */
        fun getCurrentAndMaxPageForQiDian(data: String): List<String>? = getDataFromContentByRegex(data, "data-page=\"(\\d+)\" data-pageMax=\"(\\d+)\"", listOf(1, 2))
                ?: getDataFromContentByRegex(data, "data-pageMax=\"(\\d+)\" data-page=\"(\\d+)\"", listOf(2, 1))

        /**
         * remove the unuseful chars from chapter
         * remove the unuseful infos from chapter:1212 测试章节(第五更) -> 1212 测试章节
         */
        fun removeUnusefulCharsFromChapter(chapterName: String): String =replaceDataOfContentByRegex(chapterName, "[\\(\\[（【第]+[0-9一二三四五六七八九十零终末上中下]+[\\)\\]）】更]+")

        /**
         * get the related data from content by regex
         * @param content the content
         * @param regex the regex
         * @param groups the groups
         * @param useMatch true-match the total contant; false-find from the total contant
         * @return null-not find
         */
        @JvmOverloads
        fun getDataFromContentByRegex(content: String, regex: String, groups: List<Int>, useMatch: Boolean = false): List<String>? {
            Pattern.compile(regex).matcher(content).let { matcher ->
                if (if (useMatch) matcher.matches() else matcher.find()) {
                    return mutableListOf<String>().apply {
                        groups.forEach {
                            add(matcher.group(it))
                        }
                    }
                }
            }
            return null
        }

        /**
         * get the related data list from content by regex, such as get chapter list from content
         * @param content the content
         * @param regex the regex
         * @param groups the groups
         * @return if didn't find, will return null
         */
        fun getDataListFromContentByRegex(content: String, regex: String, groups: List<Int>): List<List<String>>? {
            Pattern.compile(regex).matcher(content).let { matcher ->
                var result: MutableList<List<String>>? = null
                while (matcher.find()) {
                    if (result == null) result = mutableListOf()
                    result.add(mutableListOf<String>().apply {
                        groups.forEach {
                            add(matcher.group(it))
                        }
                    })
                }
                return result
            }
        }

        /**
         * replace the regex part with new value
         * @param content the content
         * @param regex the regex
         * @param newValue the new value
         * @return the new data after replace
         */
        @JvmOverloads
        fun replaceDataOfContentByRegex(content: String, regex: String, newValue: String = ""): String = content.replace(regex.toRegex(), newValue)

        fun parseLinkForAiShuWang(url: String): String = Constants.SEARCH_WEB_BASEURL_MAP[Constants.RESOLVE_FROM_AISHU]?.let {
            if (url.startsWith("//"))
                return "http:$url"
            if (url.startsWith("/"))
                return it + url.substring(1)
            return url
        } ?: url

        fun formatTime(time: Long): String = DateFormat.format("yyyy-MM-dd HH:mm", Date(time)).toString()

        fun removeSeconds(data: String): String = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}):\\d{2}").matcher(data).takeIf { it.matches() }?.group(1)
                ?: data

        fun removeAdsFromContent(content: String): String = content.let {
            var result = it
            listOf(
                    "顶点小说 Ｘ２３ＵＳ．ＣＯＭ更新最快"
            ).forEach {
                result = result.replace(it, "")
            }
            result
        }
    }
}