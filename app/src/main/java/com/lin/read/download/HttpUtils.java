package com.lin.read.download;

import android.util.Log;

import com.lin.read.filter.search.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lisonglin on 2018/3/1.
 */

public class HttpUtils {
    /**
     * get an available connection
     *
     * @param urlLink
     * @return
     */
    public static HttpURLConnection getConn(String urlLink, int totalNum) throws IOException {
        if (StringUtils.isEmpty(urlLink)) {
            Log.e("Test", "empty url:" + urlLink);
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
            Log.e("Test", "redirect code error url:" + code + ",url=" + urlLink);
            return getConn(urlLink, totalNum - 1);
        }
        Log.e("Test", "code error url:" + code + ",url=" + urlLink);
        return getConn(urlLink, totalNum - 1);
    }

    /**
     * get an available connection
     *
     * @param urlLink
     * @return
     */
    public static HttpURLConnection getConnWithUserAgent(String urlLink, int totalNum) throws IOException {
        if (StringUtils.isEmpty(urlLink)) {
            Log.e("Test", "empty url:" + urlLink);
            return null;
        }
        if (totalNum <= 0) {
            return null;
        }
        URL url = new URL(urlLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(20000);
//        .setUserAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)")
        conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 7.0; Windows 7)");
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
            Log.e("Test", "redirect code error url:" + code + ",url=" + urlLink);
            return getConn(urlLink, totalNum - 1);
        }
        Log.e("Test", "code error url:" + code + ",url=" + urlLink);
        return getConn(urlLink, totalNum - 1);
    }

    /**
     * get an available connection
     *
     * @param urlLink
     * @return
     */
    public static HttpURLConnection getConn(String urlLink, int totalNum, long fromPos, long toPos) throws IOException {
        if (StringUtils.isEmpty(urlLink)) {
            Log.e("Test", "empty url:" + urlLink);
            return null;
        }
        if (totalNum <= 0) {
            return null;
        }
        URL url = new URL(urlLink);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(20000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty(
                "Accept",
                "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-    shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap,   application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint,  application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("Referer", url.toString());
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Range", "bytes=" + fromPos + "-" + toPos);// 设置获取实体数据的范围
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
            Log.e("Test", "redirect code error url:" + code + ",url=" + urlLink);
            return getConn(urlLink, totalNum - 1);
        }
        Log.e("Test", "code error url:" + code + ",url=" + urlLink);
        return getConn(urlLink, totalNum - 1);
    }
}
