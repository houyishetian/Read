package com.lin.read.filter.search.aishuwang;

import android.util.Log;
import com.lin.read.download.HttpUtils;
import com.lin.read.filter.BookInfo;
import com.lin.read.filter.scan.StringUtils;
import com.lin.read.filter.search.RegexUtils;
import com.lin.read.filter.search.ResolveUtilsFactory;
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
 * resolve from: http://www.22ff.org/txt/
 */

public class ResolveUtilsForAiShuWang extends ResolveUtilsFactory {
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
        String url="http://www.22ff.org/s_"+bookName;
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
        Log.e("Test","笔趣阁2开始搜索："+url);
//	<ul>
//		<li class="neirong1"><a href="/xs/180297/">修真聊天群</a><a href="/xs/180297.txt" title="修真聊天群TXT下载">TXT下载</a></li>
//		<li class="neirong2"><a href="/xs/180297/20993544/">第325章 子曰：剥掉皮就是肉！</a></li>
//		<li class="neirong4"><a href="//www.22ff.org/author/圣骑士的传说(书坊)">圣骑士的传说(书坊)</a></li>
//		<li class="neirong3">02-08 07:26</li>
//	</ul>



        while ((current = reader.readLine()) != null) {
            if (bookInfo == null) {
                //bookname & booklink & downloadLink
                //<li class="neirong1"><a href="/xs/180297/">修真聊天群</a><a href="/xs/180297.txt" title="修真聊天群TXT下载">TXT下载</a></li>
                List<Integer> groups = Arrays.asList(1, 2, 3);
                List<String> resolveBookNameResult=RegexUtils.getDataByRegex(current.trim(),
                        "<li class=\"neirong1\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a><a href=\"([^\"^\n]{1,})\" title",
                        groups);
                if (resolveBookNameResult != null && resolveBookNameResult.size() == groups.size()) {
                    bookInfo = new BookInfo();
                    bookInfo.setBookLink(AiShuWangParseLinkUtils.parseLink(resolveBookNameResult.get(0)));
                    bookInfo.setBookName(resolveBookNameResult.get(1));
                    //http://www.22ff.org/xs/180297.txt --> http://67.229.159.202/full/181/180297.txt  http://67.229.159.202/full/232/231584.txt
                    bookInfo.setDownloadLink(AiShuWangParseLinkUtils.parseDownloadLink(resolveBookNameResult.get(2)));
                }
            }else{
                //lastChapter
                //<li class="neirong2"><a href="/xs/180445/42899859/">第1824章 姐，你的体质肯定有问题</a></li>
                List<String> resolveLastChapterResult=RegexUtils.getDataByRegex(current.trim(),"<li class=\"neirong2\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></li>",Arrays.asList(2));
                if (resolveLastChapterResult != null && resolveLastChapterResult.size() == 1) {
                    bookInfo.setLastChapter(ChapterHandleUtils.handleUpdateStr(resolveLastChapterResult.get(0)));
                    continue;
                }

                //authorName
                //<li class="neirong4"><a href="//www.22ff.org/author/圣骑士的传说">圣骑士的传说</a></li>
                List<String> resolveAuthorResult=RegexUtils.getDataByRegex(current.trim(),"<li class=\"neirong4\"><a href=\"([^\"^\n]{1,})\">([^\"^\n]{1,})</a></li>",Arrays.asList(2));
                if (resolveAuthorResult != null && resolveAuthorResult.size() == 1) {
                    bookInfo.setAuthorName(resolveAuthorResult.get(0));
                    continue;
                }

                //lastUpdate
                //<li class="neirong3">02-08 07:26</li>
                List<String> resolveDownloadLinkResult=RegexUtils.getDataByRegex(current.trim(),"<li class=\"neirong3\">([^\"^\n]{1,})</li>",Arrays.asList(1));
                if (resolveDownloadLinkResult != null && resolveDownloadLinkResult.size() == 1) {
                    bookInfo.setLastUpdate(resolveDownloadLinkResult.get(0));
                    bookInfo.setWebType(Constants.RESOLVE_FROM_AISHU);
                    bookInfo.setWebName(webName);
                    result.add(bookInfo);
                    bookInfo=null;
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
