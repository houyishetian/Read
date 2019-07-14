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
}