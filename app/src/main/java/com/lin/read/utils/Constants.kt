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
        const val CODE_FROM_READING_UPDATE_BOOKMARK = 15
        const val CODE_FROM_STORAGE_PERMISSIONS = 16

        const val KEY_BUNDLE_FOR_BOOK_DATA = "KEY_BUNDLE_FOR_BOOK_DATA"
        const val KEY_INTENT_FOR_BOOK_DATA = "KEY_INTENT_FOR_BOOK_DATA"

        const val SCAN_RESPONSE_SUCC = 13
        const val SCAN_RESPONSE_FAILED = 14

        const val KEY_SKIP_TO_READ = "KEY_SKIP_TO_READ"

        const val RESOLVE_FROM_BIQUGE = "BIQUGE"
        const val RESOLVE_FROM_BIQUGE2 = "BIQUGE2"
        const val RESOLVE_FROM_BIQUGE3 = "BIQUGE3"
        const val RESOLVE_FROM_DINGDIAN = "DINGDIAN"
        const val RESOLVE_FROM_BIXIA = "BIXIA"
        const val RESOLVE_FROM_AISHU = "AISHUWANG"
        const val RESOLVE_FROM_QINGKAN = "QINGKAN"
        const val RESOLVE_FROM_DPCQ = "DPCQ"

        val SEARCH_WEB_NAME_MAP: LinkedHashMap<String, String> = linkedMapOf<String, String>().apply {
            put(RESOLVE_FROM_BIQUGE, "笔趣阁")
//            put(RESOLVE_FROM_BIQUGE2, "笔趣2")
            put(RESOLVE_FROM_BIQUGE3, "笔趣3")
            put(RESOLVE_FROM_DINGDIAN, "顶点")
//            put(RESOLVE_FROM_BIXIA, "笔下")
            put(RESOLVE_FROM_AISHU, "爱书网")
            put(RESOLVE_FROM_QINGKAN, "请看")
            put(RESOLVE_FROM_DPCQ, "DPCQ")
        }

        val SEARCH_WEB_BASEURL_MAP:HashMap<String,String> = hashMapOf<String, String>().apply {
            put(RESOLVE_FROM_BIQUGE, "https://www.biquge5200.com/")
            put(RESOLVE_FROM_BIQUGE2, "https://so.biqusoso.com/")
            put(RESOLVE_FROM_BIQUGE3, "https://www.xbiquge6.com/")
            put(RESOLVE_FROM_DINGDIAN, "https://www.23wxc.com/")
            put(RESOLVE_FROM_BIXIA, "https://www.bxwx666.org/")
            put(RESOLVE_FROM_AISHU, "http://www.22ff.org/")
            put(RESOLVE_FROM_QINGKAN, "https://www.qk6.org/")
            put(RESOLVE_FROM_DPCQ, "https://dpcq1.net/")
        }

        val SEARCH_WEB_RETRO_PARAMS_MAP = fun(tag: String, bookName: String): Any {
            val searchKey = when (tag) {
                RESOLVE_FROM_BIQUGE, RESOLVE_FROM_BIQUGE2, RESOLVE_FROM_BIQUGE3, RESOLVE_FROM_AISHU, RESOLVE_FROM_DPCQ -> bookName
                RESOLVE_FROM_DINGDIAN, RESOLVE_FROM_BIXIA, RESOLVE_FROM_QINGKAN -> URLEncoder.encode(bookName, "gbk")
                else -> throw Exception("cannot resolve current type:$tag")
            }
            return when (tag) {
                //"http://www.biquge5200.com/modules/article/search.php?searchkey="+bookName;
                // "http://www.bxwx666.org/search.aspx?bookname=" + URLEncoder.encode(params[0], "gbk")
                //"http://www.22ff.org/s_"+bookName;
                //http://www.37shuwu.com/modules/article/ss.php?searchkey=%CF%C9%C4%E6
                RESOLVE_FROM_BIQUGE, RESOLVE_FROM_BIQUGE3, RESOLVE_FROM_BIXIA, RESOLVE_FROM_AISHU -> searchKey
                RESOLVE_FROM_DINGDIAN -> hashMapOf<String, String>().apply {
                    //"https://www.x23us.com/modules/article/search.php?searchtype=articlename&searchkey=" + URLEncoder.encode(params[0], "gbk");
                    put("searchtype", "articlename")
                    put("searchkey", searchKey)
                }
                //"https://www.qk6.org/novel.php?action=search&searchtype=novelname&searchkey=" + searchKey + "&input="
                RESOLVE_FROM_QINGKAN -> hashMapOf<String, String>().apply {
                    put("action", "search")
                    put("searchtype", "novelname")
                    put("input", "")
                    put("searchkey", searchKey)
                }
                //https://dpcq1.net/search.html?searchtype=novelname&searchkey=%E8%9B%8A%E7%9C%9F%E4%BA%BA
                RESOLVE_FROM_DPCQ -> hashMapOf<String, String>().apply {
                    put("searchtype", "novelname")
                    put("searchkey", searchKey)
                }
                RESOLVE_FROM_BIQUGE2 -> hashMapOf<String, String>().apply {
                    //https://so.biqusoso.com/s.php?ie=utf-8&siteid=biqugex.com&q=%E8%B6%85%E7%BB%B4%E6%9C%AF%E5%A3%AB
                    put("ie", "utf-8")
                    put("siteid", "biqugex.com")
                    put("q", searchKey)
                }
                else -> throw Exception("cannot resolve current params:$tag")
            }
        }

        const val WEB_YOU_SHU = "优书"

        const val WEB_QIDIAN_ID = "qidian"
        const val WEB_QIDIAN_FINISH_ID = "qidianfinish"
        const val WEB_CHUANGSHI_ID = "chuangshi"
        const val WEB_YOUSHU_ID = "youshu"

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