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
            fun parseClass(ori: Class<*>): Class<*> {
                return when (ori) {
                    java.lang.Integer::class.java -> Int::class.java
                    java.lang.Byte::class.java -> Byte::class.java
                    java.lang.Short::class.java -> Short::class.java
                    java.lang.Long::class.java -> Long::class.java
                    java.lang.Double::class.java -> Double::class.java
                    java.lang.Float::class.java -> Float::class.java
                    java.lang.Character::class.java -> Char::class.java
                    java.lang.Boolean::class.java -> Boolean::class.java
                    else -> ori
                }
            }

            return obj.javaClass.getMethod(methodName, *params.map { parseClass(it.javaClass) }.toTypedArray()).invoke(obj, *params).let {
                (it as? T) ?: returnClz.cast(it)
            }
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