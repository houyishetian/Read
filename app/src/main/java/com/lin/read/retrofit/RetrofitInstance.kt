package com.lin.read.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance (baseUrl: String) {
//    companion object : SingleInstanceHolder<RetrofitInstance, String>(::RetrofitInstance)

    private val retrofit: Retrofit

    init {
        val okhttpclient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(object: Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                        var response = chain.proceed(request)
                        Log.e("Test","${request.url} --> ${response.code}")
                        if ( response.code == 301 || response.code == 302) {
                            val location = response.headers.get("Location");
                            Log.e("redirect url：", "location = " + location);
                            val newRequest = request.newBuilder().url(location!!).build()
                            response = chain.proceed(newRequest);
                        }
                        return response;
                    }
                })
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okhttpclient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun <T> create(clz: Class<T>): T {
        return retrofit.create(clz)
    }
}