package com.lin.read.filter.qidian;

import com.lin.read.filter.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class QiDianRegexUtils {

    /**
     * get book url from rank page
     *
     * @param data
     * @return
     */
    public static String getBookUrlFromRankPage(String data) {
//		<h4><a href="//book.qidian.com/info/1003438608"
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        Pattern p = Pattern.compile("<h4><a href=\"([\\S^\"]+)\"");
        Matcher m = p.matcher(data);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    /**
     * get max page number
     *
     * @param data
     * @return
     */
    public static String getMaxPage(String data) {
//		data-pageMax="25"
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        Pattern p = Pattern.compile("data-pageMax=\"(\\d+)\"");
        Matcher m = p.matcher(data);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
