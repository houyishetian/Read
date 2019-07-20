package com.lin.read.filter.search.bixia;

import android.util.Log;
import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.ResolveUtilsFactory;
import com.lin.read.utils.Constants;
import com.lin.read.utils.StringKtUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResolveUtilsForBiXia extends ResolveUtilsFactory {
    @Override
    public List<BookInfo> getBooksByBookname(String... params) throws IOException {
        String url = "http://www.bxwx666.org/search.aspx?bookname=" + URLEncoder.encode(params[0], "gbk");
        List<BookInfo> result = new ArrayList<>();
        HttpURLConnection conn = HttpUtils.getConnWithUserAgent(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        BufferedReader reader;
        String unicodeType = StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));
        String current;
        BookInfo bookInfo = null;
        boolean isStartResolve = false;
        while ((current = reader.readLine()) != null) {
// <li>
//<span class="s1">[武侠仙侠]</span>
//<span class="s2">
//<a href="https://www.bxwx666.org/txt/2/" target="_blank">凡人修仙传</a></span>
//<span class="s3"><a href="https://www.bxwx666.org/txt/2/529382.htm" target="_blank">忘语新书《玄界之门》</a></span>
//<span class="s4">忘语</span><span class="s5">2019/5/10 </span></li>

            current = current.trim();
            if(current.contains("id=\"main\"")){
                isStartResolve = true;
                continue;
            }
            if(isStartResolve){
                if(current.equals("</div>")){
                    break;
                }
                if (current.equals("<li>")) {
                    //start resolve
                    bookInfo = new BookInfo();
                    bookInfo.setWebType(Constants.RESOLVE_FROM_BIXIA);
                    bookInfo.setWebName(params[2]);
                    continue;
                }
                if (bookInfo != null) {
                    List<String> resolveBookNameResult = StringKtUtil.Companion.getDataFromContentByRegex(current, "<a href=\"([^\"^\n]{1,})\" target=\"_blank\">([^\"^\n]{1,})</a></span>", Arrays.asList(1,2),true);
                    if (resolveBookNameResult != null && resolveBookNameResult.size() != 0) {
                        bookInfo.setBookLink(resolveBookNameResult.get(0));
                        bookInfo.setBookName(resolveBookNameResult.get(1));
                    }

                    List<String> resolveLastChapterResult = StringKtUtil.Companion.getDataFromContentByRegex(current, "<span class=\"s3\"><a href=\"([^\"^\n]{1,})\" target=\"_blank\">([^\"^\n]{1,})</a></span>", Arrays.asList(2),true);
                    if (resolveLastChapterResult != null && resolveLastChapterResult.size() != 0) {
                        bookInfo.setLastChapter(resolveLastChapterResult.get(0));
                    }

                    List<String> resolveLastUpdateResult = StringKtUtil.Companion.getDataFromContentByRegex(current, "<span class=\"s4\">([^\"^\n]{1,})</span><span class=\"s5\">([^\"^\n]{1,})</span></li>", Arrays.asList(1,2),true);
                    if (resolveLastUpdateResult != null && resolveLastUpdateResult.size() != 0) {
                        bookInfo.setAuthorName(resolveLastUpdateResult.get(0));
                        bookInfo.setLastUpdate(resolveLastUpdateResult.get(1));
                    }
                }
            }
            if (current.trim().contains("</li>")) {
                //end resolve
                if (bookInfo != null) {
                    result.add(bookInfo);
                }
                bookInfo = null;
            }
        }
        Log.d("Test", "all books:" + result);
        return result;
    }
}
