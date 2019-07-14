package com.lin.read.filter.search.dingdian;

import android.text.TextUtils;
import android.util.Log;
import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.RegexUtils;
import com.lin.read.filter.search.ResolveUtilsFactory;
import com.lin.read.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResolveUtilsForDingDian extends ResolveUtilsFactory {
    @Override
    public List<BookInfo> getBooksByBookname(String... params) throws IOException {
        String url = "https://www.x23us.com/modules/article/search.php?searchtype=keywords&searchkey=" + URLEncoder.encode(params[0], "gbk");
        List<BookInfo> result = new ArrayList<BookInfo>();
        Object responsObj = HttpUtils.getConnOrRedirectUrlWithUserAgent(url, 3);
        if (responsObj == null) {
            throw new IOException();
        }
        if(responsObj instanceof HttpURLConnection){
            HttpURLConnection conn = (HttpURLConnection) responsObj;
            BufferedReader reader = null;
            String unicodeType = StringUtils.getCharSet(conn);
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), unicodeType));
            String current = null;
            BookInfo bookInfo = null;
            while ((current = reader.readLine()) != null) {
// <tr>
//    <td class="odd"><a href="https://www.x23us.com/book/328"><b style="color:red">凡人修仙传</b></a></td>
//    <td class="even"><a href="http://www.x23us.com/html/0/328/" target="_blank"> 写在新书正式上传前的回忆！</a></td>
//    <td class="odd">忘语</td>
//	  <td class="even">16240K</td>
//    <td class="odd" align="center">18-10-18</td>
//    <td class="even" align="center">完成</td>
//  </tr>
                current = current.trim();
                if (current.equals("<tr>")) {
                    //start resolve
                    bookInfo = new BookInfo();
                    bookInfo.setWebType(Constants.RESOLVE_FROM_DINGDIAN);
                    bookInfo.setWebName(params[2]);
                }
                if (current.equals("</tr>")) {
                    //end resolve
                    if (bookInfo != null && !TextUtils.isEmpty(bookInfo.getBookName())) {
                        result.add(bookInfo);
                    }
                    bookInfo = null;
                }
                if (bookInfo != null) {
                    current = current.replaceAll("<b style=\"color:red\">", "").replaceAll("</b>", "");
                    List<String> resolveBookNameResult = RegexUtils.getDataByRegexMatch(current, "<td class=\"odd\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></td>", Arrays.asList(1,2));
                    if (resolveBookNameResult != null && resolveBookNameResult.size() != 0) {
                        bookInfo.setBookName(resolveBookNameResult.get(1));
                    }

                    List<String> resolveLastChapterResult = RegexUtils.getDataByRegexMatch(current, "<td class=\"even\"><a href=\"([^\"^\n]{1,})\" target=\"_blank\">([^\"^\n]{1,})</a></td>", Arrays.asList(1,2));
                    if (resolveLastChapterResult != null && resolveLastChapterResult.size() != 0) {
                        bookInfo.setLastChapter(resolveLastChapterResult.get(1));
                        bookInfo.setBookLink(resolveLastChapterResult.get(0));
                    }

                    List<String> resolveLastUpdateResult = RegexUtils.getDataByRegexMatch(current, "<td class=\"odd\" align=\"center\">([^\"^\n]{1,})</td>", Arrays.asList(1));
                    if (resolveLastUpdateResult != null && resolveLastUpdateResult.size() != 0) {
                        bookInfo.setLastUpdate(resolveLastUpdateResult.get(0));
                    }

                    List<String> resolveAuthorResult = RegexUtils.getDataByRegexMatch(current, "<td class=\"odd\">([^\"^\n]{1,})</td>", Arrays.asList(1));
                    if (resolveAuthorResult != null && resolveAuthorResult.size() != 0) {
                        bookInfo.setAuthorName(resolveAuthorResult.get(0));
                    }
                }
            }
        }else if(responsObj instanceof String){
            HttpURLConnection conn = HttpUtils.getConnWithUserAgent((String)responsObj,3);
            if(conn==null){
                throw new IOException();
            }
            BufferedReader reader = null;
            String unicodeType = StringUtils.getCharSet(conn);
            reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), unicodeType));
            String current = null;
            BookInfo bookInfo = new BookInfo();
            bookInfo.setWebType(Constants.RESOLVE_FROM_DINGDIAN);
            bookInfo.setWebName(params[2]);
            bookInfo.setBookName(params[0]);

            while ((current = reader.readLine()) != null) {
                current = current.trim();
                //<th>最后更新</th><td>&nbsp;2018-10-18</td>
                List<String> resolveLastUpdateResult = RegexUtils.getDataByRegex(current, "<th>最后更新</th><td>&nbsp;([^\n^<]{1,})</td>", Arrays.asList(1));
                if (resolveLastUpdateResult != null && resolveLastUpdateResult.size() != 0) {
                    bookInfo.setLastUpdate(resolveLastUpdateResult.get(0));
                    continue;
                }

                //最新章节：<a href="https://www.x23us.com/html/0/328/">写在新书正式上传前的回忆！</a></p>
                List<String> resolveLastChapterResult = RegexUtils.getDataByRegex(current, "最新章节：<a href=\"([^\n^\"]{1,})\">([^\\n]{1,})</a></p>", Arrays.asList(1,2));
                if (resolveLastChapterResult != null && resolveLastChapterResult.size() != 0) {
                    bookInfo.setBookLink(resolveLastChapterResult.get(0));
                    bookInfo.setLastChapter(resolveLastChapterResult.get(1));
                    continue;
                }

                //<th>文章作者</th><td>&nbsp;忘语</td><th>文章状态</th><td>&nbsp;已完成</td></tr>
                List<String> resolveAuthorNameResult = RegexUtils.getDataByRegex(current, "<th>文章作者</th><td>&nbsp;([^\n^<]{1,})</td>", Arrays.asList(1));
                if (resolveAuthorNameResult != null && resolveAuthorNameResult.size() != 0) {
                    bookInfo.setAuthorName(resolveAuthorNameResult.get(0));
                }
            }
            result.add(bookInfo);
        }
        Log.d("Test", "all books:" + result);
        return result;
    }
}
