package com.lin.read.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import com.google.gson.GsonBuilder
import com.lin.read.R
import com.lin.read.activity.LoadingDialogActivity
import com.lin.read.adapter.ScanBookItemAdapter
import com.lin.read.adapter.ScanFilterItemAdapter
import com.lin.read.decoration.ScanBooksItemDecoration
import com.lin.read.decoration.ScanItemDecoration
import com.lin.read.filter.BookComparatorUtil
import com.lin.read.filter.ScanBookBean
import com.lin.read.filter.scan.ScanBean
import com.lin.read.utils.Constants
import com.lin.read.utils.animationListener
import com.lin.read.utils.makeMsg
import com.lin.read.utils.setOnNoDoubleClickListener
import com.lin.read.view.DialogUtil
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.layout_scan_filter.*
import java.io.InputStreamReader
import java.util.*

class ScanFragment : Fragment() {
    private lateinit var allBookData: MutableList<ScanBookBean>
    private var bookComparatorUtil: BookComparatorUtil? = null
    private var scanMenuIsSliding = false
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_scan, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        allBookData = mutableListOf()
        DialogUtil.getInstance().showLoadingDialog(activity)
        GsonBuilder().create().fromJson(InputStreamReader(activity.assets.open("read_scan.json")), ScanBean::class.java).apply {
            Log.e("Test","get read scan data successfully!")
            DialogUtil.getInstance().hideLoadingView()
            rcv_scan.let {
                it.layoutManager = LinearLayoutManager(activity)
                it.addItemDecoration(ScanItemDecoration(activity, top = 15))
                it.adapter = ScanFilterItemAdapter(activity, this)
                it.setOnTouchListener { view, event ->
                    (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken,0)
                    false
                }
            }
        }.takeIf { it.webs.values.first().isNotEmpty() }?.let {
            scan_filter.setOnNoDoubleClickListener {
                layout_scan_filter.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.set_scan_filter_menu_in))
                layout_scan_filter.visibility = View.VISIBLE
            }
        }
        rcv_scan_all_books.run {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ScanBooksItemDecoration(activity))
            adapter = ScanBookItemAdapter(activity, allBookData)
        }
        scan_filter_blank.setOnNoDoubleClickListener {
            hideFilter()
        }
        scan_ok.setOnNoDoubleClickListener {
            hideFilter()
            startScanning()
        }
        scan_sort.setOnNoDoubleClickListener {
            bookComparatorUtil?.showSortDialog(activity, allBookData) {
                rcv_scan_all_books.adapter.notifyDataSetChanged()
                rcv_scan_all_books.smoothScrollToPosition(0)
            }
        }
    }

    fun hideFilter(showAnimation: Boolean = true) {
        hideSoft()
        val logic = fun() {
            layout_scan_filter.visibility = View.GONE
        }
        if (showAnimation && !scanMenuIsSliding) {
            scanMenuIsSliding = true
            AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_out).apply {
                animationListener {
                    logic.invoke()
                    scanMenuIsSliding = false
                }
                layout_scan_filter.startAnimation(this)
            }
        } else {
            logic.invoke()
        }
    }

    fun isFilterLayoutVisible(): Boolean = layout_scan_filter.visibility == View.VISIBLE

    fun hideSoft() = (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(rcv_scan.windowToken, 0)

    private fun startScanning() {
        (rcv_scan.adapter as ScanFilterItemAdapter).getScanDataBean()?.let {
            Intent(activity, LoadingDialogActivity::class.java).apply {
                putExtra(Constants.KEY_SEARCH_INFO, it)
                startActivityForResult(this, Constants.SCAN_REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.SCAN_REQUEST_CODE) {
            when (resultCode) {
                Constants.SCAN_RESPONSE_FAILED -> {
                    scan_result_qidian.visibility = View.VISIBLE
                    scan_result_qidian.text = String.format(Locale.CHINA, Constants.TEXT_SCAN_BOOK_INFO_RESULT, 0)
                    allBookData.clear()
                    rcv_scan_all_books.adapter.notifyDataSetChanged()
                    rcv_scan_all_books.visibility = View.GONE
                    empty_view.visibility = View.VISIBLE
                    bookComparatorUtil = null
                    makeMsg("扫描失败，请检查网络！")
                }
                Constants.SCAN_RESPONSE_SUCC -> {
                    allBookData.clear()
                    val newBookData: List<ScanBookBean> = data?.getBundleExtra(Constants.KEY_INTENT_FOR_BOOK_DATA)?.getParcelableArrayList(Constants.KEY_BUNDLE_FOR_BOOK_DATA)
                            ?: listOf()
                    allBookData.addAll(newBookData)
                    scan_result_qidian.text = String.format(Locale.CHINA, Constants.TEXT_SCAN_BOOK_INFO_RESULT, allBookData.size)
                    scan_result_qidian.visibility = View.VISIBLE
                    rcv_scan_all_books.visibility = if (allBookData.isEmpty()) View.GONE else View.VISIBLE
                    empty_view.visibility = if (allBookData.isEmpty()) View.VISIBLE else View.GONE
                    makeMsg(if (allBookData.isEmpty()) "未扫描到数据！" else "扫描结束！")
                    bookComparatorUtil = BookComparatorUtil()
                    bookComparatorUtil?.sortByDefaultRules(allBookData) {
                        rcv_scan_all_books.adapter.notifyDataSetChanged()
                        rcv_scan_all_books.smoothScrollToPosition(0)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}