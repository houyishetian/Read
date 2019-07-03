package com.lin.read.utils

open class SingleInstanceHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }
        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}

open class SingleInstanceHolderWith3Params<out T, in A, in B, in C>(creator: (A, B, C) -> T) {
    private var creator: ((A, B, C) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(argA: A, argB: B, argC: C): T {
        val i = instance
        if (i != null) {
            return i
        }
        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(argA, argB, argC)
                instance = created
                creator = null
                created
            }
        }
    }
}