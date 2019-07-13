package com.lin.read.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.widget.Toast
import com.lin.read.filter.search.BookChapterInfo

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit){
    val fragmentTransaction = this.beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun FragmentActivity.addFragment(id: Int, fragment: Fragment):FragmentActivity {
    this.supportFragmentManager.inTransaction { add(id, fragment) }
    return this
}

fun FragmentActivity.showFragment(fragment: Fragment) {
    this.supportFragmentManager.inTransaction {
        show(fragment)
    }
}

fun FragmentActivity.hideFragment(fragment: Fragment){
    this.supportFragmentManager.inTransaction {
        hide(fragment)
    }
}

@JvmOverloads
fun Activity.makeMsg(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, duration).show()
}

@JvmOverloads
fun List<BookChapterInfo>.split(eachPageLen: Int, resetPageAndIndex: Boolean = false): List<MutableList<BookChapterInfo>> {
    val splitResult = mutableListOf<MutableList<BookChapterInfo>>()
    this.takeIf { it.isNotEmpty() }?.let {
        val maxPage = it.takeIf { it.size % eachPageLen == 0 }?.size?.div(eachPageLen)
                ?: (it.size.div(eachPageLen) + 1)
        for (page in 0 until maxPage) {
            val subList = mutableListOf<BookChapterInfo>()
            val currentMaxLen = it.takeIf { (page + 1) * eachPageLen > it.size }?.size
                    ?: ((page + 1) * eachPageLen)
            for (index in page * eachPageLen until currentMaxLen) {
                subList.add(it[index].apply {
                    if (resetPageAndIndex) {
                        this.page = page
                        this.index = index
                    }
                })
            }
            splitResult.add(subList)
        }
    }
    return splitResult
}