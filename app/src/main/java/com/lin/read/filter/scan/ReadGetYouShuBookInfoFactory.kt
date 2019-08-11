package com.lin.read.filter.scan

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.util.Log
import com.lin.read.filter.ScanBookBean
import com.lin.read.filter.scan.youshu.YouShuHttpUtil
import com.lin.read.retrofit.ReadRetrofitService
import com.lin.read.retrofit.RetrofitInstance
import com.lin.read.utils.MessageUtils
import okhttp3.ResponseBody
import rx.Observer
import rx.schedulers.Schedulers

class ReadGetYouShuBookInfoFactory : ReadGetBookInfoFactory() {
    private val tag:String
    init {
        tag = ReadGetYouShuBookInfoFactory::class.java.simpleName
    }
    override fun getBookInfo(context: Context, handler: Handler, searchInfo: ScanInfo, onScanResult: OnScanResult) {
        MessageUtils.sendWhat(handler, MessageUtils.SCAN_START)
        Log.e(tag, "scan start")
        try {
            val service = RetrofitInstance(searchInfo.mainUrl!![0]).create(ReadRetrofitService::class.java)

            service.getYouShuBookList(searchInfo.rolePathValue!!, searchInfo.roleParamPairs!!, searchInfo.page)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe(object : Observer<ResponseBody> {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable?) {
                            e?.printStackTrace()
                            Log.e(tag, "error:${e?.message}")
                            (context as Activity).runOnUiThread{
                                onScanResult.onFailed(e)
                            }
                        }

                        override fun onNext(t: ResponseBody?) {
                            t?.run {
                                val result = YouShuHttpUtil.getAllBookInfo(charStream())
                                if (result != null && result.isNotEmpty()) {
                                    val maxNum = result[0].toString().toInt()
                                    result.removeAt(0)
                                    val books = mutableListOf<ScanBookBean>()
                                    result.forEach { books.add(it as ScanBookBean) }
                                    (context as Activity).runOnUiThread {
                                        onScanResult.onSucceed(maxNum, books)
                                    }
                                }
                            }
                        }
                    })

        } catch (e: Exception) {
            e.printStackTrace()
            onScanResult.onFailed(e)
        }
    }
}