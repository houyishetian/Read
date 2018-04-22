package com.lin.read.filter.scan;

/**
 * Created by lisonglin on 2018/4/22.
 */

public class SortInfo {
    private String sortText;
    private int sortId;

    public static String SORT_BY_DEFAULT="默认排序";
    public static final int ID_SORT_BY_DEFAULT = 0;
    
    //qidian
    public static String QD_SORT_BY_SCORE="按分数排序";
    public static String QD_SORT_BY_SCORE_NUM="按评分人数排序";
    public static String QD_SORT_BY_WORDS_NUM="按总字数排序";
    public static String QD_SORT_BY_RECOMMEND="按推荐票排序";
    public static String QD_SORT_BY_VIP_CLICK="按会员点击排序";
    
    public static final int QD_ID_SORT_BY_SCORE = 1;
    public static final int QD_ID_SORT_BY_SCORE_NUM = 2;
    public static final int QD_ID_SORT_BY_WORDS = 3;
    public static final int QD_ID_SORT_BY_RECOMMEND = 4;
    public static final int QD_ID_SORT_BY_VIP_CLICK = 5;

    //zongheng
    public static String ZH_SORT_BY_RAISE="按打赏人数排序";
    public static String ZH_SORT_BY_COMMENT="按评论人数排序";
    public static String ZH_SORT_BY_WORDS_NUM="按总字数排序";
    public static String ZH_SORT_BY_RECOMMEND="按推荐票排序";
    public static String ZH_SORT_BY_CLICK="按点击数排序";

    public static final int ZH_ID_SORT_BY_RAISE = 6;
    public static final int ZH_ID_SORT_BY_COMMENT = 7;
    public static final int ZH_ID_SORT_BY_WORDS = 8;
    public static final int ZH_ID_SORT_BY_RECOMMEND = 9;
    public static final int ZH_ID_SORT_BY_CLICK = 10;

    public SortInfo() {

    }

    public SortInfo(String sortText, int sortId) {
        this.sortId = sortId;
        this.sortText = sortText;
    }

    public String getSortText() {
        return sortText;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortText(String sortText) {
        this.sortText = sortText;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    @Override
    public String toString() {
        return "SortInfo{" +
                "sortText='" + sortText + '\'' +
                ", sortId=" + sortId +
                '}';
    }
}