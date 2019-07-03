package com.lin.read.filter.scan.qidian;

import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.utils.ChapterHandleUtils;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean getQiDianBookNameAndAuthorName(BookInfo bookInfo, String data){
//        <h1><em>火照鬼擎传</em><span><a class="writer" href="//me.qidian.com/authorIndex.aspx?id=7049455" target="_blank" data-eid="qd_G08">小韵和小云</a> 著</span></h1>

//        <h1>
//
//            <em>神武图</em>
//            <span><a class="writer" href="//me.qidian.com/authorIndex.aspx?id=7634821" target="_blank" data-eid="qd_G08">醉上空城</a> 著</span>
//
//        </h1>
//        <title>《神武图》_醉上空城著_玄幻_起点中文网</title>
        if(bookInfo==null||StringUtils.isEmpty(data)){
            return false;
        }
//        Pattern pattern=Pattern.compile("<h1>[\\s]*<em>([^\n]{1,})</em>[\\s]*[^\n]{1,}>([^\n]{1,})</a> 著");
        Pattern pattern=Pattern.compile("<title>《([^\\n]{1,})》_([^\\n]{1,})著_[^\\n]{1,}_起点中文网</title>");
        Matcher matcher=pattern.matcher(data);
        if(matcher.find()){
            bookInfo.setBookName(matcher.group(1));
            bookInfo.setAuthorName(matcher.group(2));
            return true;
        }
        return false;
    }
//    <p><em>30.36</em><cite>万字</cite><i>|</i><em>28.62</em><cite>万总点击<span>&#183;</span>会员周点击6310</cite><i>|</i><em>6.08</em><cite>万总推荐<span>&#183;</span>周898</cite></p>
    public static boolean getQiDianWordsNumVipClickRecommend(BookInfo bookInfo, String data){
        if(bookInfo==null||StringUtils.isEmpty(data)){
            return false;
        }
        Pattern pattern=Pattern.compile("<p><em>([\\d\\.]{1,})</em><cite>万字[^\n]{1,}<em>([\\d\\.]{1,})</em><cite>万总点击[^\n]{1,}<em>([\\d\\.]{1,})</em><cite>万总推荐");
        Matcher matcher=pattern.matcher(data);
        if(matcher.find()){
            bookInfo.setWordsNum(matcher.group(1));
            bookInfo.setClick(matcher.group(2));
            bookInfo.setRecommend(matcher.group(3));
            return true;
        }
        return false;
    }

//    title="第一百四十七章不甘心的乌鲁娜下" target="_blank">第一百四十七章不甘心的乌鲁娜下</a><i>&#183;</i><em class="time">今天20:50更新</em>
    public static boolean getQiDianLastUpdateAndLastChapter(BookInfo bookInfo, String data){
        if(bookInfo==null||StringUtils.isEmpty(data)){
            return false;
        }
        Pattern pattern=Pattern.compile("title=\"([^\"^\n]{1,})\"[^\n]{1,}\"time\">([^\"^\n]{1,})</em>");
        Matcher matcher=pattern.matcher(data);
        if(matcher.find()){
            bookInfo.setLastChapter(ChapterHandleUtils.handleUpdateStr(matcher.group(1)));
            bookInfo.setLastUpdate(matcher.group(2));
            return true;
        }
        return false;
    }

    /**
     * get max page number
     *
     * @param data
     * @return
     */
    public static List<String> getCurrentAndMaxPage(String data) {
//		data-page="2" data-pageMax="5"></div>
//       id="page-container" data-pageMax="5" data-page="1"
        if (StringUtils.isEmpty(data)) {
            return null;
        }
        List<String> result = new ArrayList<>();
        Pattern p = Pattern.compile("data-page=\"(\\d+)\" data-pageMax=\"(\\d+)\"");
        Matcher m = p.matcher(data);
        if (m.find()) {
            result.add(m.group(1));
            result.add(m.group(2));
            return result;
        }
        p = Pattern.compile("data-pageMax=\"(\\d+)\" data-page=\"(\\d+)\"");
        m = p.matcher(data);
        if (m.find()) {
            result.add(m.group(2));
            result.add(m.group(1));
            return result;
        }
        return null;
    }
}
