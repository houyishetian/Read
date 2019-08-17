package com.lin.read.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.widget.Toast
import com.lin.read.filter.search.BookChapterInfo
import okhttp3.ResponseBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.charset.Charset

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

fun ResponseBody.readLinesOfHtml(): List<String> {
    val byteOutPutStream = ByteArrayOutputStream()
    byteStream().copyTo(byteOutPutStream)
    val inputStream0 = ByteArrayInputStream(byteOutPutStream.toByteArray())
    val inputStream1 = ByteArrayInputStream(byteOutPutStream.toByteArray())
    val charset = inputStream0.bufferedReader().readLines().firstOrNull { it.toLowerCase().contains("charset=\"?[^\"^\n^;]+\"?".toRegex()) }?.let {
        StringKtUtil.getDataFromContentByRegex(it.toLowerCase(), "charset=\"?([^\"^\n^;]+)\"?", listOf(1))?.get(0)
    } ?: throw Exception("no charset found!")
    Log.d("charset", charset)
    return inputStream1.bufferedReader(Charset.forName(charset)).readLines().apply {
        inputStream0.close()
        inputStream1.close()
        byteOutPutStream.close()
    }
}

fun String.baseUrl(): String {
    URL(this).apply {
        return "$protocol://$host/"
    }
}