package com.lin.read.utils

import android.content.Context
import android.content.SharedPreferences
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedUtil<T:Any>(private val ctx: Context, private val key: String, private val default: T, private val sharedName: String = Constants.DEFAULT_SHARED_NAME) : ReadWriteProperty<Any?, T> {
    private val shared: SharedPreferences by lazy { ctx.getSharedPreferences(sharedName, Context.MODE_PRIVATE) }
    private val CHARSET_NAME = "ISO-8859-1"
    override fun getValue(thisRef: Any?, property: KProperty<*>): T = shared.run {
        val result:Any = when (default) {
            is String -> getString(key, default)
            is Long -> getLong(key, default)
            is Float -> getFloat(key, default)
            is Int -> getInt(key, default)
            is Boolean -> getBoolean(key, default)
            else -> parseStr2Obj(getString(key, parseObj2Str(default)))
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
                else -> putString(key, parseObj2Str(value))
            }
        }.apply()
    }

    fun removeKey() = shared.edit().remove(key).apply()

    fun clear() = shared.edit().clear().apply()

    private fun parseObj2Str(value: T): String {
        val byteOutputStream = ByteArrayOutputStream()
        val objOutputStream = ObjectOutputStream(byteOutputStream)
        objOutputStream.writeObject(value)
        return byteOutputStream.toString(CHARSET_NAME).apply {
            objOutputStream.close()
            byteOutputStream.close()
        }
    }

    private fun parseStr2Obj(value: String): T {
        val byteInputStream = ByteArrayInputStream(value.toByteArray(charset(CHARSET_NAME)))
        val objInputStream = ObjectInputStream(byteInputStream)
        return (objInputStream.readObject() as T).apply {
            byteInputStream.close()
            objInputStream.close()
        }
    }
}