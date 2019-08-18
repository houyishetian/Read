package com.lin.read.filter.search

import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.*
import okhttp3.ResponseBody
import rx.Observer
import rx.schedulers.Schedulers

class GetChapterContentTask(private val bookChapterInfo: BookChapterInfo, private val onTaskListener: OnTaskListener) {
    fun getChapterContent(chapterLink: String = bookChapterInfo.chapterLink, previousChapter: String = "") {
        RetrofitInstance(chapterLink.baseUrl()).create(ReadRetrofitService::class.java).getResponse(chapterLink)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        onTaskListener.onFailed()
                    }

                    override fun onNext(t: ResponseBody?) {
                        t?.let {
                            val bookChapterContent = ReflectUtil.invokeMethod(ReadBookResolveUtil.Companion, "getChapterContentFrom${bookChapterInfo.webType}", BookChapterContent::class.java, ReadResolveBean(bookChapterInfo, null, it))!!
                            bookChapterContent.takeIf { it.isComplete }?.content?.let {
                                onTaskListener.onSucc(StringKtUtil.removeAdsFromContent(previousChapter + it))
                            } ?: getChapterContent(bookChapterContent.nextLink, previousChapter + bookChapterContent.content)
                        }
                    }

                    override fun onCompleted() {

                    }
                })
    }

    interface OnTaskListener {
        fun onSucc(content: String)
        fun onFailed()
    }
}