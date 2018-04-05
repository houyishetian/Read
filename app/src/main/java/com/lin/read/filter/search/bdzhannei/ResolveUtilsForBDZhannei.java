package com.lin.read.filter.search.bdzhannei;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/2/24.
 */

public class ResolveUtilsForBDZhannei extends ResolveUtilsFactory {

    public Map<String, List<String>> resolveDataByRegex(String url,
                                                               List<String> regexs) throws IOException {
        if (regexs == null || regexs.size() == 0) {
            return null;
        }
        HttpURLConnection conn = HttpUtils.getConn(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
        for (String regex : regexs) {
            result.put(regex, new ArrayList<String>());
        }
        BufferedReader reader = null;
        String unicodeType=StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));
        String current = null;
        while ((current = reader.readLine()) != null) {
            for (String regex : regexs) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(current);
                if (matcher.find()) {
                    result.get(regex).add(current);
                    break;
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

    String tag = null;

    public void setTag(String tag) {
        this.tag=tag;
    }

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
        switch (tag) {
            case Constants.RESOLVE_FROM_NOVEL80:
                url = "http://zhannei.baidu.com/cse/search?searchtype=complex&q="+bookName+"&s=18140131260432570322&p="+page;
                break;
            case Constants.RESOLVE_FROM_DINGDIAN:
                url = "http://zhannei.baidu.com/cse/search?s=8053757951023821596&q="+bookName+"&p="+page;
                break;
            case Constants.RESOLVE_FROM_BIXIA:
                url="http://zhannei.baidu.com/cse/search?s=3677118700255927857&q="+bookName+"&p="+page;;
                break;
            default:
                url = null;
                break;
        }

        Log.e("Test","search url:"+url);
        return resolveSearchResultFromBDZhanNei(url,tag,webName);
    }
    private List<BookInfo> resolveSearchResultFromBDZhanNei(String url, String webType,String webName)
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
        String unicodeType=StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));

        String current = null;
        BookInfo bookInfo = null;
        int index = -1;
        BookIndex bookIndex = null;
        Log.e("Test", "百度站内开始搜索:" + webType + "-->" + url);
        while ((current = reader.readLine()) != null) {
            if (bookIndex != null) {
                index++;
            }
            if (bookInfo == null) {
                // get bookLink and book name
                String bookRegex = "<a cpos=\"title\" href=\"([^\"^\n]+)\" title=\"([^\"^\n]+)\"";
                List<Integer> bookGroupsList = Arrays.asList(new Integer[]{1,
                        2});
                List<String> resolveResult = RegexUtils.getDataByRegex(current, bookRegex,
                        bookGroupsList);
                if (resolveResult != null && resolveResult.size() != 0) {
                    bookInfo = new BookInfo();
                    String bookLink=resolveResult.get(0);
                    if(Constants.RESOLVE_FROM_NOVEL80.equals(tag)){
                        //handle book link
                        if(bookLink.endsWith(".html")){
                            bookInfo.setBookLink(bookLink);
                        }else{
                            if(bookLink.endsWith("/")){
                                StringBuilder temp=new StringBuilder(bookLink);
                                bookInfo.setBookLink(temp.deleteCharAt(temp.length()-1).append(".html").toString());
                            }else{

                                bookInfo.setBookLink(bookLink);
                            }
                        }
                    }else{
                        bookInfo.setBookLink(bookLink);
                    }

                    bookInfo.setBookName(resolveResult.get(1));
                }
            } else {
                // get author
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex.AUTHOR_INDEX
                        && index == bookIndex.getNextLine()) {
                    bookInfo.setAuthorName(current.trim().replaceAll("<em>","").replaceAll("</em>",""));
                    index = -1;
                    bookIndex = null;
                    continue;
                }
                // get book type
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex.TYPE_INDEX
                        && index == bookIndex.getNextLine()) {
                    // <span class="result-game-item-info-tag-title">仙侠修真</span>
                    String bookRegex = "<span class=\"result-game-item-info-tag-title\">([^\n]+)</span>";
                    List<Integer> bookGroupsList = Arrays
                            .asList(new Integer[]{1});
                    List<String> resolveResult = RegexUtils.getDataByRegex(current,
                            bookRegex, bookGroupsList);
                    if (resolveResult != null && resolveResult.size() != 0) {
                        bookInfo.setBookType(resolveResult.get(0));
                    }
                    bookIndex = null;
                    index = -1;
                    continue;
                }
                // get latest update time
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex.UPDATE_TIME_INDEX
                        && index == bookIndex.getNextLine()) {
                    // <span
                    // class="result-game-item-info-tag-title">2017-07-27</span>
                    String bookRegex = "<span class=\"result-game-item-info-tag-title\">([^\n]+)</span>";
                    List<Integer> bookGroupsList = Arrays
                            .asList(new Integer[]{1});
                    List<String> resolveResult = RegexUtils.getDataByRegex(current,
                            bookRegex, bookGroupsList);
                    if (resolveResult != null && resolveResult.size() != 0) {
                        bookInfo.setLastUpdate(resolveResult.get(0));
                    }
                    bookIndex = null;
                    index = -1;
                    continue;
                }

                // get latest update content
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex.UPDATE_CONTENT_INDEX
                        && index == bookIndex.getNextLine()) {
                    // class="result-game-item-info-tag-item"
                    // target="_blank">新书《一念永恒》，VIP上架啦</a>
                    String bookRegex = "class=\"result-game-item-info-tag-item\" target=\"_blank\">([^\n]+)</a>";
                    List<Integer> bookGroupsList = Arrays
                            .asList(new Integer[]{1});
                    List<String> resolveResult = RegexUtils.getDataByRegex(current,
                            bookRegex, bookGroupsList);
                    if (resolveResult != null && resolveResult.size() != 0) {
                        bookInfo.setLastChapter(ChapterHandleUtils.handleUpdateStr(resolveResult.get(0)));
                    }
                    bookIndex = null;
                    index = -1;
                    bookInfo.setDownloadLink(parseBookLinkToDownloadLink(bookInfo));
                    bookInfo.setWebType(webType);
                    bookInfo.setWebName(webName);
                    result.add(bookInfo);
                    bookInfo = null;
                    continue;
                }

                boolean isGetAuthor = isGetKey(current, AUTHOR_STR);
                if (isGetAuthor) {
                    index = 0;
                    bookIndex = new BookIndex();
                    bookIndex.setIndex(BookIndex.AUTHOR_INDEX);
                    continue;
                }
                boolean isGetType = isGetKey(current, TYPE_STR);
                if (isGetType) {
                    index = 0;
                    bookIndex = new BookIndex();
                    bookIndex.setIndex(BookIndex.TYPE_INDEX);
                    continue;
                }
                boolean isGetUpdateTime = isGetKey(current, UPDATE_TIME_STR);
                if (isGetUpdateTime) {
                    index = 0;
                    bookIndex = new BookIndex();
                    bookIndex.setIndex(BookIndex.UPDATE_TIME_INDEX);
                    continue;
                }
                boolean isGetUpdateContent = isGetKey(current,
                        UPDATE_CONTENT_STR);
                if (isGetUpdateContent) {
                    index = 0;
                    bookIndex = new BookIndex();
                    bookIndex.setIndex(BookIndex.UPDATE_CONTENT_INDEX);
                    continue;
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

    private static final String AUTHOR_STR = "<span class=\"result-game-item-info-tag-title preBold\">作者：</span>";
    private static final String TYPE_STR = "<span class=\"result-game-item-info-tag-title preBold\">类型：</span>";
    private static final String UPDATE_TIME_STR = "<span class=\"result-game-item-info-tag-title preBold\">更新时间：</span>";
    private static final String UPDATE_CONTENT_STR = "<span class=\"result-game-item-info-tag-title preBold\">最新章节：</span>";

    private static boolean isGetKey(String content, String keyString) {
        if (StringUtils.isEmpty(content)) {
            return false;
        }
        return content.trim().equalsIgnoreCase(keyString);
    }

    private String parseBookLinkToDownloadLink(BookInfo bookInfo) {
//		http://www.80txt.com/txtml_853/ 
//		http://www.80txt.com/txtml_853.html
//		http://dt.80txt.com/853/仙及.txt
        if (bookInfo == null) {
            return null;
        }
        String bookRegex = "http://www.80txt.com/txtml_(\\d+)\\D";
        List<Integer> bookGroupsList = Arrays
                .asList(new Integer[]{1});
        List<String> resolveResult = RegexUtils.getDataByRegex(bookInfo.getBookLink(),
                bookRegex, bookGroupsList);
        if (resolveResult != null && resolveResult.size() != 0) {
            String bookId = resolveResult.get(0);
            return "http://dt.80txt.com/" + bookId + "/" + bookInfo.getBookName() + ".txt";
        }
        return null;
    }
}
