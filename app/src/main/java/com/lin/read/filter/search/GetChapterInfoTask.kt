package com.lin.read.filter.search

import android.os.AsyncTask
import com.lin.read.filter.BookInfo
import com.lin.read.utils.Constants
import com.lin.read.utils.split

class GetChapterInfoTask(val bookInfo: BookInfo, val onTaskListener: OnTaskListener) : AsyncTask<Unit, Unit, Unit>() {
    private var eachPageSize = Constants.CHAPTER_NUM_FOR_EACH_PAGE
    init {
        if (eachPageSize <= 0) {
            throw Exception("param illegal:the each page size must be bigger than 0!")
        }
    }

    override fun doInBackground(vararg params: Unit?) {
        try {
            val allInfo = ResolveChapterUtils.getChapterInfo(bookInfo)
            onTaskListener.onSucc(allInfo, allInfo.split(Constants.CHAPTER_NUM_FOR_EACH_PAGE, true))
        } catch (e: Exception) {
            e.printStackTrace()
            onTaskListener.onFailed()
        }
    }

    interface OnTaskListener {
        fun onSucc(allInfo: List<BookChapterInfo>, splitInfos: List<List<BookChapterInfo>>)
        fun onFailed()
    }

//    private fun splitList(allInfo: List<BookChapterInfo>?): List<List<BookChapterInfo>> {
//        val splitResult = mutableListOf<List<BookChapterInfo>>()
//        allInfo?.takeIf { it.isNotEmpty() }?.let {
//            val maxPage = it.takeIf { it.size % eachPageSize == 0 }?.size?.div(eachPageSize)
//                    ?: (it.size.div(eachPageSize) + 1)
//            for (page in 0 until maxPage) {
//                val subList = mutableListOf<BookChapterInfo>()
//                val currentMaxLen = it.takeIf { (page + 1) * eachPageSize > it.size }?.size
//                        ?: ((page + 1) * eachPageSize)
//                for (index in page * eachPageSize until currentMaxLen) {
//                    subList.add(it[index].apply {
//                        this.page = page
//                        this.index = index
//                    })
//                }
//                splitResult.add(subList)
//            }
//        }
//        return splitResult
//    }
}