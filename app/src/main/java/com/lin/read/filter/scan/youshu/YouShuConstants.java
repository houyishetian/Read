package com.lin.read.filter.scan.youshu;

import com.lin.read.filter.scan.ScanTypeInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class YouShuConstants {
    public static final String YS_CATE_ALL = "全部分类";
    public static final String YS_CATE_FANTASY = "玄幻奇幻";
    public static final String YS_CATE_WUXIA = "仙侠武侠";
    public static final String YS_CATE_CITY = "现代都市";
    public static final String YS_CATE_HISTORY = "历史军事";
    public static final String YS_CATE_GAME = "游戏竞技";
    public static final String YS_CATE_FUTURE = "科幻灵异";
    public static final String YS_CATE_DOUJIN = "同人短篇";
    public static final String YS_CATE_LOVE = "古代言情";
    public static final String YS_CATE_ROMANCE = "现代言情";
    public static final String YS_CATE_FANTASYLOVE = "玄幻言情";
    public static final String YS_CATE_LITERATURE = "文学艺术";
    public static final String YS_CATE_NIJI = "二次元";

    public static final String YS_TYPE_UNLIMIT = "不限制";

    public static final String YS_WORDS_1 = "10万字-";
    public static final String YS_WORDS_2 = "10-30万";
    public static final String YS_WORDS_3 = "30-50万";
    public static final String YS_WORDS_4 = "50-100万";
    public static final String YS_WORDS_5 = "100-200万";
    public static final String YS_WORDS_6 = "200万+";

    public static final String YS_STATUS_1 = "更新中";
    public static final String YS_STATUS_2 = "已完结";
    public static final String YS_STATUS_3 = "已断更";

    public static final String YS_UPDATE_1 = "三日内";
    public static final String YS_UPDATE_2 = "七日内";
    public static final String YS_UPDATE_3 = "一月内";
    public static final String YS_UPDATE_4 = "六月内";

    public static final String YS_SORT_DEFAULT = "默认";
    public static final String YS_SORT_TOTAL = "综合";
    public static final String YS_SORT_WORD = "字数";
    public static final String YS_SORT_RATE = "评分";

    public static List<ScanTypeInfo> categoryList;
    public static List<ScanTypeInfo> wordsNumList;
    public static List<ScanTypeInfo> bookStatusList;
    public static List<ScanTypeInfo> updateDateList;
    public static List<ScanTypeInfo> sortTypeList;

    public static final String YS_FILTER_CATE = "分类";
    public static final String YS_FILTER_WORDS = "字数";
    public static final String YS_FILTER_STATUS = "状态";
    public static final String YS_FILTER_UPDATE = "更新";
    public static final String YS_FILTER_SORT = "排序";

    public static LinkedHashMap<String,List<ScanTypeInfo>> filterMap;

    static{
        categoryList = new ArrayList<>();
        categoryList.add(new ScanTypeInfo(YS_CATE_ALL,false,"all"));
        categoryList.add(new ScanTypeInfo(YS_CATE_FANTASY,true,"fantasy"));
        categoryList.add(new ScanTypeInfo(YS_CATE_WUXIA,false,"wuxia"));
        categoryList.add(new ScanTypeInfo(YS_CATE_CITY,false,"city"));
        categoryList.add(new ScanTypeInfo(YS_CATE_HISTORY,false,"history"));
        categoryList.add(new ScanTypeInfo(YS_CATE_GAME,false,"game"));
        categoryList.add(new ScanTypeInfo(YS_CATE_FUTURE,false,"future"));
        categoryList.add(new ScanTypeInfo(YS_CATE_DOUJIN,false,"doujin"));
        categoryList.add(new ScanTypeInfo(YS_CATE_LOVE,false,"love"));
        categoryList.add(new ScanTypeInfo(YS_CATE_ROMANCE,false,"romance"));
        categoryList.add(new ScanTypeInfo(YS_CATE_FANTASYLOVE,false,"fantasylove"));
        categoryList.add(new ScanTypeInfo(YS_CATE_LITERATURE,false,"literature"));
        categoryList.add(new ScanTypeInfo(YS_CATE_NIJI,false,"niji"));

        wordsNumList = new ArrayList<>();
        wordsNumList.add(new ScanTypeInfo(YS_TYPE_UNLIMIT,false,null));
        wordsNumList.add(new ScanTypeInfo(YS_WORDS_1,false,"1"));
        wordsNumList.add(new ScanTypeInfo(YS_WORDS_2,false,"2"));
        wordsNumList.add(new ScanTypeInfo(YS_WORDS_3,false,"3"));
        wordsNumList.add(new ScanTypeInfo(YS_WORDS_4,false,"4"));
        wordsNumList.add(new ScanTypeInfo(YS_WORDS_5,false,"5"));
        wordsNumList.add(new ScanTypeInfo(YS_WORDS_6,true,"6"));

        bookStatusList = new ArrayList<>();
        bookStatusList.add(new ScanTypeInfo(YS_TYPE_UNLIMIT,true,null));
        bookStatusList.add(new ScanTypeInfo(YS_STATUS_1,false,"1"));
        bookStatusList.add(new ScanTypeInfo(YS_STATUS_2,false,"2"));
        bookStatusList.add(new ScanTypeInfo(YS_STATUS_3,false,"3"));

        updateDateList = new ArrayList<>();
        updateDateList.add(new ScanTypeInfo(YS_TYPE_UNLIMIT,true,null));
        updateDateList.add(new ScanTypeInfo(YS_UPDATE_1,false,"1"));
        updateDateList.add(new ScanTypeInfo(YS_UPDATE_2,false,"2"));
        updateDateList.add(new ScanTypeInfo(YS_UPDATE_3,false,"3"));
        updateDateList.add(new ScanTypeInfo(YS_UPDATE_4,false,"4"));

        sortTypeList = new ArrayList<>();
        sortTypeList.add(new ScanTypeInfo(YS_SORT_DEFAULT,false,null));
        sortTypeList.add(new ScanTypeInfo(YS_SORT_TOTAL,false,"total"));
        sortTypeList.add(new ScanTypeInfo(YS_SORT_WORD,false,"word"));
        sortTypeList.add(new ScanTypeInfo(YS_SORT_RATE,true,"rate"));

        filterMap = new LinkedHashMap<>();
        filterMap.put(YS_FILTER_CATE, categoryList);
        filterMap.put(YS_FILTER_WORDS, wordsNumList);
        filterMap.put(YS_FILTER_STATUS, bookStatusList);
        filterMap.put(YS_FILTER_UPDATE, updateDateList);
        filterMap.put(YS_FILTER_SORT, sortTypeList);
    }
}
