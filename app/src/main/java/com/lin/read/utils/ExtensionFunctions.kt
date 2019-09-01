package com.lin.read.utils

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Point
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Toast
import com.lin.read.R
import com.lin.read.filter.search.BookChapterInfo
import okhttp3.ResponseBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
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

fun Fragment.makeMsg(msg: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(activity, msg, duration).show()
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

fun Cursor.forEachRow(block: (Cursor) -> Unit) {
    this.takeIf { it.moveToFirst() }?.run {
        for (index in 0 until count) {
            moveToPosition(index)
            block(this)
        }
    }
}

fun Cursor.getAllColumnsByString(): LinkedHashMap<String, String> {
    return linkedMapOf<String, String>().also {
        for (index in 0 until columnCount) {
            it.put(getColumnName(index), getString(index))
        }
    }
}

fun Cursor.getAllColumnsByType(): LinkedHashMap<String, Any> {
    return linkedMapOf<String, Any>().also {
        for (index in 0 until columnCount) {
            it.put(getColumnName(index), when (getType(index)) {
                Cursor.FIELD_TYPE_BLOB -> getBlob(index)
                Cursor.FIELD_TYPE_FLOAT -> getFloat(index)
                Cursor.FIELD_TYPE_INTEGER -> getInt(index)
                Cursor.FIELD_TYPE_NULL -> ""
                Cursor.FIELD_TYPE_STRING -> getString(index)
                else -> throw java.lang.Exception("unsupported type!")
            })
        }
    }
}

fun Activity.showFullScreenDialog(layoutId: Int, outCancellable: Boolean = false, block: (Dialog, View) -> Unit) {
    Dialog(this, R.style.Dialog_Fullscreen).also {
        it.show()
        val view = LayoutInflater.from(this).inflate(layoutId, null)
        Point().apply {
            windowManager.defaultDisplay.getSize(this)
            it.setContentView(view, ViewGroup.LayoutParams(x, y))
        }
        it.setCanceledOnTouchOutside(outCancellable)
        it.setCancelable(true)
        block(it, view)
    }
}

inline fun <T, R> Iterable<T>.minusBy(elements: Iterable<T>, block: (T) -> R): List<T> {
    return elements.toList().takeIf { it.isNotEmpty() }?.let {
        this.filterNot { pendingRemovedItem ->
            it.firstOrNull { block(pendingRemovedItem) == block(it) } != null
        }
    } ?: this.toList()
}

fun Fragment.shareFile(filePath: String, requestCode: Int? = null, title: String = "分享") {
    Intent(Intent.ACTION_SEND).run {
        type = "*/*"
        setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(activity, getString(R.string.file_provider_auth), File(filePath)))
        requestCode?.let {
            startActivityForResult(Intent.createChooser(this, title), requestCode)
        } ?: startActivity(Intent.createChooser(this, title))
    }
}

fun Activity.shareFile(filePath: String, requestCode: Int? = null, title: String = "分享") {
    Intent(Intent.ACTION_SEND).run {
        type = "*/*"
        setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this@shareFile, getString(R.string.file_provider_auth), File(filePath)))
        requestCode?.let {
            startActivityForResult(Intent.createChooser(this, title), requestCode)
        } ?: startActivity(Intent.createChooser(this, title))
    }
}

fun View.setOnNoDoubleClickListener(block: (View) -> Unit) {
    this.setOnClickListener(object : NoDoubleClickListener() {
        override fun onNoDoubleClick(v: View) {
            block(v)
        }
    })
}

fun Animation.animationListener(start: ((Animation?) -> Unit)? = null, repeat: ((Animation?) -> Unit)? = null, end: ((Animation?) -> Unit)? = null) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            repeat?.invoke(animation)
        }

        override fun onAnimationEnd(animation: Animation?) {
            end?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animation?) {
            start?.invoke(animation)
        }
    })
}