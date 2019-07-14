package com.lin.read.filter.search.biquge;

import android.util.Log;

import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.search.RegexUtils;
import com.lin.read.filter.search.ResolveUtilsFactory;
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
 * Created by lisonglin on 2018/2/24.
 */

public class ResolveUtilsForBiQuGe extends ResolveUtilsFactory {
    @Override
    public List<BookInfo> getBooksByBookname(String... params) throws IOException {
        if (params == null || params.length == 0) {
            return null;
        }
        String bookName = params[0];
        String webName = params[2];
        if (StringUtils.isEmpty(bookName)) {
            return null;
        }
        String url="http://www.biquge5200.com/modules/article/search.php?searchkey="+bookName;
        Log.e("Test","search url:"+url);
        return resolveSearchResultFromBiQuGe(url,webName);
    }

    private List<BookInfo> resolveSearchResultFromBiQuGe(String url,String webName)
            throws IOException {

        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        List<BookInfo> result = new ArrayList<>();
        BufferedReader reader;
        String unicodeType=StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));

        String current;
        BookInfo bookInfo = null;
        Log.e("Test","笔趣阁开始搜索："+url);
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
                    List<String> resolveBookNameResult=RegexUtils.getDataByRegex(current.trim(),"<td class=\"odd\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></td>",Arrays.asList(1,2));
                    if (resolveBookNameResult != null && resolveBookNameResult.size() == 2) {
                        bookInfo = new BookInfo();
                        bookInfo.setBookLink(resolveBookNameResult.get(0));
                        bookInfo.setBookName(resolveBookNameResult.get(1));
                    }else{
                        startIndex=-1;
                    }
                }else{
                    //lastChapter
                    //    <td class="even"><a href="http://www.biquge5200.com/70_70322/144888240.html" target="_blank"> 第九十九章：大结局〔完〕</a></td>
                    List<String> resolveLastChapterResult=RegexUtils.getDataByRegex(current.trim(),"<td class=\"even\"><a href=\"([^\"^\n]{1,})\" target=\"_blank\">([^\"^\n]{1,})</a></td>",Arrays.asList(2));
                    if (resolveLastChapterResult != null && resolveLastChapterResult.size() == 1) {
                        bookInfo.setLastChapter(ChapterHandleUtils.handleUpdateStr(resolveLastChapterResult.get(0)));
                        continue;
                    }

                    //authorname
                    //<td class="odd">仙宸</td>
                    List<String> resolveAuthorResult= RegexUtils.getDataByRegex(current.trim(),"<td class=\"odd\">([^\"^\n]{1,})</td>",Arrays.asList(1));
                    if (resolveAuthorResult != null && resolveAuthorResult.size() == 1) {
                        bookInfo.setAuthorName(resolveAuthorResult.get(0));
                        continue;
                    }

                    //lastUpdate
                    //<td class="odd" align="center">2017-05-26</td>
                    List<String> resolveLastUpdateResult=RegexUtils.getDataByRegex(current.trim(),"<td class=\"odd\" align=\"center\">([^\"^\n]{1,})</td>",Arrays.asList(1));
                    if (resolveLastUpdateResult != null && resolveLastUpdateResult.size() == 1) {
                        bookInfo.setLastUpdate(resolveLastUpdateResult.get(0));
                        bookInfo.setWebType(Constants.RESOLVE_FROM_BIQUGE);
                        bookInfo.setWebName(webName);
                        result.add(bookInfo);
                        bookInfo=null;
                        startIndex=-1;
                    }
                }
            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
