package com.lin.read.utils

import android.content.Context
import android.content.SharedPreferences
import java.lang.Exception
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedUtil<T>(private val ctx: Context, private val key: String, private val default: T, private val sharedName: String = Constants.DEFAULT_SHARED_NAME) : ReadWriteProperty<Any?, T> {
    private val shared: SharedPreferences by lazy { ctx.getSharedPreferences(sharedName, Context.MODE_PRIVATE) }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = shared.run {
        val result = when (default) {
            is String -> getString(key, default)
            is Long -> getLong(key, default)
            is Float -> getFloat(key, default)
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            else -> throw Exception("the default format is wrong")
        }
        result as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = shared.run {
        edit().apply {
            when (value) {
                is String -> putString(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                else -> throw Exception("the default format is wrong")
            }
        }.apply()
    }

    fun removeKey() = shared.edit().remove(key).apply()
}