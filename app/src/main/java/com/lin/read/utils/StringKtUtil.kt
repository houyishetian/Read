package com.lin.read.utils

import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.ScanInfo
import java.util.regex.Pattern

class StringKtUtil {
    companion object {
        /**
         * @param scanInfo standard value
         * @param bookInfo pending for compare
         * @return true-the book is ok, false-the book is not ok
         */
        fun compareFilterInfo(scanInfo: ScanInfo, bookInfo: BookInfo): Boolean {
            try {
                if(scanInfo.inputtedBeans == null || scanInfo.inputtedBeans.isEmpty()){
                    return true
                }
                loop@ for (it in scanInfo.inputtedBeans) {
                    val thisInfo = ReflectUtil.getProperty(bookInfo, it.key, String::class.java)
                    when (it.inputType) {
                        Constants.INPUT_INT, Constants.INPUT_FLOAT -> {
                            val valueStandard = it.value!!.toFloat()
                            val valuePendingCompare = thisInfo!!.toFloat()
                            if (valueStandard > valuePendingCompare) return false
                        }
                        else -> continue@loop
                    }
                }
                return true
            }catch (e:Exception){
                e.printStackTrace()
            }
            return false
        }

        fun getBookLinkFromRankPageForQiDian(data: String): String? {
            //<h4><a href="//book.qidian.com/info/1003438608"
            return getDataFromContentByRegex(data, "<h4><a href=\"([\\S^\"]+)\"", listOf(1))?.get(0)
        }

        /**
         * get current page num and max page num for qidian
         * @param data page data
         * @return position 0 -> current page; position 1 -> max page num. null-> not found
         */
        fun getCurrentAndMaxPageForQiDian(data: String): List<String>? {
            //		data-page="2" data-pageMax="5"></div>
            //       id="page-container" data-pageMax="5" data-page="1"
            return getDataFromContentByRegex(data, "data-page=\"(\\d+)\" data-pageMax=\"(\\d+)\"", listOf(1, 2))
                    ?: getDataFromContentByRegex(data, "data-pageMax=\"(\\d+)\" data-page=\"(\\d+)\"", listOf(2, 1))
        }

        /**
         * remove the unuseful chars from chapter
         */
        fun removeUnusefulCharsFromChapter(chapterName: String): String {
            //remove the unuseful infos from chapter:1212 测试章节(第五更) -> 1212 测试章节
            return replaceDataOfContentByRegex(chapterName, "[\\(\\[（【第]+[0-9一二三四五六七八九十零终末上中下]+[\\)\\]）】更]+")
        }

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
         * @return if didn't find, will return an empty list
         */
        fun getDataListFromContentByRegex(content: String, regex: String, groups: List<Int>): List<List<String>> {
            Pattern.compile(regex).matcher(content).let { matcher ->
                return mutableListOf<List<String>>().apply {
                    while (matcher.find()) {
                        add(mutableListOf<String>().apply {
                            groups.forEach {
                                add(matcher.group(it))
                            }
                        })
                    }
                }
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
        fun replaceDataOfContentByRegex(content: String, regex: String, newValue: String = ""): String {
            return content.replace(regex.toRegex(), newValue)
        }
    }
}