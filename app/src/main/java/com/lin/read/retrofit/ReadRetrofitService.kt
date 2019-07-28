package com.lin.read.retrofit

import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

interface ReadRetrofitService {
    //get qidian book list
    @GET("/rank/{last}?")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getQiDianBookList(@Path("last") path: String, @QueryMap(encoded = true) map: Map<String, String>, @Query("page") page: Int = 1): Observable<ResponseBody>

    //get qidian finish book list
    @GET("/finish")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getQiDianBookList(@QueryMap(encoded = true) map: Map<String, String>, @Query("page") page: Int = 1): Observable<ResponseBody>

    //get qidian book details
    @GET("/book/{bookId}")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getQiDianBookDetails(@Path("bookId") bookId:String): Observable<ResponseBody>

    //get you shu book list and book details
    @GET("/category/{last}")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    fun getYouShuBookList(@Path("last") path: String, @QueryMap(encoded = true) map: Map<String, String>, @Query("page") page: Int): Observable<ResponseBody>

    @GET("/modules/article/search.php")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    //"http://www.biquge5200.com/modules/article/search.php?searchkey="+bookName;
    fun searchFromBIQUGE(@Query("searchkey", encoded = true) bookName: String): Observable<ResponseBody>

    @GET("/modules/article/search.php")
    @Headers("User-Agent:Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
    //"https://www.x23us.com/modules/article/search.php?searchtype=keywords&searchkey=" + URLEncoder.encode(params[0], "gbk");
    fun searchFromDINGDIAN(@QueryMap(encoded = true) params:HashMap<String,String>): Observable<ResponseBody>

    @GET("/search.aspx")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    // "http://www.bxwx666.org/search.aspx?bookname=" + URLEncoder.encode(params[0], "gbk")
    fun searchFromBIXIA(@Query("bookname", encoded = true) bookNameEncoded: String): Observable<ResponseBody>

    @GET("/s_{last}")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    //"http://www.22ff.org/s_"+bookName;
    fun searchFromAISHUWANG(@Path("last") bookName: String): Observable<ResponseBody>

    @GET("/novel.php")
    @Headers("User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
    //"https://www.qk6.org/novel.php?action=search&searchtype=novelname&searchkey=" + searchKey + "&input="
    fun searchFromQINGKAN(@QueryMap(encoded = true) params:HashMap<String,String>): Observable<ResponseBody>
}