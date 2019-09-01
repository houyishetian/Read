package com.lin.read.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import com.google.gson.GsonBuilder
import com.lin.read.R
import com.lin.read.activity.LoadingDialogActivity
import com.lin.read.activity.MainActivity
import com.lin.read.adapter.ScanBookItemAdapter
import com.lin.read.adapter.ScanTypeAdapter
import com.lin.read.decoration.ScanBooksItemDecoration
import com.lin.read.decoration.ScanTypeItemDecoration
import com.lin.read.filter.BookComparatorUtil
import com.lin.read.filter.ScanBookBean
import com.lin.read.filter.scan.ReadScanBean
import com.lin.read.filter.scan.ScanInfo
import com.lin.read.filter.scan.toPair
import com.lin.read.utils.Constants
import com.lin.read.utils.ReadAnimationListener
import com.lin.read.utils.makeMsg
import com.lin.read.utils.setOnNoDoubleClickListener
import com.lin.read.view.ScanTypeRecyclerViewUtil
import kotlinx.android.synthetic.main.fragment_scan.*
import kotlinx.android.synthetic.main.layout_scan_filter.*
import java.io.InputStreamReader
import java.util.*

class ScanFragment : Fragment() {

    private var softDisplay = false
    private var lastHeight = -1
    private lateinit var allBookData: MutableList<ScanBookBean>
    private var bookComparatorUtil: BookComparatorUtil? = null
    private val globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        // the input view height
        val inputViewHeight = activity.window.decorView.rootView.height - Rect().also { activity.window.decorView.getWindowVisibleDisplayFrame(it) }.bottom
        softDisplay = inputViewHeight != 0
        if (lastHeight != inputViewHeight) {
            tempView_for_soft.layoutParams = tempView_for_soft.layoutParams.also { it.height = if (inputViewHeight != 0) 200 else inputViewHeight }
            (activity as MainActivity).hideBottomViews(inputViewHeight != 0)
            if (inputViewHeight != 0) {
                Handler().post {
                    val focusEt = getFocusEt()
                    //first scroll, then request new focus
                    scroll_view.fullScroll(ScrollView.FOCUS_DOWN)
                    focusEt?.run {
                        isFocusable = true
                        requestFocus()
                    }
                }
            }
            lastHeight = inputViewHeight
        }
    }
    private lateinit var scanTypeRecyclerViewUtil: ScanTypeRecyclerViewUtil
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_scan, null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        allBookData = mutableListOf()
        GsonBuilder().create().fromJson(InputStreamReader(activity.assets.open("read_scan.json")), ReadScanBean::class.java).apply {
            scanTypeRecyclerViewUtil = ScanTypeRecyclerViewUtil.getInstance(activity, ll_filter_layout, this)
            rcv_scan_web.adapter = ScanTypeAdapter(activity, webs.map { it toPair false }).apply {
                onScanItemClickListener = { position, clickText ->
                    hideSoft()
                    scanTypeRecyclerViewUtil.showWebLayout((rcv_scan_web.adapter as ScanTypeAdapter).getCheckedInfo().key)
                }
            }
        }.takeIf { it.webs.isNotEmpty() }?.let {
            scan_filter.setOnNoDoubleClickListener {
                scanTypeRecyclerViewUtil.showWebLayout((rcv_scan_web.adapter as ScanTypeAdapter).getCheckedInfo().key)
                layout_scan_filter.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.set_scan_filter_menu_in))
                layout_scan_filter.visibility = View.VISIBLE
                scroll_view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
            }
        }

        rcv_scan_web.run {
            layoutManager = GridLayoutManager(activity, 4)
            addItemDecoration(ScanTypeItemDecoration(activity, 15))
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

    fun hideFilter(showAnimation: Boolean = true,afterAnimation:(()->Unit)? = null) {
        hideSoft()
        val logic = fun() {
            layout_scan_filter.visibility = View.GONE
            scroll_view.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            afterAnimation?.invoke()
        }
        if (showAnimation) {
            AnimationUtils.loadAnimation(getActivity(), R.anim.set_scan_filter_menu_out).apply {
                setAnimationListener(object : ReadAnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        logic.invoke()
                    }
                })
                layout_scan_filter.startAnimation(this)
            }
        } else {
            logic.invoke()
        }
    }

    fun getFocusEt(): EditText? = scanTypeRecyclerViewUtil.getFocusOfEditText((rcv_scan_web.adapter as ScanTypeAdapter).getCheckedInfo().key)

    fun isFilterLayoutVisible(): Boolean = layout_scan_filter.visibility == View.VISIBLE

    fun hideSoft() = takeIf { softDisplay }?.activity?.getSystemService(Context.INPUT_METHOD_SERVICE)?.let {
        (it as InputMethodManager).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        getFocusEt()?.clearFocus()
    }

    private fun getScanInfoBean(): ScanInfo? {
        return scanTypeRecyclerViewUtil.getSearchInfo((rcv_scan_web.adapter as ScanTypeAdapter).getCheckedInfo().key)?.run {
            inputtedBeans?.firstOrNull {
                it.value == null || !(it.inputType in listOf(Constants.INPUT_FLOAT, Constants.INPUT_INT))
                        || (it.min != null && it.value.toFloat() < it.min.toFloat()) || (it.max != null && it.value.toFloat() > it.max.toFloat())
            }?.let {
                makeMsg("${it.typeName}输入有误！")
                null
            } ?: this
        } ?: throw Exception("cannot get scan info bean!")
    }

    private fun startScanning() {
        getScanInfoBean()?.let {
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