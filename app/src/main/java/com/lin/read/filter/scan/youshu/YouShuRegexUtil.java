package com.lin.read.filter.scan.youshu;

import android.text.TextUtils;
import com.lin.read.filter.scan.SearchInfo;

public class YouShuRegexUtil {
    public static final String YOUSHU_LINK = "http://www.yousuu.com/category/%s?";

    public static String getYouShuUrl(SearchInfo searchInfo) {
        if (searchInfo == null) {
            return null;
        }
        String categoryId = searchInfo.getCategoryInfo().getId();
        String wordsNumId = searchInfo.getWordsNumInfo().getId();
        String bookStatusId = searchInfo.getBookStatusInfo().getId();
        String updateDateId = searchInfo.getUpdateDateInfo().getId();
        String sortTypeId = searchInfo.getSortTypeInfo().getId();
        String link = String.format(YOUSHU_LINK, categoryId);
        if (!TextUtils.isEmpty(wordsNumId)) {
            link += "wordnum=" + wordsNumId;
        }
        if (!TextUtils.isEmpty(bookStatusId)) {
            if (!link.endsWith("?")) {
                link += "&";
            }
            link += "status=" + bookStatusId;
        }
        if (!TextUtils.isEmpty(updateDateId)) {
            if (!link.endsWith("?")) {
                link += "&";
            }
            link += "update=" + updateDateId;
        }
        if (!TextUtils.isEmpty(sortTypeId)) {
            if (!link.endsWith("?")) {
                link += "&";
            }
            link += "sort=" + sortTypeId;
        }
        if (searchInfo.getCurrentPage() > 0) {
            if (!link.endsWith("?")) {
                link += "&";
            }
            link += "page=" + searchInfo.getCurrentPage();
        }
        return link;
    }
}
