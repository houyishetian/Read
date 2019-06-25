package com.lin.read.filter.scan

import android.os.Handler
import android.util.Log
import com.lin.read.filter.BookInfo
import com.lin.read.filter.ScanBookBean
import com.lin.read.filter.scan.qidian.QiDianHttpUtils
import com.lin.read.utils.ConcurrentExecutorUtil
import com.lin.read.utils.Constants
import com.lin.read.utils.MessageUtils
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.Callable

class ReadGetQiDianBookInfoFactory : ReadGetBookInfoFactory() {
    val tag = ReadGetQiDianBookInfoFactory::class.java.simpleName
    lateinit var searchInfo: SearchInfo
    lateinit var handler: Handler
    private var maxNumInFirstPage:Int = 0
    override fun getBookInfo(handler: Handler, searchInfo: SearchInfo):List<Any>? {
        this.searchInfo = searchInfo
        this.handler = handler
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_START)
        Log.e(tag, "scan start")
        try {
            val scanBookBeans = mutableListOf<ScanBookBean>()
            val firstPageInfo = ConcurrentExecutorUtil.execute(getBookInfoCallables(searchInfo))
            val maxPage = (firstPageInfo[0][0] as String).toInt()
            firstPageInfo[0].removeAt(0)
            scanBookBeans.addAll(firstPageInfo[0].map { it as ScanBookBean })
            val otherPageInfo = ConcurrentExecutorUtil.execute(getBookInfoCallables(searchInfo, 2, maxPage))
            otherPageInfo.forEach { scanBookBeans.addAll(it.map { it as ScanBookBean }) }
            MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, scanBookBeans.size)
            maxNumInFirstPage = firstPageInfo[0].size
            Log.e(tag, "scan end, pending for filting")

            MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START)
            val filterBookInfo = ConcurrentExecutorUtil.execute(getBookDetailsCallables(searchInfo, scanBookBeans))
            filterBookInfo.forEach { it.webName = Constants.WEB_QIDIAN }
            MessageUtils.sendMessageOfArrayList(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_END, filterBookInfo as ArrayList<BookInfo>)
            Log.e(tag,"totally "+filterBookInfo.size)
            return filterBookInfo
        }catch (e:Exception){
            e.printStackTrace()
            throw e
        }
    }

    private fun getBookInfoCallables(searchInfo: SearchInfo, firstPage: Int = 1, maxPage: Int = 1): MutableList<Callable<MutableList<Any>>> {
        val result = mutableListOf<Callable<MutableList<Any>>>()
        if (maxPage <= 0 || maxPage < firstPage) return result
        for (item in firstPage..maxPage) {
            val callable = Callable<MutableList<Any>> {
                val bookInfoList = QiDianHttpUtils.getMaxPageAndBookInfoFromRankPage(searchInfo, item)
                if (firstPage == 1 && maxPage == 1) {
                } else {
                    bookInfoList.removeAt(0)
                }
                bookInfoList
            }
            result.add(callable)
        }
        return result
    }

    private fun getBookDetailsCallables(searchInfo: SearchInfo, scanbookBeans: MutableList<ScanBookBean>): MutableList<Callable<BookInfo>> {
        val result = mutableListOf<Callable<BookInfo>>()
        for (item in scanbookBeans) {
            val callable = Callable {
                val bookInfo = QiDianHttpUtils.getBookDetailsInfo(searchInfo, item.url)
                bookInfo?.position = item.page * maxNumInFirstPage + item.position
                MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE)
                if (bookInfo != null) MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE)
                bookInfo
            }
            result.add(callable)
        }
        return result
    }


}