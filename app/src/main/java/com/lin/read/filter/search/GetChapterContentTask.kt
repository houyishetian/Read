package com.lin.read.filter.search

import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.Constants
import com.lin.read.utils.StringKtUtil
import com.lin.read.utils.baseUrl
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
                            val bookChapterContent = ReadBookResolveUtil.run {
                                when (bookChapterInfo.webType) {
                                    Constants.RESOLVE_FROM_BIQUGE -> getChapterContentFromBIQUGE(bookChapterInfo, it)
                                    Constants.RESOLVE_FROM_DINGDIAN -> getChapterContentFromDINGDIAN(bookChapterInfo, it)
                                    Constants.RESOLVE_FROM_BIXIA -> getChapterContentFromBIXIA(bookChapterInfo, it)
                                    Constants.RESOLVE_FROM_AISHU -> getChapterContentFromAISHUWANG(bookChapterInfo, it)
                                    Constants.RESOLVE_FROM_QINGKAN -> getChapterContentFromQINGKAN(bookChapterInfo, it)
                                    else -> throw Exception("the webType is not found:${bookChapterInfo.webType}")
                                }
                            }
                            bookChapterContent.takeIf { it.isComplete }?.content?.let {
                                onTaskListener.onSucc(StringKtUtil.removeAdsFromContent(previousChapter + it))
                            }?: getChapterContent(bookChapterContent.nextLink, previousChapter + bookChapterContent.content)
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