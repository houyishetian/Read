package com.lin.read.download;

import android.util.Log;

import com.lin.read.filter.scan.StringUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

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
        } else if (code >= 400) {
            return null;
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

    /**
     * get an available connection
     *
     * @param urlLink
     * @return
     */
    public static HttpURLConnection getConnPost(String urlLink, int totalNum, Map<String, String> params) throws IOException {
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



//        Connection: keep-alive
//        Content-Length: 114
//        Cache-Control: max-age=0
//        Origin: http://www.txt80.com
//        Upgrade-Insecure-Requests: 1
//        User-Agent: Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36
//        Content-Type: application/x-www-form-urlencoded
//        Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8
//Referer: http://www.txt80.com/e/search/result/?searchid=1460
//Accept-Encoding: gzip, deflate
//Accept-Language: zh-CN,zh;q=0.9
//Cookie: UM_distinctid=16af4129c1d1f6-003f5ed1f7376c-3c604504-1fa400-16af4129c1e17b; CNZZDATA1263014046=219582599-1558870453-https%253A%252F%252Fwww.baidu.com%252F%7C1558870453; ovoaklastsearchtime=1558873611
//
//tbname=download&tempid=1&keyboard=%E5%87%A1%E4%BA%BA&show=title%2Csoftsay%2Csoftwriter&Submit22=%E6%90%9C%E7%B4%A2


        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection","keep-alive");
        conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Cookie","UM_distinctid=16af4129c1d1f6-003f5ed1f7376c-3c604504-1fa400-16af4129c1e17b; CNZZDATA1263014046=219582599-1558870453-https%253A%252F%252Fwww.baidu.com%252F%7C1558870453; ovoaklastsearchtime=1558873611");

        if (params != null && !params.isEmpty()) {
            StringBuilder param = new StringBuilder();
            Set<String> keys = params.keySet();
            for (String item : keys) {
                param.append(item).append("=").append(URLEncoder.encode(params.get(item), "utf-8")).append("&");
            }
            if (param.length() > 1) {
                param.deleteCharAt(param.length() - 1);
            }
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(param.toString());
            Log.d("params:", param.toString());
            dos.flush();
            dos.close();
        }

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

    public static Object getConnOrRedirectUrlWithUserAgent(String urlLink, int totalNum) throws IOException {
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
        conn.setInstanceFollowRedirects(false);
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
            return conn.getHeaderField("Location");
        }
        Log.e("Test", "code error url:" + code + ",url=" + urlLink);
        return getConn(urlLink, totalNum - 1);
    }
}
