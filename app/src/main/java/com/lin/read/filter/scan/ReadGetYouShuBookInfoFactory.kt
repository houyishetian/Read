package com.lin.read.filter.scan

import android.os.Handler
import android.util.Log
import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.youshu.YouShuHttpUtil
import com.lin.read.utils.ConcurrentExecutorUtil
import com.lin.read.utils.MessageUtils
import java.util.ArrayList
import java.util.concurrent.Callable

class ReadGetYouShuBookInfoFactory : ReadGetBookInfoFactory() {
    val tag = ReadGetYouShuBookInfoFactory::class.java.simpleName
    override fun getBookInfo(handler: Handler, searchInfo: SearchInfo): List<Any> {
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_START)
        Log.e(tag, "scan start")
        val result = ConcurrentExecutorUtil.execute(listOf(Callable<MutableList<Any>> {
            YouShuHttpUtil.getAllBookInfo(searchInfo)
        }))[0]
        val count = (result[0] as String).toInt()
        val temp = mutableListOf<Any>()
        result.map { temp.add(it)}
        result.removeAt(0)
        return temp
    }
}