package com.lin.read.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.lin.read.R
import com.lin.read.adapter.DialogWebTypeAdapter
import com.lin.read.adapter.SearchBookItemAdapter
import com.lin.read.decoration.ScanBooksItemDecoration
import com.lin.read.filter.BookInfo
import com.lin.read.filter.search.GetDownloadInfoTask
import com.lin.read.filter.search.SearchWebBean
import com.lin.read.utils.Constants
import com.lin.read.utils.makeMsg
import com.lin.read.view.DialogUtil
import kotlinx.android.synthetic.main.dialog_search_select_web.*
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private lateinit var webBeansList: List<SearchWebBean>
    private lateinit var allBookInfo: MutableList<BookInfo>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_search, null)?.apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        webBeansList = getWebTypeBeans()
        allBookInfo = mutableListOf()
        //set default value
        select_web.text = webBeansList.let loop@{
            it.forEach {
                if (it.default) return@loop it.webName
            }
            it[0].default = true
            return@loop it[0].webName
        }
        select_web.setOnClickListener(View.OnClickListener {
            showSelectWebDialog()
        })
        rcv_search_result.run {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ScanBooksItemDecoration(activity))
            adapter = SearchBookItemAdapter(activity, allBookInfo)
        }
        et_search_bookname.setOnEditorActionListener loop@{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoft()
                search()
            }
            return@loop false
        }
        btn_search.setOnClickListener(View.OnClickListener {
            hideSoft()
            search()
        })
    }

    private fun getWebTypeBeans(): List<SearchWebBean> {
        return mutableListOf<SearchWebBean>().apply {
            add(SearchWebBean("笔趣阁", Constants.RESOLVE_FROM_BIQUGE))
            add(SearchWebBean("顶点", Constants.RESOLVE_FROM_DINGDIAN))
            add(SearchWebBean("笔下", Constants.RESOLVE_FROM_BIXIA, default = true))
            add(SearchWebBean("爱书网", Constants.RESOLVE_FROM_AISHU))
            add(SearchWebBean("请看", Constants.RESOLVE_FROM_QINGKAN))
        }
    }

    private fun showSelectWebDialog() {
        val selectWebDialog = Dialog(this.activity, R.style.Dialog_Fullscreen)
        selectWebDialog.show()
        val inflater = LayoutInflater.from(activity)
        val viewDialog = inflater.inflate(R.layout.dialog_search_select_web, null)
        val width = activity.windowManager.defaultDisplay.width
        val height = activity.windowManager.defaultDisplay.height
        //设置dialog的宽高为屏幕的宽高
        selectWebDialog.setContentView(viewDialog, ViewGroup.LayoutParams(width, height))
        (viewDialog.findViewById(R.id.dialog_select_web_rcv) as RecyclerView).run {
            layoutManager = LinearLayoutManager(activity)
            addItemDecoration(ScanBooksItemDecoration(activity))
            adapter = DialogWebTypeAdapter(activity, webBeansList).apply {
                onItemWebClickListener = object : DialogWebTypeAdapter.OnItemWebClickListener {
                    override fun onItemWebClick(searchWebBean: SearchWebBean) {
                        select_web.text = searchWebBean.webName
                        selectWebDialog.dismiss()
                    }
                }
            }
        }
    }

    fun hideSoft() {
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(et_search_bookname.windowToken, 0)
    }

    private fun search() {
        if (TextUtils.isEmpty(et_search_bookname.text.toString())) {
            activity.makeMsg("请输入书名!")
            return
        }
        DialogUtil.getInstance().showLoadingDialog(this.getActivity());
        val currentSelectWeb = webBeansList.let loop@{
            it.forEach { if (it.checked!!) return@loop it }
            it[0].checked = true
            return@loop it[0]
        }
        GetDownloadInfoTask(activity, currentSelectWeb, object : GetDownloadInfoTask.OnTaskListener {
            override fun onSucc(allBooks: MutableList<BookInfo>?) {
                DialogUtil.getInstance().hideLoadingView()
                allBooks?.takeIf { it.isNotEmpty() }?.apply {
                    empty_view.visibility = View.GONE
                    rcv_search_result.visibility = View.VISIBLE
                    allBookInfo.clear()
                    allBookInfo.addAll(this)
                    rcv_search_result.adapter.notifyDataSetChanged()
                } ?: let {
                    empty_view.visibility = View.VISIBLE
                    rcv_search_result.visibility = View.GONE
                    activity.makeMsg("未获取到数据!")
                }
            }

            override fun onFailed() {
                DialogUtil.getInstance().hideLoadingView()
                empty_view.visibility = View.VISIBLE
                rcv_search_result.visibility = View.GONE
                activity.makeMsg("网络请求失败!")
            }
        }).execute(et_search_bookname.text.toString(), "0", currentSelectWeb.webName)
    }

    fun setSearchType(bookInfo: BookInfo) {
        et_search_bookname.setText(bookInfo.bookName)
        btn_search.performClick()
    }
}