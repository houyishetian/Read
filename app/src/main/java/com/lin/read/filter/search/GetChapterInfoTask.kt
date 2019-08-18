package com.lin.read.filter.search

import com.lin.read.filter.ReadBookBean
import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.Constants
import com.lin.read.utils.baseUrl
import com.lin.read.utils.split
import okhttp3.ResponseBody
import rx.Observer
import rx.schedulers.Schedulers

class GetChapterInfoTask(private val readBookBean: ReadBookBean, private val onTaskListener: OnTaskListener){
    private var eachPageSize = Constants.CHAPTER_NUM_FOR_EACH_PAGE
    init {
        if (eachPageSize <= 0) {
            throw Exception("param illegal:the each page size must be bigger than 0!")
        }
    }

    fun getChapters() {
        RetrofitInstance(readBookBean.chapterLink.baseUrl()).create(ReadRetrofitService::class.java).getResponse(readBookBean.chapterLink)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        onTaskListener.onFailed()
                    }

                    override fun onNext(t: ResponseBody?) {
                        t?.let {
                            ReadBookResolveUtil.run {
                                when (readBookBean.webType) {
                                    Constants.RESOLVE_FROM_BIQUGE -> getChapterListFromBIQUGE(readBookBean, it)
                                    Constants.RESOLVE_FROM_DINGDIAN -> getChapterListFromDINGDIAN(readBookBean, it)
                                    Constants.RESOLVE_FROM_BIXIA -> getChapterListFromBIXIA(readBookBean, it)
                                    Constants.RESOLVE_FROM_AISHU -> getChapterListFromAISHUWANG(readBookBean, it)
                                    Constants.RESOLVE_FROM_QINGKAN -> getChapterListFromQINGKAN(readBookBean, it)
                                    Constants.RESOLVE_FROM_SANQI -> getChapterListFromSANQI(readBookBean,it)
                                    else -> throw Exception("the webType is not found:${readBookBean.webType}")
                                }
                            }.let {
                                onTaskListener.onSucc(it, it.split(eachPageSize, true))
                            }
//                            ReflectUtil.invokeMethod(ReadBookResolveUtil.Companion, "getChapterListFrom${readBookBean.webType}", List::class.java, readBookBean, it).let {
//                                val result = it as List<BookChapterInfo>
//                                onTaskListener.onSucc(result, result.split(eachPageSize, true))
//                            }
                        }
                    }

                    override fun onCompleted() {

                    }
                })
    }

    interface OnTaskListener {
        fun onSucc(allInfo: List<BookChapterInfo>, splitInfos: List<List<BookChapterInfo>>)
        fun onFailed()
    }
}