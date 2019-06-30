package com.lin.read.filter.scan

import android.os.Handler
import com.lin.read.utils.Constants

abstract class ReadGetBookInfoFactory {
    abstract fun getBookInfo(handler: Handler, searchInfo: ScanInfo):List<Any>?

    companion object {
        fun getInstance(type: String): ReadGetBookInfoFactory? {
            return when (type) {
                Constants.WEB_YOU_SHU -> ReadGetYouShuBookInfoFactory()
                Constants.WEB_QIDIAN,Constants.WEB_QIDIAN_FINISH -> ReadGetQiDianBookInfoFactory()
                else -> null
            }
        }
    }
}