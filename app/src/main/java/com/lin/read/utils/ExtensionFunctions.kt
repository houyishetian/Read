package com.lin.read.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.widget.Toast

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

fun Any?.printContent() {
    this ?: Log.d("debug", "${this}")
    this?.run {
        var tag = 0
        for (index in 0 until toString().length step 3000) {
            val next = if (index + 3000 <= toString().length - 1) index + 3000 else toString().length - 1
            Log.d("debug", "${toString().substring(index, next)}")
            tag++
        }
    }
}

