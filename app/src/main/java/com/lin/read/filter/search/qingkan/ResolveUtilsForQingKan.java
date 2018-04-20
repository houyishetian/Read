package com.lin.read.filter.search.qingkan;

import android.text.TextUtils;
import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.RegexUtils;
import com.lin.read.filter.search.ResolveUtilsFactory;
import com.lin.read.pinyin.PinYinUtils;
import com.lin.read.utils.Constants;
import com.lin.read.utils.DateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/2/24.
 */

public class ResolveUtilsForQingKan extends ResolveUtilsFactory {

    @Override
    public List<BookInfo> getBooksByBookname(String... params) throws IOException {
        if (params == null || params.length == 0) {
            return null;
        }
        String bookName = params[0];
        String pageStr = params[1];
        String webName = params[2];
        int page = -1;
        try {
            page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(bookName) || page < 0) {
            return null;
        }
        String url = null;
        try {
            String searchKey = PinYinUtils.getAllPinYin(bookName).get(0);
            url = "https://www.qingkan9.com/novel.php?action=search&searchtype=novelname&searchkey=" + searchKey + "&input=";
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        Log.e("Test", "search url:" + url);
        return resolveSearchResultFromBDZhanNei(url, Constants.RESOLVE_FROM_QINGKAN, webName);
    }

    private List<BookInfo> resolveSearchResultFromBDZhanNei(String url, String webType, String webName)
            throws IOException {

        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        List<BookInfo> result = new ArrayList<BookInfo>();
        BufferedReader reader = null;
        String unicodeType = StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));

        String current = null;
        Log.e("Test", "请看开始搜索:" + webType + "-->" + url);
        BookInfo bookInfo = null;
        while ((current = reader.readLine()) != null) {
            if (bookInfo == null) {
                //<span class="sp_name"><a class="sp_bookname" href="https://www.qingkan9.com/book/daojun_7399/info.html" target="_blank">道君</a> / 跃千愁</span>
                String regex = "<span class=\"sp_name\"><a class=\"sp_bookname\" href=\"([^\n^\"]+)\" target=\"_blank\">([^\n^\"^<]+)</a>([^\n^\"^<]+)</span>";
                List<Integer> groups = Arrays.asList(1, 2, 3);
                List<String> bookNameResult = RegexUtils.getDataByRegex(current.trim(), regex, groups);
                if (bookNameResult != null && bookNameResult.size() == groups.size()) {
                    bookInfo = new BookInfo();
                    bookInfo.setBookLink(parseBookLink(bookNameResult.get(0)));
                    bookInfo.setBookName(bookNameResult.get(1));
                    bookInfo.setAuthorName(bookNameResult.get(2).replaceAll(" ", "").replaceAll("/", ""));
                }
            } else {
                //<a class="sp_chaptername" href="https://www.qingkan9.com/book/daojun_7399/53937964.html" target="_blank">第七七四章 不想成为道爷的累赘</a>（2018-04-08 17:32:45）</h4>
                String regex = "<a class=\"sp_chaptername\" href=\"([^\n^\"]+)\" target=\"_blank\">([^\n^\"]+)</a>([^\n^\"]+)</h4>";
                List<Integer> groups = Arrays.asList(2, 3);
                List<String> chapterInfoResult = RegexUtils.getDataByRegex(current.trim(), regex, groups);
                if (chapterInfoResult != null && chapterInfoResult.size() == groups.size()) {
                    bookInfo.setLastChapter(chapterInfoResult.get(0));
                    bookInfo.setLastUpdate(DateUtils.removeSeconds(chapterInfoResult.get(1).replaceAll("（", "").replaceAll("）", "")));
                    bookInfo.setWebName(webName);
                    bookInfo.setWebType(webType);
                    result.add(bookInfo);
                    bookInfo = null;
                }
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String parseBookLink(String oriLink) {
//		https://www.qingkan9.com/book/daojun_7399/info.html --> https://www.qingkan9.com/book/daojun_7399/txt.html
//      https://www.qingkan9.com/book/douzhandaojun/info.html --> https://www.qingkan9.com/book/douzhandaojun/
        if (TextUtils.isEmpty(oriLink)) {
            return null;
        }
        return oriLink.replace("info.html", "txt.html");
    }

    public void getDownloadLink(BookInfo bookInfo) throws IOException {
        if (bookInfo == null || TextUtils.isEmpty(bookInfo.getBookLink())) {
            return;
        }
        String url = bookInfo.getBookLink();
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        BufferedReader reader = null;
        String unicodeType = StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));

        String current = null;
        Log.e("Test", "请看下载地址:" + url);
        while ((current = reader.readLine()) != null) {
            //<a href="https://down.qingkan9.com/144/144797.txt">道君TXT下载（右键目标另存为）</a></DIV>
            String regex = "<a href=\"([^\n^\"]+)\">([^\n^\"^<]+)</a></DIV>";
            List<Integer> groups = Arrays.asList(1);
            List<String> downloadLinkResult = RegexUtils.getDataByRegex(current.trim(), regex, groups);
            if (downloadLinkResult != null && downloadLinkResult.size() == groups.size()) {
                bookInfo.setDownloadLink(downloadLinkResult.get(0));
                break;
            }
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
