package com.lin.read.utils

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
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

