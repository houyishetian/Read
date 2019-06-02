package com.lin.read.utils;

import com.lin.read.R;
import com.lin.read.filter.scan.ScanTypeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lisonglin on 2017/10/15.
 */

public class Constants {
    public static final String KEY_SEARCH_INFO="KEY_SEARCH_INFO";
    public static final String TEXT_SCAN_START="正在检索书籍...";
    public static final String TEXT_SCAN_BOOK_INFO_END="检索书籍结束，共%d本书！";
    public static final String TEXT_SCAN_BOOK_INFO_BY_CONDITION_START="正在扫描书籍...";
    public static final String TEXT_SCAN_BOOK_INFO_BY_CONDITION_GET_ONE="扫描书籍，扫描到%d本...";
    public static final String TEXT_SCAN_BOOK_INFO_BY_CONDITION_END="扫描书籍结束，共扫描到%d本！";
    public static final String TEXT_SCAN_BOOK_INFO_RESULT="共找到%d本";
    public static final int SCAN_REQUEST_CODE=12;
    public static final int READ_REQUEST_CODE=15;

    public static final String KEY_BUNDLE_FOR_BOOK_DATA="KEY_BUNDLE_FOR_BOOK_DATA";
    public static final String KEY_INTENT_FOR_BOOK_DATA="KEY_INTENT_FOR_BOOK_DATA";

    public static final int SCAN_RESPONSE_SUCC=13;
    public static final int SCAN_RESPONSE_FAILED=14;

    public static final String KEY_SKIP_TO_READ="KEY_SKIP_TO_READ";

    public static final String RESOLVE_FROM_NOVEL80 = "NOVEL80";
    public static final String RESOLVE_FROM_BIQUGE = "BIQUGE";
    public static final String RESOLVE_FROM_DINGDIAN = "DINGDIAN";
    public static final String RESOLVE_FROM_BIXIA = "BIXIA";
    public static final String RESOLVE_FROM_AISHU = "AISHUWANG";
    public static final String RESOLVE_FROM_QINGKAN = "QINGKAN";

    public static final String EXCEPTION_GET_CONN_ERROR="EXCEPTION_GET_CONN_ERROR";

    public static List<ScanTypeInfo> scanWebTypeList;
    public static final String WEB_QIDIAN="起点";
    public static final String WEB_ZONGHENG="纵横";
    public static final String WEB_17K="17k";
    public static final String WEB_YOU_SHU="优书";

    public static Map<String,Integer> scanWebTypeLayoutIdsMap;
    static{
        scanWebTypeList=new ArrayList<>();
        scanWebTypeList.add(new ScanTypeInfo(WEB_QIDIAN, false, "1"));
//        scanWebTypeList.add(new ScanTypeInfo(WEB_ZONGHENG, false, "2"));
//        scanWebTypeList.add(new ScanTypeInfo(WEB_17K, false, "3"));
        scanWebTypeList.add(new ScanTypeInfo(WEB_YOU_SHU, false, "4"));

        scanWebTypeLayoutIdsMap = new HashMap<>();
        scanWebTypeLayoutIdsMap.put(WEB_QIDIAN, R.id.layout_scan_qidian);
        scanWebTypeLayoutIdsMap.put(WEB_ZONGHENG, R.id.layout_scan_zongheng);
        scanWebTypeLayoutIdsMap.put(WEB_17K, R.id.layout_scan_17k);
        scanWebTypeLayoutIdsMap.put(WEB_YOU_SHU, R.id.layout_scan_youshu);
    }
}
