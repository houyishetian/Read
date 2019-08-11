package com.lin.read.filter

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
data class BookMark(val webType: String, val bookName: String, val authorName: String, val chapterLink: String,
                    var page: Int = 0, var index: Int = 0, var lastReadTime: Long = 0, var lastReadChapter: String = "") : Serializable, Parcelable {
    val markKey: String
        get() = arrayOf(webType, bookName, authorName, chapterLink).joinToString("_")
    @IgnoredOnParcel
    var isChecked: Boolean = false
    @IgnoredOnParcel
    var isShowCheckBox: Boolean = false
}

@Parcelize
data class ReadBookBean(val webType: String, val bookName: String, val authorName: String, val chapterLink: String) : Serializable, Parcelable {
    val markKey: String
        get() = arrayOf(webType, bookName, authorName, chapterLink).joinToString("_")
}

/**
//zongheng
private String raiseNum;
private String commentNum;

 */
//@Parcelize
//data class BookInfof(val webType: String,val bookName: String,val authorName: String,val wordsNum:String,val score:String,val scoreNum:String,
//                    val lastUpdate:String,val lastChapter:String,val position:Int):Serializable,Parcelable

@Parcelize
data class SearchBookBean(val webType: String, var bookName: String = "", var authorName: String = "",
                          var lastUpdate: String = "", var lastChapter: String = "", var chapterLink: String = "") : Serializable, Parcelable