package com.lin.read.utils

import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.ScanInfo
import java.lang.Exception

class StringKtUtil {
    companion object {
        @JvmOverloads
        fun getRequestUrlByScanInfo(scanInfo: ScanInfo, page: Int = 1, pageKey: String = "page"): String? {
            if (scanInfo.mainUrl == null) {
                return null;
            }
            val path = String.format(scanInfo.mainUrl, scanInfo.rolePathValue) + "?"
            var params = StringBuffer()
            scanInfo.roleParamPairs?.forEach {
                params.append(it).append("&")
            }
            params.append(pageKey).append("=").append(page)
            return path + params.toString()
        }

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
                    when (it?.inputType) {
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
    }
}