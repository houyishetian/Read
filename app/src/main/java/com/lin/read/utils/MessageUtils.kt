package com.lin.read.utils

import android.os.Handler

class MessageUtils {
    companion object {
        const val SCAN_START = 0
        const val SCAN_BOOK_INFO_END = 1
        const val SCAN_BOOK_INFO_BY_CONDITION_START = 2
        const val SCAN_BOOK_INFO_BY_CONDITION_GET_ONE = 3
        const val SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE = 4

        const val TOTAL_PAGE = "TOTAL_PAGE"
        const val CURRENT_PAGE = "CURRENT_PAGE"

        fun sendWhat(handler: Handler?, what: Int) {
            handler?.sendEmptyMessage(what)
        }

        fun sendMessageOfInteger(handler: Handler?, what: Int, message: Int) {
            handler?.run {
                val msg = obtainMessage()
                msg.what = what
                msg.arg1 = message
                sendMessage(msg)
            }
        }
    }
}