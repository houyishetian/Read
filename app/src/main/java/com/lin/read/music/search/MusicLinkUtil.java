package com.lin.read.music.search;

import android.text.TextUtils;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MusicLinkUtil {
    private static final String TAG = MusicLinkUtil.class.getSimpleName();
    private static final String HOST = "http://c.y.qq.com";
    private static final String SEARCH_URL = "%s/soso/fcgi-bin/client_search_cp?ct=24&qqmusic_ver=1298&new_json=1&remoteplace=txt.yqq.center&t=0&aggr=1&cr=1&catZhida=1&lossless=0&flag_qc=0&p=1&n=100&w=%s&&jsonpCallback=searchCallbacksong2020&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
    private static final String GET_V_KEY_URL = "%s/base/fcgi-bin/fcg_music_express_mobile3.fcg?g_tk=0&loginUin=1008611&hostUin=0&format=json&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&cid=205361747&uin=1008611&songmid=003a1tne1nSz1Y&filename=C400003a1tne1nSz1Y.m4a&guid=1234567890";
    private static final String DOWNLOAD_URL = "http://182.140.219.28/streamoc.music.tc.qq.com/M800%s.mp3?vkey=%s&guid=1234567890&uin=1008611&fromtag=64";

    public static String getSearchLink(String searchKey) {
        if (TextUtils.isEmpty(searchKey)) {
            Log.e(TAG, "search key must not be null!");
            return null;
        }
        try {
            return String.format(SEARCH_URL, HOST, URLEncoder.encode(searchKey, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "encode search key failed:");
            e.printStackTrace();
        }
        return null;
    }

    public static String getVKeyUrl() {
        return String.format(GET_V_KEY_URL, HOST);
    }

    public static String getDownloadUrl(String mediaMid, String vKey) {
        if (TextUtils.isEmpty(mediaMid) || TextUtils.isEmpty(vKey)) {
            Log.e(TAG, "request params missing!");
            return null;
        }
        return String.format(DOWNLOAD_URL, mediaMid, vKey);
    }
}
