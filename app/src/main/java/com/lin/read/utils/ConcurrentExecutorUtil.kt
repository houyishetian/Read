package com.lin.read.utils

import java.util.concurrent.*

class ConcurrentExecutorUtil {
    companion object {
        private val service: ExecutorService = Executors.newCachedThreadPool()
        fun <T> execute(requests: List<Callable<T>>): MutableList<T> {
            val readyLatch = CountDownLatch(requests.size)
            val startLatch = CountDownLatch(1)
            val endLatch = CountDownLatch(requests.size)
            val result = mutableListOf<T>()
            for (item in requests) {
                service.execute {
                    readyLatch.countDown()
                    startLatch.await()
                    val t = item.call()
                    if (t != null) result.add(t)
                    endLatch.countDown()
                }
            }
            readyLatch.await()
            startLatch.countDown()
            endLatch.await()
            return result
        }
    }
}