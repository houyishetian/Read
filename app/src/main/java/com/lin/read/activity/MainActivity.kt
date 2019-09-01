package com.lin.read.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import com.lin.read.R
import com.lin.read.fragment.BookMarksFragment
import com.lin.read.fragment.ScanFragment
import com.lin.read.fragment.SearchFragment
import com.lin.read.utils.addFragment
import com.lin.read.utils.hideFragment
import com.lin.read.utils.makeMsg
import com.lin.read.utils.showFragment
import kotlinx.android.synthetic.main.activity_main.*

class  MainActivity : FragmentActivity(),View.OnClickListener {
    private lateinit var fragmentViews:HashMap<View,Fragment>
    private lateinit var scanFragment: ScanFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var bookMarksFragment: BookMarksFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
    }

    private fun initData() {
        fragmentViews = hashMapOf()

        scanFragment = ScanFragment()
        searchFragment = SearchFragment()
        bookMarksFragment = BookMarksFragment()

        fragmentViews.put(rl_scan, scanFragment)
        fragmentViews.put(rl_search, searchFragment)
        fragmentViews.put(rl_history, bookMarksFragment)

        addFragment(R.id.fragment_container, scanFragment).addFragment(R.id.fragment_container, searchFragment).addFragment(R.id.fragment_container, bookMarksFragment)

        showFragment(scanFragment)
        hideFragment(searchFragment)
        hideFragment(bookMarksFragment)

        rl_scan.setOnClickListener(this)
        rl_search.setOnClickListener(this)
        rl_history.setOnClickListener(this)
    }

    private fun setClickedRlBackground(view: View) {
        fragmentViews.forEach { (it, _) ->
            if (view === it) it.setBackgroundResource(R.color.main_selected) else it.setBackgroundResource(R.color.main_unselected)
        }
    }

    override fun onClick(v: View?) {
        if(v != rl_scan && scanFragment.isFilterLayoutVisible()){
            scanFragment.hideFilter(false)
        }
        setClickedRlBackground(v!!)
        fragmentViews.forEach{(view,fragment)->
            if(v === view) showFragment(fragment) else hideFragment(fragment)
        }
        scanFragment.hideSoft()
        searchFragment.hideSoft()
    }

    fun hideBottomViews(hide:Boolean){
        val visibility = if(hide) View.GONE else View.VISIBLE
        main_split_view.visibility = visibility
        ll_main_functions.visibility = visibility
    }

    fun clickScanBookItem(bookName:String){
        searchFragment.setSearchType(bookName)
        rl_search.performClick()
    }

    private var lastClickTime: Long = 0
    override fun onBackPressed() {
        if (scanFragment.isFilterLayoutVisible()) {
            scanFragment.hideFilter()
            return
        }
        if (bookMarksFragment.isEditMode()) {
            bookMarksFragment.exitEditMode()
            return
        }
        val currentClickTime = System.currentTimeMillis()
        if (currentClickTime - lastClickTime <= 2000) {
            super.onBackPressed()
            return
        }
        lastClickTime = currentClickTime
        makeMsg("再次按返回键退出")
    }
}