package com.lin.read.filter.search.novel80;

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
import java.util.*;

public class ResolveUtilsForNovel80 extends ResolveUtilsFactory {

    @Override
    public List<BookInfo> getBooksByBookname(String... params) throws IOException {
        String url = "http://www.txt80.com/e/search/result/?searchid=1460";
        List<BookInfo> result = new ArrayList<BookInfo>();
//        tbname=download&tempid=1&keyboard=%E5%87%A1%E4%BA%BA&show=title%2Csoftsay%2Csoftwriter&Submit22=%E6%90%9C%E7%B4%A2
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("tbname", "download");
        requestParams.put("tempid", "1");
        requestParams.put("keyboard", params[0]);
        requestParams.put("show", "title%2Csoftsay%2Csoftwriter");
        requestParams.put("Submit22", "搜索");
        HttpURLConnection conn = HttpUtils.getConnPost(url, 3, requestParams);
        if (conn == null) {
            throw new IOException();
        }
        BufferedReader reader = null;
        String unicodeType = StringUtils.getCharSet(conn);
        reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), unicodeType));
        String current = null;
        BookInfo bookInfo = null;
        int i = 1;
        while ((current = reader.readLine()) != null) {
            Log.d("content:"+i,current);
            i++;
        }
        Log.d("Test", "all books:" + result);
        return result;
    }
}
