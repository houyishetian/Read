package com.lin.read.filter.search

import com.lin.read.filter.BookInfo
import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.Constants
import com.lin.read.utils.ReflectUtil
import okhttp3.ResponseBody
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

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
        Constants.SEARCH_WEB_BASEURL_MAP[searchWebBean.tag]?.let {
            val bookNameEncoded = Constants.SEARCH_WEB_BOOK_NAME_MAP(searchWebBean.tag, bookName)
            return when (searchWebBean.tag) {
                Constants.RESOLVE_FROM_BIQUGE -> RetrofitInstance(it).create(ReadRetrofitService::class.java).searchFromBIQUGE(bookNameEncoded)
                Constants.RESOLVE_FROM_DINGDIAN -> RetrofitInstance(it).create(ReadRetrofitService::class.java).searchFromDINGDIAN(bookNameEncoded)
                Constants.RESOLVE_FROM_BIXIA -> RetrofitInstance(it).create(ReadRetrofitService::class.java).searchFromBIXIA(bookNameEncoded)
                Constants.RESOLVE_FROM_AISHU -> RetrofitInstance(it).create(ReadRetrofitService::class.java).searchFromAISHUWANG(bookNameEncoded)
                Constants.RESOLVE_FROM_QINGKAN -> RetrofitInstance(it).create(ReadRetrofitService::class.java).searchFromQINGKAN(bookNameEncoded)
                else -> throw Exception("cannot get observable! Pls check tag!")
            }
        } ?: throw Exception("cannot get observable by  ${searchWebBean.tag}! Pls check tag!")
    }

    @SuppressWarnings("unchecked")
    private fun getBookList(responseBody: ResponseBody): List<BookInfo>? {
        Constants.SEARCH_WEB_BASEURL_MAP[searchWebBean.tag]?.let {
            return ReflectUtil.invokeMethod(SearchBookResolveUtil.Companion, "resolveFrom${searchWebBean.tag}", List::class.java, SearchResolveBean(it, bookName, responseBody)) as? List<BookInfo>
        } ?: throw java.lang.Exception("cannot get ${searchWebBean.tag}'s booklist!")
    }

    interface OnSearchResult{
        fun onSucceed(bookInfoList:List<BookInfo>?)
        fun onFailed(e:Throwable?)
    }
}