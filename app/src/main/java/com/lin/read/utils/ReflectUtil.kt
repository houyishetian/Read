package com.lin.read.utils

import android.util.Log

class ReflectUtil {
    companion object {
        fun <T> getProperty(obj: Any, propertyName: String, returnClz: Class<T>): T? {
            //get property or getter
            return obj.javaClass.declaredFields.apply { forEach { it.isAccessible = true } }.firstOrNull { it.name == propertyName }?.let { returnClz.cast(it.get(obj)) }
                    ?: invokeMethod(obj, "get" + propertyName[0].toUpperCase() + propertyName.substring(1), returnClz)
        }

        fun <T> invokeMethod(obj: Any, methodName: String, returnClz: Class<T>, vararg params: Any): T? {
            obj.javaClass.methods.forEach {
                Log.d("available item", "${it.name},params type:${it.parameterTypes.map { it.name }.joinToString("/")}")
            }
            return obj.javaClass.getMethod(methodName, *params.map {
                it::class.javaPrimitiveType ?: it.javaClass
            }.toTypedArray()).invoke(obj, *params).let {
                (it as? T) ?: returnClz.cast(it)
            }
        }

        fun <T : Any> copyProperteries(src: T, des: T) {
            src.javaClass.declaredFields.forEach {
                it.isAccessible = true
                it.set(des, it[src])
            }
        }
    }
}