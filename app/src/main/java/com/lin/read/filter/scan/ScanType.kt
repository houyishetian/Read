package com.lin.read.filter.scan

import android.text.InputFilter
import com.google.gson.annotations.Expose
import com.lin.read.utils.ScanInputTypeEnum
import java.io.Serializable

data class ScanTypeInfo @JvmOverloads constructor(var text: String, var checked: Boolean, var id: String?, val use4Words: Boolean = false) : Serializable
data class ScanInputInfo @JvmOverloads constructor(var tag: String, var hint: String?, var unit: String, var inputType: ScanInputTypeEnum = ScanInputTypeEnum.INT, var inputFilters: Array<InputFilter>? = null) : Serializable


//{
//   "webs":[
//      {"webName": "起点","key": "qidian","id": "1","default": true}
//   ],
//   "qidian":{
//      "scanTypes":[
//         {
//            "typeName": "榜单",
//            "key": "rankType",
//            "roleInUrl":"path",
//            "roleKeyInUrl":"chn",
//            "data":[
//                {"name":"月票","roleValueInUrl":"yuepiao","hasNoSubItems":["recomDate"]}
//            ]
//         }
//      ],
//      "inputTypes":{
//          "typeName": "筛选",
//          "data": [
//              {"key":"score","inputType": "float","hint": "8.0-10","unitPrompt": "分"}
//          ]
//
//      }
//   }
// }
data class ReadScanBean(@Expose val webs: List<ReadScanWebInfo>, @Expose val qidian: ReadScanDetailsInfo, @Expose val youshu: ReadScanDetailsInfo) : Serializable

data class ReadScanWebInfo(@Expose val webName: String, @Expose val key: String, @Expose val id: String, @Expose val default: Boolean = false): Serializable
data class ReadScanDetailsInfo(@Expose val scanTypes: List<ReadScanTypeBean>, @Expose val inputTypes: ReadScanInputBean): Serializable
data class ReadScanTypeBean(@Expose val typeName: String, @Expose val key: String, @Expose val roleInUrl: String, @Expose val roleKeyInUrl: String, @Expose val data: List<ReadScanTypeData>): Serializable
data class ReadScanTypeData(@Expose val name: String, @Expose val roleValueInUrl: String, @Expose val hasNoSubItems: List<String>? = null, @Expose val default: Boolean = false): Serializable
data class ReadScanInputBean(@Expose val typeName: String, @Expose val data: List<ReadScanInputData>): Serializable
data class ReadScanInputData(@Expose val key: String, @Expose val inputType: String, @Expose val hint: String, @Expose val unitPrompt: String): Serializable

