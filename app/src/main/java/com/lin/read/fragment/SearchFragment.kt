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
import com.lin.read.filter.SearchBookBean
import com.lin.read.filter.search.SearchBookTask
import com.lin.read.filter.search.SearchWebBean
import com.lin.read.utils.Constants
import com.lin.read.utils.makeMsg
import com.lin.read.view.DialogUtil
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {
    private lateinit var webBeansList: List<SearchWebBean>
    private lateinit var allBookInfo: MutableList<SearchBookBean>

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
        select_web.text = webBeansList.firstOrNull { it.default }?.webName ?: let {
            webBeansList[0].default = true
            webBeansList[0].webName
        }
        select_web.setOnClickListener{
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
        btn_search.setOnClickListener{
            hideSoft()
            search()
        }
    }

    private fun getWebTypeBeans(): List<SearchWebBean> {
        return mutableListOf<SearchWebBean>().apply {
            Constants.SEARCH_WEB_NAME_MAP.forEach { tag, webName ->
                add(SearchWebBean(webName, tag))
            }
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
        DialogUtil.getInstance().showLoadingDialog(activity)
        val currentSelectWeb = webBeansList.firstOrNull { it.checked!! } ?: let {
            webBeansList[0].checked = true
            webBeansList[0]
        }
        SearchBookTask(currentSelectWeb, et_search_bookname.text.toString(), object : SearchBookTask.OnSearchResult {
            override fun onSucceed(allBooks: List<SearchBookBean>?) {
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

            override fun onFailed(e: Throwable?) {
                DialogUtil.getInstance().hideLoadingView()
                empty_view.visibility = View.VISIBLE
                rcv_search_result.visibility = View.GONE
                activity.makeMsg("网络请求失败!")
            }
        }).searchBook()
    }

    fun setSearchType(bookName: String) {
        et_search_bookname.setText(bookName)
        btn_search.performClick()
    }
}