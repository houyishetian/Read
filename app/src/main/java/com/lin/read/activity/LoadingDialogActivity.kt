package com.lin.read.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.lin.read.R
import com.lin.read.filter.BookInfo
import com.lin.read.filter.scan.ReadGetBookInfoFactory
import com.lin.read.filter.scan.ScanInfo
import com.lin.read.utils.Constants
import com.lin.read.utils.MessageUtils
import kotlinx.android.synthetic.main.activity_loading_dialog.*

class LoadingDialogActivity : Activity() {
    private var totalBookNum: Int = 0
    private var alreadyFinish:Int = 0
    private var alreadyFind:Int = 0
    private val handler by lazy {
        Handler {
            when (it.what) {
                MessageUtils.SCAN_START -> {
                    loading_scan_book.visibility = View.VISIBLE
                    loading_scan_book.text = Constants.TEXT_SCAN_START
                }
                MessageUtils.SCAN_BOOK_INFO_END -> {
                    totalBookNum = it.arg1
                    loading_scan_book.text = String.format(Constants.TEXT_SCAN_BOOK_INFO_END, totalBookNum)
                }
                MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_START ->{
                    loading_progress.visibility = View.VISIBLE
                    loading_progress.text = "0%"
                    loading_scan_book_result.visibility = View.VISIBLE
                    loading_scan_book_result.text = Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_START
                }
                MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_FINISH_ONE ->{
                    alreadyFinish++
                    loading_progress.text = String.format("%d%%", alreadyFinish * 100 / totalBookNum)
                }
                MessageUtils.SCAN_BOOK_INFO_BY_CONDITION_GET_ONE ->{
                    alreadyFind++
                    loading_scan_book_result.text = String.format(Constants.TEXT_SCAN_BOOK_INFO_BY_CONDITION_GET_ONE, alreadyFind)
                }
            }
            return@Handler false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_dialog)
        initView()
    }

    private fun initView() {
        loading_progress.visibility = View.INVISIBLE
        loading_scan_book.visibility = View.INVISIBLE
        loading_scan_book_result.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        val scanInfo = intent.getSerializableExtra(Constants.KEY_SEARCH_INFO) as ScanInfo
        this.takeIf { scanInfo.webName != null }?.let {
            ReadGetBookInfoFactory.getInstance(scanInfo.webName!!)?.getBookInfo(this, handler, scanInfo, object : ReadGetBookInfoFactory.OnScanResult {
                override fun onSucceed(totalNum: Int, bookInfoList: List<BookInfo>) {
                    setResult(Constants.SCAN_RESPONSE_SUCC, Intent().apply {
                        if (Constants.WEB_YOU_SHU == scanInfo.webName) {
                            putExtra(MessageUtils.TOTAL_PAGE, totalNum)
                            putExtra(MessageUtils.CURRENT_PAGE, scanInfo.page)
                        }
                        putExtra(Constants.KEY_INTENT_FOR_BOOK_DATA, Bundle().apply {
                            putParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA, bookInfoList as ArrayList<BookInfo>)
                        })
                    })
                    finish()
                }

                override fun onFailed(e: Throwable?) {
                    setResult(Constants.SCAN_RESPONSE_FAILED, Intent().apply {
                        putExtra(Constants.KEY_INTENT_FOR_BOOK_DATA, Bundle())
                    })
                    finish()
                }
            })
        }
    }

    override fun onBackPressed() {
        //no need to handle
    }
}