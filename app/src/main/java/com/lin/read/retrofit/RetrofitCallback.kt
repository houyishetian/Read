package com.lin.read.retrofit

import rx.Observer

abstract class RetrofitCallback<T>:Observer<T> {
    override fun onNext(t: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(e: Throwable?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompleted() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}