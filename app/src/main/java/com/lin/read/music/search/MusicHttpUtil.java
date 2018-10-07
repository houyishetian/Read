package com.lin.read.music.search;

import com.google.gson.GsonBuilder;
import com.lin.read.download.HttpUtils;
import com.lin.read.music.search.bean.seachresult.MusicBean;
import com.lin.read.music.search.bean.seachresult.MusicDataSongBean;
import com.lin.read.music.search.bean.vkey.VKeyBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class MusicHttpUtil {

    public static MusicDataSongBean searchMusic(String keywrods) {
        String linkUrl = MusicLinkUtil.getSearchLink(keywrods);
        if (linkUrl == null) {
            return null;
        }
        try {
            HttpURLConnection conn = HttpUtils.getConnWithUserAgent(linkUrl, 3);
            if (conn != null) {
                String str = parseInputStreamToStr(conn.getInputStream());
                int firstIndex = str.indexOf("{");
                int lastIndex = str.lastIndexOf("}");
                MusicBean musicBean = new GsonBuilder().create().fromJson(str.substring(firstIndex, lastIndex + 1), MusicBean.class);
                return musicBean.getData().getSong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static VKeyBean getVKey() {
        String linkUrl = MusicLinkUtil.getVKeyUrl();
        if (linkUrl == null) {
            return null;
        }
        try {
            HttpURLConnection conn = HttpUtils.getConnWithUserAgent(linkUrl, 3);
            if (conn != null) {
                VKeyBean vKeyBean = new GsonBuilder().create().fromJson(parseInputStreamToStr(conn.getInputStream()), VKeyBean.class);
                return vKeyBean;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String parseInputStreamToStr(InputStream inputStream) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        inputStream.close();
        String str = result.toString(StandardCharsets.UTF_8.name());
        return str;
    }
}
