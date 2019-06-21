package com.lin.read.utils

import android.os.Bundle
import android.os.Handler
import com.lin.read.filter.BookInfo
import java.util.ArrayList

class MessageUtils {
    companion object {
        const val SCAN_START = 0
        const val SCAN_BOOK_INFO_END = 1
        const val SCAN_BOOK_INFO_BY_CONDITION_START = 2
        const val SCAN_BOOK_INFO_BY_CONDITION_GET_ONE = 3
        const val SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE = 4
        const val SCAN_BOOK_INFO_BY_CONDITION_END = 5
        const val SCAN_YOUSHU_END = 6

        const val BOOK_LIST = "BOOK_LIST"
        const val TOTAL_PAGE = "TOTAL_PAGE"
        const val CURRENT_PAGE = "CURRENT_PAGE"

        fun sendWhat(handler: Handler?, what: Int) {
            handler?.sendEmptyMessage(what)
        }

        fun sendMessageOfInteger(handler: Handler?, what: Int, message: Int) {
            if (handler != null) {
                val msg = handler.obtainMessage()
                msg.what = what
                msg.arg1 = message
                handler.sendMessage(msg)
            }
        }


        fun sendMessageOfArrayList(handler: Handler?, what: Int, allData: ArrayList<BookInfo>,count:Int = -1) {
            if (handler != null) {
                val msg = handler.obtainMessage()
                msg.what = what
                if(count != -1) msg.arg1 = count
                val bundle = Bundle()
                bundle.putParcelableArrayList(BOOK_LIST, allData)
                msg.data = bundle
                handler.sendMessage(msg)
            }
        }
    }
}