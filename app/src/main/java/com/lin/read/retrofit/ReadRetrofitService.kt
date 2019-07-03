package com.lin.read.retrofit

import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

interface ReadRetrofitService {
    //get qidian book list
    @GET("/rank/{last}?")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getQiDianBookList(@Path("last") path: String, @QueryMap map: Map<String, String>, @Query("page") page: Int = 1): Observable<ResponseBody>

    //get qidian finish book list
    @GET("/finish")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getQiDianBookList(@QueryMap map: Map<String, String>, @Query("page") page: Int = 1): Observable<ResponseBody>

    //get qidian book details
    @GET("/book/{bookId}")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getQiDianBookDetails(@Path("bookId") bookId:String): Observable<ResponseBody>

    //get you shu book list and book details
    @GET("/category/{last}?")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getYouShuBookList(@Path("last") path: String, @QueryMap map: Map<String, String>, @Query("page") page: Int): Observable<ResponseBody>
}