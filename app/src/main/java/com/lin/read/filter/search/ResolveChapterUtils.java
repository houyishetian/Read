package com.lin.read.filter.search;

import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lisonglin on 2018/3/17.
 */

public class ResolveChapterUtils {
    public static List<BookChapterInfo> getChapterInfo(BookInfo bookInfo)
            throws IOException {
        if (bookInfo == null) {
            return null;
        }
        String url = bookInfo.getBookLink();
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        String regex = null;
        List<Integer> allGroups = Arrays.asList(new Integer[]{1, 2});
        String uniCodeType;
        switch (bookInfo.getWebType()) {
            case Constants.RESOLVE_FROM_NOVEL80:
                //<li><a rel="nofollow" href="http://www.qiushu.cc/t/67053/17977700.html">第1章 脱去球衣，换上西服</a></li>
                regex = "<li><a rel=\"nofollow\" href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></li>";
                uniCodeType = Constants.UNICODE_NOVEL_80;
                break;
            case Constants.RESOLVE_FROM_BIQUGE:
                //<dd><a href="http://www.biquge5200.com/51_51647/19645389.html">第一章 十六年</a></dd>
                regex = "<dd><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></dd>";
                uniCodeType = Constants.UNICODE_BIQUGE;
                break;
            default:
                return null;
        }
        List<BookChapterInfo> result = new ArrayList<BookChapterInfo>();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), uniCodeType));

        String current = null;
        Log.e("Test", "开始解析...");
        while ((current = reader.readLine()) != null) {
            List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), regex, allGroups);
            if (currentResult != null && currentResult.size() == allGroups.size()) {
                BookChapterInfo bookChapterInfo=new BookChapterInfo();
                bookChapterInfo.setWebType(bookInfo.getWebType());
                bookChapterInfo.setChapterLink(currentResult.get(0));
                bookChapterInfo.setChapterName(currentResult.get(1));
                result.add(bookChapterInfo);
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

    public static String getChapterContent(BookChapterInfo bookChapterInfo)
            throws IOException {
        if (bookChapterInfo == null) {
            return null;
        }
        String url = bookChapterInfo.getChapterLink();
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }

        String uniCodeType;
        switch (bookChapterInfo.getWebType()) {
            case Constants.RESOLVE_FROM_NOVEL80:
                uniCodeType = Constants.UNICODE_BIQUGE;
                break;
            case Constants.RESOLVE_FROM_BIQUGE:
                uniCodeType = Constants.UNICODE_BIQUGE;
                break;
            default:
                return null;
        }
        List<BookChapterInfo> result = new ArrayList<BookChapterInfo>();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), uniCodeType));

        String current = null;
        Log.e("Test", "开始解析...");

        String resultContent="";

        boolean isStart=false;

        while ((current = reader.readLine()) != null) {
            if(Constants.RESOLVE_FROM_BIQUGE.equals(bookChapterInfo.getWebType())){
                String biqugeRegex = "<div id=\"content\">([^\n]{1,})";;
                List<Integer> biqugeGroups = Arrays.asList(new Integer[]{1});
                List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), biqugeRegex, biqugeGroups);
                if (currentResult != null && currentResult.size() == biqugeGroups.size()) {
                    resultContent=currentResult.get(0);
                    break;
                }
            } else if (Constants.RESOLVE_FROM_NOVEL80.equals(bookChapterInfo.getWebType())) {
                if ("<div class=\"book_content\" id=\"content\">".equals(current.trim())) {
                    isStart = true;
                    continue;
                }
                if (isStart && current.contains("<div class=\"con_l\"><script>read_di()")) {
                    isStart = false;
                    continue;
                }
                if (isStart) {
                    resultContent += current;
                }
            }else{
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
        return resultContent;
    }
}
