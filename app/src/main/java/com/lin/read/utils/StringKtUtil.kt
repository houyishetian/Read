package com.lin.read.utils

import android.util.Log
import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.ScanInfo

class StringKtUtil {
    companion object {
        /**
         * @param scanInfo standard value
         * @param bookInfo pending for compare
         * @return true-the book is ok, false-the book is not ok
         */
        fun compareFilterInfo(scanInfo: ScanInfo, bookInfo: BookInfo): Boolean {
            try {
                if(scanInfo.inputtedBeans == null || scanInfo.inputtedBeans.isEmpty()){
                    return true
                }
                loop@ for (it in scanInfo.inputtedBeans) {
                    val thisInfo = ReflectUtil.getProperty(bookInfo, it.key, String::class.java)
                    when (it?.inputType) {
                        Constants.INPUT_INT, Constants.INPUT_FLOAT -> {
                            val valueStandard = it.value!!.toFloat()
                            val valuePendingCompare = thisInfo!!.toFloat()
                            if (valueStandard > valuePendingCompare) return false
                        }
                        else -> continue@loop
                    }
                }
                return true
            }catch (e:Exception){
                e.printStackTrace()
            }
            return false
        }

        fun printLog(log:String?){
            if(log == null){
                return
            }
            for(index in 0 until log.length step 3000){
                var next = if(index + 3000 <= log.length-1) index+3000 else log.length-1
                Log.e("Test index","${log.substring(index,next)}")
            }
        }
    }
}