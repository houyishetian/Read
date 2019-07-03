package com.lin.read.filter.scan

import com.google.gson.annotations.Expose
import java.io.Serializable

//{
//   "webs":[
//      {"name": "起点","key": "qidian","id": "1","default": true}
//   ],
//   "qidian":{
//      "mainUrl": ["http://www.yousuu.com/"],
//      "scanTypes":[
//         {
//            "typeName": "榜单",
//            "key": "rankType",
//            "roleInUrl":"path",
//            "roleKeyInUrl":"chn",
//			  "use4Words": "true",
//            "data":[
//                {"name":"月票","roleValueInUrl":"yuepiao","hasNoSubItems":["recomDate"]}
//            ]
//         }
//      ],
//      "inputTypes":{
//          "typeName": "筛选",
//          "data": [
//              {"typeName": "分数","key":"score","inputType": "float","hint": "8.0-10","unitPrompt": "分","default": 8.0,"min": 8.0,"max": 10.0}
//          ]
//
//      }
//   }
// }
data class ReadScanBean(@Expose val webs: List<ReadScanTypeData>, @Expose val qidian: ReadScanDetailsInfo, @Expose val youshu: ReadScanDetailsInfo, @Expose val qidianfinish: ReadScanDetailsInfo) : Serializable

data class ReadScanDetailsInfo(@Expose val mainUrl: List<String>, @Expose val scanTypes: List<ReadScanTypeBean>, @Expose val inputTypes: ReadScanInputBean?) : Serializable
data class ReadScanTypeBean(@Expose val typeName: String, @Expose val key: String, @Expose val roleInUrl: String, @Expose val roleKeyInUrl: String?, @Expose val use4Words: Boolean = false, @Expose val data: List<ReadScanTypeData>) : Serializable
data class ReadScanTypeData(@Expose val name: String,@Expose val key: String, @Expose val roleValueInUrl: String, @Expose val hasNoSubItems: List<String>?, @Expose val default: Boolean = false) : Serializable{
    var checked:Boolean
    init {
        checked = false
    }
}
data class ReadScanInputBean(@Expose val typeName: String, @Expose val data: List<ReadScanInputData>) : Serializable
data class ReadScanInputData(@Expose val typeName: String?, @Expose val key: String, @Expose val inputType: String, @Expose val hint: String, @Expose val unitPrompt: String, @Expose val default:Number?, @Expose val min:Number?, @Expose val max:Number?) : Serializable

data class ReadScanSelectedBean(val typeName: String, val key: String, val name: String, val roleInUrl: String, val roleKeyInUrl: String?, val roleValueInUrl: String?) : Serializable
data class ReadScanInputtedBean(val typeName: String?, val key: String, val inputType: String, val value: Number?, val min: Number?, val max: Number?) : Serializable

data class ScanInfo(val webName: String?, val mainUrl: List<String>?, val rolePathValue: String?, val roleParamPairs: Map<String,String>?, val inputtedBeans: List<ReadScanInputtedBean>?, var page: Int = 1) : Serializable

data class BookLinkInfo(val bookLink: String, var page: Int?, var position: Int?) : Serializable
