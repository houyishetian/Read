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
            return Pattern.compile("<h4><a href=\"([\\S^\"]+)\"").matcher(data).let {
                it.takeIf { it.find() }?.group(1)
            }
        }

        fun getCurrentAndMaxPageForQiDian(data: String): List<String>? {
            //		data-page="2" data-pageMax="5"></div>
            //       id="page-container" data-pageMax="5" data-page="1"
            val result = mutableListOf<String>()
            return Pattern.compile("data-page=\"(\\d+)\" data-pageMax=\"(\\d+)\"").matcher(data).let {
                it.takeIf { it.find() }?.run {
                    result.add(group(1))
                    result.add(group(2))
                    result
                }
            } ?: Pattern.compile("data-pageMax=\"(\\d+)\" data-page=\"(\\d+)\"").matcher(data).let {
                it.takeIf { it.find() }?.run {
                    result.add(group(2))
                    result.add(group(1))
                    result
                }
            }
        }
    }
}