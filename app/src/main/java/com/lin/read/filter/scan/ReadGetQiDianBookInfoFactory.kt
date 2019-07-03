package com.lin.read.filter.scan

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.qidian.QiDianHttpUtils
import com.lin.read.filter.scan.qidian.QiDianResolveUtil
import com.lin.read.retrofit.NullResponseException
import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.Constants
import com.lin.read.utils.MessageUtils
import okhttp3.ResponseBody
import rx.Observable
import rx.Observer
import rx.functions.Func1
import rx.schedulers.Schedulers

class ReadGetQiDianBookInfoFactory : ReadGetBookInfoFactory() {
    private val tag = ReadGetQiDianBookInfoFactory::class.java.simpleName
    private lateinit var searchInfo: ScanInfo
    private lateinit var handler: Handler
    private lateinit var onScanResult: OnScanResult
    private lateinit var context: Context
    private var maxNumInFirstPage:Int = 0
    override fun getBookInfo(context: Context, handler: Handler, searchInfo: ScanInfo, onScanResult: OnScanResult) {
        this.searchInfo = searchInfo
        this.handler = handler
        this.onScanResult = onScanResult
        this.context = context
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_START)
        Log.e(tag, "scan start")
        try {
            getBookInfos(searchInfo)
        }catch (e:Exception){
            e.printStackTrace()
            onScanResult.onFailed(e)
        }
    }

    private fun getBookInfos(searchInfo: ScanInfo) {
        val service = RetrofitInstance(searchInfo.mainUrl!![0]).create(ReadRetrofitService::class.java)
        val result = mutableListOf<BookLinkInfo>()
        val observable:Observable<ResponseBody>
        when(searchInfo.webName){
            Constants.WEB_QIDIAN -> observable = service.getQiDianBookList(searchInfo.rolePathValue!!, searchInfo.roleParamPairs!!, 1)
            else -> observable = service.getQiDianBookList(searchInfo.roleParamPairs!!, 1)
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap(object : Func1<ResponseBody, Observable<ResponseBody>> {
                    override fun call(t: ResponseBody?): Observable<ResponseBody> {
                        if (t == null) {
                            throw Exception("response is null!")
                        }
                        val maxPageWithBookInfo = QiDianResolveUtil.getMaxPageAndBookInfoFromRandPage(t.charStream(), true)
                        val maxPage = maxPageWithBookInfo[0] as Int
                        maxPageWithBookInfo.removeAt(0)
                        maxPageWithBookInfo.forEach { result.add(it as BookLinkInfo) }
                        maxNumInFirstPage = maxPageWithBookInfo.size
                        Log.e(tag, "first page complete, max page:${maxPage}, book count:${maxPageWithBookInfo.size}")
                        val observableArray = arrayListOf<Observable<ResponseBody>>()
                        for (item in (2..maxPage)) {
                            val observable: Observable<ResponseBody>
                            when (searchInfo.webName) {
                                Constants.WEB_QIDIAN -> observable = service.getQiDianBookList(searchInfo.rolePathValue!!, searchInfo.roleParamPairs, item)
                                else -> observable = service.getQiDianBookList(searchInfo.roleParamPairs, item)
                            }
                            observableArray.add(observable.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()))
                        }
                        return Observable.merge(observableArray, 7)
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onNext(t: ResponseBody?) {
                        if (t == null) {
                            (context as Activity).runOnUiThread{
                                onScanResult.onFailed(NullResponseException())
                            }
                            return
                        }
                        val bookLinkInfos = QiDianResolveUtil.getMaxPageAndBookInfoFromRandPage(t.charStream())
                        bookLinkInfos.forEach { result.add(it as BookLinkInfo) }
                        Log.e(tag, "${(bookLinkInfos[0] as BookLinkInfo).page} page complete, book count::${bookLinkInfos.size}")
                        MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, result.size)
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        Log.e(tag, "error:${e?.message}")
                        (context as Activity).runOnUiThread{
                            onScanResult.onFailed(e)
                        }
                    }

                    override fun onCompleted() {
                        Log.e(tag, "all completed, totally:${result.size}")
                        getBookDetailsInfoAndFilter(searchInfo,result)
                    }
                })
    }

    private fun getBookDetailsInfoAndFilter(searchInfo: ScanInfo,bookInfoList:List<BookLinkInfo>){
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START)
        val service = RetrofitInstance(searchInfo.mainUrl!![1]).create(ReadRetrofitService::class.java)
        val result = mutableListOf<BookInfo>()
        val observableArray = arrayListOf<Observable<ResponseBody>>()
        bookInfoList.forEach{
            val bookId = StringUtils.getBookId(it.bookLink)
            observableArray.add(service.getQiDianBookDetails(bookId).subscribeOn(Schedulers.io()).observeOn(Schedulers.io()))
        }
        Observable.merge(observableArray,20)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object:Observer<ResponseBody>{
                    override fun onNext(t: ResponseBody?) {
                        if (t == null) {
                            (context as Activity).runOnUiThread{
                                onScanResult.onFailed(NullResponseException())
                            }
                            return
                        }
                        val bookInfo = QiDianHttpUtils.getBookDetailsInfo(searchInfo,t.charStream())
                        MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE)
                        if(bookInfo != null){
                            result.add(bookInfo)
                            MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE)
                            Log.e(tag,"find one：${bookInfo}")
                        }
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        Log.e(tag, "error:${e?.message}")
                        (context as Activity).runOnUiThread{
                            onScanResult.onFailed(e)
                        }
                    }

                    override fun onCompleted() {
                        Log.e(tag, "all completed, totally:${result.size}")
                        result.forEach {
                            it.webName = searchInfo.webName
                        }
                        (context as Activity).runOnUiThread{
                            onScanResult.onSucceed(result.size, result)
                        }
                    }
                })
    }
}