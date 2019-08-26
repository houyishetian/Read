package com.lin.read.filter.search

import com.lin.read.filter.SearchBookBean
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
        Constants.SEARCH_WEB_BASEURL_MAP[searchWebBean.webType]?.let {
            val param = Constants.SEARCH_WEB_RETRO_PARAMS_MAP(searchWebBean.webType, bookName)
            return ReflectUtil.invokeMethod(RetrofitInstance(it).create(ReadRetrofitService::class.java), "searchFrom${searchWebBean.webType}", Observable::class.java, param) as Observable<ResponseBody>
        } ?: throw Exception("cannot get observable by  ${searchWebBean.webType}! Pls check tag!")
    }

    @SuppressWarnings("unchecked")
    private fun getBookList(responseBody: ResponseBody): List<SearchBookBean>? {
        Constants.SEARCH_WEB_BASEURL_MAP[searchWebBean.webType]?.let {
            return ReflectUtil.invokeMethod(SearchBookResolveUtil.Companion, "resolveFrom${searchWebBean.webType}", List::class.java, SearchResolveBean(it, bookName, responseBody)) as? List<SearchBookBean>
        } ?: throw java.lang.Exception("cannot get ${searchWebBean.webType}'s booklist!")
    }

    interface OnSearchResult{
        fun onSucceed(bookInfoList:List<SearchBookBean>?)
        fun onFailed(e:Throwable?)
    }
}