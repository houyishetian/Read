package com.lin.read.filter.download.biquge;

import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.download.novel80.BookIndex80;
import com.lin.read.filter.search.StringUtils;

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

public class ResolveUtilsForBiQuGe {

    public static List<BookInfo> getBooksByBookname(String bookName) throws IOException {
        if(StringUtils.isEmpty(bookName)){
            return null;
        }
        String url="http://www.biquge5200.com/modules/article/search.php?searchkey="+bookName;
        Log.e("Test","search url:"+url);
        return resolveSearchResultFromBiQuGe(url);
    }

    private static List<BookInfo> resolveSearchResultFromBiQuGe(String url)
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
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "GBK"));

        String current = null;
        BookInfo bookInfo = null;
        Log.e("Test","开始解析...");
//  <tr>
//    <td class="odd"><a href="http://www.biquge5200.com/70_70322/">仙宸</a></td>
//    <td class="even"><a href="http://www.biquge5200.com/70_70322/144888240.html" target="_blank"> 第九十九章：大结局〔完〕</a></td>
//    <td class="odd">仙宸</td>
//	  <td class="even">331K</td>
//    <td class="odd" align="center">2017-05-26</td>
//    <td class="even" align="center">完结</td>
//  </tr>

        int startIndex = -1;
        while ((current = reader.readLine()) != null) {
            if(startIndex<0){
                if(current.trim().equals("<tr>")){
                    startIndex=0;
                }
            } else{
                if(bookInfo==null){
                    //bookname & booklink
                    //<td class="odd"><a href="http://www.biquge5200.com/70_70322/">仙宸</a></td>
                    List<String> resolveBookNameResult=getDataByRegex(current.trim(),"<td class=\"odd\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></td>",Arrays.asList(new Integer[]{1,2}));
                    if (resolveBookNameResult != null && resolveBookNameResult.size() == 2) {
                        bookInfo = new BookInfo();
                        bookInfo.setBookLink(resolveBookNameResult.get(0));
                        bookInfo.setBookName(resolveBookNameResult.get(1));
                    }else{
                        startIndex=-1;
                        continue;
                    }
                }else{
                    //lastChapter
                    //    <td class="even"><a href="http://www.biquge5200.com/70_70322/144888240.html" target="_blank"> 第九十九章：大结局〔完〕</a></td>
                    List<String> resolveLastChapterResult=getDataByRegex(current.trim(),"<td class=\"even\"><a href=\"([^\"^\n]{1,})\" target=\"_blank\">([^\"^\n]{1,})</a></td>",Arrays.asList(new Integer[]{2}));
                    if (resolveLastChapterResult != null && resolveLastChapterResult.size() == 1) {
                        bookInfo.setLastChapter(resolveLastChapterResult.get(0));
                        continue;
                    }

                    //authorname
                    //<td class="odd">仙宸</td>
                    List<String> resolveAuthorResult=getDataByRegex(current.trim(),"<td class=\"odd\">([^\"^\n]{1,})</td>",Arrays.asList(new Integer[]{1}));
                    if (resolveAuthorResult != null && resolveAuthorResult.size() == 1) {
                        bookInfo.setAuthorName(resolveAuthorResult.get(0));
                        continue;
                    }

                    //lastUpdate
                    //<td class="odd" align="center">2017-05-26</td>
                    List<String> resolveLastUpdateResult=getDataByRegex(current.trim(),"<td class=\"odd\" align=\"center\">([^\"^\n]{1,})</td>",Arrays.asList(new Integer[]{1}));
                    if (resolveLastUpdateResult != null && resolveLastUpdateResult.size() == 1) {
                        bookInfo.setLastUpdate(resolveLastUpdateResult.get(0));
                        result.add(bookInfo);
                        bookInfo=null;
                        startIndex=-1;
                        continue;
                    }
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

    private static List<String> getDataByRegex(String content, String regex,
                                               List<Integer> allGroup) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(regex)
                || allGroup == null || allGroup.size() == 0) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        try {
            if (matcher.find()) {
                List<String> resultList = new ArrayList<String>();
                for (Integer group : allGroup) {
                    resultList.add(matcher.group(group));
                }
                return resultList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}