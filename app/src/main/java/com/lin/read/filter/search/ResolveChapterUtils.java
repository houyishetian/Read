package com.lin.read.filter.search;

import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.utils.ChapterHandleUtils;
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
        String uniCodeType=StringUtils.getCharSet(conn);
        Log.e("Test","unicodeType:"+uniCodeType);
        switch (bookInfo.getWebType()) {
            case Constants.RESOLVE_FROM_NOVEL80:
                //<li><a rel="nofollow" href="http://www.qiushu.cc/t/67053/17977700.html">第1章 脱去球衣，换上西服</a></li>
                regex = "<li><a rel=\"nofollow\" href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></li>";
                break;
            case Constants.RESOLVE_FROM_BIQUGE:
                //<dd><a href="http://www.biquge5200.com/51_51647/19645389.html">第一章 十六年</a></dd>
                regex = "<dd><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></dd>";
                break;
            case Constants.RESOLVE_FROM_DINGDIAN:
                //<td class="L"><a href="http://www.23us.so/files/article/html/10/10674/7219354.html">新书《一念永恒》，VIP上架啦</a></td>
                regex = "<td class=\"L\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></td>";
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
            if(Constants.RESOLVE_FROM_DINGDIAN.equals(bookInfo.getWebType())){
                List<List<String>> currentResult = RegexUtils.getDataByRegexManyData(current.trim(), regex, allGroups);
                if (currentResult != null && currentResult.size() > 0) {
                    for (List<String> item : currentResult) {
                        if (item != null && item.size() == allGroups.size()) {
                            BookChapterInfo bookChapterInfo = new BookChapterInfo();
                            bookChapterInfo.setWebType(bookInfo.getWebType());
                            bookChapterInfo.setChapterLink(item.get(0));
                            String oriChapterName = item.get(1);
                            bookChapterInfo.setChapterNameOri(oriChapterName);
                            bookChapterInfo.setChapterName(ChapterHandleUtils.handleUpdateStr(oriChapterName));
                            result.add(bookChapterInfo);
                        }
                    }
                    break;
                }
            }else{
                List<String> currentResult = RegexUtils.getDataByRegex(current.trim(), regex, allGroups);
                if (currentResult != null && currentResult.size() == allGroups.size()) {
                    BookChapterInfo bookChapterInfo=new BookChapterInfo();
                    bookChapterInfo.setWebType(bookInfo.getWebType());
                    bookChapterInfo.setChapterLink(currentResult.get(0));
                    String oriChapterName = currentResult.get(1);
                    bookChapterInfo.setChapterNameOri(oriChapterName);
                    bookChapterInfo.setChapterName(ChapterHandleUtils.handleUpdateStr(oriChapterName));
                    result.add(bookChapterInfo);
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

        String uniCodeType = StringUtils.getCharSet(conn);
        Log.e("Test", "uniCodeType:" + uniCodeType);
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
            }else if(Constants.RESOLVE_FROM_DINGDIAN.equals(bookChapterInfo.getWebType())){
                if ("<dd id=\"contents\">".equals(current.trim())) {
                    isStart = true;
                    continue;
                }
                if (isStart && current.trim().equals("<div class=\"adhtml\"><script>show_htm3();</script></div>")) {
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
