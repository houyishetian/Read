package com.lin.read.filter.search

import android.os.AsyncTask
import android.util.Log
import com.lin.read.utils.StringKtUtil
import java.lang.Exception

class GetChapterContentTask(private val bookChapterInfo: BookChapterInfo,private val onTaskListener: OnTaskListener):AsyncTask<Unit,Unit,Unit>() {
    override fun doInBackground(vararg params: Unit?) {
        try {
            var content = ResolveChapterUtils.getChapterContent(bookChapterInfo)
            while (!bookChapterInfo.isComplete) {
                Log.d("Test", "continue search ..")
                content += ResolveChapterUtils.getChapterContent(bookChapterInfo) ?: ""
            }
            onTaskListener.onSucc(StringKtUtil.removeAdsFromContent(content))
        } catch (e: Exception) {
            e.printStackTrace()
            onTaskListener.onFailed()
        }
    }

    interface OnTaskListener {
        fun onSucc(content: String)
        fun onFailed()
    }
}