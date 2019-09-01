package com.lin.read.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView
import com.lin.read.R
import com.lin.read.adapter.BookChapterAdapter
import com.lin.read.adapter.DialogSearchChapterAdapter
import com.lin.read.bookmark.BookMarkUtil
import com.lin.read.filter.BookMark
import com.lin.read.filter.ReadBookBean
import com.lin.read.filter.search.BookChapterInfo
import com.lin.read.filter.search.CurrentReadInfo
import com.lin.read.filter.search.GetChapterContentTask
import com.lin.read.filter.search.GetChapterInfoTask
import com.lin.read.utils.*
import com.lin.read.view.DialogUtil
import kotlinx.android.synthetic.main.activity_read_book.*
import kotlinx.android.synthetic.main.layout_book_chapter.*
import java.text.NumberFormat

class ReadBookActivity : Activity() {
    private lateinit var readBookBean:ReadBookBean
    private lateinit var currentReadInfo: CurrentReadInfo
    private var currentShownPage:Int = 0
    private lateinit var currentDisplayChapterList:MutableList<BookChapterInfo>
    private lateinit var chaptersList: MutableList<BookChapterInfo>
    private lateinit var splitChaptersList:MutableList<List<BookChapterInfo>>
    private var chapterMenuIsSliding = false
    private var searchChapterLayoutIsSliding = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_book)
        init()
        getChapterInfo()
    }

    private fun init() {
        readBookBean = intent.getParcelableExtra(Constants.KEY_SKIP_TO_READ)
        chaptersList = mutableListOf()
        val bookChapterInfo = BookChapterInfo(readBookBean.webType, Constants.SEARCH_WEB_NAME_MAP[readBookBean.webType]!!, "", "", "")
        currentReadInfo = CurrentReadInfo(false, false, false, false, bookChapterInfo)
        splitChaptersList = mutableListOf()
        currentDisplayChapterList = mutableListOf()
        chapter_bookName.text = readBookBean.bookName

        lv_chapters.adapter = BookChapterAdapter(this, currentDisplayChapterList, currentReadInfo.bookChapterInfo).apply {
            setOnChapterClickListener {
                hideSoft()
                hideMenu()
                getContentInfo(LoadChapter.NEXT, it)
            }
        }
        chapter_menu.setOnNoDoubleClickListener {
            showMenu()
            goToPage(currentReadInfo.bookChapterInfo.page)
        }
        chapter_blank_view.setOnNoDoubleClickListener {
            hideSoft()
            it.isClickable = false
            hideMenu()
            goToPage(currentReadInfo.bookChapterInfo.page)
        }
        chapter_previous_page.setOnNoDoubleClickListener {
            hideSoft()
            if (currentReadInfo.hasPreviousPage) {
                goToPage(currentShownPage - 1)
            } else {
                makeMsg("已经是第一页！")
            }
        }
        chapter_next_page.setOnNoDoubleClickListener {
            hideSoft()
            if (currentReadInfo.hasNextPage) {
                goToPage(currentShownPage + 1)
            } else {
                makeMsg("已经是最后一页！")
            }
        }
        chapter_previous_chapter.setOnNoDoubleClickListener {
            hideSoft()
            if(currentReadInfo.hasPreviousChapter){
                getContentInfo(LoadChapter.PREVIOUS)
            }else{
                makeMsg("已经是第一章！")
            }
        }
        chapter_next_chapter.setOnNoDoubleClickListener {
            hideSoft()
            if(currentReadInfo.hasNextChapter){
                getContentInfo(LoadChapter.NEXT)
            }else{
                makeMsg("已经是最后一章！")
            }
        }
        page_skip.setOnNoDoubleClickListener {
            hideSoft()
            try {
                val skipPage = page_skip_et.text.toString().toInt()
                skipPage.takeIf { it > 0 && it <= splitChaptersList.size }?.let {
                    goToPage(skipPage - 1)
                } ?: throw Exception("index error")
            } catch (e: Exception) {
                e.printStackTrace()
                makeMsg("输入错误")
                goToPage(currentReadInfo.bookChapterInfo.page)
            }
        }
        chapter_search_iv.setOnNoDoubleClickListener {
            showSearchChapterLayout()
        }
        chapter_search_et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                chapter_search_btn.performClick()
            }
            return@setOnEditorActionListener false
        }
        chapter_search_btn.setOnNoDoubleClickListener {
            searchByChapterName()
        }
        chapter_content.setOnTouchListener { _, _ ->
            hideSearchChapterLayout()
            hideSoft()
            return@setOnTouchListener false
        }
        read_scroll.setOnTouchListener { _, _ ->
            hideSearchChapterLayout()
            hideSoft()
            return@setOnTouchListener false
        }
        page_skip_et.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                page_skip.performClick()
            }
            return@setOnEditorActionListener false
        }
    }

    private fun showSearchChapterLayout(){
        chapter_content_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_read_chapter_view_out))
        chapter_search_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_read_search_view_in).apply {
            animationListener {
                chapter_search_layout.visibility = View.VISIBLE
                chapter_content_layout.visibility = View.GONE
            }
        })
    }

    private fun hideSearchChapterLayout(){
        if(chapter_search_layout.visibility == View.VISIBLE && !searchChapterLayoutIsSliding){
            searchChapterLayoutIsSliding = true
            chapter_search_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_read_search_view_out))
            chapter_content_layout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_read_chapter_view_in).apply {
                animationListener {
                    chapter_search_layout.visibility = View.GONE
                    chapter_content_layout.visibility = View.VISIBLE
                    chapter_search_et.setText("")
                    searchChapterLayoutIsSliding = false
                }
            })
        }
    }

    private fun hideSoft(){
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(page_skip_et.windowToken, 0)
        inputMethodManager.hideSoftInputFromWindow(chapter_search_et.windowToken, 0)
    }

    override fun onBackPressed() {
        hideSoft()
        if (layout_chapters.visibility == View.VISIBLE) {
            hideMenu()
            return
        }
        if (chapter_search_layout.visibility == View.VISIBLE) {
            hideSearchChapterLayout()
            return
        }
        super.onBackPressed()
    }

    private fun showMenu(){
        layout_chapters.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_scan_filter_menu_in))
        layout_chapters.visibility = View.VISIBLE
    }

    private fun hideMenu() {
        if (!chapterMenuIsSliding) {
            chapterMenuIsSliding = true
            layout_chapters.startAnimation(AnimationUtils.loadAnimation(this, R.anim.set_scan_filter_menu_out).apply {
                animationListener {
                    layout_chapters.visibility = View.GONE
                    chapterMenuIsSliding = false
                }
            })
        }
    }

    private fun setChapterAndPageEnable(){
        setTextViewEnableImpl(chapter_previous_chapter,currentReadInfo.hasPreviousChapter)
        setTextViewEnableImpl(chapter_next_chapter,currentReadInfo.hasNextChapter)
        setTextViewEnableImpl(chapter_previous_page,currentReadInfo.hasPreviousPage)
        setTextViewEnableImpl(chapter_next_page,currentReadInfo.hasNextPage)
    }

    private fun setTextViewEnableImpl(textView: TextView, enabled: Boolean) {
        textView.takeIf { enabled }?.setTextColor(getColor(android.R.color.black))?:textView.setTextColor(getColor(android.R.color.darker_gray))
    }

    private fun getChapterInfo() {
        DialogUtil.getInstance().showLoadingDialog(this)
        GetChapterInfoTask(readBookBean, object : GetChapterInfoTask.OnTaskListener {
            override fun onSucc(allInfo: List<BookChapterInfo>, splitInfos: List<List<BookChapterInfo>>) {
                runOnUiThread {
                    DialogUtil.getInstance().hideLoadingView()
                    takeIf { allInfo.isNotEmpty() && splitInfos.isNotEmpty() }?.run {
                        val bookMarkBean = BookMarkUtil.getInstance(this@ReadBookActivity).getAllBookMarks().firstOrNull { it.markKey == readBookBean.markKey }
                                ?.apply {
                                    page = splitInfos.takeIf { page >= it.size }?.size?.minus(1) ?: page
                                    index = allInfo.takeIf { index >= it.size }?.size?.minus(1) ?: index
                                } ?: BookMark(readBookBean.webType, readBookBean.bookName, readBookBean.authorName, readBookBean.chapterLink)
                        chapter_total_page.text = "${splitInfos.size}"
                        chaptersList.clear()
                        chaptersList.addAll(allInfo)
                        splitChaptersList.clear()
                        splitChaptersList.addAll(splitInfos)
                        ReflectUtil.copyProperteries(allInfo[bookMarkBean.index], currentReadInfo.bookChapterInfo)
                        updatePosition(bookMarkBean.page)
                        getContentInfo(LoadChapter.CURRENT)

                    } ?: (run {
                        makeMsg("未获取到数据!")
                        finish()
                    })
                }
            }

            override fun onFailed() {
                runOnUiThread {
                    DialogUtil.getInstance().hideLoadingView()
                    makeMsg("网络请求失败!")
                    finish()
                }
            }
        }).getChapters()
    }

    private fun getContentInfo(loadChapter: LoadChapter,toBookChapter: BookChapterInfo? = null){
        DialogUtil.getInstance().showLoadingDialog(this)
        val bookChapterInfo = toBookChapter ?: when (loadChapter) {
            LoadChapter.CURRENT -> currentReadInfo.bookChapterInfo
            LoadChapter.PREVIOUS -> chaptersList[currentReadInfo.bookChapterInfo.index - 1]
            LoadChapter.NEXT -> chaptersList[currentReadInfo.bookChapterInfo.index + 1]
        }
        val afterCopy = ReflectUtil.deepCopy(bookChapterInfo)
        GetChapterContentTask(afterCopy, object : GetChapterContentTask.OnTaskListener {
            override fun onSucc(content: String) {
                runOnUiThread {
                    DialogUtil.getInstance().hideLoadingView()
                    chapter_name.text = afterCopy.chapterName
                    chapter_content.text = Html.fromHtml(content)
                    ReflectUtil.copyProperteries(afterCopy, currentReadInfo.bookChapterInfo)
                    updatePosition(afterCopy.page)
                    //12.22%
                    read_progress.text =
                            NumberFormat.getPercentInstance().apply {
                                minimumFractionDigits = 2
                            }.format((currentReadInfo.bookChapterInfo.index + 1).toFloat() / chaptersList.size)
                    //save bookmark
                    BookMark(readBookBean.webType, readBookBean.bookName, readBookBean.authorName, readBookBean.chapterLink).run {
                        page = currentReadInfo.bookChapterInfo.page
                        index = currentReadInfo.bookChapterInfo.index
                        lastReadTime = System.currentTimeMillis()
                        lastReadChapter = currentReadInfo.bookChapterInfo.chapterName
                        BookMarkUtil.getInstance(this@ReadBookActivity).saveBookMark(this)
                    }
                    Handler().post {
                        read_scroll.scrollTo(0, 0)
                    }
                }
            }

            override fun onFailed() {
                runOnUiThread {
                    DialogUtil.getInstance().hideLoadingView()
                    makeMsg("网络请求失败!")
                    if (loadChapter == LoadChapter.CURRENT) finish()
                }
            }
        }).getChapterContent()
    }

    private fun searchByChapterName() {
        DialogUtil.getInstance().showLoadingDialog(this)
        hideSoft()
        hideSearchChapterLayout()
        chapter_search_et.text.toString().takeIf { it.isNotBlank() }?.run {
            chaptersList.filter {
                it.chapterName.contains(this)
            }.takeIf { it.isNotEmpty() }?.run {
                DialogUtil.getInstance().hideLoadingView()
                showSearchResultDialog(this)
            } ?: let {
                DialogUtil.getInstance().hideLoadingView()
                makeMsg("未找到相应章节，请更换关键字!")
            }
        } ?: let {
            DialogUtil.getInstance().hideLoadingView()
            makeMsg("请输入章节名!")
        }
    }

    private fun showSearchResultDialog(searchResult: List<BookChapterInfo>) {
        showFullScreenDialog(R.layout.dialog_search_chapter) { dialog, view ->
            val splitSearchResult = searchResult.split(20)
            val searchChapterLv = view.findViewById(R.id.dialog_search_chapter_lv) as ListView
            val searchPrePage = view.findViewById(R.id.dialog_search_chapter_pre_page) as TextView
            val searchNextPage = view.findViewById(R.id.dialog_search_chapter_next_page) as TextView
            searchPrePage.isEnabled = false
            searchNextPage.isEnabled = splitSearchResult.size >= 2

            val keyTag = R.id.dialog_search_chapter_lv
            val displayList = mutableListOf<BookChapterInfo>().apply { addAll(splitSearchResult[0]) }
            searchChapterLv.apply listView@{
                setTag(keyTag, 0)
                adapter = DialogSearchChapterAdapter(this@ReadBookActivity, displayList).apply adapter@{
                    setOnItemChapterClickListener {
                        dialog.dismiss()
                        getContentInfo(LoadChapter.NEXT, it)
                    }
                }
            }
            val lambdaForPage = fun(page: Int) {
                searchChapterLv.setTag(keyTag, page)
                displayList.clear()
                displayList.addAll(splitSearchResult[page])
                (searchChapterLv.adapter as DialogSearchChapterAdapter).notifyDataSetChanged()
                searchChapterLv.smoothScrollToPosition(0)
            }
            searchPrePage.setOnNoDoubleClickListener {
                val page = (searchChapterLv.getTag(keyTag) as Int) - 1
                lambdaForPage(page)
                it.isEnabled = page != 0
                searchNextPage.isEnabled = true
            }
            searchNextPage.setOnNoDoubleClickListener {
                val page = (searchChapterLv.getTag(keyTag) as Int) + 1
                lambdaForPage(page)
                it.isEnabled = page != splitSearchResult.size - 1
                searchPrePage.isEnabled = true
            }
        }
    }

    private fun goToPage(page:Int){
        currentShownPage = page
        updatePosition(currentShownPage)
        if (currentShownPage == currentReadInfo.bookChapterInfo.page) {
            lv_chapters.smoothScrollToPosition(currentReadInfo.bookChapterInfo.index - Constants.CHAPTER_NUM_FOR_EACH_PAGE * currentReadInfo.bookChapterInfo.page)
        } else {
            lv_chapters.smoothScrollToPosition(0)
        }
    }

    private fun updateChapterPageInput(currentPage:Int){
        page_skip_et.setText("")
        page_skip_et.hint = "${currentPage + 1}"
    }

    private fun updatePosition(currentPage: Int) {
        updateChapterPageInput(currentPage)
        currentDisplayChapterList.clear()
        currentDisplayChapterList.addAll(splitChaptersList[currentShownPage])
        (lv_chapters.adapter as BookChapterAdapter).notifyDataSetChanged()
        currentReadInfo.hasPreviousPage = takeIf { splitChaptersList.size < 2 }?.let { false } ?: currentPage != 0
        currentReadInfo.hasNextPage = takeIf { splitChaptersList.size < 2 }?.let { false } ?: currentPage != splitChaptersList.size.minus(1)
        currentReadInfo.hasPreviousChapter = takeIf { chaptersList.size < 2 }?.let { false } ?: currentReadInfo.bookChapterInfo.index != 0
        currentReadInfo.hasNextChapter = takeIf { chaptersList.size < 2 }?.let { false } ?: currentReadInfo.bookChapterInfo.index != chaptersList.size.minus(1)
        setChapterAndPageEnable()
    }

    internal enum class LoadChapter {
        CURRENT, PREVIOUS, NEXT
    }
}