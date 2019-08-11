package com.lin.read.utils

import java.lang.Exception
import java.net.URLEncoder

class Constants {
    companion object {
        const val KEY_SEARCH_INFO = "KEY_SEARCH_INFO"
        const val TEXT_SCAN_START = "正在检索书籍..."
        const val TEXT_SCAN_BOOK_INFO_END = "检索书籍结束，共%d本书！"
        const val TEXT_SCAN_BOOK_INFO_BY_CONDITION_START = "正在扫描书籍..."
        const val TEXT_SCAN_BOOK_INFO_BY_CONDITION_GET_ONE = "扫描书籍，扫描到%d本..."
        const val TEXT_SCAN_BOOK_INFO_RESULT = "共找到%d本"
        const val SCAN_REQUEST_CODE = 12
        const val READ_REQUEST_CODE = 15

        const val KEY_BUNDLE_FOR_BOOK_DATA = "KEY_BUNDLE_FOR_BOOK_DATA"
        const val KEY_INTENT_FOR_BOOK_DATA = "KEY_INTENT_FOR_BOOK_DATA"

        const val SCAN_RESPONSE_SUCC = 13
        const val SCAN_RESPONSE_FAILED = 14

        const val KEY_SKIP_TO_READ = "KEY_SKIP_TO_READ"

        const val RESOLVE_FROM_NOVEL80 = "NOVEL80"
        const val RESOLVE_FROM_BIQUGE = "BIQUGE"
        const val RESOLVE_FROM_DINGDIAN = "DINGDIAN"
        const val RESOLVE_FROM_BIXIA = "BIXIA"
        const val RESOLVE_FROM_AISHU = "AISHUWANG"
        const val RESOLVE_FROM_QINGKAN = "QINGKAN"

        val SEARCH_WEB_NAME_MAP: LinkedHashMap<String, String> = linkedMapOf<String, String>().apply {
            put(RESOLVE_FROM_BIQUGE, "笔趣阁")
            put(RESOLVE_FROM_DINGDIAN, "顶点")
            put(RESOLVE_FROM_BIXIA, "笔下")
            put(RESOLVE_FROM_AISHU, "爱书网")
            put(RESOLVE_FROM_QINGKAN, "请看")
        }

        val SEARCH_WEB_BASEURL_MAP:HashMap<String,String> = hashMapOf<String, String>().apply {
            put(RESOLVE_FROM_BIQUGE, "http://www.biquge5200.com/")
            put(RESOLVE_FROM_DINGDIAN, "https://www.x23us.com/")
            put(RESOLVE_FROM_BIXIA, "http://www.bxwx666.org/")
            put(RESOLVE_FROM_AISHU, "http://www.22ff.org/")
            put(RESOLVE_FROM_QINGKAN, "https://www.qk6.org/")
        }

        val SEARCH_WEB_RETRO_PARAMS_MAP = fun(tag: String, bookName: String): Any {
            val searchKey = when (tag) {
                RESOLVE_FROM_BIQUGE, RESOLVE_FROM_AISHU -> bookName
                RESOLVE_FROM_DINGDIAN, RESOLVE_FROM_BIXIA, RESOLVE_FROM_QINGKAN -> URLEncoder.encode(bookName, "gbk")
                else -> throw Exception("cannot resolve current type:$tag")
            }
            return when (tag) {
                //"http://www.biquge5200.com/modules/article/search.php?searchkey="+bookName;
                RESOLVE_FROM_BIQUGE -> searchKey
                RESOLVE_FROM_DINGDIAN -> hashMapOf<String, String>().apply {
                    //"https://www.x23us.com/modules/article/search.php?searchtype=keywords&searchkey=" + URLEncoder.encode(params[0], "gbk");
                    put("searchtype", "keywords")
                    put("searchkey", searchKey)
                }
                // "http://www.bxwx666.org/search.aspx?bookname=" + URLEncoder.encode(params[0], "gbk")
                RESOLVE_FROM_BIXIA -> searchKey
                //"http://www.22ff.org/s_"+bookName;
                RESOLVE_FROM_AISHU -> searchKey
                //"https://www.qk6.org/novel.php?action=search&searchtype=novelname&searchkey=" + searchKey + "&input="
                RESOLVE_FROM_QINGKAN -> hashMapOf<String, String>().apply {
                    put("action", "search")
                    put("searchtype", "novelname")
                    put("input", "")
                    put("searchkey", searchKey)
                }
                else -> throw Exception("cannot resolve current params:$tag")
            }
        }

        const val WEB_QIDIAN = "起点"
        const val WEB_QIDIAN_FINISH = "完本"
        const val WEB_YOU_SHU = "优书"

        const val INPUT_INT = "int"
        const val INPUT_FLOAT = "float"

        const val ROLE_PATH = "path"
        const val ROLE_PARAM = "param"

        const val CHAPTER_NUM_FOR_EACH_PAGE = 30

        const val DEFAULT_SHARED_NAME = "BOOK_MARK_PREFS"
        const val BOOK_MARK_LIST = "BOOK_MARK_LIST"
        const val BOOK_MARK_NEW_LIST = "NEW_BOOK_MARK_LIST"
        const val BOOK_MARK_NEW_LIST2 = "NEW_BOOK_MARK_LIST2"
    }
}