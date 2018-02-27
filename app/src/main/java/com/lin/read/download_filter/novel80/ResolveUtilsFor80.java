package com.lin.read.download_filter.novel80;

import com.lin.read.filter.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lisonglin on 2018/2/24.
 */

public class ResolveUtilsFor80 {
    /**
     * get an available connection
     *
     * @param urlLink
     * @return
     */
    private static HttpURLConnection getConn(String urlLink, int totalNum)
            throws IOException {
        if (StringUtils.isEmpty(urlLink)) {
            System.out.println("empty url:" + urlLink);
            return null;
        }
        if (totalNum <= 0) {
            return null;
        }
        URL url = new URL(urlLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(20000);
        int code = -1;
        try {
            code = conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
            if (totalNum == 1) {
                throw new IOException();
            }
        }
        if (code == 200) {
            return conn;
        } else if (code == 301 || code == 302) {
            String location = conn.getHeaderField("Location");
            System.out.println("location=" + location);
            url = new URL(location);
            conn = (HttpURLConnection) url.openConnection();
            code = conn.getResponseCode();
            if (code == 200) {
                return conn;
            }
            System.out.println("redirect code error url:" + code + ",url="
                    + urlLink);
            return getConn(urlLink, totalNum - 1);
        }
        System.out.println("code error url:" + code + ",url=" + urlLink);
        return getConn(urlLink, totalNum - 1);
    }

    public static Map<String, List<String>> resolveDataByRegex(String url,
                                                               List<String> regexs) throws IOException {
        if (regexs == null || regexs.size() == 0) {
            return null;
        }
        HttpURLConnection conn = getConn(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        Map<String, List<String>> result = new LinkedHashMap<String, List<String>>();
        for (String regex : regexs) {
            result.put(regex, new ArrayList<String>());
        }
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "UTF-8"));
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

    public static List<BookInfo80> resolveSearchResultFrom80(String url)
            throws IOException {

        if (StringUtils.isEmpty(url)) {
            return null;
        }
        HttpURLConnection conn = getConn(url, 3);
        if (conn == null) {
            throw new IOException();
        }
        List<BookInfo80> result = new ArrayList<BookInfo80>();
        BufferedReader reader = null;
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), "UTF-8"));

        String current = null;
        BookInfo80 bookInfo = null;
        int index = -1;
        BookIndex80 bookIndex = null;
        while ((current = reader.readLine()) != null) {
            if (bookIndex != null) {
                index++;
            }
            if (bookInfo == null) {
                // get bookLink and book name
                String bookRegex = "<a cpos=\"title\" href=\"([^\"^\n]+)\" title=\"([^\"^\n]+)\"";
                List<Integer> bookGroupsList = Arrays.asList(new Integer[]{1,
                        2});
                List<String> resolveResult = getDataByRegex(current, bookRegex,
                        bookGroupsList);
                if (resolveResult != null && resolveResult.size() != 0) {
                    bookInfo = new BookInfo80();
                    bookInfo.setBookLink(resolveResult.get(0));
                    bookInfo.setBookName(resolveResult.get(1));
                }
            } else {
                // get author
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex80.AUTHOR_INDEX
                        && index == bookIndex.getNextLine()) {
                    bookInfo.setAuthor(current.trim());
                    index = -1;
                    bookIndex = null;
                    continue;
                }
                // get book type
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex80.TYPE_INDEX
                        && index == bookIndex.getNextLine()) {
                    // <span class="result-game-item-info-tag-title">仙侠修真</span>
                    String bookRegex = "<span class=\"result-game-item-info-tag-title\">([^\n]+)</span>";
                    List<Integer> bookGroupsList = Arrays
                            .asList(new Integer[]{1});
                    List<String> resolveResult = getDataByRegex(current,
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
                        && bookIndex.getIndex() == BookIndex80.UPDATE_TIME_INDEX
                        && index == bookIndex.getNextLine()) {
                    // <span
                    // class="result-game-item-info-tag-title">2017-07-27</span>
                    String bookRegex = "<span class=\"result-game-item-info-tag-title\">([^\n]+)</span>";
                    List<Integer> bookGroupsList = Arrays
                            .asList(new Integer[]{1});
                    List<String> resolveResult = getDataByRegex(current,
                            bookRegex, bookGroupsList);
                    if (resolveResult != null && resolveResult.size() != 0) {
                        bookInfo.setLatestUpdate(resolveResult.get(0));
                    }
                    bookIndex = null;
                    index = -1;
                    continue;
                }

                // get latest update content
                if (bookIndex != null
                        && bookIndex.getIndex() == BookIndex80.UPDATE_CONTENT_INDEX
                        && index == bookIndex.getNextLine()) {
                    // class="result-game-item-info-tag-item"
                    // target="_blank">新书《一念永恒》，VIP上架啦</a>
                    String bookRegex = "class=\"result-game-item-info-tag-item\" target=\"_blank\">([^\n]+)</a>";
                    List<Integer> bookGroupsList = Arrays
                            .asList(new Integer[]{1});
                    List<String> resolveResult = getDataByRegex(current,
                            bookRegex, bookGroupsList);
                    if (resolveResult != null && resolveResult.size() != 0) {
                        bookInfo.setLatestContent(resolveResult.get(0));
                    }
                    bookIndex = null;
                    index = -1;
                    result.add(bookInfo);
                    bookInfo = null;
                    continue;
                }

                boolean isGetAuthor = isGetKey(current, AUTHOR_STR);
                if (isGetAuthor) {
                    index = 0;
                    bookIndex = new BookIndex80();
                    bookIndex.setIndex(BookIndex80.AUTHOR_INDEX);
                    continue;
                }
                boolean isGetType = isGetKey(current, TYPE_STR);
                if (isGetType) {
                    index = 0;
                    bookIndex = new BookIndex80();
                    bookIndex.setIndex(BookIndex80.TYPE_INDEX);
                    continue;
                }
                boolean isGetUpdateTime = isGetKey(current, UPDATE_TIME_STR);
                if (isGetUpdateTime) {
                    index = 0;
                    bookIndex = new BookIndex80();
                    bookIndex.setIndex(BookIndex80.UPDATE_TIME_INDEX);
                    continue;
                }
                boolean isGetUpdateContent = isGetKey(current,
                        UPDATE_CONTENT_STR);
                if (isGetUpdateContent) {
                    index = 0;
                    bookIndex = new BookIndex80();
                    bookIndex.setIndex(BookIndex80.UPDATE_CONTENT_INDEX);
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

    public static String parseBookLinkToDownloadLink(BookInfo80 bookInfo) {
//		http://www.80txt.com/txtml_853/ 
//		http://www.80txt.com/txtml_853.html
//		http://dt.80txt.com/853/�ɼ�.txt
        if (bookInfo == null) {
            return null;
        }
        String bookRegex = "http://www.80txt.com/txtml_(\\d+)\\D";
        List<Integer> bookGroupsList = Arrays
                .asList(new Integer[]{1});
        List<String> resolveResult = getDataByRegex(bookInfo.getBookLink(),
                bookRegex, bookGroupsList);
        if (resolveResult != null && resolveResult.size() != 0) {
            String bookId = resolveResult.get(0);
            return "http://dt.80txt.com/" + bookId + "/" + bookInfo.getBookName() + ".txt";
        }
        return null;
    }
}
