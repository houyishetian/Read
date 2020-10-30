package com.lin.read.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.lin.read.filter.SearchBookBean
import com.lin.read.filter.search.SearchBookTask
import com.lin.read.filter.search.SearchWebBean
import com.lin.read.utils.Constants
import com.lin.read.utils.makeMsg
import com.lin.read.utils.showFullScreenDialog
import com.lin.read.view.DialogUtil
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private lateinit var webBeansList: List<SearchWebBean>
    private lateinit var allBookInfo: MutableList<SearchBookBean>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, null)?.apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData() {
        activity?.let { activity ->
            webBeansList = getWebTypeBeans()
            allBookInfo = mutableListOf()
            //set default value
            select_web.text = webBeansList[0].webName
            select_web.setOnClickListener {
                showSelectWebDialog()
            }
            rcv_search_result.run {
                layoutManager = LinearLayoutManager(activity)
                addItemDecoration(ScanBooksItemDecoration(activity))
                adapter = SearchBookItemAdapter(activity, allBookInfo)
            }
            et_search_bookname.setOnEditorActionListener loop@{ _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoft()
                    search()
                }
                return@loop false
            }
            btn_search.setOnClickListener {
                hideSoft()
                search()
            }
        }
    }

    private fun getWebTypeBeans(): List<SearchWebBean> {
        return mutableListOf<SearchWebBean>().apply {
            Constants.SEARCH_WEB_NAME_MAP.forEach { webType, webName ->
                add(SearchWebBean(webType, webName))
            }
        }
    }

    private fun showSelectWebDialog() {
        activity?.let { activity ->
            activity.showFullScreenDialog(R.layout.dialog_search_select_web) { dialog, view ->
                (view.findViewById(R.id.dialog_select_web_rcv) as RecyclerView).run {
                    layoutManager = LinearLayoutManager(activity)
                    addItemDecoration(ScanBooksItemDecoration(activity))
                    adapter = DialogWebTypeAdapter(activity, webBeansList).apply {
                        onItemWebClickListener = object : DialogWebTypeAdapter.OnItemWebClickListener {
                            override fun onItemWebClick(searchWebBean: SearchWebBean) {
                                select_web.text = searchWebBean.webName
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }

    fun hideSoft() {
        (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(et_search_bookname.windowToken, 0)
    }

    private fun search() {
        if (TextUtils.isEmpty(et_search_bookname.text.toString())) {
            activity?.makeMsg("请输入书名!")
            return
        }
        DialogUtil.getInstance().showLoadingDialog(activity)
        val currentSelectWeb = webBeansList.first { it.webName == select_web.text }
        SearchBookTask(currentSelectWeb, et_search_bookname.text.toString(), object : SearchBookTask.OnSearchResult {
            override fun onSucceed(allBooks: List<SearchBookBean>?) {
                DialogUtil.getInstance().hideLoadingView()
                allBooks?.takeIf { it.isNotEmpty() }?.apply {
                    empty_view.visibility = View.GONE
                    rcv_search_result.visibility = View.VISIBLE
                    allBookInfo.clear()
                    allBookInfo.addAll(this)
                    rcv_search_result.adapter?.notifyDataSetChanged()
                } ?: let {
                    empty_view.visibility = View.VISIBLE
                    rcv_search_result.visibility = View.GONE
                    activity?.makeMsg("未获取到数据!")
                }
            }

            override fun onFailed(e: Throwable?) {
                DialogUtil.getInstance().hideLoadingView()
                empty_view.visibility = View.VISIBLE
                rcv_search_result.visibility = View.GONE
                activity?.makeMsg("网络请求失败!")
            }
        }).searchBook()
    }

    fun setSearchType(bookName: String) {
        et_search_bookname.setText(bookName)
        btn_search.performClick()
    }
}