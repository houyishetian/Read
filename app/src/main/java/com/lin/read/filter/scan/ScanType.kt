package com.lin.read.filter.scan

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

data class BookLinkInfo(val bookLink: String, var page: Int?, var position: Int?) : Serializable

data class VarPair<A, B>(var first: A, var second: B) : Serializable

infix fun <A, B> A.toPair(that: B): VarPair<A, B> = VarPair(this, that)

data class VarTriple<A, B, C>(var first: A, var second: B, var third: C) : Serializable

fun <A, B, C> A.toTriple(second: B, third: C): VarTriple<A, B, C> = VarTriple(this, second, third)
