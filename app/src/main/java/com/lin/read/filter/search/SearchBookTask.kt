package com.lin.read.filter.search

import com.lin.read.filter.BookInfo
import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.Constants
import okhttp3.ResponseBody
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.URLEncoder

class SearchBookTask(val searchWebBean: SearchWebBean, val bookName: String,val onSearchResult: OnSearchResult) {
    @SuppressWarnings("unchecked")
    fun searchBook() {
        getObservable().run {
            subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<ResponseBody> {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable?) {
                            e?.printStackTrace()
                            onSearchResult.onFailed(e)
                        }

                        override fun onNext(t: ResponseBody?) {
                            t?.let {
                                onSearchResult.onSucceed(getBookList(it))
                            }
                        }
                    })
        } ?: throw Exception("init retrofit service failed!")
    }

    private fun getObservable(): Observable<ResponseBody> {
        return when (searchWebBean.tag) {
            Constants.RESOLVE_FROM_BIQUGE -> RetrofitInstance("http://www.biquge5200.com/").create(ReadRetrofitService::class.java).searchFromBIQUGE(bookName)
            Constants.RESOLVE_FROM_DINGDIAN -> RetrofitInstance("https://www.x23us.com/").create(ReadRetrofitService::class.java).searchFromDINGDIAN(URLEncoder.encode(bookName, "gbk"))
            Constants.RESOLVE_FROM_BIXIA -> RetrofitInstance("http://www.bxwx666.org/").create(ReadRetrofitService::class.java).searchFromBIXIA(URLEncoder.encode(bookName, "gbk"))
            Constants.RESOLVE_FROM_AISHU -> RetrofitInstance("http://www.22ff.org/").create(ReadRetrofitService::class.java).searchFromAISHUWANG(bookName)
            Constants.RESOLVE_FROM_QINGKAN -> RetrofitInstance("https://www.qk6.org/").create(ReadRetrofitService::class.java).searchFromQINGKAN(URLEncoder.encode(bookName, "gbk"))
            else -> throw Exception("cannot get observable! Pls check tag!")
        }
    }

    private fun getBookList(responseBody: ResponseBody): List<BookInfo>? {
        return when (searchWebBean.tag) {
            Constants.RESOLVE_FROM_BIQUGE -> SearchBookResolveUtil.resolveFromBIQUGE(responseBody)
            Constants.RESOLVE_FROM_DINGDIAN -> SearchBookResolveUtil.resolveFromDINGDIAN(responseBody,bookName)
            Constants.RESOLVE_FROM_BIXIA -> SearchBookResolveUtil.resolveFromBIXIA(responseBody)
            Constants.RESOLVE_FROM_AISHU -> SearchBookResolveUtil.resolveFromAISHUWANG(responseBody)
            Constants.RESOLVE_FROM_QINGKAN -> SearchBookResolveUtil.resolveFromQINGKAN(responseBody)
            else -> throw Exception("cannot get observable! Pls check tag!")
        }
    }

    interface OnSearchResult{
        fun onSucceed(bookInfoList:List<BookInfo>?)
        fun onFailed(e:Throwable?)
    }
}