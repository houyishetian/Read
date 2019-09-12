package com.lin.read.filter.scan

import android.content.Context
import android.os.Handler
import com.lin.read.filter.ScanBookBean
import com.lin.read.utils.Constants

abstract class ReadGetBookInfoFactory {
    abstract fun getBookInfo(context: Context,handler: Handler, searchInfo: ScanDataBean, onScanResult: OnScanResult)
    companion object {
        fun getInstance(type: String): ReadGetBookInfoFactory? {
            return when (type) {
                Constants.WEB_YOUSHU_ID -> ReadGetYouShuBookInfoFactory()
                Constants.WEB_QIDIAN_ID,Constants.WEB_QIDIAN_FINISH_ID -> ReadGetQiDianBookInfoFactory()
                else -> null
            }
        }
    }

    interface OnScanResult{
        fun onSucceed(totalNum:Int,bookInfoList:List<ScanBookBean>)
        fun onFailed(e:Throwable?)
    }
}