package com.lin.read.utils

class ReflectUtil {
    companion object {
        fun <T> getProperty(obj: Any, propertyName: String, returnClz: Class<T>): T? {
            val fields = obj.javaClass.fields
            fields.forEach {
                if (it.name == propertyName) {
                    return returnClz.cast(it.get(obj))
                }
            }
            return invokeMethod(obj, "get" + propertyName[0].toUpperCase() + propertyName.substring(1), returnClz)
        }

        fun <T> invokeMethod(obj: Any, methodName: String, returnClz: Class<T>, vararg params: Any): T? {
            val methods = obj.javaClass.methods
            methods.forEach {
                if (it.name == methodName) {
                    if (params.isNotEmpty()) {
                        return returnClz.cast(it.invoke(obj, params))
                    } else {
                        return returnClz.cast(it.invoke(obj))
                    }
                }
            }
            return null
        }
    }
}