package com.lin.read.filter.scan

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.qidian.QiDianResolveUtil
import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.Constants
import com.lin.read.utils.MessageUtils
import okhttp3.ResponseBody
import retrofit2.HttpException
import rx.Observable
import rx.Observer
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
        val observable: Observable<ResponseBody> = when (searchInfo.webName) {
            Constants.WEB_QIDIAN -> service.getQiDianBookList(searchInfo.rolePathValue!!, searchInfo.roleParamPairs!!, 1)
            else -> service.getQiDianBookList(searchInfo.roleParamPairs!!, 1)
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { t ->
                    return@flatMap t?.run {
                        val maxPageWithBookInfo = QiDianResolveUtil.getMaxPageAndBookInfoFromRandPage(this, true)
                        val maxPage = maxPageWithBookInfo[0] as Int
                        maxPageWithBookInfo.removeAt(0)
                        maxPageWithBookInfo.forEach { result.add(it as BookLinkInfo) }
                        maxNumInFirstPage = maxPageWithBookInfo.size
                        Log.e(tag, "first page complete, max page:$maxPage, book count:${maxPageWithBookInfo.size}")
                        val observableArray = arrayListOf<Observable<ResponseBody>>()
                        for (item in (2..maxPage)) {
                            val otherPagesInfo: Observable<ResponseBody> = when (searchInfo.webName) {
                                Constants.WEB_QIDIAN -> service.getQiDianBookList(searchInfo.rolePathValue!!, searchInfo.roleParamPairs, item)
                                else -> service.getQiDianBookList(searchInfo.roleParamPairs, item)
                            }
                            observableArray.add(otherPagesInfo.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()))
                        }
                        Observable.merge(observableArray, 7)
                    }
                }.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object : Observer<ResponseBody> {
                    override fun onNext(t: ResponseBody?) {
                        t?.run {
                            val bookLinkInfos = QiDianResolveUtil.getMaxPageAndBookInfoFromRandPage(this)
                            bookLinkInfos.forEach { result.add(it as BookLinkInfo) }
                            Log.e(tag, "${(bookLinkInfos[0] as BookLinkInfo).page} page complete, book count::${bookLinkInfos.size}")
                            MessageUtils.sendMessageOfInteger(handler, MessageUtils.SCAN_BOOK_INFO_END, result.size)
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
                        getBookDetailsInfoAndFilter(searchInfo,result)
                    }
                })
    }

    @Suppress("UNREACHABLE_CODE")
    private fun getBookDetailsInfoAndFilter(searchInfo: ScanInfo,bookInfoList:List<BookLinkInfo>){
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START)
        val service = RetrofitInstance(searchInfo.mainUrl!![1]).create(ReadRetrofitService::class.java)
        val result = mutableListOf<BookInfo>()
        val observableArray = arrayListOf<Observable<ResponseBody>>()
        bookInfoList.forEach{
            val bookId = StringUtils.getBookId(it.bookLink)
            observableArray.add(service.getQiDianBookDetails(bookId).subscribeOn(Schedulers.io()).onErrorReturn {
                Log.e("exception error", "${it.javaClass.name}")
                when (it) {
                    is HttpException -> {
                        it.response()?.takeIf { it.code() >= 400 }?.errorBody().let {
                            Log.e("error for current item", "${it?.string()}")
                            return@onErrorReturn null
                        } ?: throw  it
                    }
                    else -> throw  it
                }
            }.observeOn(Schedulers.io()))
        }
        Observable.merge(observableArray,20)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(object:Observer<ResponseBody>{
                    override fun onNext(t: ResponseBody?) {
                        t?.run {
                            val bookInfo = QiDianResolveUtil.getBookDetailsInfo(searchInfo,this)
                            MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE)
                            if(bookInfo != null){
                                result.add(bookInfo)
                                MessageUtils.sendWhat(handler, MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE)
                                Log.e(tag,"find one：$bookInfo")
                            }
                        } ?: Log.e("meet error", "null response, skip error!")
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                        Log.e(tag, "error:${e?.message}")
                        (context as Activity).runOnUiThread{
                            onScanResult.onFailed(e)
                        }
                    }

                    override fun onCompleted() {
                        Log.d(tag, "all completed, totally:${result.size}")
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