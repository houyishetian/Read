package com.lin.read.utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ReflectUtil {
    companion object {
        fun <T> getProperty(obj: Any, propertyName: String, returnClz: Class<T>): T? {
            //get property or getter
            return obj.javaClass.declaredFields.apply { forEach { it.isAccessible = true } }.firstOrNull { it.name == propertyName }?.let { returnClz.cast(it.get(obj)) }
                    ?: invokeMethod(obj, "get" + propertyName[0].toUpperCase() + propertyName.substring(1), returnClz)
        }

        fun <T> invokeMethod(obj: Any, methodName: String, returnClz: Class<T>, vararg params: Any): T? {
            return obj.javaClass.methods.firstOrNull { it.name == methodName }?.let { if (params.isNotEmpty()) returnClz.cast(it.invoke(obj, params)) else returnClz.cast(it.invoke(obj)) }
        }

        fun <T> deepCopy(src: T): T {
            val byteOut = ByteArrayOutputStream()
            ObjectOutputStream(byteOut).writeObject(src)
            return ObjectInputStream(ByteArrayInputStream(byteOut.toByteArray())).readObject() as T
        }

        fun <T : Any> copyProperteries(src: T, des: T) {
            src.javaClass.declaredFields.forEach {
                it.isAccessible = true
                it.set(des, it[src])
            }
        }
    }
}