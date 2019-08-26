package com.lin.read.utils

import android.view.View

abstract class NoDoubleClickListener : View.OnClickListener {
    companion object {
        @JvmStatic
        private val MIN_CLICK_DELAY_TIME = 800
        @JvmStatic
        private var lastClickTime: Long = 0
    }

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            onNoDoubleClick(v)
        }
    }

    abstract fun onNoDoubleClick(v: View)
}