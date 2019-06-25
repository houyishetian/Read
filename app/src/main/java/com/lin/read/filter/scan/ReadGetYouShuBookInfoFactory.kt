package com.lin.read.filter.scan

import android.os.Handler
import android.util.Log
import com.lin.read.filter.scan.youshu.YouShuHttpUtil
import com.lin.read.utils.ConcurrentExecutorUtil
import com.lin.read.utils.MessageUtils
import java.lang.Exception
import java.util.concurrent.Callable

class ReadGetYouShuBookInfoFactory : ReadGetBookInfoFactory() {
    val tag = ReadGetYouShuBookInfoFactory::class.java.simpleName
    override fun getBookInfo(handler: Handler, searchInfo: SearchInfo): List<Any>? {
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_START)
        Log.e(tag, "scan start")
        try {
            return ConcurrentExecutorUtil.execute(listOf(Callable<MutableList<Any>> {
                YouShuHttpUtil.getAllBookInfo(searchInfo)
            }))[0]
        }catch (e:Exception){
            e.printStackTrace()
            throw e
        }
    }
}